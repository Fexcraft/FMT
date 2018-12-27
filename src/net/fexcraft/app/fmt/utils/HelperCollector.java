package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.zip.ZipFile;

import javax.script.Invocable;
import javax.script.ScriptException;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.porters.PorterManager.ExternalPorter;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.common.utils.ZipUtil;

public class HelperCollector {
	
	public static final ArrayList<GroupCompound> LOADED = new ArrayList<>();
	
	public static final void reload(){
		LOADED.clear(); File root = new File("./helpers"); if(!root.exists()) root.mkdirs();
	}
	
	public static final void load(File file, ExImPorter exim){
		if(file == null || exim == null) return;
		Print.console("Loading Preview/Helper model: " + file.getName());
		if(exim.isInternal()){
			LOADED.add(((InternalPorter)exim).importModel(file));
		}
		else{
			try{
				Invocable inv = (Invocable)((ExternalPorter)exim).eval();
				String result = (String)inv.invokeFunction("importModel", file);
				LOADED.add(SaveLoad.getModel(JsonUtil.getObjectFromString(result)));
			}
			catch(FileNotFoundException | ScriptException | NoSuchMethodException e){
				e.printStackTrace();
			}
		}
	}

	/** For loading FMTBs.*/
	public static void loadFMTB(File file){
		if(file == null || !file.exists()){
			FMTB.showDialogbox("Invalid Model File!", "(does it even exists?)", "ok.", null, DialogBox.NOTHING, null);
			return;
		}
		GroupCompound compound = null;
		try{
			boolean conM = ZipUtil.contains(file, "model.jtmt"), conT = ZipUtil.contains(file, "texture.png");
			ZipFile zip = new ZipFile(file);
			if(conM){
				compound = SaveLoad.getModel(JsonUtil.getObjectFromInputStream(zip.getInputStream(zip.getEntry("model.jtmt"))));
			}
			else{
				FMTB.showDialogbox("Invalid Model File", "model.jtmt missing.", "ok.", null, DialogBox.NOTHING, null);
				zip.close(); return;
			}
			if(conT){
				TextureManager.loadTextureFromZip(zip.getInputStream(zip.getEntry("texture.png")), "temp/" + compound.name, true);
				compound.setTexture("temp/" + compound.name);
			} zip.close();
		}
		catch(Exception e){
			e.printStackTrace();
			FMTB.showDialogbox("Errors occured", "while parsing save file", "ok", null, DialogBox.NOTHING, null);
		}
		if(compound != null){ LOADED.add(compound); }
	}

}
