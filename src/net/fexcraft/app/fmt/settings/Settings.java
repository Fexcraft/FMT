package net.fexcraft.app.fmt.settings;

import static net.fexcraft.app.fmt.FMT.rgba;
import static net.fexcraft.app.fmt.utils.Translator.format;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.BoxComponent;
import net.fexcraft.app.fmt.ui.components.CylinderComponentFull;
import net.fexcraft.app.fmt.ui.components.GroupGeneral;
import net.fexcraft.app.fmt.ui.components.MarkerComponent;
import net.fexcraft.app.fmt.ui.components.PolygonGeneral;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.ui.components.ShapeboxComponent;
import net.fexcraft.app.fmt.ui.trees.PolygonTree;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.Time;

public class Settings {
	
	public static final int FORMAT = 2;
	public static final float FONT_SIZE = 20f;
	public static final String FONT = FontRegistry.ROBOTO_BOLD;
	public static final String FONT_PATH = "org/liquidengine/legui/style/font/Roboto-Bold.ttf";
	public static String UPDATE_FOUND, UPDATE_SKIPPED = "";
	public static boolean FOUND_UPDATE, UPDATECHECK_FAILED;
	public static ArrayList<File> RECENT = new ArrayList<File>();
	public static File NO_FILE_DOTS = new File("...");
	public static Setting<Integer> WINDOW_WIDTH, WINDOW_HEIGHT, ROUNDING_DIGITS, BACKUP_INTERVAL, GIF_DELAY_TIME, GIF_ROT_PASS;
	public static Setting<Boolean> DISCORD_RPC, DISCORD_HIDE, DISCORD_RESET_ON_NEW, FULLSCREEN, NO_RANDOM_TITLE;
	public static Setting<Boolean> VSYNC, HVSYNC, TRIANGULATION_Q, TRIANGULATION_L, INTERNAL_CHOOSER;
	public static Setting<Boolean> DEMO, FLOOR, CUBE, CMARKER, LINES, POLYMARKER, ADD_TO_LAST, SPHERE_MARKER;
	public static Setting<Float> MOUSE_SENSIVITY, MOVE_SPEED;
	public static Setting<String> LANGUAGE, POLYGON_SUFFIX, GROUP_SUFFIX, COPIED_POLYGON, PASTED_GROUP;
	public static Setting<Boolean> ASK_POLYGON_REMOVAL, ASK_GROUP_REMOVAL, OPEN_FOLDER_AFTER_SAVE, OPEN_FOLDER_AFTER_IMG;
	public static Setting<Boolean> SHOW_WELCOME, SHOW_UPDATE, SELECT_COPIED, SHOW_BOTTOMBAR, GIF_LOOP, HIDE_UI_FOR_IMAGE;
	public static RGBSetting BACKGROUND, SELECTION_LINES;
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
	public static RGBSetting POLYGON_NORMAL, POLYGON_SELECTED, POLYGON_INVISIBLE, POLYGON_INV_SEL, GROUP_NORMAL, GROUP_SELECTED, GROUP_INVISIBLE, GROUP_INV_SEL;
	//
	public static String GENERAL = "general";
	public static String GRAPHIC = "graphic";
	public static String DISCORD = "discord";
	public static String CONTROL = "control";
	public static String SPACE3D = "space3d";
	public static String NAMING = "naming";
	public static String IMAGE = "image";
	public static String THEME = "theme";
	//
	public static Map<String, Map<String, Setting<?>>> SETTINGS = new LinkedHashMap<>();
	
	public static void load(){
		var file = new File("./settings.json");
		var obj = file.exists() ? JsonHandler.parse(file) : new JsonMap();
		if(obj.has("format") && obj.get("format").integer_value() != FORMAT) obj = new JsonMap();
		UPDATE_FOUND = obj.get("update_found", FMT.VERSION);
		UPDATE_SKIPPED = obj.get("update_skipped", UPDATE_SKIPPED);
		if(obj.has("recent_files")){
			JsonArray array = obj.getArray("recent_files");
			for(int i = 0; i < 10; i++){
				if(i >= array.size()) RECENT.add(new File("..."));
				else RECENT.add(new File(array.get(i).string_value()));
			}
		}
		else for(int i = 0; i < 10; i++) RECENT.add(NO_FILE_DOTS);
		//
		SETTINGS.put(GENERAL, new LinkedHashMap<>());
		SETTINGS.put(GRAPHIC, new LinkedHashMap<>());
		SETTINGS.put(CONTROL, new LinkedHashMap<>());
		SETTINGS.put(SPACE3D, new LinkedHashMap<>());
		SETTINGS.put(DISCORD, new LinkedHashMap<>());
		SETTINGS.put(NAMING, new LinkedHashMap<>());
		SETTINGS.put(IMAGE, new LinkedHashMap<>());
		SETTINGS.put(THEME, new LinkedHashMap<>());
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
		TRIANGULATION_Q = new Setting<>("triangulated_quads", true, GRAPHIC, obj);
		TRIANGULATION_L = new Setting<>("triangulated_lines", false, GRAPHIC, obj);
		LANGUAGE = new Setting<>("language", "null", GENERAL, obj);
		INTERNAL_CHOOSER = new Setting<>("internal_filechooser", false, GENERAL, obj);
		ROUNDING_DIGITS = new Setting<>("rounding_digits", 4, GENERAL, obj);
		DEMO = new Setting<>("demo_model", false, SPACE3D, obj);
		FLOOR = new Setting<>("floor", true, SPACE3D, obj);
		CUBE = new Setting<>("center_cube", true, SPACE3D, obj);
		CMARKER = new Setting<>("center_marker", true, SPACE3D, obj);
		LINES = new Setting<>("lines", true, SPACE3D, obj);
		POLYMARKER = new Setting<>("polygon_marker", true, SPACE3D, obj);
		ADD_TO_LAST = new Setting<>("add_to_last", false, GENERAL, obj);
		FULLSCREEN = new Setting<>("fullscreen", false, GRAPHIC, obj);
		POLYGON_SUFFIX = new Setting<>("polygon_duplicate_suffix", "_%s", NAMING, obj);
		GROUP_SUFFIX = new Setting<>("group_duplicate_suffix", "_%s", NAMING, obj);
		ASK_GROUP_REMOVAL = new Setting<>("ask_group_removal", true, GENERAL, obj);
		ASK_POLYGON_REMOVAL = new Setting<>("ask_polygon_removal", false, GENERAL, obj);
		OPEN_FOLDER_AFTER_SAVE = new Setting<>("open_folder_after_save", true, GENERAL, obj);
		SHOW_WELCOME = new Setting<>("show_welcome", true, GENERAL, obj);
		SHOW_UPDATE = new Setting<>("show_update", true, GENERAL, obj);
		SPHERE_MARKER = new Setting<>("sphere_marker", false, SPACE3D, obj);
		BACKUP_INTERVAL = new Setting<>("backup_interval", 5, GENERAL, obj);
		COPIED_POLYGON = new Setting<>("copied_polygon", "%s_cp", NAMING, obj);
		SELECT_COPIED = new Setting<>("select_copied", true, GENERAL, obj);
		PASTED_GROUP = new Setting<>("pasted_group_suffix", "-cb", NAMING, obj);
		SHOW_BOTTOMBAR = new Setting<>("show_bottom_bar", true, GENERAL, obj);
		OPEN_FOLDER_AFTER_IMG = new Setting<>("open_folder_after_image", true, IMAGE, obj);
		GIF_DELAY_TIME = new Setting<>("gif_delay_time", 100, IMAGE, obj);
		GIF_LOOP = new Setting<>("gif_loop", true, IMAGE, obj);
		GIF_ROT_PASS = new Setting<>("gif_rotation_passes", 36, IMAGE, obj);
		HIDE_UI_FOR_IMAGE = new Setting<>("hide_ui_for_image", true, IMAGE, obj);
		BACKGROUND = new RGBSetting("background", new RGB(0x7f7f7f), SPACE3D, obj);
		NO_RANDOM_TITLE = new Setting<>("no_random_title", false, IMAGE, obj);
		SELECTION_LINES = new RGBSetting("selection_lines", new RGB(0xffff00), SPACE3D, obj);
		//
		SEL_THEME = new StringArraySetting("selected_theme", "light", THEME, obj, "light", "dark", "custom");
		DARKTHEME = new Setting<>("is_dark", false, THEME, obj);
		THEME_BACKGROUND = new RGBSetting("background", new RGB(0x212121), THEME, obj);
		THEME_BORDER = new RGBSetting("border", new RGB(0x616161), THEME, obj);
		THEME_SLIDER = new RGBSetting("slider", new RGB(0x616161), THEME, obj);
		THEME_STROKE = new RGBSetting("stroke", new RGB(0x0277BD), THEME, obj);
		THEME_ALLOW = new RGBSetting("allow", new RGB(0x1B5E20), THEME, obj);
		THEME_DENY = new RGBSetting("deny", new RGB(0xBD1C1C), THEME, obj);
		THEME_SHADOW = new RGBSetting("shadow", new RGB(0x0), THEME, obj);
		THEME_TEXT = new RGBSetting("text", new RGB(0xCCCCCC), THEME);
		THEME_BUTTON = new RGBSetting("button", new RGB(0x212121), THEME, obj);
		THEME_FONT = new StringArraySetting("font", FONT, THEME, obj, FontRegistry.ENTYPO, FontRegistry.ROBOTO_LIGHT, FontRegistry.ROBOTO_BOLD, FontRegistry.ROBOTO_REGULAR);
		POLYGON_NORMAL = new RGBSetting("component_polygon_normal", new RGB(38, 127, 0), THEME, obj);
		GROUP_NORMAL = new RGBSetting("component_group_normal", new RGB(0, 74, 127), THEME, obj);
		POLYGON_SELECTED = new RGBSetting("component_polygon_selected", new RGB(219, 156, 46), THEME, obj);
		GROUP_SELECTED = new RGBSetting("component_group_selected", new RGB(191, 128, 50), THEME, obj);
		POLYGON_INVISIBLE = new RGBSetting("component_polygon_invisible", new RGB(126, 196, 96), THEME, obj);
		GROUP_INVISIBLE = new RGBSetting("component_group_invisible", new RGB(67, 142, 196), THEME, obj);
		POLYGON_INV_SEL = new RGBSetting("component_polygon_invis_sel", new RGB(250, 202, 117), THEME, obj);
		GROUP_INV_SEL = new RGBSetting("component_group_invis_sel", new RGB(232, 158, 67), THEME, obj);
		//
		for(Map.Entry<String, Map<String, Setting<?>>> entry : SETTINGS.entrySet()){
			if(!obj.has(entry.getKey())) continue;
			JsonMap def = obj.getMap(entry.getKey());
			for(Setting<?> setting : entry.getValue().values()) setting.load(def);
		}//TODO load plugin settings ?
		//
		if(!SEL_THEME.value.equals("custom")) DARKTHEME.value = SEL_THEME.value.contains("dark");
	}
	
	public static void apply(FMT fmt){
		FMT.WIDTH = WINDOW_WIDTH.value;
		FMT.HEIGHT = WINDOW_HEIGHT.value;
		TexturedPolygon.TRIANGULATED_QUADS = TRIANGULATION_Q.value;
		refresh();
	}
	
	public static void save(){
		JsonMap obj = new JsonMap();
		obj.add("format", FORMAT);
		//
		SETTINGS.entrySet().forEach(entry -> {
			JsonMap jsn = new JsonMap();
			entry.getValue().values().forEach(setting -> setting.save(jsn));
			obj.add(entry.getKey(), jsn);
		});
		//
		obj.add("last_fmt_version", FMT.VERSION);
		obj.add("last_fmt_exit", Time.getAsString(Time.getDate()));
		obj.add("update_found", UPDATE_FOUND);
		obj.add("update_skipped", UPDATE_SKIPPED);
		JsonArray recent = new JsonArray();
		for(File file : RECENT){
			if(NO_FILE_DOTS.equals(file)) continue;
			recent.add(file.toString().replace("\\", "\\\\"));
		}
		if(recent.size() > 0) obj.add("recent_files", recent);
		JsonHandler.print(new File("./settings.json"), obj, false, false);
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

	public static void loadEditors(){
		JsonMap obj = JsonHandler.parse(new File("./editors.fmt"));
		if(obj == null || obj.empty()) loadDefaultEditors();
		else{
			for(Entry<String, JsonObject<?>> entry : obj.entries()){
				new Editor(entry.getKey(), entry.getValue().asMap());
			}
		}
		Editor.EDITORS.get("polygon_editor").show();
		Editor.EDITORS.get("polygon_tree").show();
	}

	private static void loadDefaultEditors(){
		Editor editor = new Editor("polygon_editor", "Polygon Editor", false, true);
		new PolygonTree(null, false);
		editor.addComponent(new QuickAdd());
		editor.addComponent(new PolygonGeneral());
		editor.addComponent(new BoxComponent());
		editor.addComponent(new ShapeboxComponent());
		editor.addComponent(new CylinderComponentFull());
		editor.addComponent(new MarkerComponent());
		editor = new Editor("group_editor", "Group Editor", false, true);
		editor.addComponent(new QuickAdd());
		editor.addComponent(new GroupGeneral());
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
			if(obj == null || !obj.has("latest_version")){
				Logging.log("Couldn't fetch latest version.");
				Logging.log(obj == null ? ">> no version response received" : obj.toString());
				UPDATECHECK_FAILED = true;
			}
			else if(obj.has("blocked_versions")){
				JsonArray array = obj.getArray("blocked_versions");
				for(JsonObject<?> elm : array.elements()){
					if(elm.string_value().equals(FMT.VERSION)){
						Logging.log("Blocked version detected, causing panic.");
						System.exit(2); System.exit(2); System.exit(2); System.exit(2);
					}
				}
			}
			else{
				UPDATE_FOUND = obj.get("latest_version").string_value();
				Logging.log("Received remote version: " + UPDATE_FOUND);
		        //
				if(!UPDATE_FOUND.equals(FMT.VERSION) && remoteVersionIsNewer()){
					Logging.log(">> Remote version is newer than installed version.");
					FOUND_UPDATE = true;
				}
				if(!UPDATE_FOUND.equals(UPDATE_SKIPPED)) UPDATE_SKIPPED = "";
			}
			showWelcome(true);
			SessionHandler.checkIfLoggedIn(true, true);
		});
		thread.setName("UPCK");
		thread.start();
	}

	private static boolean remoteVersionIsNewer(){
		Long remote = Long.parseLong(UPDATE_FOUND.replaceAll("[^0-9]", ""));
		Long local = Long.parseLong(FMT.VERSION.replaceAll("[^0-9]", ""));
		return remote > local;
	}

	public static void showWelcome(boolean welcome){
		boolean update = FOUND_UPDATE && !UPDATE_FOUND.equals(UPDATE_SKIPPED);
		if(welcome){
			if(!SHOW_WELCOME.value && (!update || !SHOW_UPDATE.value)) return;
			if(!SHOW_UPDATE.value && update) update = false;
		}
		float width = 300;
		Dialog dialog = new Dialog(translate("welcome.title"), width, update ? 140 : 110);
		if(update){
			dialog.getContainer().add(new Label(translate("welcome.update.available"), 10, 10, width - 20, 20));
			dialog.getContainer().add(new Label(format("welcome.update.version", FMT.VERSION, UPDATE_FOUND), 10, 35, width - 20, 20));
			dialog.getContainer().add(new Label(translate("welcome.update.select"), 10, 60, width - 20, 20));
			Button button0 = new Button(translate("dialog.button.yes"), 10, 90, 80, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		dialog.close();
            		FMT.close(10);
            	}
            });
			dialog.getContainer().add(button0);
			Button button1 = new Button(translate("welcome.update.skip"), 100, 90, 100, 20);
            button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
            		UPDATE_SKIPPED = UPDATE_FOUND;
            		dialog.close();
            		save();
            	}
            });
			dialog.getContainer().add(button1);
			Button button2 = new Button(translate("dialog.button.close"), 210, 90, 80, 20);
            button2.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
			dialog.getContainer().add(button2);
		}
		else if(welcome){
			dialog.getContainer().add(new Label(translate("welcome.normal.greeting_" + "guest"), 10, 10, width - 20, 20));//TODO session handler
			dialog.getContainer().add(new Label(format("welcome.normal.version", FMT.VERSION), 10, 35, width - 20, 20));
			Button button = new Button(translate("dialog.button.close"), width - 90, 60, 80, 20);
            button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> { if(CLICK == e.getAction()) dialog.close(); });
			dialog.getContainer().add(button);
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
