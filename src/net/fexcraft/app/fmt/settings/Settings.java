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

public class Settings {
	
	public static final ArrayList<Consumer<Boolean>> THEME_CHANGE_LISTENERS = new ArrayList<>();
	public static int WSX = 1280, WSY = 720;
	public static Boolean SELTHEME;
	public static boolean VSYNC = true, HVSYNC, DISCORD = true, DISCORD_HIDE;
	public static float MOUSE_SENSIVITY = 2, MOVE_SPEED = 1;
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
		WSX = get(obj, "window_width", WSX);
		WSY = get(obj, "window_height", WSY);
		VSYNC = get(obj, "vsync", VSYNC);
		HVSYNC = get(obj, "vsync/2", HVSYNC);
		String theme = get(obj, "theme", "false");
		if(theme.equals("custom")) SELTHEME = null;
		else SELTHEME = Boolean.parseBoolean(theme);
	}
	
	public static void apply(FMT fmt){
		FMT.WIDTH = WSX;
		FMT.HEIGHT = WSY;
	}
	
	public static void save(){
		JsonObject obj = new JsonObject();
		obj.addProperty("window_width", WSX);
		obj.addProperty("window_height", WSY);
		obj.addProperty("vsync", VSYNC);
		obj.addProperty("vsync/2", HVSYNC);
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
