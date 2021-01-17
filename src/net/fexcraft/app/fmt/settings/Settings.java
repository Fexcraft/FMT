package net.fexcraft.app.fmt.settings;

import static net.fexcraft.app.fmt.FMT.rgba;
import static net.fexcraft.app.fmt.utils.Jsoniser.get;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.lib.common.math.Time;

public class Settings {
	
	public static final ArrayList<Consumer<Boolean>> THEME_CHANGE_LISTENERS = new ArrayList<>();
	public static final int FORMAT = 2;
	public static Setting<Integer> WINDOW_WIDTH, WINDOW_HEIGHT;
	public static Setting<Boolean> VSYNC, HVSYNC, DISCORD, DISCORD_HIDE, TRIANGULATION;
	public static Setting<Float> MOUSE_SENSIVITY, MOVE_SPEED;
	public static Setting<String> LANGUAGE;
	public static Boolean SELTHEME;
	//
	public static Map<String, Map<String, Setting<?>>> SETTINGS = new LinkedHashMap<>();//External settings, e.g. plugins.
	//
	public static int ct_background = 0x212121;
	public static int ct_border = 0x616161;
	public static int ct_slider = 0x616161;
	public static int ct_stroke = 0x0277BD;
	public static int ct_allow = 0x1B5E20;
	public static int ct_deny = 0xBD1C1C;
	public static int ct_shadow = 0x0;
	public static int ct_text = 0xCCCCCC;
	public static int ct_buttom = 0x212121;
	
	public static void load(){
		JsonObject obj = Jsoniser.parseObj(new File("./settings.json"), true);
		if(obj.has("format") && obj.get("format").getAsInt() != FORMAT) obj = new JsonObject();
		if(!obj.has("default")) obj.add("default", new JsonObject());
		//
		JsonObject def = obj.get("default").getAsJsonObject();
		VSYNC = new Setting<>("vsync", true, def);
		HVSYNC = new Setting<>("vsync/2", false, def);
		DISCORD = new Setting<>("discord_rpc", true, def);
		DISCORD_HIDE = new Setting<>("discord_rpc_hide_mode", false, def);
		WINDOW_WIDTH = new Setting<>("window_width", 1280, def);
		WINDOW_HEIGHT = new Setting<>("window_height", 720, def);
		MOUSE_SENSIVITY = new Setting<>("mouse_sensivity", 2f, def);
		MOVE_SPEED = new Setting<>("movement_speed", 1f, def);
		TRIANGULATION = new Setting<>("triangulated_quads", false, def);
		LANGUAGE = new Setting<>("language", "null", def);
		//
		for(Map.Entry<String, Map<String, Setting<?>>> entry : SETTINGS.entrySet()){
			if(!obj.has(entry.getKey())) continue;
			def = obj.get(entry.getKey()).getAsJsonObject();
			for(Setting<?> setting : entry.getValue().values()) setting.load(def);
		}
		//
		String theme = get(obj, "theme", "false");
		if(theme.equals("custom")) SELTHEME = null;
		else SELTHEME = Boolean.parseBoolean(theme);
	}
	
	public static void apply(FMT fmt){
		FMT.WIDTH = WINDOW_WIDTH.value;
		FMT.HEIGHT = WINDOW_HEIGHT.value;
	}
	
	public static void save(){
		JsonObject obj = new JsonObject();
		obj.addProperty("format", FORMAT);
		//
		JsonObject def = new JsonObject();
		VSYNC.save(def);
		HVSYNC.save(def);
		DISCORD.save(def);
		DISCORD_HIDE.save(def);
		WINDOW_WIDTH.save(def);
		WINDOW_HEIGHT.save(def);
		MOUSE_SENSIVITY.save(def);
		MOVE_SPEED.save(def);
		TRIANGULATION.save(def);
		LANGUAGE.save(def);
		obj.add("default", def);
		//
		for(Map.Entry<String, Map<String, Setting<?>>> entry : SETTINGS.entrySet()){
			def = new JsonObject();
			for(Setting<?> setting : entry.getValue().values()) setting.save(def);
			if(def.size() > 0) obj.add(entry.getKey(), def);
		}
		//
		obj.addProperty("theme", SELTHEME == null ? "custom" : SELTHEME + "");
		obj.addProperty("last_fmt_version", FMT.VERSION);
		obj.addProperty("last_fmt_exit", Time.getAsString(Time.getDate()));
		Jsoniser.print(new File("./settings.json"), obj);
	}

	public static void applyTheme(){
		if(SELTHEME == null){
			Themes.setDefaultTheme(new FlatColoredTheme(
				rgba(ct_background),
				rgba(ct_border),
				rgba(ct_slider),
				rgba(ct_stroke),
				rgba(ct_allow),
				rgba(ct_deny),
				ColorConstants.transparent(),//TODO
				rgba(ct_text),
				FontRegistry.ROBOTO_BOLD, 20f
			));
		}
		else if(SELTHEME){
			Themes.setDefaultTheme(new FlatColoredTheme(
				rgba(33, 33, 33, 1),
				rgba(97, 97, 97, 1),
				rgba(97, 97, 97, 1),
				rgba(2, 119, 189, 1),
				rgba(27, 94, 32, 1),
				rgba(183, 28, 28, 1),
				ColorConstants.transparent(),
				ColorConstants.lightGray(),
				FontRegistry.ROBOTO_BOLD, 20f
			));
		}
		else{
			Themes.setDefaultTheme(new FlatColoredTheme(
				rgba(245, 245, 245, 1),
				rgba(176, 190, 197, 1),
				rgba(176, 190, 197, 1),
				rgba(100, 181, 246, 1),
				rgba(165, 214, 167, 1),
				rgba(239, 154, 154, 1),
				ColorConstants.transparent(),
				ColorConstants.darkGray(),
				FontRegistry.ROBOTO_BOLD, 20f
			));
		}
		if(FMT.FRAME != null) Themes.getDefaultTheme().applyAll(FMT.FRAME);
		THEME_CHANGE_LISTENERS.forEach(listener -> listener.accept(SELTHEME));
	}
	
	public static void applyMenuTheme(Component com){
		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
			com.getStyle().setBorderRadius(0);
			com.getStyle().setBorder(null);
			float col = bool != null && bool ? 0.25f : 0.75f;
			com.getStyle().setTextColor(bool != null && bool ? ColorConstants.lightGray() : ColorConstants.darkGray());
			com.getStyle().getBackground().setColor(col, col, col, 1);
		});
	}
	
	public static void applyBorderless(Component com){
		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
			com.getStyle().setBorderRadius(0);
			com.getStyle().setBorder(null);
		});
	}

}
