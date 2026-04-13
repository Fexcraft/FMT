package net.fexcraft.app.fmt.settings;

import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.updater.Catalog;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.utils.*;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.oui.Editor;
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
	public static boolean FOUND_UPDATE;
	public static long LAST_CATALOG_RELOAD;
	public static ArrayList<File> RECENT = new ArrayList<File>();
	public static ArrayList<File> BOOKMARKS = new ArrayList<File>();
	public static File NO_FILE_DOTS = new File("...");
	//
	public static Map<String, Map<String, Setting<?>>> SETTINGS = new LinkedHashMap<>();
	public static String GENERAL = "general";
	public static String GRAPHIC = "graphic";
	public static String INTERFACE = "interface";
	public static String DISCORD = "discord";
	public static String CONTROL = "control";
	public static String SPACE3D = "space3d";
	public static String NAMING = "naming";
	public static String IMAGE = "image";
	public static String WORKSPACE = "workspace";
	public static String LIGHTING = "lighting";
 	//General
	public static Setting<String> LANGUAGE = new Setting<>("language", "null", GENERAL);
	public static Setting<Boolean> INTERNAL_CHOOSER = new Setting<>("internal_filechooser", false, GENERAL);
	public static Setting<Integer> ROUNDING_DIGITS = new Setting<>("rounding_digits", 4, GENERAL)
		.minmax(0, 10).consumer(con -> Field.updateRoundingDigits());
	public static Setting<Boolean> ADD_TO_LAST = new Setting<>("add_to_last", false, GENERAL);
	public static Setting<Boolean> NO_CLIPBOARD = new Setting<>("no_clipboard", false, GENERAL);
	public static Setting<Boolean> ASK_POLYGON_REMOVAL = new Setting<>("ask_polygon_removal", false, GENERAL);
	public static Setting<Boolean> ASK_GROUP_REMOVAL = new Setting<>("ask_group_removal", true, GENERAL);
	public static Setting<Boolean> ASK_PIVOT_REMOVAL = new Setting<>("ask_pivot_removal", false, GENERAL);
	public static Setting<Boolean> ASK_HELPER_REMOVAL = new Setting<>("ask_helper_removal", false, GENERAL);
	public static Setting<Boolean> ASK_TEXTURE_GROUP_REMOVAL = new Setting<>("ask_texture_group_removal", true, GENERAL);
	public static Setting<Boolean> OPEN_FOLDER_AFTER_SAVE = new Setting<>("open_folder_after_save", true, GENERAL);
	public static Setting<Boolean> OPEN_FOLDER_AFTER_IMG = new Setting<>("open_folder_after_image", true, IMAGE);
	public static Setting<Boolean> SHOW_WELCOME = new Setting<>("show_welcome", true, GENERAL);
	public static Setting<Boolean> SHOW_UPDATE = new Setting<>("show_update", true, GENERAL);
	public static Setting<Integer> BACKUP_INTERVAL = new Setting<>("backup_interval", 5, GENERAL);
	public static Setting<Boolean> SELECT_COPIED = new Setting<>("select_copied", true, GENERAL);
	public static Setting<Boolean> SELECT_NEW = new Setting<>("select_new", true, GENERAL);
	public static Setting<Boolean> NO_RANDOM_TITLE = new Setting<>("no_random_title", false, GENERAL);
	public static Setting<Boolean> LOG_UPDATES = new Setting<>("log_updates", false, GENERAL);
	public static Setting<Boolean> HIDE_MENU_AFTER_POLYGON = new Setting<>("hide_menu_after_polygon", true, GENERAL);
	public static Setting<Boolean> NUMBERFIELD_BUTTONS = new Setting<>("numberfield_buttons", false, GENERAL);
	public static Setting<Integer> PAINTER_CHANNELS = new Setting<>("painter_channels", 2, GENERAL);
	//public static Setting<Boolean> TESTING = new Setting<>("testing", false, GENERAL);
	//Interface
	public static Setting<Float> UI_SCALE = new Setting<>("scale", 1f, INTERFACE);
	public static RGBSetting GENERIC_BACKGROUND_0 = new RGBSetting("background_0", 0xcdcdcd, INTERFACE);
	public static RGBSetting GENERIC_BACKGROUND_1 = new RGBSetting("background_1", 0xbdbdbd, INTERFACE);
	public static RGBSetting GENERIC_BACKGROUND_2 = new RGBSetting("background_2", 0x858585, INTERFACE);
	public static RGBSetting GENERIC_HOVER = new RGBSetting("hover", 0xdede00, INTERFACE);
	public static RGBSetting GENERIC_SELECTED = new RGBSetting("selected", 0x43f0ae, INTERFACE);
	public static RGBSetting GENERIC_TEXT_0 = new RGBSetting("text_0", 0x0, INTERFACE);
	public static RGBSetting GENERIC_TEXT_1 = new RGBSetting("text_1", 0xffffff, INTERFACE);
	public static RGBSetting GENERIC_TEXT_2 = new RGBSetting("text_2", 0xcdcdcd, INTERFACE);
	public static RGBSetting GENERIC_FIELD = new RGBSetting("field", 0xa6b3b3, INTERFACE);
	public static RGBSetting DROPLIST_CONFIRM = new RGBSetting("list_entry_confirm", 0x62b4e3, INTERFACE);
	public static RGBSetting POLYGON_NORMAL = new RGBSetting("polygon_normal", new RGB(38, 127, 0), INTERFACE);
	public static RGBSetting POLYGON_SELECTED = new RGBSetting("polygon_selected", new RGB(219, 156, 46), INTERFACE);
	public static RGBSetting POLYGON_INVISIBLE = new RGBSetting("polygon_invisible", new RGB(126, 196, 96), INTERFACE);
	public static RGBSetting POLYGON_INV_SEL = new RGBSetting("polygon_invis_sel", new RGB(250, 202, 117), INTERFACE);
	public static RGBSetting GROUP_NORMAL = new RGBSetting("group_normal", new RGB(0, 74, 127), INTERFACE);
	public static RGBSetting GROUP_SELECTED = new RGBSetting("group_selected", new RGB(191, 128, 50), INTERFACE);
	public static RGBSetting GROUP_INVISIBLE = new RGBSetting("group_invisible", new RGB(67, 142, 196), INTERFACE);
	public static RGBSetting GROUP_INV_SEL = new RGBSetting("group_invis_sel", new RGB(232, 158, 67), INTERFACE);
	public static RGBSetting PIVOT_NORMAL = new RGBSetting("pivot_normal", new RGB(5, 158, 127), INTERFACE);
	public static RGBSetting PIVOT_SELECTED = new RGBSetting("pivot_selected", new RGB(219, 213, 31), INTERFACE);
	public static RGBSetting PIVOT_INVISIBLE = new RGBSetting("pivot_invisible", new RGB(123, 158, 151), INTERFACE);
	public static RGBSetting PIVOT_INV_SEL = new RGBSetting("pivot_invis_sel", new RGB(209, 205, 100), INTERFACE);
	public static RGBSetting TEXTURE_GROUP = new RGBSetting("texture_group", new RGB(200, 200, 200), INTERFACE);
	public static RGBSetting TEXTURE_OPTION = new RGBSetting("texture_option", new RGB(0, 74, 127), INTERFACE);
	//Graphic
	public static Setting<Integer> DEF_WIDTH = new Setting<>("window_width", 1024, GRAPHIC);
	public static Setting<Integer> DEF_HEIGHT = new Setting<>("window_height", 576, GRAPHIC);
	public static Setting<Boolean> FVSYNC = new Setting<>("vsync", true, GRAPHIC);
	public static Setting<Boolean> HVSYNC = new Setting<>("vsync/2", false, GRAPHIC);
	public static Setting<Boolean> QVSYNC = new Setting<>("vsync/4", false, GRAPHIC);
	public static Setting<Boolean> TRIANGULATION_Q = new Setting<>("triangulated_quads", true, GRAPHIC);
	public static Setting<Boolean> TRIANGULATION_L = new Setting<>("triangulated_lines", false, GRAPHIC);
	public static Setting<Boolean> FULLSCREEN = new Setting<>("fullscreen", false, GRAPHIC);
	//Lighting
	public static Setting<Boolean> LIGHTING_ON = new Setting<>("enabled", false, LIGHTING);
	public static Setting<Float> LIGHT_AMBIENT = new Setting<>("ambient", 0.5f, LIGHTING).minmax(0f, 1f);
	public static Setting<Float> LIGHT_DIFFUSE = new Setting<>("diffuse", 1f, LIGHTING).minmax(0f, 1f);
	public static RGBSetting LIGHT_COLOR = new RGBSetting("color", RGB.WHITE.copy(), LIGHTING);
	public static Setting<Float> LIGHT_POSX = new Setting<>("pos_x", 600f, LIGHTING);
	public static Setting<Float> LIGHT_POSY = new Setting<>("pos_y", -600f, LIGHTING);
	public static Setting<Float> LIGHT_POSZ = new Setting<>("pos_z", -600f, LIGHTING);
	//Control
	public static Setting<Float> MOUSE_SENSIVITY = new Setting<>("mouse_sensitivity", 2f, CONTROL);
	public static Setting<Float> MOVE_SPEED = new Setting<>("movement_speed", 20f, CONTROL);
	public static Setting<Float> SCROLL_SPEED = new Setting<>("scroll_speed", 1f, CONTROL);
	public static Setting<Float> ZOOM_LEVEL = new Setting<>("zoom_by", 10f, CONTROL).minmax(0.001f, 160f);
	public static Setting<Float> ARROW_SENSIVITY = new Setting<>("arrow_sensivity", 8f, CONTROL).minmax(0.001f, 100f);
	//Space3D
	public static Setting<Boolean> DEMO = new Setting<>("demo_model", false, SPACE3D);
	public static Setting<Boolean> FLOOR = new Setting<>("floor", true, SPACE3D);
	public static Setting<Boolean> CUBE = new Setting<>("center_cube", true, SPACE3D);
	public static Setting<Boolean> CMARKER = new Setting<>("center_marker", true, SPACE3D);
	public static Setting<Boolean> PMARKER = new Setting<>("pivot_marker", true, SPACE3D);
	public static Setting<Boolean> LINES = new Setting<>("lines", true, SPACE3D);
	public static Setting<Boolean> POLYMARKER = new Setting<>("polygon_marker", true, SPACE3D);
	public static Setting<Boolean> SPHERE_MARKER = new Setting<>("sphere_marker", false, SPACE3D);
	public static RGBSetting BACKGROUND = new RGBSetting("background", new RGB(0x7f7f7f), SPACE3D);
	public static RGBSetting SELECTION_LINES = new RGBSetting("selection_lines", new RGB(0xffff00), SPACE3D);
	public static Setting<Boolean> ANIMATE = new Setting<>("animate", true, SPACE3D);
	//Naming
	public static Setting<String> POLYGON_SUFFIX = new Setting<>("polygon_duplicate_suffix", "_%s", NAMING);
	public static Setting<String> GROUP_SUFFIX = new Setting<>("group_duplicate_suffix", "_%s", NAMING);
	public static Setting<String> COPIED_POLYGON = new Setting<>("copied_polygon", "%s_cp", NAMING);
	public static Setting<String> PASTED_GROUP = new Setting<>("pasted_group_suffix", "-cb", NAMING);
	//Image
	public static Setting<Integer> GIF_DELAY_TIME = new Setting<>("gif_delay_time", 100, IMAGE);
	public static Setting<Boolean> GIF_LOOP = new Setting<>("gif_loop", true, IMAGE);
	public static Setting<Integer> GIF_ROT_PASS = new Setting<>("gif_rotation_passes", 36, IMAGE);
	public static Setting<Boolean> HIDE_UI_FOR_IMAGE = new Setting<>("hide_ui_for_image", true, IMAGE);
	//Workspace
	public static Setting<String> WORKSPACE_NAME = new Setting<>("name", "FMT Workspace", WORKSPACE);
	public static Setting<String> WORKSPACE_ROOT = new Setting<>("root", "./workspace/", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> JAVA8_PATH = new Setting<>("java8", "/java/install/path/", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> JAVA17_PATH = new Setting<>("java17", "/java/install/path/", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> M12PATH = new Setting<>("m12path", "/install/path", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> M20PATH = new Setting<>("m20path", "/install/path/", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> M12RCMD = new Setting<>("m12run", "{JAVA} -jar client.jar", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> M20RCMD = new Setting<>("m20run", "{JAVA} -jar client.jar", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	public static Setting<String> TEXT_EDITOR = new Setting<>("text_editor", "/usr/bin/mousepad %s", WORKSPACE){
		@Override
		public boolean basicstr(){
			return false;
		}
	};
	//Discord
	public static Setting<Boolean> DISCORD_RPC = new Setting<>("enabled", false, DISCORD);
	public static Setting<Boolean> DISCORD_HIDE = new Setting<>("hidden_mode", false, DISCORD);
	public static Setting<Boolean> DISCORD_RESET_ON_NEW = new Setting<>("reset_on_new", false, DISCORD);
	
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
		for(Map.Entry<String, Map<String, Setting<?>>> group : SETTINGS.entrySet()){
			JsonMap gmap = map.has(group.getKey()) ? map.getMap(group.getKey()) : new JsonMap();
			for(Setting<?> set : group.getValue().values()) set.load(gmap);
		}
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
		if(map.has("last_model")){
			FMT.MODEL = new Model(new File(map.get("last_model").string_value()), "Loaded Model");
		}
		else{
			FMT.MODEL = new Model(null, "Unnamed Model");
		}
	}
	
	public static void apply(FMT fmt){
		FMT.WIDTH = DEF_WIDTH.value;
		FMT.HEIGHT = DEF_HEIGHT.value;
		FMT.SCALED_WIDTH = FMT.WIDTH / UI_SCALE.value;
		FMT.SCALED_HEIGHT = FMT.HEIGHT / UI_SCALE.value;
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

	public static void register(String group, String id, Setting<?> setting){
		if(!SETTINGS.containsKey(group)) SETTINGS.put(group, new LinkedHashMap<>());
		SETTINGS.get(group).put(id, setting);
	}

	public static void toggleFullScreen(boolean toggle){
		boolean bool = toggle ? FULLSCREEN.toggle() : FULLSCREEN.value;
		long moni = glfwGetPrimaryMonitor();
		GLFWVidMode mode = glfwGetVideoMode(moni);
		int width = bool ? mode.width() : Settings.DEF_WIDTH.value;
		int height = bool ? mode.height() : Settings.DEF_HEIGHT.value;
		int x = bool ? 0 : 100, y = bool ? 0 : 100;
		glfwSetWindowMonitor(FMT.INSTANCE.window, bool ? moni : MemoryUtil.NULL, x, y, width, height, GLFW_DONT_CARE);
	}

	public static void checkForUpdatesAndLogin(){
		Thread thread = new Thread(() -> {
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
			FMT.queue(() -> showWelcome(true));
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
		Dialog dia = FMT.UI.createDialog(360, 140, "welcome.title");
		if(update){
			dia.addText(0, "welcome.update.available");
			dia.addText(1, "welcome.update.files");
			dia.addText(2, "welcome.update.select");
			dia.consumer(d -> FMT.close(10), null);
			dia.buttons(100, DialogButton.YES, DialogButton.CLOSE);
		}
		else if(welcome){
			if(SessionHandler.isLoggedIn()){
				dia.addText(0, "welcome.normal.greeting_logged", SessionHandler.getUserName());
			}
			else{
				dia.addText(0, "welcome.normal.greeting_guest");
			}
			dia.addText(1, "welcome.normal.version", FMT.VERSION);
			dia.consumer(d -> SaveHandler.newDialog(), d -> SaveHandler.openDialog(null));
			dia.buttons(80, DialogButton.NEW, DialogButton.LOAD, DialogButton.CLOSE);
		}
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
