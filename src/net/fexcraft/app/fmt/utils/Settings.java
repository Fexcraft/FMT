package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;

public class Settings {
	
	private static Setting floor, lines, demo, cube, polygon_marker, lighting, cullface, animate,
		discordrpc, discordrpc_sm, discordrpc_rtonm, numberfieldarrows, preview_colorpicker;
	public static Setting movespeed, mouse_sensivity, internal_cursor, vsync, vsynchalf, debug;
	public static Setting darktheme, no_scroll_fields, old_rotation, center_marker;
	public static Setting orbital_camera, oc_center_on_part, internal_filechooser;
	public static Setting drag_painting, background_color, decimal_sizes;
	//
	public static final ArrayList<Consumer<Boolean>> THEME_CHANGE_LISTENER = new ArrayList<>();

	public static boolean floor(){ return floor.getValue(); }

	public static boolean lines(){ return lines.getValue(); }

	public static boolean demo(){ return demo.getValue(); }

	public static boolean cube(){ return cube.getValue(); }
	
	public static boolean polygonMarker(){ return polygon_marker.getValue(); }

	public static boolean lighting(){ return lighting.getValue(); }

	public static boolean cullface(){ return cullface.getValue(); }
	
	public static boolean animate(){ return animate.getValue(); }

	public static boolean discordrpc(){ return discordrpc.getValue(); }
	
	public static boolean discordrpc_showmodel(){ return discordrpc_sm.getValue(); }
	
	public static boolean discordrpc_resettimeronnewmodel(){ return discordrpc_rtonm.getValue(); }

	public static boolean numberfieldarrows(){ return numberfieldarrows.getValue(); }

	public static boolean preview_colorpicker(){ return preview_colorpicker.getValue(); }
	
	public static boolean internal_cursor(){ return internal_cursor.getValue(); }

	public static boolean vsync(){ return vsync.getValue(); }

	public static boolean darktheme(){ return darktheme.getValue(); }
	
	public static boolean no_scroll_fields(){ return no_scroll_fields.getValue(); }

	public static boolean oldrot(){ return old_rotation.getValue(); }
	
	public static boolean orbital_camera(){ return orbital_camera.getValue(); }
	
	public static boolean center_on_part(){ return oc_center_on_part.getValue(); }
	
	public static boolean internal_filechooser(){ return internal_filechooser.getValue(); }

	public static boolean center_marker(){ return center_marker.getBooleanValue(); }

	public static boolean ui_debug(){ return debug.getBooleanValue(); }

	public static boolean dragPainting(){ return drag_painting.getBooleanValue(); }

	public static boolean vsyncHalf(){ return vsynchalf.getValue(); }
	
	public static boolean decimal_sizes(){ return decimal_sizes.getValue(); }
	
	//

	public static boolean toggleFloor(){
		return floor.toggle();
	}

	public static boolean toggleLines(){
		return lines.toggle();
	}

	public static boolean toggleCube(){
		return cube.toggle();
	}

	public static boolean togglePolygonMarker(){
		return polygon_marker.toggle();
	}

	public static boolean toggleCenterMarker(){
		return center_marker.toggle();
	}
	
	public static boolean toggleDemo(){
		return demo.toggle();
	}

	public static boolean togglePreviewColorpicker(){
		return preview_colorpicker.toggle();
	}

	public static boolean toggleLighting(){
		log("Toggling lighting: " + !lighting.getBooleanValue());
		return lighting.toggle();
	}

	public static RGB getSelectedColor(){
		return SETTINGS.get("selection_color").getValue();
	}

	public static RGB getSelectedColor2(){
		return SETTINGS.get("selection_color2").getValue();
	}

	public static RGB getBoundingBoxColor(){
		return SETTINGS.get("boundingbox_color").getValue();
	}

	public static float[] getBackGroundColor(){
		return background_color.getValue();
	}

	public static float[] getLight0Position(){
		return SETTINGS.get("light0_position").getValue();
	}
	
	public static boolean toggleAnimations(){
		boolean bool = animate.toggle();
		if(!bool){
			for(TurboList list : FMTB.MODEL.getGroups())
				for(PolygonWrapper wrapper : list) wrapper.resetPosRot();
			for(GroupCompound compound : HelperCollector.LOADED){
				for(TurboList list : compound.getGroups()){
					for(PolygonWrapper wrapper : list) wrapper.resetPosRot();
				}
			}
		}
		log("Toggling animations: " + animate.getBooleanValue());
		return bool;
	}
	
	//
	
	public static SettingsMap DEFAULTS = new SettingsMap(), SETTINGS = new SettingsMap();
	static {
		DEFAULTS.add(new Setting("selection_color", new RGB(255, 255, 0)));
		DEFAULTS.add(new Setting("selection_color2", new RGB(255, 255, 255)));
		DEFAULTS.add(new Setting("background_color", new float[]{ 0.5f, 0.5f, 0.5f, 0.2f }));
		DEFAULTS.add(new Setting("floor", true));
		DEFAULTS.add(new Setting("lines", true));
		DEFAULTS.add(new Setting("cube", true));
		DEFAULTS.add(new Setting("demo", false));
		DEFAULTS.add(new Setting("polygon_marker", true));
		//DEFAULTS.add(new Setting(Type.BOOLEAN, "polygon_count", true));
		DEFAULTS.add(new Setting("lighting", false));
		DEFAULTS.add(new Setting("light0_position", new float[]{ 0, 10, 0, 1 }));
		DEFAULTS.add(new Setting("language_code", "none"));
		DEFAULTS.add(new Setting("cullface", true));
		DEFAULTS.add(new Setting("animate", false));
		DEFAULTS.add(new Setting("discord_rpc-enabled", true));
		DEFAULTS.add(new Setting("discord_rpc-show_model", true));
		DEFAULTS.add(new Setting("discord_rpc-reset_timer_on_new_model", true));
		//DEFAULTS.add(new Setting(Type.INTEGER, "ui_scale", 1));
		DEFAULTS.add(new Setting("bottombar", false));
		DEFAULTS.add(new Setting("numberfield_arrows", true));
		DEFAULTS.add(new Setting("preview_colorpicker", false));
		DEFAULTS.add(new Setting("dark_theme", false));
		DEFAULTS.add(new Setting("no_scroll_fields", false));
		DEFAULTS.add(new Setting("old_rotation", true));
		DEFAULTS.add(new Setting("orbital_camera", false));
		DEFAULTS.add(new Setting("oc_center_on_part", true));
		//
		DEFAULTS.add(new Setting("mouse_sensivity", 2f));
		DEFAULTS.add(new Setting("camera_movespeed", 2f));
		DEFAULTS.add(new Setting("vsync", true));
		DEFAULTS.add(new Setting("last_file", "null"));
		DEFAULTS.add(new Setting("internal_cursor", false));
		DEFAULTS.add(new Setting("internal_filechooser", false));
		DEFAULTS.add(new Setting("center_marker", false));
		DEFAULTS.add(new Setting("ui_debug", false));
		DEFAULTS.add(new Setting("drag_painting", false));
		DEFAULTS.add(new Setting("boundingbox_color", new RGB(0, 255, 0)));
		DEFAULTS.add(new Setting("vsync_half", false));
		DEFAULTS.add(new Setting("rounding_digits", 4));
		DEFAULTS.add(new Setting("decimal_sizes", false));
	}

	public static void load(){
		JsonObject obj = JsonUtil.get(new File("./settings.json"));
		if(!obj.has("format")){ SETTINGS.putAll(DEFAULTS); }//assume it's the old format
		if(obj.has("settings")){
			obj = obj.get("settings").getAsJsonObject();
			obj.entrySet().forEach(entry -> {
				try{
					JsonObject jsn = entry.getValue().getAsJsonObject();
					String type = jsn.get("type").getAsString();
					SETTINGS.add(new Setting(type, entry.getKey(), jsn.get("value")));
				}
				catch(Exception e){
					log(e);
					log("Failed to load setting '" + entry.getKey() + "'!");
				}
			});
		}
		for(String key : DEFAULTS.keySet()){ if(!SETTINGS.containsKey(key)) SETTINGS.put(key, DEFAULTS.get(key)); }
		SETTINGS.entrySet().removeIf(entry -> !DEFAULTS.containsKey(entry.getKey()));
		//
		floor = SETTINGS.get("floor");
		lines = SETTINGS.get("lines");
		demo = SETTINGS.get("demo");
		cube = SETTINGS.get("cube");
		polygon_marker = SETTINGS.get("polygon_marker");
		lighting = SETTINGS.get("lighting");
		cullface = SETTINGS.get("cullface");
		animate = SETTINGS.get("animate");
		discordrpc = SETTINGS.get("discord_rpc-enabled");
		discordrpc_sm = SETTINGS.get("discord_rpc-show_model");
		discordrpc_rtonm = SETTINGS.get("discord_rpc-reset_timer_on_new_model");
		numberfieldarrows = SETTINGS.get("numberfield_arrows");
		preview_colorpicker = SETTINGS.get("preview_colorpicker");
		movespeed = SETTINGS.get("camera_movespeed");
		mouse_sensivity = SETTINGS.get("mouse_sensivity");
		vsync = SETTINGS.get("vsync");
		internal_cursor = SETTINGS.get("internal_cursor");;
		darktheme = SETTINGS.get("dark_theme");
		no_scroll_fields = SETTINGS.get("no_scroll_fields");
		old_rotation = SETTINGS.get("old_rotation");
		orbital_camera = SETTINGS.get("orbital_camera");
		oc_center_on_part = SETTINGS.get("oc_center_on_part");
		internal_filechooser = SETTINGS.get("internal_filechooser");
		center_marker = SETTINGS.get("center_marker");
		debug = SETTINGS.get("ui_debug");
		drag_painting = SETTINGS.get("drag_painting");
		background_color = SETTINGS.get("background_color");
		vsynchalf = SETTINGS.get("vsync_half");
		decimal_sizes = SETTINGS.get("decimal_sizes");
	}

	public static void save(){
		if(FMTB.MODEL != null && FMTB.MODEL.file != null){
			SETTINGS.get("last_file").setValue(FMTB.MODEL.file.getAbsolutePath());
		}
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 1);
		JsonObject settings = new JsonObject();
		SETTINGS.values().forEach(entry -> {
			try{
				JsonObject jsn = new JsonObject();
				jsn.addProperty("type", entry.getType().name().toLowerCase());
				jsn.add("value", entry.save()); settings.add(entry.getId(), jsn);
			}
			catch(Exception e){
				log(e);
			}
		});
		obj.add("settings", settings);
		obj.addProperty("last_fmt_version_used", FMTB.VERSION);
		obj.addProperty("last_fmt_exit", Time.getAsString(Time.getDate()));
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

	public static String getLanguage(){
		return SETTINGS.get("language_code").getValue();
	}

	public static SettingsMap getMap(){
		return SETTINGS;
	}
	
	public static class SettingsMap extends TreeMap<String, Setting> {
		
		public void add(Setting setting){
			this.put(setting.getId(), setting);
		}

		public Setting getAt(int i){
			return this.values().toArray(new Setting[0])[i];
		}
		
	}

	public static void updateTheme(){
		THEME_CHANGE_LISTENER.forEach(listener -> listener.accept(darktheme.getValue()));
	}

}
