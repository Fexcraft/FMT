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
	
	private static boolean fullscreen, floor = true, demo, lines = true, cube = true, polygon_marker = true, polygon_count = true, lighting = false, editor_shortcuts = false;//, raypick = false;
	public static RGB selectedColor = new RGB(255, 255, 0);
	public static RGB background_color = new RGB(127, 127, 127);
	static{ background_color.alpha = 0.2f; }
	public static float[] light0_position = new float[]{ 0, 1, 0, 0 };
	
	public static boolean fullscreen(){ return fullscreen; }

	public static boolean floor(){ return floor; }

	public static boolean lines(){ return lines; }

	public static boolean demo(){ return demo; }

	public static boolean cube(){ return cube; }
	
	public static boolean polygonMarker(){ return polygon_marker; }

	public static boolean polygonCount(){ return polygon_count; }

	//public static boolean rayPicking(){ return raypick; }

	public static boolean lighting(){ return lighting; }

	public static boolean editorShortcuts(){ return editor_shortcuts; }
	
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

	public static boolean togglePolygonCount(){
		return polygon_count = !polygon_count;
	}

	public static boolean toggleLighting(){
		return lighting = !lighting;
	}

	public static boolean toggleEditorShortcuts(){
		return editor_shortcuts = !editor_shortcuts;
	}
	
	/*public static boolean toggleRaypick(){
		return raypick = !raypick;
	}*/
	
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

	public static void load() throws Throwable {
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
			int[] arr = JsonUtil.getIntegerArray(obj.get("selection_color").getAsJsonArray());
			selectedColor = new RGB(arr[0], arr[1], arr[2]);
		}
		if(obj.has("background_color")){
			int[] arr = JsonUtil.getIntegerArray(obj.get("background_color").getAsJsonArray());
			background_color = new RGB(arr[0], arr[1], arr[2]);
			background_color.alpha = arr[3] / 255f;
		}
		//
		fullscreen = JsonUtil.getIfExists(obj, "fullscreen", fullscreen);
		floor = JsonUtil.getIfExists(obj, "floor", floor); lines = JsonUtil.getIfExists(obj, "lines", lines);
		cube = JsonUtil.getIfExists(obj, "cube", cube); demo = JsonUtil.getIfExists(obj, "demo", demo);
		//raypick = JsonUtil.getIfExists(obj, "raypick", raypick);
		polygon_marker = JsonUtil.getIfExists(obj, "polygon_marker", polygon_marker);
		polygon_count = JsonUtil.getIfExists(obj, "polygon_count", polygon_count);
		lighting = JsonUtil.getIfExists(obj, "lighting", lighting);
		light0_position = obj.has("light0_position") ? JsonUtil.getFloatArray(obj.get("light0_position").getAsJsonArray()) : light0_position;
		editor_shortcuts = JsonUtil.getIfExists(obj, "editor_shortcuts", editor_shortcuts);
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
				try{ sub.addProperty(key, elm.toString()); }
				catch(Exception e){ e.printStackTrace(); }
			}
		});
		obj.add("settings", sub);
		//
		JsonArray colarr = new JsonArray();
		for(byte bit : selectedColor.toByteArray()){
			colarr.add(new JsonPrimitive(bit + 128));
		}
		obj.add("selection_color", colarr);
		colarr = new JsonArray();
		for(byte bit : background_color.toByteArray()){
			colarr.add(new JsonPrimitive(bit + 128));
		} colarr.add(background_color.alpha * 255f);
		obj.add("background_color", colarr);
		//
		obj.addProperty("fullscreen", fullscreen);
		obj.addProperty("floor", floor); obj.addProperty("lines", lines);
		obj.addProperty("cube", cube); obj.addProperty("demo", demo);
		//obj.addProperty("raypick", raypick);
		obj.addProperty("polygon_marker", polygon_marker);
		obj.addProperty("polygon_count", polygon_count);
		obj.addProperty("lighting", lighting);
		obj.add("light0_position", JsonUtil.toJsonArray(light0_position));
		obj.addProperty("editor_shortcuts", editor_shortcuts);
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
