package net.fexcraft.app.fmt.settings;

import static net.fexcraft.app.fmt.FMT.rgba;
import static net.fexcraft.app.fmt.utils.Jsoniser.get;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.lib.common.math.Time;

public class Settings {
	
	public static final ArrayList<ThemeChangeEntry> THEME_CHANGE_LISTENERS = new ArrayList<>();
	public static final int FORMAT = 2;
	public static final float FONT_SIZE = 20f;
	public static final String FONT = FontRegistry.ROBOTO_BOLD;
	public static final String FONT_PATH = "org/liquidengine/legui/style/font/Roboto-Bold.ttf";
	public static Setting<Integer> WINDOW_WIDTH, WINDOW_HEIGHT, ROUNDING_DIGITS;
	public static Setting<Boolean> DISCORD_RPC, DISCORD_HIDE, DISCORD_RESET_ON_NEW;
	public static Setting<Boolean> VSYNC, HVSYNC, TRIANGULATION, INTERNAL_CHOOSER;
	public static Setting<Float> MOUSE_SENSIVITY, MOVE_SPEED;
	public static Setting<String> LANGUAGE;
	public static Boolean SELTHEME;
	//
	public static String GENERAL = "general";
	public static String GRAPHIC = "graphic";
	public static String DISCORD = "discord";
	public static String CONTROL = "control";
	//
	public static Map<String, Map<String, Setting<?>>> SETTINGS = new LinkedHashMap<>();
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
		var file = new File("./settings.json");
		var obj = file.exists() ? Jsoniser.parseObj(file, true) : new JsonObject();
		if(obj == null) obj = new JsonObject();
		if(obj.has("format") && obj.get("format").getAsInt() != FORMAT) obj = new JsonObject();
		//
		VSYNC = new Setting<>("vsync", true, GRAPHIC, obj);
		HVSYNC = new Setting<>("vsync/2", false, GRAPHIC, obj);
		DISCORD_RPC = new Setting<>("enabled", true, DISCORD, obj);
		DISCORD_HIDE = new Setting<>("hidden_mode", false, DISCORD, obj);
		DISCORD_RESET_ON_NEW = new Setting<>("reset_on_new", false, DISCORD, obj);
		WINDOW_WIDTH = new Setting<>("window_width", 1280, GRAPHIC, obj);
		WINDOW_HEIGHT = new Setting<>("window_height", 720, GRAPHIC, obj);
		MOUSE_SENSIVITY = new Setting<>("mouse_sensivity", 2f, CONTROL, obj);
		MOVE_SPEED = new Setting<>("movement_speed", 1f, CONTROL, obj);
		TRIANGULATION = new Setting<>("triangulated_quads", false, GRAPHIC, obj);
		LANGUAGE = new Setting<>("language", "null", GENERAL, obj);
		INTERNAL_CHOOSER = new Setting<>("internal_filechooser", true, GENERAL, obj);//TODO later set to false
		ROUNDING_DIGITS = new Setting<>("rounding_digits", 4, GENERAL, obj);
		//
		for(Map.Entry<String, Map<String, Setting<?>>> entry : SETTINGS.entrySet()){
			if(!obj.has(entry.getKey())) continue;
			JsonObject def = obj.get(entry.getKey()).getAsJsonObject();
			for(Setting<?> setting : entry.getValue().values()) setting.load(def);
		}//TODO load plugin settings ?
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
		SETTINGS.entrySet().forEach(entry -> {
			JsonObject jsn = new JsonObject();
			entry.getValue().values().forEach(setting -> setting.save(jsn));
			obj.add(entry.getKey(), jsn);
		});
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
				FONT, FONT_SIZE
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
				FONT, FONT_SIZE
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
				FONT, FONT_SIZE
			));
		}
		if(FMT.FRAME != null) Themes.getDefaultTheme().applyAll(FMT.FRAME);
		THEME_CHANGE_LISTENERS.forEach(listener -> listener.getValue().accept(SELTHEME));
	}
	
	public static void applyMenuTheme(Component com){
		Settings.THEME_CHANGE_LISTENERS.add(new ThemeChangeEntry(com, bool -> {
			com.getStyle().setBorderRadius(0);
			com.getStyle().setBorder(null);
			float col = bool != null && bool ? 0.25f : 0.75f;
			com.getStyle().setTextColor(bool != null && bool ? ColorConstants.lightGray() : ColorConstants.darkGray());
			com.getStyle().getBackground().setColor(col, col, col, 1);
		}));
	}

	public static Consumer<Boolean> applyComponentTheme(EditorComponent com){
		Consumer<Boolean> consumer = bool -> {
			com.getStyle().setBorderRadius(0);
			com.getStyle().setBorder(null);
			float col = bool != null && bool ? 0.1875f : 0.8125f;
			com.getStyle().setTextColor(bool != null && bool ? ColorConstants.lightGray() : ColorConstants.darkGray());
			com.getStyle().getBackground().setColor(col, col, col, 1);
		};
		Settings.THEME_CHANGE_LISTENERS.add(new ThemeChangeEntry(com, consumer));
		return consumer;
	}
	
	public static void applyBorderless(Component com){
		Settings.THEME_CHANGE_LISTENERS.add(new ThemeChangeEntry(com, bool -> {
			com.getStyle().setBorderRadius(0);
			com.getStyle().setBorder(null);
		}));
	}
	
	public static void applyBorderless(Component com, Style style){
		Settings.THEME_CHANGE_LISTENERS.add(new ThemeChangeEntry(com, bool -> {
			style.setBorderRadius(0);
			style.setBorder(null);
		}));
	}

	public static void loadEditors(){
		JsonObject obj = Jsoniser.parseObj(new File("./editors.fmt"), false);
		if(obj == null || obj.entrySet().isEmpty()) loadDefaultEditors();
		else{
			for(Entry<String, JsonElement> entry : obj.entrySet()){
				new Editor(entry.getKey(), entry.getValue().getAsJsonObject());
			}
		}
		Editor.EDITORS.get("polygon_editor").show();
		Editor.EDITORS.get("polygon_tree").show();
	}

	private static void loadDefaultEditors(){
		Editor editor = new Editor("polygon_editor", "Polygon Editor", true);
		new Editor("polygon_tree", "Polygon Tree", false);
		editor.addComponent(new QuickAdd());
		editor.addComponent(new EditorComponent("test0", 80, false));
		editor.addComponent(new EditorComponent("test1", 200, false));
		editor.addComponent(new EditorComponent("test2"));
	}

	public static void register(String group, String id, Setting<?> setting){
		if(!SETTINGS.containsKey(group)) SETTINGS.put(group, new LinkedHashMap<String, Setting<?>>());
		SETTINGS.get(group).put(id, setting);
	}

	public static void applyTheme(Component com, Consumer<Boolean> con){
		THEME_CHANGE_LISTENERS.add(new ThemeChangeEntry(com, con));
	}

	public static void deapply(Component com){
		THEME_CHANGE_LISTENERS.removeIf(pre -> pre.getKey().equals(com));
	}

	public static void deapply(Component... coms){
		for(Component com : coms) deapply(com);
	}
	
	private static final class ThemeChangeEntry implements Map.Entry<Component, Consumer<Boolean>> {
		
	    private final Component key;
	    private Consumer<Boolean> value;

	    public ThemeChangeEntry(Component key, Consumer<Boolean> value){
	        this.key = key;
	        this.value = value;
	    }

	    @Override
	    public Component getKey(){
	        return key;
	    }

	    @Override
	    public Consumer<Boolean> getValue(){
	        return value;
	    }

	    @Override
	    public Consumer<Boolean> setValue(Consumer<Boolean> value){
	        return this.value = value;
	    }
	    
	}

}
