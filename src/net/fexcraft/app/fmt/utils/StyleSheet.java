package net.fexcraft.app.fmt.utils;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;

//TODO see about general ARGB/RGBA conversion correctness.
public class StyleSheet {
	
	public static final TreeMap<String, TreeMap<String, Integer>> COLOURS = new TreeMap<>();
	
	public static final Integer getColourFor(String group, String entry){
		return getColourFor(group, entry, 0xffcfcfcf);
	}
	
	public static final Integer getColourFor(String group, String entry, int def){
		if(!COLOURS.containsKey(group) || !COLOURS.get(group).containsKey(entry)) return def; else return COLOURS.get(group).get(entry);
	}

	public static void load(){
		JsonObject object = JsonUtil.get(new java.io.File("./stylesheel.json"));
		if(!object.has("groups")) return; JsonObject obj;
		JsonObject groups = object.get("groups").getAsJsonObject();
		for(Map.Entry<String, JsonElement> elm : groups.entrySet()){
			obj = elm.getValue().getAsJsonObject();
			if(obj.entrySet().size() > 0) COLOURS.put(elm.getKey(), new TreeMap<>()); else continue;
			for(Map.Entry<String, JsonElement> entry : obj.entrySet()){
				if(!entry.getValue().isJsonPrimitive()) continue;
				COLOURS.get(elm.getKey()).put(entry.getKey(), new RGB(entry.getValue().getAsString()).packed);
			}
		}
	}

	public static void save(){
		JsonObject object = new JsonObject(), obj = null;
		for(Map.Entry<String, TreeMap<String, Integer>> map : COLOURS.entrySet()){
			if(map.getValue().isEmpty()) continue; obj = new JsonObject();
			for(Map.Entry<String, Integer> entry : map.getValue().entrySet()){
				obj.addProperty(entry.getKey(), "#" + entry.getValue());
			}
			object.add(map.getKey(), obj);
		}
		object.addProperty("last_fmt_version_used", FMTB.version);
		object.addProperty("last_fmt_exit", Time.getAsString(Time.getDate()));
		JsonUtil.write(new java.io.File("./stylesheet.json"), object);
	}

}
