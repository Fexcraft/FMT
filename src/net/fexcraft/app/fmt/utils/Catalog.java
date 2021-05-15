package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.utils.HttpUtil;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Catalog {
	
	private static ArrayList<Resource> files = new ArrayList<>();
	private static ArrayList<File> mismatches = new ArrayList<>();
	private static final File CATALOG_FILE = new File("./catalog.fmt");
	private static String REMOTE_ROOT = "http://fexcraft.net/files/app_data/fmt/";

	public static void fetch(){
		Logging.log("Fetching catalog...");
		JsonElement elm = HttpUtil.request("http://fexcraft.net/files/app_data/fmt/catalog.fmt");
		if(!CATALOG_FILE.getParentFile().exists()) CATALOG_FILE.getParentFile().mkdirs();
		if(elm == null || !elm.isJsonObject()) return;
		Jsoniser.print(CATALOG_FILE, elm, false);
	}
	
	public static boolean load(){
		Logging.log("Loading file catalog...");
		JsonObject obj = Jsoniser.parseObj(CATALOG_FILE);
		if(obj == null || !obj.has("files")){
			Logging.log(">> Catalog is empty or missing.");
			return false;
		}
		REMOTE_ROOT = Jsoniser.get(obj, "file_root", REMOTE_ROOT);
		obj.get("files").getAsJsonArray().forEach(elm -> files.add(new Resource(elm)));
		Logging.log(obj);
		return true;
	}
	
	public static void clear(){
		files.clear();
		mismatches.clear();
	}

	public static void check(){
		for(Resource res : files){
			if(!res.file.exists() || (res.file.lastModified() != res.date && res.override) || res.remove) mismatches.add(res.file);
		}
		if(mismatches.size() > 0){
			Logging.log("Found " + mismatches.size() + " missing or outdated files.");
		}
	}
	
	public static class Resource {
		
		public String id;
		private File file;
		private long date;
		private boolean override, remove;
		
		public Resource(JsonElement elm){
			if(elm.isJsonArray()){
				JsonArray array = elm.getAsJsonArray();
				file = new File(id = array.get(0).getAsString());
				if(array.size() > 1) date = array.get(1).getAsLong();
				if(array.size() > 2) override = array.get(2).getAsBoolean();
				if(array.size() > 3) remove = array.get(3).getAsBoolean();
			}
			else{
				JsonObject obj = elm.getAsJsonObject();
				file = new File(id = Jsoniser.get(obj, "file", null));
				date = Jsoniser.get(obj, "date", 0);
				override = Jsoniser.get(obj, "override", true);
				remove = Jsoniser.get(obj, "remove", false);
			}
			id = file.toString();
		}
		
	}

	public static void process(boolean fresh){
		if(fresh) fetch();
		if(!load()) return;
		check();
		update();
		clear();
	}

	private static void update(){
		mismatches.forEach(file -> {
			if(file.exists()) file.delete();
		});
		//TODO
	}

}
