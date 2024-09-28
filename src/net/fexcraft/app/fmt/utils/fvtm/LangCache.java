package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.fmt.ui.workspace.FvtmPack;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class LangCache {

	public LinkedHashMap<String, LangEntry> entries = new LinkedHashMap<>();
	public final FvtmPack pack;
	public File lang;
	public File json;

	public LangCache(FvtmPack pack){
		this.pack = pack;
		json = new File(pack.file, "/assets/" + pack.id + "/lang/en_us.json");
		lang = new File(pack.file, "/assets/" + pack.id + "/lang/en_us.lang");
		if(!lang.exists()) genLangFile(lang);
		try{
			Scanner scanner = new Scanner(lang);
			String line = null;
			while(scanner.hasNextLine()){
				line = scanner.nextLine();
				if(!line.contains("=")){
					entries.put(line, new LangEntry(null, false));
					continue;
				}
				String l = line.substring(0, line.indexOf("="));
				if(l.startsWith("item.")){
					l = l.substring(5, l.length() - 5).replace(":", ".");
					entries.put(l, new LangEntry(line.substring(line.indexOf("=") + 1), true));
				}
				else{
					entries.put(l, new LangEntry(line.substring(l.length() + 1), false));
				}
			}
		}
		catch(Throwable e){
			e.printStackTrace();
		}
		if(!json.exists()) genLangJson(json);
		JsonMap map = JsonHandler.parse(json).asMap();
		for(Map.Entry<String, JsonValue<?>> entry : map.entries()){
			if(entry.getKey().startsWith("item.")){
				entries.put(entry.getKey().substring(5), new LangEntry(entry.getValue().string_value(), true));
			}
			else entries.put(entry.getKey(), new LangEntry(entry.getValue().string_value(), false));
		}
	}

	public void fill(String cid, String name){
		entries.put(pack.id + "." + cid, new LangEntry(name, true));
		save();
	}

	private void save(){
		JsonMap map = new JsonMap();
		for(Map.Entry<String, LangEntry> entry : entries.entrySet()){
			if(entry.getValue().item) map.add("item." + entry.getKey(), entry.getValue().name);
			else if(entry.getValue().name != null) map.add(entry.getKey(), entry.getValue().name);
		}
		JsonHandler.print(json, map, JsonHandler.PrintOption.SPACED);
		try{
			FileWriter writer = new FileWriter(lang);
			for(Map.Entry<String, LangEntry> entry : entries.entrySet()){
				if(entry.getValue().item){
					writer.write("item." + entry.getKey().replace(".", ":") + ".name=" + entry.getValue().name + "\n");
				}
				else if(entry.getValue().name == null){
					writer.write(entry.getKey() + "\n");
				}
				else writer.write(entry.getKey() + "=" + entry.getValue().name + "\n");
			}
			writer.flush();
			writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void genLangFile(File fl){
		try{
			if(!fl.getParentFile().exists()) fl.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(fl);
			writer.write("#info=File generated via FMT.\n");
			writer.flush();
			writer.close();
		}
		catch(Throwable e){
			e.printStackTrace();
		}
	}

	public static void genLangJson(File fl){
		try{
			if(!fl.getParentFile().exists()) fl.getParentFile().mkdirs();
			JsonMap map = new JsonMap();
			map.add("#info", "File generated via FMT.");
			JsonHandler.print(fl, map, JsonHandler.PrintOption.DEFAULT);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private record LangEntry(String name, boolean item){}

}
