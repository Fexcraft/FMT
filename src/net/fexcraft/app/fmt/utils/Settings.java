package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;

public class Settings {
	
	private static boolean fullscreen, floor = true, demo, lines = true, cube = true, polygon_marker = true;
	public static RGB selectedColor = new RGB(255, 255, 0);
	
	public static boolean fullscreen(){ return fullscreen; }

	public static boolean floor(){ return floor; }

	public static boolean lines(){ return lines; }

	public static boolean demo(){ return demo; }

	public static boolean cube(){ return cube; }
	
	public static boolean polygonMarker(){ return polygon_marker; }
	
	//
	
	public static boolean setFullScreen(boolean full){
		return fullscreen = full;
	}

	public static boolean toogleFullscreen(){
		return fullscreen = !fullscreen;
	}

	public static boolean toggleFloor(){
		return floor = !floor;
	}

	public static boolean toggleLines(){
		return lines = !lines;
	}

	public static boolean toggleCube(){
		return cube = !cube;
	}

	public static boolean togglePolygonMarker(){
		return polygon_marker = !polygon_marker;
	}
	
	public static boolean toggleDemo(){
		return demo = !demo;
	}
	
	//
	
	private static TreeMap<String, Object> values = new TreeMap<String, Object>();
	private static Object temp;
	
	public float getFloat(String str){
		temp = values.get(str); if(temp != null && temp instanceof Float){ return (float)temp; } else return 0;
	}
	
	public String getString(String str){
		return values.containsKey(str) ? values.get(str).toString() : "null";
	}
	
	public boolean getBoolean(String str){
		return getBoolean(str, false);
	}
	
	public boolean getBoolean(String str, boolean def){
		return ((temp = values.get(str)) != null && temp instanceof Boolean) ? (Boolean)temp : temp instanceof String ? Boolean.parseBoolean(str) : def;
	}
	
	public JsonObject getJsonObject(String str){
		return (temp = values.get(str)) instanceof JsonObject ? (JsonObject)temp : null;
	}
	
	public Object setValue(String str, Object value){
		temp = values.put(str, value); save(); return temp;
	}

	public static void load(){
		if(values.size() > 0) values.clear();
		JsonObject obj = JsonUtil.get(new File("./settings.json"));
		if(obj.has("settings")){
			obj.get("settings").getAsJsonObject().entrySet().forEach((entry) -> {
				JsonElement elm = entry.getValue();
				if(elm.isJsonObject()){
					values.put(entry.getKey(), elm.getAsJsonObject());
				}
				else if(elm.isJsonPrimitive()){
					JsonPrimitive prim = elm.getAsJsonPrimitive();
					if(prim.isBoolean()){
						values.put(entry.getKey(), prim.getAsBoolean());
					}
					else if(prim.isNumber()){
						values.put(entry.getKey(), prim.getAsNumber().floatValue());
					}
					else if(prim.isString()){
						values.put(entry.getKey(), prim.getAsString());
					}
				}
			});
		}
		//
		if(obj.has("selection_color")){
			JsonArray array = obj.get("selection_color").getAsJsonArray();
			if(array.size() >= 3){
				selectedColor = new RGB(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
			}
		}
		//
		fullscreen = JsonUtil.getIfExists(obj, "fullscreen", fullscreen);
		floor = JsonUtil.getIfExists(obj, "floor", floor); lines = JsonUtil.getIfExists(obj, "lines", lines);
		cube = JsonUtil.getIfExists(obj, "cube", cube); demo = JsonUtil.getIfExists(obj, "demo", demo);
	}

	public static void save(){
		JsonObject obj = new JsonObject();
		JsonObject sub = new JsonObject();
		values.forEach((key, elm) -> {
			if(elm instanceof JsonObject){
				sub.add(key, (JsonObject)elm);
			}
			else if(elm instanceof Boolean){
				sub.addProperty(key, (Boolean)elm);
			}
			else if(elm instanceof Float){
				sub.addProperty(key, (Float)elm);
			}
			else if(elm instanceof String){
				sub.addProperty(key, (String)elm);
			}
			else{
				try{
					sub.addProperty(key, elm.toString());
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		obj.add("settings", sub);
		//
		JsonArray colarr = new JsonArray();
		for(byte bit : selectedColor.toByteArray()){
			colarr.add(new JsonPrimitive(bit + 128));
		}
		obj.add("selection_color", colarr);
		//
		obj.addProperty("fullscreen", fullscreen);
		obj.addProperty("floor", floor); obj.addProperty("lines", lines);
		obj.addProperty("cube", cube); obj.addProperty("demo", demo);
		JsonUtil.write(new File("./settings.json"), obj);
	}
	
	/** see https://stackoverflow.com/a/3758880/6095495 */
	public static final String byteCountToString(long bytes, boolean si){
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
