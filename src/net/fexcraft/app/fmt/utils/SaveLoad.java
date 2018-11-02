package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.Map.Entry;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToTMT;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExInPorter;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
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
		checkIfShouldSave(false);
		File modelfile = getFile("Select file to open.");
		if(modelfile == null || !modelfile.exists()){
			FMTB.showDialogbox("Invalid Model File!", "(does it even exists?)", "ok.", null, DialogBox.NOTHING, null);
			return;
		}
		int errorcount = 0;
		try{
			errorcount = loadModel(JsonUtil.read(modelfile, false).getAsJsonObject());
		}
		catch(Exception e){
			FMTB.showDialogbox("Error", e.getLocalizedMessage(), "ok.", null, DialogBox.NOTHING, null); e.printStackTrace();
		}
		if(errorcount > 0){
			FMTB.showDialogbox(errorcount + " errors occured", "while parsing save file", "ok", null, DialogBox.NOTHING, null);//TODO 2nd button as "open console"
		}
	}
	
	public static int loadModel(JsonObject obj){
		GroupCompound compound = new GroupCompound(); compound.getCompound().clear(); int errs = 0;
		compound.textureX = JsonUtil.getIfExists(obj, "texture_x", 256).intValue();
		compound.textureY = JsonUtil.getIfExists(obj, "texture_y", 256).intValue();
		compound.creators = JsonUtil.jsonArrayToStringArray(JsonUtil.getIfExists(obj, "creators", new JsonArray()).getAsJsonArray());
		JsonObject model = obj.get("model").getAsJsonObject();
		for(Entry<String, JsonElement> entry : model.entrySet()){
			try{
				TurboList list = new TurboList(entry.getKey()); JsonArray array = entry.getValue().getAsJsonArray();
				for(JsonElement elm : array){ list.add(JsonToTMT.parseWrapper(compound, elm.getAsJsonObject())); }
				compound.getCompound().put(entry.getKey(), list);
			}
			catch(Exception e){
				e.printStackTrace(); errs++;
			}
		} FMTB.MODEL = compound; FMTB.MODEL.updateFields(); FMTB.MODEL.recompile(); return errs;
	}
	
	public static void checkIfShouldSave(boolean shouldclose){
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
						JsonUtil.write(FMTB.MODEL.file, modelToJTMT(false));
						if(shouldclose){ FMTB.get().close(true); }
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
			for(ExInPorter porter : PorterManager.getPorters(!load)){
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
					return arg0.isDirectory() || arg0.getName().endsWith(".jtmt");
				}
				//
				@Override
				public String getDescription(){
					return "JTMT Save File";
				}
				//
				@Override
				public String getFileEnding(){
					return ".jtmt";
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

	public static void saveModel(boolean bool){
		if(bool || FMTB.MODEL.file == null){
			FMTB.MODEL.file = getFile("Select save location.");
		}
		if(FMTB.MODEL.file == null){
			FMTB.showDialogbox("Model save file is 'null'!", "Model will not be saved.", "OK", null, DialogBox.NOTHING, null);
			return;
		}
		JsonUtil.write(FMTB.MODEL.file, modelToJTMT(false));
	}

	/**
	 * @return JTMT save form of the Model/GroupCompound
	 */
	public static JsonObject modelToJTMT(boolean export){
		GroupCompound compound = FMTB.MODEL;
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 1);
		obj.addProperty("texture_size_x", compound.textureX);
		obj.addProperty("texture_size_y", compound.textureY);
		JsonArray creators = new JsonArray();
		if(compound.creators.isEmpty()){
			creators.add(new JsonPrimitive("//TODO"));//TODO
		}
		else{
			for(String str : compound.creators) creators.add(new JsonPrimitive(str));
		}
		obj.add("creators", creators);
		obj.addProperty("type", "jtmt");
		JsonObject model = new JsonObject();
		for(Entry<String, TurboList> entry : compound.getCompound().entrySet()){
			JsonArray array = new JsonArray(); TurboList list = entry.getValue();
			for(PolygonWrapper wrapper : list){
				array.add(wrapper.toJson(export));
			}
			model.add(entry.getKey(), array);
		}
		obj.add("model", model);
		return obj;
	}
	
}