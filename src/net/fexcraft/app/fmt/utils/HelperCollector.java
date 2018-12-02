package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.script.Invocable;
import javax.script.ScriptException;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.porters.PorterManager.ExternalPorter;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.Print;

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

}
