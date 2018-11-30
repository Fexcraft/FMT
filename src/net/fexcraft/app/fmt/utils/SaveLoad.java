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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToTMT;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.ui.generic.FileChooser.AfterTask;
import net.fexcraft.app.fmt.ui.generic.FileChooser.ChooserMode;
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
		UserInterface.FILECHOOSER.show(new String[]{ "Select file to open.", "Open" }, root, new AfterTask(){
			@Override
			public void run(){
				if(file == null || !file.exists()){
					FMTB.showDialogbox("Invalid Model File!", "(does it even exists?)", "ok.", null, DialogBox.NOTHING, null);
					return;
				}
				try{
					ZipFile zip = new ZipFile(file);
					zip.stream().forEach(elm -> {
						if(elm.getName().equals("model.jtmt")){
							try{
								loadModel(JsonUtil.getObjectFromInputStream(zip.getInputStream(elm)));
							} catch(IOException e){ e.printStackTrace(); }
						}
						else if(elm.getName().equals("texture.png")){
							FMTB.MODEL.setTexture("temp/" + FMTB.MODEL.name);
			            	try{ //in theory this should be always 2nd in the stream, so it is expected the model loaded already
								TextureManager.loadTextureFromZip(zip.getInputStream(elm), "temp/" + FMTB.MODEL.name, true);
							} catch(IOException e){ e.printStackTrace(); }
						}
					}); zip.close();
				}
				catch(Exception e){
					e.printStackTrace();
					FMTB.showDialogbox("Errors occured", "while parsing save file", "ok", null, DialogBox.NOTHING, null);
				}
			}
		}, ChooserMode.SAVEFILE_LOAD);
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
						UserInterface.FILECHOOSER.show(new String[]{ "Select save location.", "Select"}, root, new AfterTask(){
							@Override
							public void run(){
								if(file == null){
									FMTB.showDialogbox("Model save file is 'null'!", "Model will not be saved.", "OK", "Save", new Runnable(){
										@Override public void run(){ if(shouldclose){ FMTB.get().close(true); } }
									}, new Runnable(){
										@Override public void run(){ checkIfShouldSave(shouldclose); }
									});
								}
								else{
									FMTB.MODEL.file = file;
									saveModel(false, shouldclose); if(shouldclose){ FMTB.get().close(true); }
								}
							}
						}, ChooserMode.SAVEFILE_SAVE);
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

	public static void openNewModel(){
		checkIfShouldSave(false);
		FMTB.MODEL = new GroupCompound();
	}

	public static void saveModel(boolean bool, boolean openfile){
		if(bool || FMTB.MODEL.file == null){
			UserInterface.FILECHOOSER.show(new String[]{ "Select save location.", "Select" }, root, new AfterTask(){
				@Override
				public void run(){
					if(file == null){ FMTB.showDialogbox("Model save file is 'null'!", "Model will not be saved.", "OK", null, DialogBox.NOTHING, null); return; }
					FMTB.MODEL.file = file; toFile(FMTB.MODEL, null, openfile); FMTB.showDialogbox("Model Saved!", "", "ok!", null, DialogBox.NOTHING, null); return;
				}
			}, ChooserMode.SAVEFILE_SAVE);
		}
		else{
			toFile(FMTB.MODEL, null, openfile); return;
		} 
	}
	
	public static void toFile(GroupCompound compound, File file, boolean openfile){
		try{
	        FileOutputStream fileout = new FileOutputStream(file == null ? compound.file : file);
	        ZipOutputStream zipout = new ZipOutputStream(fileout);
	        zipout.putNextEntry(new ZipEntry("marker.fmt")); zipout.write(new byte[]{ Byte.MIN_VALUE }); zipout.closeEntry();
	        InputStream[] arr = new InputStream[compound.texture == null ? 1 : 2];
	        arr[0] = new ByteArrayInputStream(modelToJTMT(null, false).toString().getBytes(StandardCharsets.UTF_8));
	        if(arr.length > 1){
	        	try{
	        		ByteArrayOutputStream os = new ByteArrayOutputStream();
	        		ImageIO.write(TextureManager.getTexture(compound.texture, false).getImage(), "png", os);
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
	        if(file == null){
	        	Print.console("Saved model as FMTB Archive" + (arr.length > 1 ? " with texture." : "."));
	        }
	        file = file == null ? compound.file : file;
	        if(openfile && file.getParentFile() != null){
		        Desktop.getDesktop().open(file.getParentFile());
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