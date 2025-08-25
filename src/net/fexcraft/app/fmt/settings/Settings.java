package net.fexcraft.app.fmt.settings;

import static net.fexcraft.app.fmt.FMT.rgba;
import static net.fexcraft.app.fmt.utils.Translator.format;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.updater.Catalog;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.ui.editors.*;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.trees.HelperTree;
import net.fexcraft.app.fmt.ui.trees.PivotTree;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.json.JsonValue;
import org.joml.Vector4f;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.style.font.FontRegistry;
import com.spinyowl.legui.theme.Themes;
import com.spinyowl.legui.theme.colored.FlatColoredTheme;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.trees.PolygonTree;
import net.fexcraft.app.fmt.ui.trees.TextureTree;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.Time;

public class Settings {
	
	public static final int FORMAT = 2;
	public static final float FONT_SIZE = 16f;
	public static final int FONT_SIZEN = 20;
	public static final String FONT = FontRegistry.ROBOTO_BOLD;
	public static final String FONT_PATH = "com/spinyowl/legui/style/font/Roboto-Bold.ttf";
	public static boolean FOUND_UPDATE, UPDATECHECK_FAILED;
	public static long UPDATE_FOR_FILES_FOUND = 0, LAST_CATALOG_RELOAD;
	public static ArrayList<File> RECENT = new ArrayList<File>();
	public static ArrayList<File> BOOKMARKS = new ArrayList<File>();
	public static File NO_FILE_DOTS = new File("...");
	public static Setting<Integer> WINDOW_WIDTH;
	public static Setting<Integer> WINDOW_HEIGHT;
	public static Setting<Integer> ROUNDING_DIGITS;
	public static Setting<Integer> BACKUP_INTERVAL;
	public static Setting<Integer> GIF_DELAY_TIME;
	public static Setting<Integer> GIF_ROT_PASS;
	public static Setting<Boolean> DISCORD_RPC;
	public static Setting<Boolean> DISCORD_HIDE;
	public static Setting<Boolean> DISCORD_RESET_ON_NEW;
	public static Setting<Boolean> FULLSCREEN;
	public static Setting<Boolean> NO_RANDOM_TITLE;
	public static Setting<Boolean> FVSYNC;
	public static Setting<Boolean> HVSYNC;
	public static Setting<Boolean> QVSYNC;
	public static Setting<Boolean> TRIANGULATION_Q;
	public static Setting<Boolean> TRIANGULATION_L;
	public static Setting<Boolean> INTERNAL_CHOOSER;
	public static Setting<Boolean> DEMO;
	public static Setting<Boolean> FLOOR;
	public static Setting<Boolean> CUBE;
	public static Setting<Boolean> CMARKER;
	public static Setting<Boolean> PMARKER;
	public static Setting<Boolean> LINES;
	public static Setting<Boolean> POLYMARKER;
	public static Setting<Boolean> ADD_TO_LAST;
	public static Setting<Boolean> SPHERE_MARKER;
	public static Setting<Boolean> AUTO_SHOW_COMPONENTS;
	public static Setting<Float> MOUSE_SENSIVITY;
	public static Setting<Float> MOVE_SPEED;
	public static Setting<Float> SCROLL_SPEED;
	public static Setting<Float> LIGHT_AMBIENT;
	public static Setting<Float> LIGHT_DIFFUSE;
	public static Setting<Float> LIGHT_POSX;
	public static Setting<Float> LIGHT_POSY;
	public static Setting<Float> LIGHT_POSZ;
	public static Setting<String> LANGUAGE;
	public static Setting<String> POLYGON_SUFFIX;
	public static Setting<String> GROUP_SUFFIX;
	public static Setting<String> COPIED_POLYGON;
	public static Setting<String> PASTED_GROUP;
	public static Setting<Boolean> ASK_POLYGON_REMOVAL;
	public static Setting<Boolean> ASK_GROUP_REMOVAL;
	public static Setting<Boolean> ASK_PIVOT_REMOVAL;
	public static Setting<Boolean> ASK_HELPER_REMOVAL;
	public static Setting<Boolean> ASK_TEXTURE_GROUP_REMOVAL;
	public static Setting<Boolean> OPEN_FOLDER_AFTER_SAVE;
	public static Setting<Boolean> OPEN_FOLDER_AFTER_IMG;
	public static Setting<Boolean> SHOW_WELCOME;
	public static Setting<Boolean> SHOW_UPDATE;
	public static Setting<Boolean> SELECT_COPIED;
	public static Setting<Boolean> SELECT_NEW;
	public static Setting<Boolean> SHOW_BOTTOMBAR;
	public static Setting<Boolean> GIF_LOOP;
	public static Setting<Boolean> HIDE_UI_FOR_IMAGE;
	public static Setting<Boolean> HIDE_MENU_AFTER_POLYGON;
	public static Setting<Boolean> LOG_UPDATES;
	public static Setting<Boolean> LIGHTING_ON;
	public static Setting<Boolean> NUMBERFIELD_BUTTONS;
	public static Setting<Float> ZOOM_LEVEL;
	public static Setting<Float> ARROW_SENSIVITY;
	public static RGBSetting BACKGROUND;
	public static RGBSetting SELECTION_LINES;
	public static RGBSetting BOTTOM_INFO_BAR_COLOR;
	public static RGBSetting LIGHT_COLOR;
	public static Setting<Integer> PAINTER_CHANNELS;
	public static Setting<Boolean> ANIMATE;
	//
	public static Setting<Boolean> TESTING;
	public static Setting<String> WORKSPACE_NAME;
	public static Setting<String> WORKSPACE_ROOT;
	public static Setting<String> JAVA8_PATH;
	public static Setting<String> JAVA17_PATH;
	public static Setting<String> M12PATH;
	public static Setting<String> M20PATH;
	public static Setting<String> M12RCMD;
	public static Setting<String> M20RCMD;
	public static Setting<String> TEXT_EDITOR;
	//
	public static Setting<String> SEL_THEME;
	public static Setting<Boolean> DARKTHEME;
	public static RGBSetting THEME_BACKGROUND;
	public static RGBSetting THEME_BORDER;
	public static RGBSetting THEME_SLIDER;
	public static RGBSetting THEME_STROKE;
	public static RGBSetting THEME_ALLOW;
	public static RGBSetting THEME_DENY;
	public static RGBSetting THEME_SHADOW;
	public static RGBSetting THEME_TEXT;
	public static RGBSetting THEME_BUTTON;
	public static Setting<String> THEME_FONT;
	public static RGBSetting POLYGON_NORMAL;
	public static RGBSetting POLYGON_SELECTED;
	public static RGBSetting POLYGON_INVISIBLE;
	public static RGBSetting POLYGON_INV_SEL;
	public static RGBSetting GROUP_NORMAL;
	public static RGBSetting GROUP_SELECTED;
	public static RGBSetting GROUP_INVISIBLE;
	public static RGBSetting GROUP_INV_SEL;
	public static RGBSetting PIVOT_NORMAL;
	public static RGBSetting PIVOT_SELECTED;
	public static RGBSetting PIVOT_INVISIBLE;
	public static RGBSetting PIVOT_INV_SEL;
	public static RGBSetting TEXTURE_GROUP;
	public static RGBSetting TEXTURE_OPTION;
	//
	public static String GENERAL = "general";
	public static String GRAPHIC = "graphic";
	public static String DISCORD = "discord";
	public static String CONTROL = "control";
	public static String SPACE3D = "space3d";
	public static String NAMING = "naming";
	public static String IMAGE = "image";
	public static String THEME = "theme";
	public static String WORKSPACE = "workspace";
	public static String LIGHTING = "lighting";
	//
	public static Map<String, Map<String, Setting<?>>> SETTINGS = new LinkedHashMap<>();
	
	public static void load(){
		var file = new File("./settings.json");
		var map = file.exists() ? JsonHandler.parse(file) : new JsonMap();
		if(map.has("format") && map.get("format").integer_value() != FORMAT) map = new JsonMap();
		if(map.has("last_catalog_reload")) LAST_CATALOG_RELOAD = map.get("last_catalog_reload").long_value();
		if(map.has("recent_files")){
			JsonArray array = map.getArray("recent_files");
			for(int i = 0; i < 10; i++){
				if(i >= array.size()) RECENT.add(new File("..."));
				else RECENT.add(new File(array.get(i).string_value()));
			}
		}
		else for(int i = 0; i < 10; i++) RECENT.add(NO_FILE_DOTS);
		if(map.has("filechooser_bookmarks")){
			map.getArray("filechooser_bookmarks").value.forEach(val -> {
				BOOKMARKS.add(new File(val.string_value()));
			});
		}
		//
		SETTINGS.put(GENERAL, new LinkedHashMap<>());
		SETTINGS.put(GRAPHIC, new LinkedHashMap<>());
		SETTINGS.put(LIGHTING, new LinkedHashMap<>());
		SETTINGS.put(CONTROL, new LinkedHashMap<>());
		SETTINGS.put(SPACE3D, new LinkedHashMap<>());
		SETTINGS.put(WORKSPACE, new LinkedHashMap<>());
		SETTINGS.put(DISCORD, new LinkedHashMap<>());
		SETTINGS.put(NAMING, new LinkedHashMap<>());
		SETTINGS.put(IMAGE, new LinkedHashMap<>());
		SETTINGS.put(THEME, new LinkedHashMap<>());
		//
		FVSYNC = new Setting<>("vsync", true, GRAPHIC, map);
		HVSYNC = new Setting<>("vsync/2", false, GRAPHIC, map);
		QVSYNC = new Setting<>("vsync/4", false, GRAPHIC, map);
		DISCORD_RPC = new Setting<>("enabled", true, DISCORD, map);
		DISCORD_HIDE = new Setting<>("hidden_mode", false, DISCORD, map);
		DISCORD_RESET_ON_NEW = new Setting<>("reset_on_new", false, DISCORD, map);
		WINDOW_WIDTH = new Setting<>("window_width", 1024, GRAPHIC, map);
		WINDOW_HEIGHT = new Setting<>("window_height", 576, GRAPHIC, map);
		MOUSE_SENSIVITY = new Setting<>("mouse_sensivity", 2f, CONTROL, map);
		MOVE_SPEED = new Setting<>("movement_speed", 20f, CONTROL, map);
		SCROLL_SPEED = new Setting<>("scroll_speed", 1f, CONTROL, map);
		TRIANGULATION_Q = new Setting<>("triangulated_quads", true, GRAPHIC, map);
		TRIANGULATION_L = new Setting<>("triangulated_lines", false, GRAPHIC, map);
		LANGUAGE = new Setting<>("language", "null", GENERAL, map);
		INTERNAL_CHOOSER = new Setting<>("internal_filechooser", false, GENERAL, map);
		ROUNDING_DIGITS = new Setting<>("rounding_digits", 4, GENERAL, map).minmax(0, 10).consumer(con -> NumberField.updateRoundingDigits());
		DEMO = new Setting<>("demo_model", false, SPACE3D, map);
		FLOOR = new Setting<>("floor", true, SPACE3D, map);
		CUBE = new Setting<>("center_cube", true, SPACE3D, map);
		CMARKER = new Setting<>("center_marker", true, SPACE3D, map);
		PMARKER = new Setting<>("pivot_marker", true, SPACE3D, map);
		LINES = new Setting<>("lines", true, SPACE3D, map);
		POLYMARKER = new Setting<>("polygon_marker", true, SPACE3D, map);
		ADD_TO_LAST = new Setting<>("add_to_last", false, GENERAL, map);
		FULLSCREEN = new Setting<>("fullscreen", false, GRAPHIC, map);
		POLYGON_SUFFIX = new Setting<>("polygon_duplicate_suffix", "_%s", NAMING, map);
		GROUP_SUFFIX = new Setting<>("group_duplicate_suffix", "_%s", NAMING, map);
		ASK_GROUP_REMOVAL = new Setting<>("ask_group_removal", true, GENERAL, map);
		ASK_POLYGON_REMOVAL = new Setting<>("ask_polygon_removal", false, GENERAL, map);
		ASK_PIVOT_REMOVAL = new Setting<>("ask_pivot_removal", false, GENERAL, map);
		ASK_HELPER_REMOVAL = new Setting<>("ask_helper_removal", false, GENERAL, map);
		OPEN_FOLDER_AFTER_SAVE = new Setting<>("open_folder_after_save", true, GENERAL, map);
		SHOW_WELCOME = new Setting<>("show_welcome", true, GENERAL, map);
		SHOW_UPDATE = new Setting<>("show_update", true, GENERAL, map);
		SPHERE_MARKER = new Setting<>("sphere_marker", false, SPACE3D, map);
		BACKUP_INTERVAL = new Setting<>("backup_interval", 5, GENERAL, map);
		COPIED_POLYGON = new Setting<>("copied_polygon", "%s_cp", NAMING, map);
		SELECT_COPIED = new Setting<>("select_copied", true, GENERAL, map);
		SELECT_NEW = new Setting<>("select_new", true, GENERAL, map);
		PASTED_GROUP = new Setting<>("pasted_group_suffix", "-cb", NAMING, map);
		SHOW_BOTTOMBAR = new Setting<>("show_bottom_bar", true, GENERAL, map);
		OPEN_FOLDER_AFTER_IMG = new Setting<>("open_folder_after_image", true, IMAGE, map);
		GIF_DELAY_TIME = new Setting<>("gif_delay_time", 100, IMAGE, map);
		GIF_LOOP = new Setting<>("gif_loop", true, IMAGE, map);
		GIF_ROT_PASS = new Setting<>("gif_rotation_passes", 36, IMAGE, map);
		HIDE_UI_FOR_IMAGE = new Setting<>("hide_ui_for_image", true, IMAGE, map);
		BACKGROUND = new RGBSetting("background", new RGB(0x7f7f7f), SPACE3D, map);
		NO_RANDOM_TITLE = new Setting<>("no_random_title", false, IMAGE, map);
		SELECTION_LINES = new RGBSetting("selection_lines", new RGB(0xffff00), SPACE3D, map);
		ASK_TEXTURE_GROUP_REMOVAL = new Setting<>("ask_texture_group_removal", true, GENERAL, map);
		HIDE_MENU_AFTER_POLYGON = new Setting<>("hide_menu_after_polygon", true, GENERAL, map);
		LOG_UPDATES = new Setting<>("log_updates", false, GENERAL, map);
		LIGHTING_ON = new Setting<>("enabled", false, LIGHTING, map);
		LIGHT_AMBIENT = new Setting<>("ambient", 0.5f, LIGHTING, map).minmax(0f, 1f);
		LIGHT_DIFFUSE = new Setting<>("diffuse", 1f, LIGHTING, map).minmax(0f, 1f);
		LIGHT_COLOR = new RGBSetting("color", RGB.WHITE.copy(), LIGHTING, map);
		LIGHT_POSX = new Setting<>("pos_x", 600f, LIGHTING, map);
		LIGHT_POSY = new Setting<>("pos_y", -600f, LIGHTING, map);
		LIGHT_POSZ = new Setting<>("pos_z", -600f, LIGHTING, map);
		ZOOM_LEVEL = new Setting<>("zoom_by", 10f, CONTROL, map).minmax(0.001f, 160f);
		ARROW_SENSIVITY = new Setting<>("arrow_sensivity", 8f, CONTROL, map).minmax(0.001f, 100f);
		NUMBERFIELD_BUTTONS = new Setting<>("numberfield_buttons", false, GENERAL, map);
		AUTO_SHOW_COMPONENTS = new Setting<>("auto_show_components", true, GENERAL, map);
		PAINTER_CHANNELS = new Setting<>("painter_channels", 2, GENERAL, map);
		ANIMATE = new Setting<>("animate", true, SPACE3D, map);
		//
		TESTING = new Setting<>("testing", false, GENERAL, map);
		WORKSPACE_NAME = new Setting<>("name", "FMT Workspace", WORKSPACE, map);
		WORKSPACE_ROOT = new Setting<>("root", "./workspace/", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		JAVA8_PATH = new Setting<>("java8", "/java/install/path/", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		JAVA17_PATH = new Setting<>("java17", "/java/install/path/", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		M12PATH = new Setting<>("m12path", "/install/path", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		M20PATH = new Setting<>("m20path", "/install/path/", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		M12RCMD = new Setting<>("m12run", "{JAVA} -jar client.jar", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		M20RCMD = new Setting<>("m20run", "{JAVA} -jar client.jar", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		TEXT_EDITOR = new Setting<>("text_editor", "/usr/bin/mousepad %s", WORKSPACE, map){
			@Override
			public boolean basicstr(){
				return false;
			}
		};
		//
		SEL_THEME = new StringArraySetting("selected_theme", "light", THEME, map, "light", "dark", "custom");
		DARKTHEME = new Setting<>("is_dark", false, THEME, map);
		THEME_BACKGROUND = new RGBSetting("background", new RGB(0x212121), THEME, map);
		THEME_BORDER = new RGBSetting("border", new RGB(0x616161), THEME, map);
		THEME_SLIDER = new RGBSetting("slider", new RGB(0x616161), THEME, map);
		THEME_STROKE = new RGBSetting("stroke", new RGB(0x0277BD), THEME, map);
		THEME_ALLOW = new RGBSetting("allow", new RGB(0x1B5E20), THEME, map);
		THEME_DENY = new RGBSetting("deny", new RGB(0xBD1C1C), THEME, map);
		THEME_SHADOW = new RGBSetting("shadow", new RGB(0x0), THEME, map);
		THEME_TEXT = new RGBSetting("text", new RGB(0xCCCCCC), THEME);
		THEME_BUTTON = new RGBSetting("button", new RGB(0x212121), THEME, map);
		THEME_FONT = new StringArraySetting("font", FONT, THEME, map, FontRegistry.ENTYPO, FontRegistry.ROBOTO_LIGHT, FontRegistry.ROBOTO_BOLD, FontRegistry.ROBOTO_REGULAR);
		POLYGON_NORMAL = new RGBSetting("component_polygon_normal", new RGB(38, 127, 0), THEME, map);
		POLYGON_SELECTED = new RGBSetting("component_polygon_selected", new RGB(219, 156, 46), THEME, map);
		POLYGON_INVISIBLE = new RGBSetting("component_polygon_invisible", new RGB(126, 196, 96), THEME, map);
		POLYGON_INV_SEL = new RGBSetting("component_polygon_invis_sel", new RGB(250, 202, 117), THEME, map);
		GROUP_NORMAL = new RGBSetting("component_group_normal", new RGB(0, 74, 127), THEME, map);
		GROUP_SELECTED = new RGBSetting("component_group_selected", new RGB(191, 128, 50), THEME, map);
		GROUP_INVISIBLE = new RGBSetting("component_group_invisible", new RGB(67, 142, 196), THEME, map);
		GROUP_INV_SEL = new RGBSetting("component_group_invis_sel", new RGB(232, 158, 67), THEME, map);
		PIVOT_NORMAL = new RGBSetting("component_pivot_normal", new RGB(5, 158, 127), THEME, map);
		PIVOT_SELECTED = new RGBSetting("component_pivot_selected", new RGB(219, 213, 31), THEME, map);
		PIVOT_INVISIBLE = new RGBSetting("component_pivot_invisible", new RGB(123, 158, 151), THEME, map);
		PIVOT_INV_SEL = new RGBSetting("component_pivot_invis_sel", new RGB(209, 205, 100), THEME, map);
		BOTTOM_INFO_BAR_COLOR = new RGBSetting("bottom_infobar_color", new RGB(200, 200, 200), THEME, map);
		TEXTURE_GROUP = new RGBSetting("component_texture_group", new RGB(0, 74, 127), THEME, map);
		TEXTURE_OPTION = new RGBSetting("component_texture_group_option", new RGB(0, 74, 127), THEME, map);
		//
		ExportManager.init(map);
		ImportManager.init(map);
		TexturePainter.CHANNELS = new RGB[PAINTER_CHANNELS.value];
		for(Integer i = 0; i < PAINTER_CHANNELS.value; i++) TexturePainter.CHANNELS[i] = RGB.WHITE.copy();
		//
		for(Map.Entry<String, Map<String, Setting<?>>> entry : SETTINGS.entrySet()){
			if(!map.has(entry.getKey())) continue;
			JsonMap def = map.getMap(entry.getKey());
			for(Setting<?> setting : entry.getValue().values()) setting.load(def);
		}//TODO load plugin settings ?
		//
		if(!SEL_THEME.value.equals("custom")) DARKTHEME.value = SEL_THEME.value.contains("dark");
		//
		if(map.has("last_model")){
			FMT.MODEL = new Model(new File(map.get("last_model").string_value()), "Loaded Model");
		}
		else{
			FMT.MODEL = new Model(null, "Unnamed Model");
		}
	}
	
	public static void apply(FMT fmt){
		FMT.WIDTH = WINDOW_WIDTH.value;
		FMT.HEIGHT = WINDOW_HEIGHT.value;
		TexturedPolygon.TRIANGULATED_QUADS = TRIANGULATION_Q.value;
		refresh();
	}
	
	public static void save(){
		JsonMap map = new JsonMap();
		map.add("format", FORMAT);
		//
		SETTINGS.entrySet().forEach(entry -> {
			JsonMap jsn = new JsonMap();
			entry.getValue().values().forEach(setting -> setting.save(jsn));
			map.add(entry.getKey(), jsn);
		});
		//
		map.add("last_catalog_reload", LAST_CATALOG_RELOAD);
		map.add("last_fmt_version", FMT.VERSION);
		map.add("last_fmt_exit", Time.getAsString(Time.getDate()));
		JsonArray recent = new JsonArray();
		for(File file : RECENT){
			if(NO_FILE_DOTS.equals(file)) continue;
			recent.add(file.toString().replace("\\", "\\\\"));
		}
		if(recent.size() > 0) map.add("recent_files", recent);
		JsonArray bookmarks = new JsonArray();
		for(File file : BOOKMARKS){
			bookmarks.add(file.toString().replace("\\", "\\\\"));
		}
		map.add("filechooser_bookmarks", bookmarks);
		if(FMT.MODEL.file != null){
			map.add("last_model", FMT.MODEL.file.toPath().toString());
		}
		JsonHandler.print(new File("./settings.json"), map, PrintOption.SPACED);
		//
		Editor.saveAll();
	}

	public static void applyTheme(){
		switch(SEL_THEME.value){
			case "custom":{
				Themes.setDefaultTheme(new FlatColoredTheme(
					rgba(THEME_BACKGROUND.value),
					rgba(THEME_BORDER.value),
					rgba(THEME_SLIDER.value),
					rgba(THEME_STROKE.value),
					rgba(THEME_ALLOW.value),
					rgba(THEME_DENY.value),
					ColorConstants.transparent(),//TODO
					rgba(THEME_TEXT.value),
					FONT, FONT_SIZE
				));
				break;
			}
			case "dark":{
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
				break;
			}
			case "light":{
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
				break;
			}
		}
		if(FMT.FRAME != null) Themes.getDefaultTheme().applyAll(FMT.FRAME);
	}
	
	public static void applyMenuTheme(Component com){
		com.getStyle().setBorderRadius(0);
		com.getStyle().setBorder(null);
		float col = DARKTHEME.value ? 0.25f : 0.75f;
		com.getStyle().setTextColor(DARKTHEME.value ? ColorConstants.lightGray() : ColorConstants.darkGray());
		com.getStyle().getBackground().setColor(col, col, col, 1);
	}

	public static void applyMenuTheme(Component... coms){
		for(Component com : coms) applyMenuTheme(com);
	}

	public static void applyComponentTheme(Component com){
		com.getStyle().setBorderRadius(0);
		com.getStyle().setBorder(null);
		float col = DARKTHEME.value ? 0.1875f : 0.8125f;
		com.getStyle().setTextColor(DARKTHEME.value ? ColorConstants.lightGray() : ColorConstants.darkGray());
		com.getStyle().getBackground().setColor(col, col, col, 1);
	}
	
	public static void applyBorderless(Component com){
		com.getStyle().setBorderRadius(0);
		com.getStyle().setBorder(null);
	}
	
	public static void applyBorderless(Style style){
		style.setBorderRadius(0);
		style.setBorder(null);
	}

	public static void applyGrayText(Component com){
		com.getStyle().setTextColor(DARKTHEME.value ? new Vector4f(.65f, .65f, .65f, 1f) : new Vector4f(.35f, .35f, .35f, 1f));
	}

	public static void applyBorderlessScrollable(ScrollablePanel scrollable, boolean nell){
		if(nell) applyBorderless(scrollable);
		else scrollable.getStyle().setBorderRadius(0);
		scrollable.getContainer().getStyle().setBorderRadius(0);
		applyBorderless(scrollable.getVerticalScrollBar());
		applyBorderless(scrollable.getHorizontalScrollBar());
	}

	public static void register(String group, String id, Setting<?> setting){
		if(!SETTINGS.containsKey(group)) SETTINGS.put(group, new LinkedHashMap<>());
		SETTINGS.get(group).put(id, setting);
	}

	public static void toggleFullScreen(boolean toggle){
		boolean bool = toggle ? FULLSCREEN.toggle() : FULLSCREEN.value;
		long moni = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(moni);
		int width = bool ? mode.width() : Settings.WINDOW_WIDTH.value;
		int height = bool ? mode.height() : Settings.WINDOW_HEIGHT.value;
		int x = bool ? 0 : 100, y = bool ? 0 : 100;
		glfwSetWindowMonitor(FMT.INSTANCE.window, bool ? moni : MemoryUtil.NULL, x, y, width, height, GLFW_DONT_CARE);
	}

	public static void checkForUpdatesAndLogin(){
		Thread thread = new Thread(() -> {
			JsonMap obj = JsonHandler.parseURL("http://fexcraft.net/minecraft/fcl/request", "mode=requestdata&modid=fmt");
			if(obj == null){
				Logging.log("Couldn't fetch latest version.");
				UPDATECHECK_FAILED = true;
			}
			else if(obj.has("blocked_versions")){
				JsonArray array = obj.getArray("blocked_versions");
				for(JsonValue<?> elm : array.elements()){
					if(elm.string_value().equals(FMT.VERSION)){
						Logging.log("Blocked version detected, causing panic.");
						System.exit(2); System.exit(2); System.exit(2); System.exit(2);
					}
				}
			}
			if(LAST_CATALOG_RELOAD + (Time.MIN_MS * 720) < Time.getDate()){
				Logging.log("Starting catalog update.");
				Catalog.fetch(false);
				Catalog.load(false);
				Catalog.check(false);
				if(Catalog.get() > 0){
					Logging.log("Found update for " + Catalog.get() + " files.");
					FOUND_UPDATE = true;
				}
				LAST_CATALOG_RELOAD = Time.getDate();
			}
			SessionHandler.checkIfLoggedIn(true, true);
			showWelcome(true);
		});
		thread.setName("UPCK");
		thread.start();
	}

	public static void showWelcome(boolean welcome){
		boolean update = FOUND_UPDATE;
		if(welcome){
			if(!SHOW_WELCOME.value && (!update || !SHOW_UPDATE.value)) return;
			if(!SHOW_UPDATE.value && update) update = false;
		}
		float width = 300;
		Dialog dialog = new Dialog(translate("welcome.title"), width, 140);
		if(update){
			dialog.getContainer().add(new Label(translate("welcome.update.available"), 10, 10, width - 20, 20));
			dialog.getContainer().add(new Label(format("welcome.update.files"), 10, 35, width - 20, 20));
			dialog.getContainer().add(new Label(translate("welcome.update.select"), 10, 60, width - 20, 20));
			Button button0 = new Button(translate("dialog.button.yes"), 10, 90, 80, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		dialog.close();
            		FMT.close(10);
            	}
            });
			dialog.getContainer().add(button0);
			Button button2 = new Button(translate("dialog.button.close"), 100, 90, 80, 20);
            button2.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
			dialog.getContainer().add(button2);
		}
		else if(welcome){
			if(SessionHandler.isLoggedIn()){
				dialog.getContainer().add(new Label(Translator.format("welcome.normal.greeting_logged", SessionHandler.getUserName()), 10, 10, width - 20, 20));//TODO session handler
			}
			else{
				dialog.getContainer().add(new Label(translate("welcome.normal.greeting_guest"), 10, 10, width - 20, 20));//TODO session handler
			}
			dialog.getContainer().add(new Label(format("welcome.normal.version", FMT.VERSION), 10, 35, width - 20, 20));
			dialog.getContainer().add(new RunButton("dialog.button.close", 210, 90, 80, 20, () -> dialog.close()));
			dialog.getContainer().add(new RunButton("dialog.button.load", 110, 90, 80, 20, () -> SaveHandler.openDialog(null)));
			dialog.getContainer().add(new RunButton("dialog.button.new", 10, 90, 80, 20, () -> SaveHandler.newDialog()));
		}
		applyComponentTheme(dialog.getContainer());
		dialog.show(FMT.FRAME);
	}

	public static void addRecentFile(File file){
		if(file == null || RECENT.get(0).equals(file)) return;
		if(RECENT.contains(file)){
			int index = RECENT.indexOf(file);
			if(index >= 0){
				RECENT.remove(index);
				RECENT.add(0, file);
			}
		}
		else{
			RECENT.add(0, file);
		}
		while(RECENT.size() > 10) RECENT.remove(RECENT.size() - 1);
	}

	public static void openRecent(int index){
		if(index < 0 || index >= 10) return;
		File file = RECENT.get(index);
		if(file.equals(NO_FILE_DOTS)) return;
		SaveHandler.openDialog(file);
	}

	public static void refresh(){
		SETTINGS.values().forEach(map -> map.values().forEach(setting -> setting.refresh()));
	}

}
