package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.script.Invocable;
import javax.script.ScriptException;

import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.porters.PorterManager.ExternalPorter;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.Print;

public class HelperCollector {
	
	private static final TreeMap<String, File> map = new TreeMap<>();
	public static final ArrayList<GroupCompound> LOADED = new ArrayList<>();
	
	public static final void reload(){
		LOADED.clear(); File root = new File("./helpers"); if(!root.exists()) root.mkdirs();
		for(File file : root.listFiles()){
			if(file.isDirectory()) continue;
			ExImPorter exim = PorterManager.getPorterFor(file, false);
			if(exim == null) continue;
			else{ map.put(file.getName(), file); }
			Print.console("Found Preview/Helper model: " + file.getName());
		}
	}
	
	public static final void load(String id){
		if(id == null || map.get(id) == null) return;
		ExImPorter exim = PorterManager.getPorterFor(map.get(id), false);
		if(exim == null) return;
		Print.console("Loading Preview/Helper model: " + map.get(id));
		if(exim.isInternal()){
			LOADED.add(((InternalPorter)exim).importModel(map.get(id)));
		}
		else{
			try{
				Invocable inv = (Invocable)((ExternalPorter)exim).eval();
				String result = (String)inv.invokeFunction("importModel", map.get(id));
				LOADED.add(SaveLoad.getModel(JsonUtil.getObjectFromString(result)));
			}
			catch(FileNotFoundException | ScriptException | NoSuchMethodException e){
				e.printStackTrace();
			}
		}
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
	
	public static final TreeMap<String, File> getMap(){ return map; }

}
