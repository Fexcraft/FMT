package net.fexcraft.app.fmt.launch;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

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
	//private static boolean DIALOG;

	public static void fetch(){
		Launcher.log("Fetching catalog...");
		JsonMap map = JsonHandler.parseURL("http://fexcraft.net/files/app_data/fmt/catalog.fmt");
		if(!CATALOG_FILE.getParentFile().exists()) CATALOG_FILE.getParentFile().mkdirs();
		if(map == null || map.empty()) return;
		JsonHandler.print(CATALOG_FILE, map, false, false);
	}
	
	public static boolean load(){
		clear();
		Launcher.log("Loading file catalog...");
		JsonMap map = JsonHandler.parse(CATALOG_FILE);
		if(map == null || !map.has("files")){
			Launcher.log("> Catalog is empty or missing.");
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
		mismatches.clear();
		Launcher.log("Looking for missing or outdated files...");
		for(Resource res : files){
			if(!res.file.exists() || (res.file.lastModified() < res.date && res.override) || res.remove) mismatches.add(res);
		}
		if(mismatches.size() > 0){
			Launcher.log("Found " + mismatches.size() + " missing or outdated files.");
			return true;
		}
		else{
			Launcher.log("# No missing or outdated files found.");
			Launcher.log("# FMT can be launched.");
			return false;
		}
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
			return new URL((REMOTE_ROOT + id).replace(" ", "%20"));
		}
		
	}

	public static void update(Runnable run){
		Thread thread = new Thread(() -> {
			boolean bool = false;
			int files = 0;
			for(Resource res : mismatches){
				if(res.file.exists() && res.file.delete()){
		        	Launcher.log("Removing " + res.id);
					bool = true;
					files++;
				}
			}
			if(bool) Launcher.log("> Removed outdated files. (" + files + " total)");
			files = 0;
			for(Resource res : mismatches){
		        try{
		        	Launcher.log("Downloading " + res.id);
		        	HttpURLConnection conn = (HttpURLConnection)res.getURL().openConnection();
		        	if(!res.file.getParentFile().exists()) res.file.getParentFile().mkdirs();
		            Files.copy(conn.getInputStream(), Paths.get(res.file.toURI()), StandardCopyOption.REPLACE_EXISTING);
		            files++;
		        }
		        catch(Exception e){
		        	Launcher.log(e);
					for(StackTraceElement trace : e.getStackTrace()){
						Launcher.log(trace.toString());
					}
		        }
			}
			Launcher.log("> Downloaded latest files. (" + files + " total)");
			if(run != null) run.run();
		});
		thread.setName("File Remover/Downloader");
		thread.start();
	}

}
