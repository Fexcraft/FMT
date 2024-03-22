package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.GGR.parseKeyAction;
import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;

public class KeyCompound {
	
	public static final ArrayList<KeyFunction> keys = new ArrayList<>();
	public static KeyFunction KEY_SPP, KEY_SPN, KEY_DU, KEY_DD, KEY_SPM, KEY_SPL;
	public static final String FORMAT = "2.0";
	
	public static void process(long window, int key, int scancode, int action, int mods){
		for(KeyFunction function : keys) if(function.id == key) function.run.run(action);
	}
	
	public static void init(){
		keys.clear();
		keys.add(new KeyFunction("move_w", GLFW_KEY_W, (action) -> FMT.CAM.w_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_a", GLFW_KEY_A, (action) -> FMT.CAM.a_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_s", GLFW_KEY_S, (action) -> FMT.CAM.s_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_d", GLFW_KEY_D, (action) -> FMT.CAM.d_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_up", GLFW_KEY_SPACE, (action) -> FMT.CAM.space_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_down", GLFW_KEY_LEFT_SHIFT, (action) -> FMT.CAM.shift_down = parseKeyAction(action)));
		keys.add(new KeyFunction("move_speed+", GLFW_KEY_R, (action) -> {
			FMT.CAM.r_down = parseKeyAction(action);
			Logging.bar("5x speed key pressed", false, 1);
		}));
		keys.add(new KeyFunction("move_speed-", GLFW_KEY_F, (action) -> {
			FMT.CAM.f_down = parseKeyAction(action);
			Logging.bar("2/ speed key pressed", false, 1);
		}));
		keys.add(new KeyFunction("move_speed*", GLFW_KEY_Y, (action) -> {
			if(GGR.isOverUI() || action != GLFW_RELEASE) return;
			if(FMT.CAM.movemod < 32){
				FMT.CAM.movemod *= 2;
				Logging.bar("Speed increased to " + (FMT.CAM.movemod * 100) + "%", true);
			}
		}));
		keys.add(new KeyFunction("move_speed/", GLFW_KEY_U, (action) -> {
			if(GGR.isOverUI() || action != GLFW_RELEASE) return;
			if(FMT.CAM.movemod > 0.03125){
				FMT.CAM.movemod *= 0.5f;
				Logging.bar("Speed decreased to " + (FMT.CAM.movemod * 100) + "%", true);
			}
		}));
		keys.add(new KeyFunction("zoom_in", GLFW_KEY_Z, (action) -> onRelease(action, () -> FMT.CAM.toggleZoom())));
		keys.add(new KeyFunction("pick_face", GLFW_KEY_I, (action) -> onRelease(action, () -> Picker.pick(Picker.PickType.FACE, Picker.PickTask.RESELECT, true))));
		//
		keys.add(new KeyFunction("toggle_editor_1", GLFW_KEY_1, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.POLYGON_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_2", GLFW_KEY_2, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.GROUP_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_3", GLFW_KEY_3, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.PIVOT_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_4", GLFW_KEY_4, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.MODEL_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_5", GLFW_KEY_5, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.TEXTURE_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_6", GLFW_KEY_6, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.UV_EDITOR.show();
		}));
		keys.add(new KeyFunction("toggle_editor_7", GLFW_KEY_7, action -> {
			if(GGR.isOverUI()) return;
			if(action == GLFW_RELEASE) Editor.PREVIEW_EDITOR.show();
		}));
		//
		keys.add(new KeyFunction("camera_rotate_left",  GLFW_KEY_LEFT,  action -> { if(!GGR.isOverUI()) FMT.CAM.hor -= Static.rad5; }));
		keys.add(new KeyFunction("camera_rotate_right", GLFW_KEY_RIGHT, action -> { if(!GGR.isOverUI()) FMT.CAM.hor += Static.rad5; }));
		keys.add(new KeyFunction("camera_rotate_up",    GLFW_KEY_UP,    action -> {
			if(!GGR.isOverUI()){
				if(Arrows.SEL == 0) FMT.CAM.ver -= Static.rad5;
				else Arrows.process(1);
			}
		}));
		keys.add(new KeyFunction("camera_rotate_down",  GLFW_KEY_DOWN,  action -> {
			if(!GGR.isOverUI()){
				if(Arrows.SEL == 0) FMT.CAM.ver += Static.rad5;
				else Arrows.process(-1);
			}
		}));
		//
		keys.add(new KeyFunction("help", GLFW_KEY_F1, action -> {}));
		keys.add(new KeyFunction("toggle_floor", GLFW_KEY_F2, action -> { if(action == GLFW_RELEASE) Settings.FLOOR.toggle(); }));
		keys.add(new KeyFunction("toggle_lines", GLFW_KEY_F3, action -> { if(action == GLFW_RELEASE) Settings.LINES.toggle(); }));
		keys.add(new KeyFunction("toggle_cube", GLFW_KEY_F4, action -> { if(action == GLFW_RELEASE) Settings.CUBE.toggle(); }));
		keys.add(new KeyFunction("toggle_pivot_marker", GLFW_KEY_F5, action -> { if(action == GLFW_RELEASE) Settings.PMARKER.toggle(); }));
		keys.add(new KeyFunction("toggle_polygon_marker", GLFW_KEY_F6, action -> { if(action == GLFW_RELEASE) Settings.POLYMARKER.toggle(); }));
		keys.add(new KeyFunction("toggle_center_marker", GLFW_KEY_F7, action -> { if(action == GLFW_RELEASE) Settings.CMARKER.toggle(); }));
		keys.add(new KeyFunction("toggle_lighting", GLFW_KEY_F8, action -> { if(action == GLFW_RELEASE) Logging.bar("Toggled lighting " + (Settings.LIGHTING_ON.toggle() ? "on" : "off") + ".", true); }));//TODO
		keys.add(new KeyFunction("toggle_animations", GLFW_KEY_F9, action -> { }));//TODO
		keys.add(new KeyFunction("toggle_gif", GLFW_KEY_F10, action -> { if(action == GLFW_RELEASE) ImageHandler.createGif(); }));
		keys.add(new KeyFunction("toggle_fullscreen", GLFW_KEY_F11, action -> { if(action == GLFW_RELEASE) Settings.toggleFullScreen(true); }));
		keys.add(new KeyFunction("screenshot", GLFW_KEY_F12, action -> { if(action == GLFW_RELEASE) ImageHandler.takeScreenshot(); }));
		//
		keys.add(new KeyFunction("delete", GLFW_KEY_DELETE, action -> { if(action == GLFW_RELEASE) FMT.MODEL.delsel(); }));
		//keys.add(new KeyFunction("raypick", GLFW_KEY_T, action -> { if(action == GLFW_RELEASE) RayCoastAway.doTest(true, null, false); }));
		keys.add(new KeyFunction("left_control", GLFW_KEY_LEFT_CONTROL, (action) -> FMT.CAM.left_control_down = parseKeyAction(action)));
		keys.add(new KeyFunction("left_alt", GLFW_KEY_LEFT_ALT, (action) -> FMT.CAM.left_alt_down = parseKeyAction(action)));
		keys.add(new KeyFunction("right_control", GLFW_KEY_RIGHT_CONTROL, (action) -> FMT.CAM.right_control_down = parseKeyAction(action)));
		keys.add(new KeyFunction("right_alt", GLFW_KEY_RIGHT_ALT, (action) -> FMT.CAM.right_alt_down = parseKeyAction(action)));
		keys.add(new KeyFunction("clipboard_copy", GLFW_KEY_C, (action) -> {
			if(action == GLFW_PRESS && GGR.isControlDown()){
				FMT.MODEL.copyToClipboard(false);
			}
		}).set_ctrl());
		keys.add(new KeyFunction("clipboard_copy_grouped", GLFW_KEY_G, (action) -> {
			if(action == GLFW_PRESS && GGR.isControlDown()){
				FMT.MODEL.copyToClipboard(true);
			}
		}).set_ctrl());
		keys.add(new KeyFunction("clipboard_paste", GLFW_KEY_V, (action) -> {
			if(action == GLFW_PRESS && GGR.isControlDown()){
				FMT.MODEL.pasteFromClipboard();
			}
		}).set_ctrl());
	}

	private static void onRelease(int action, Runnable run){
		if(action != GLFW_RELEASE) return;
		run.run();
	}

	public static void load(){
		JsonMap map = JsonHandler.parse(new File("./keys.json"));
		if(map.entries().size() == 0) return;
		for(KeyFunction func : keys){
			if(map.has(func.name)){
				func.id = map.get(func.name).integer_value();
			}
		}
	}
	
	public static void save(){
		JsonMap map = new JsonMap();
		map.add("format", FORMAT);
		for(KeyFunction func : keys){
			map.add(func.name, func.id);
		}
		JsonHandler.print(new File("./keys.json"), map, PrintOption.SPACED);
	}
	
	public static class KeyFunction {

		private String name;
		private int id, def;
		private KeyRunnable run;
		private boolean ctrl;

		public KeyFunction(String name, int defid, KeyRunnable run){
			this.name = name;
			this.id = def = defid;
			this.run = run;
		}

		public int id(){
			return id;
		}

		public int def(){
			return def;
		}

		public String name(){
			return name;
		}

		public void setId(Integer key){
			this.id = key == null || key < 0 ? def : key;
		}

		public boolean control(){
			return ctrl;
		};

		public KeyFunction set_ctrl(){
			ctrl = true;
			return this;
		}

	}
	
	@FunctionalInterface
	public static interface KeyRunnable {
		
		public void run(int action);
	}

	public static void openAdjuster(){
		//
	}

}
