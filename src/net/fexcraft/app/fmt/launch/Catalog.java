package net.fexcraft.app.fmt.launch;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Catalog {
	
	private static ArrayList<Resource> files = new ArrayList<>();
	private static ArrayList<Resource> mismatches = new ArrayList<>();
	private static final File CATALOG_FILE = new File("./catalog.fmt");
	private static String REMOTE_ROOT = "http://fexcraft.net/files/app_data/fmt/";
	private static boolean DIALOG;

	public static void fetch(){
		Logging.log("Fetching catalog...");
		JsonMap map = JsonHandler.parseURL("http://fexcraft.net/files/app_data/fmt/catalog.fmt");
		if(!CATALOG_FILE.getParentFile().exists()) CATALOG_FILE.getParentFile().mkdirs();
		if(map == null || map.empty()) return;
		JsonHandler.print(CATALOG_FILE, map, false, false);
	}
	
	public static boolean load(){
		Logging.log("Loading file catalog...");
		JsonMap map = JsonHandler.parse(CATALOG_FILE);
		if(map == null || !map.has("files")){
			Logging.log(">> Catalog is empty or missing.");
			return false;
		}
		REMOTE_ROOT = map.get("file_root", REMOTE_ROOT);
		map.getArrayElements("files").forEach(elm -> files.add(new Resource(elm)));
		return true;
	}
	
	public static void clear(){
		files.clear();
		mismatches.clear();
	}

	public static boolean check(){
		for(Resource res : files){
			if(!res.file.exists() || (res.file.lastModified() != res.date && res.override) || res.remove) mismatches.add(res);
		}
		if(mismatches.size() > 0){
			Logging.log("Found " + mismatches.size() + " missing or outdated files.");
			return true;
		}
		return false;
	}
	
	public static class Resource {
		
		public String id;
		private File file;
		private long date;
		private boolean override = true, remove;
		
		public Resource(JsonObject<?> obj){
			if(obj.isArray()){
				JsonArray array = obj.asArray();
				id = array.get(0).value();
				if(array.size() > 1) date = array.get(1).long_value();
				if(array.size() > 2) override = array.get(2).value();
				if(array.size() > 3) remove = array.get(3).value();
			}
			else{
				JsonMap map = obj.asMap();
				id = map.get("file", null);
				date = map.get("date", 0);
				override = map.get("override", override);
				remove = map.get("remove", remove);
			}
			file = new File(!id.startsWith("./") ? "./" + id : id);
		}

		public URL getURL() throws MalformedURLException {
			return new URL(REMOTE_ROOT + id);
		}
		
	}

	public static void process(boolean fresh){
		FMT.INSTANCE.setTitle("Checking Catalog...");
		if(fresh) fetch();
		if(!load()){
			FMT.updateTitle();
			return;
		}
		FMT.INSTANCE.setTitle("Updating Installation...");
		if(check() && update(fresh)){
			if(fresh) GenericDialog.show("update.title", "dialog.button.exit", null, () -> FMT.close(), null, "update.remote_catalog_update");
			else DIALOG = true;
		}
		clear();
		FMT.updateTitle();
	}

	private static boolean update(boolean fresh){
		boolean bool = false;
		int files = 0;
		for(Resource res : mismatches){
			if(res.file.exists() && res.file.delete()){
				bool = true;
				files++;
			}
		}
		if(bool) Logging.log("Removed outdated files. (" + files + " total)");
		files = 0;
		for(Resource res : mismatches){
	        try{
	        	Logging.log("Downloading " + res.id);
	        	HttpURLConnection conn = (HttpURLConnection)res.getURL().openConnection();
	            Files.copy(conn.getInputStream(), Paths.get(res.file.toURI()));
	            files++;
	        }
	        catch(Exception e){
	        	Logging.log(e);
	        }
		}
		Logging.log("Downloaded latest files. (" + files + " total)");
		return bool;
	}

	public static void show(){
		if(!DIALOG) return;
		GenericDialog.show("update.title", "dialog.button.exit", "dialog.button.ok", () -> FMT.close(), null, "update.local_catalog_update0", "update.local_catalog_update1");
		DIALOG = false;
	}

}
