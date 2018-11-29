package net.fexcraft.app.fmt.utils;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToTMT;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

public class SaveLoad {
	
	private static File root;
	static {
		root = new File("./saves");
		if(!root.exists()) root.mkdirs();
	}
	
	public static final File getRoot(){
		return root;
	}

	public static void openModel(){
		//TODO find a way to add this back -->> checkIfShouldSave(false);
		File modelfile = getFile("Select file to open.");
		if(modelfile == null || !modelfile.exists()){
			FMTB.showDialogbox("Invalid Model File!", "(does it even exists?)", "ok.", null, DialogBox.NOTHING, null);
			return;
		}
		try{
			ZipFile file = new ZipFile(modelfile);
			file.stream().forEach(elm -> {
				if(elm.getName().equals("model.jtmt")){
					try{
						loadModel(JsonUtil.getObjectFromInputStream(file.getInputStream(elm)));
					} catch(IOException e){ e.printStackTrace(); }
				}
				else if(elm.getName().equals("texture.png")){
					FMTB.MODEL.setTexture("temp/" + FMTB.MODEL.name);
	            	try{ //in theory this should be always 2nd in the stream, so it is expected the model loaded already
						TextureManager.loadTextureFromZip(file.getInputStream(elm), "temp/" + FMTB.MODEL.name, true);
					} catch(IOException e){ e.printStackTrace(); }
				}
			}); file.close();
		}
		catch(Exception e){
			e.printStackTrace();
			FMTB.showDialogbox("Errors occured", "while parsing save file", "ok", null, DialogBox.NOTHING, null);
		}
	}
	
	public static void loadModel(JsonObject obj){
		FMTB.MODEL = getModel(obj); FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
	}
	
	public static void checkIfShouldSave(boolean shouldclose){
		TextureUpdate.HALT = true;
		if(FMTB.MODEL.countTotalMRTs() > 0){
			FMTB.showDialogbox("Do you want to save the", "current model first?", "Yes", "No", new Runnable(){
				@Override
				public void run(){
					if(FMTB.MODEL.file == null){
						FMTB.MODEL.file = getFile("Select save location.");
					}
					//saveModel(false)
					if(FMTB.MODEL.file == null){
						FMTB.showDialogbox("Model save file is 'null'!", "Model will not be saved.", "OK", "Save", new Runnable(){
							@Override public void run(){ if(shouldclose){ FMTB.get().close(true); } }
						}, new Runnable(){
							@Override public void run(){ checkIfShouldSave(shouldclose); }
						});
						//TODO add cancel;
					}
					else{
						saveModel(false, shouldclose); if(shouldclose){ FMTB.get().close(true); }
					}
				}
			}, new Runnable(){
				@Override
				public void run(){
					Print.console("selected > no saving of current");
					if(shouldclose){ FMTB.get().close(true); }
				}
			});
		}
		else if(shouldclose){
			FMTB.get().close(true);
		}
	}
	
	@Deprecated
	public static File getFile(String title){
		return getFile(title, null, true, true);
	}

	@Deprecated
	public static File getFile(String title, File otherroot, boolean load, boolean nofilter){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(otherroot != null && !otherroot.exists()) otherroot.mkdirs();
		chooser.setCurrentDirectory(otherroot == null ? root : otherroot);
		chooser.setDialogTitle(title);
		if(!nofilter){
			for(ExImPorter porter : PorterManager.getPorters(!load)){
				chooser.addChoosableFileFilter(new JFileFilter(){
					@Override
					public boolean accept(File arg0){
						if(arg0.isDirectory()) return true;
						for(String ext : porter.getExtensions()){
							if(arg0.getName().endsWith(ext)) return true;
						} return false;
					}
					//
					@Override
					public String getDescription(){
						return porter.getName() + (load ? " [I]" : "[E]");
					}
					//
					@Override
					public String getFileEnding(){
						return porter.getExtensions().length == 0 ? ".no-ext" : porter.getExtensions()[0];
					}
				});
			}
		}
		else{
			chooser.addChoosableFileFilter(new JFileFilter(){
				@Override
				public boolean accept(File arg0){
					return arg0.isDirectory() || arg0.getName().endsWith(".fmtb");
				}
				//
				@Override
				public String getDescription(){
					return "FMTB Save File";
				}
				//
				@Override
				public String getFileEnding(){
					return ".fmtb";
				}
			});
		}
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.showOpenDialog(null);
		File file = chooser.getSelectedFile();
		if(file != null && !chooser.getFileFilter().accept(file)){
			file = new File(file.getParentFile(), file.getName() + ((JFileFilter)chooser.getFileFilter()).getFileEnding());
		} return file;
	}
	
	private static abstract class JFileFilter extends FileFilter {

		public abstract String getFileEnding();
		
	}

	public static void openNewModel(){
		checkIfShouldSave(false);
		FMTB.MODEL = new GroupCompound();
	}

	public static void saveModel(boolean bool, boolean openfile){
		if(bool || FMTB.MODEL.file == null){
			FMTB.MODEL.file = getFile("Select save location.");
		}
		if(FMTB.MODEL.file == null){
			FMTB.showDialogbox("Model save file is 'null'!", "Model will not be saved.", "OK", null, DialogBox.NOTHING, null);
			return;
		}
		//
		try{
	        FileOutputStream fileout = new FileOutputStream(FMTB.MODEL.file);
	        ZipOutputStream zipout = new ZipOutputStream(fileout);
	        zipout.putNextEntry(new ZipEntry("marker.fmt")); zipout.write(new byte[]{ Byte.MIN_VALUE }); zipout.closeEntry();
	        InputStream[] arr = new InputStream[FMTB.MODEL.texture == null ? 1 : 2];
	        arr[0] = new ByteArrayInputStream(modelToJTMT(null, false).toString().getBytes(StandardCharsets.UTF_8));
	        if(arr.length > 1){
	        	try{
	        		ByteArrayOutputStream os = new ByteArrayOutputStream();
	        		ImageIO.write(TextureManager.getTexture(FMTB.MODEL.texture, false).getImage(), "png", os);
	        		arr[1] = new ByteArrayInputStream(os.toByteArray());
	        	} catch(Exception e){ e.printStackTrace(); }
	        }
	        zipout.setComment("FMTB Save File generated by the FMT (Fexcraft Modelling Toolbox).");
	        for(int i = 0; i < arr.length; i++){
	            zipout.putNextEntry(new ZipEntry(i == 1 ? "texture.png" : "model.jtmt"));
	            byte[] bytes = new byte[1024]; int length;
	            while((length = arr[i].read(bytes)) >= 0){
	                zipout.write(bytes, 0, length);
	            } zipout.closeEntry(); arr[i].close();
	        }
	        zipout.close(); fileout.close();
	        Print.console("Saved model as FMTB Archive" + (arr.length > 1 ? " with texture." : "."));
	        if(openfile && FMTB.MODEL.file.getParentFile() != null){
		        Desktop.getDesktop().open(FMTB.MODEL.file.getParentFile());
	        }
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * @param root 
	 * @return JTMT save form of the Model/GroupCompound
	 */
	public static JsonObject modelToJTMT(GroupCompound root, boolean export){
		GroupCompound compound = root == null ? FMTB.MODEL : root;
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 2);
		obj.addProperty("name", compound.name);
		obj.addProperty("texture_size_x", compound.textureX);
		obj.addProperty("texture_size_y", compound.textureY);
		JsonArray creators = new JsonArray();
		if(compound.creators.isEmpty()){
			creators.add(SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "OfflineUser");
		}
		else{
			for(String str : compound.creators) creators.add(str);
			if(SessionHandler.isLoggedIn() && !compound.creators.contains(SessionHandler.getUserName())){
				creators.add(SessionHandler.getUserName());
			}
		}
		obj.add("creators", creators);
		obj.addProperty("type", "jtmt");
		JsonObject model = new JsonObject();
		for(Entry<String, TurboList> entry : compound.getCompound().entrySet()){
			JsonObject group = new JsonObject(); JsonArray array = new JsonArray();
			TurboList list = entry.getValue();
			if(!export){
				group.addProperty("visible", list.visible);
				if(list.color != null){
					byte[] colarr = list.color.toByteArray();
					JsonArray colar = new JsonArray();
					colar.add(colarr[0]); colar.add(colarr[1]); colar.add(colarr[2]);
					group.add("color", colar);
				}
				group.addProperty("minimized", list.minimized);
				group.addProperty("selected", list.selected);
			}
			group.addProperty("name", list.id);
			for(PolygonWrapper wrapper : list){
				array.add(wrapper.toJson(export));
			}
			group.add("polygons", array);
			model.add(entry.getKey(), group);
		}
		obj.add("groups", model);
		return obj;
	}
	
	public static GroupCompound getModel(JsonObject obj){
		GroupCompound compound = new GroupCompound(); compound.getCompound().clear();
		compound.name = JsonUtil.getIfExists(obj, "name", "unnamed model");
		compound.textureX = JsonUtil.getIfExists(obj, "texture_size_x", 256).intValue();
		compound.textureY = JsonUtil.getIfExists(obj, "texture_size_y", 256).intValue();
		compound.creators = JsonUtil.jsonArrayToStringArray(JsonUtil.getIfExists(obj, "creators", new JsonArray()).getAsJsonArray());
		if(JsonUtil.getIfExists(obj, "format", 2).intValue() == 1){
			JsonObject model = obj.get("model").getAsJsonObject();
			for(Entry<String, JsonElement> entry : model.entrySet()){
				try{
					TurboList list = new TurboList(entry.getKey()); JsonArray array = entry.getValue().getAsJsonArray();
					for(JsonElement elm : array){ list.add(JsonToTMT.parseWrapper(compound, elm.getAsJsonObject())); }
					compound.getCompound().put(entry.getKey(), list);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			return compound;
		}
		JsonObject groups = obj.get("groups").getAsJsonObject();
		for(Entry<String, JsonElement> entry : groups.entrySet()){
			try{
				TurboList list = new TurboList(entry.getKey()); JsonObject group = entry.getValue().getAsJsonObject();
				list.minimized = JsonUtil.getIfExists(group, "minimized", false);
				list.selected = JsonUtil.getIfExists(group, "selected", false);
				list.visible = JsonUtil.getIfExists(group, "visible", true);
				if(group.has("color")){
					JsonArray colorarr = group.get("color").getAsJsonArray();
					list.color = new RGB(colorarr.get(0).getAsByte(), colorarr.get(1).getAsByte(), colorarr.get(2).getAsByte());
				}
				JsonArray polygons = group.get("polygons").getAsJsonArray();
				for(JsonElement elm : polygons){ list.add(JsonToTMT.parseWrapper(compound, elm.getAsJsonObject())); }
				compound.getCompound().put(entry.getKey(), list);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return compound;
	}
	
}