package net.fexcraft.app.fmt.settings;

import static net.fexcraft.app.fmt.FMT.rgba;
import static net.fexcraft.app.fmt.utils.Jsoniser.get;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

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
	public static Setting<Boolean> VSYNC, HVSYNC, DISCORD, DISCORD_HIDE;
	public static Setting<Float> MOUSE_SENSIVITY, MOVE_SPEED;
	public static Boolean SELTHEME;
	//
	public static int ct_background = 0x212121;
	public static int ct_border = 0x616161;
	public static int ct_slider = 0x616161;
	public static int ct_stroke = 0x0277BD;
	public static int ct_allow = 0x1B5E20;
	public static int ct_deny = 0xBD1C1C;
	public static int ct_shadow = 0x0;
	public static int ct_text = 0xCCCCCC;
	
	public static void load(){
		JsonObject obj = Jsoniser.parseObj(new File("./settings.json"), true);
		if(obj.has("format") && obj.get("format").getAsInt() != FORMAT) obj = new JsonObject();
		if(!obj.has("default")) obj.add("default", new JsonObject());
		//
		obj = obj.get("default").getAsJsonObject();
		VSYNC = new Setting<>("vsync", true, obj);
		HVSYNC = new Setting<>("vsync/2", false, obj);
		DISCORD = new Setting<>("discord_rpc", true, obj);
		DISCORD_HIDE = new Setting<>("discord_rpc_hide_mode", false, obj);
		WINDOW_WIDTH = new Setting<>("window_width", 1280, obj);
		WINDOW_HEIGHT = new Setting<>("window_height", 720, obj);
		MOUSE_SENSIVITY = new Setting<>("mouse_sensivity", 2f, obj);
		MOVE_SPEED = new Setting<>("movement_speed", 1f, obj);
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
		obj.add("default", def);
		//
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
				FontRegistry.ROBOTO_LIGHT, 20f
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
				FontRegistry.ROBOTO_LIGHT, 20f
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
				FontRegistry.ROBOTO_LIGHT, 20f
			));
		}
		if(FMT.FRAME != null) Themes.getDefaultTheme().applyAll(FMT.FRAME);
		THEME_CHANGE_LISTENERS.forEach(listener -> listener.accept(SELTHEME));
	}

}
