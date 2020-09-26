package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.google.common.io.Files;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.lib.common.json.JsonUtil;

public class KeyCompound {
	
	public static final ArrayList<KeyFunction> keys = new ArrayList<>();
	public static KeyFunction KEY_SPP, KEY_SPN, KEY_DU, KEY_DD, KEY_SPM, KEY_SPL;
	public static final String FORMAT = "2.0";
	
	public static void process(long window, int key, int scancode, int action, int mods){
		for(KeyFunction function : keys) if(function.id == key) function.run.run(action);
	}
	
	public static void init(){
		keys.clear();
		keys.add(new KeyFunction("move_w", GLFW_KEY_W, (action) -> FMTB.ggr.w_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_a", GLFW_KEY_A, (action) -> FMTB.ggr.a_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_s", GLFW_KEY_S, (action) -> FMTB.ggr.s_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_d", GLFW_KEY_D, (action) -> FMTB.ggr.d_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_up", GLFW_KEY_SPACE, (action) -> FMTB.ggr.space_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_down", GLFW_KEY_LEFT_SHIFT, (action) -> FMTB.ggr.shift_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_speed+", GLFW_KEY_R, (action) -> FMTB.ggr.r_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_speed-", GLFW_KEY_F, (action) -> FMTB.ggr.f_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("move_speed*", GLFW_KEY_Y, (action) -> {
			if(!GGR.isNotOverUI() || action != GLFW_RELEASE) return;
			if(FMTB.ggr.movemod < 32/*1024*/){
				FMTB.ggr.movemod *= 2;
				//TODO Crossbar.show("Speed increased to " + (FMTB.ggr.movemod * 100 ) + "%", Time.getDate() + 2000);
			}
		}));
		keys.add(new KeyFunction("move_speed/", GLFW_KEY_U, (action) -> {
			if(!GGR.isNotOverUI() || action != GLFW_RELEASE) return;
			if(FMTB.ggr.movemod > 0.03125){
				FMTB.ggr.movemod *= 0.5f;
				//TODO Crossbar.show("Speed decreased to " + (FMTB.ggr.movemod * 100 ) + "%", Time.getDate() + 2000);
			}
		}));
		//
		keys.add(new KeyFunction("toggle_help", GLFW_KEY_F1, (action) -> {}));//TODO
		keys.add(new KeyFunction("toggle_floor", GLFW_KEY_F2, (action) -> { if(action == GLFW_RELEASE) Settings.toggleFloor(); }));
		keys.add(new KeyFunction("toggle_lines", GLFW_KEY_F3, (action) -> { if(action == GLFW_RELEASE) Settings.toggleLines(); }));
		keys.add(new KeyFunction("toggle_cube", GLFW_KEY_F4, (action) -> { if(action == GLFW_RELEASE) Settings.toggleCube(); }));
		keys.add(new KeyFunction("toggle_demo", GLFW_KEY_F5, (action) -> { if(action == GLFW_RELEASE) Settings.toggleDemo(); }));
		keys.add(new KeyFunction("toggle_polygon_marker", GLFW_KEY_F6, (action) -> { if(action == GLFW_RELEASE) Settings.togglePolygonMarker(); }));
		keys.add(new KeyFunction("toggle_center_marker", GLFW_KEY_F7, (action) -> { if(action == GLFW_RELEASE) Settings.toggleCenterMarker(); }));
		keys.add(new KeyFunction("toggle_lighting", GLFW_KEY_F8, (action) -> { if(action == GLFW_RELEASE) Settings.toggleLighting(); }));
		keys.add(new KeyFunction("toggle_animations", GLFW_KEY_F9, (action) -> { if(action == GLFW_RELEASE) Settings.toggleAnimations(); }));
		//keys.add(new KeyFunction("toggle_arrows", GLFW_KEY_F10, (action) -> { if(action == GLFW_RELEASE) Settings.toggleAnimations(); }));
		//
		keys.add(new KeyFunction("take_screenshot", GLFW_KEY_F12, (action) -> {
			if(action != GLFW_RELEASE) return;
			ImageHelper.takeScreenshot(false);
        	DialogBox.show(null, "dialogbox.button.ok", "dialogbox.button.open", null, () -> {
        		try{
        			FMTB.openLink(new File("./screenshots/").getAbsolutePath());
        		}
        		catch(Throwable e){
        			log(e);
        		}
        	}, "image_helper.screenshot.done");
		}));
		//
		for(int i = 1; i < 10; i++){ final int j = i - 1;
			keys.add(new KeyFunction("toggle_editor_" + i, GLFW_KEY_0 + i, (action) -> { if(action == GLFW_RELEASE) Editors.toggleWidget(j); }));
		}
		//
		keys.add(new KeyFunction("camera_rotate_left", GLFW_KEY_LEFT, action -> FMTB.ggr.rotation.yCoord -= 5));
		keys.add(new KeyFunction("camera_rotate_right", GLFW_KEY_RIGHT, action -> FMTB.ggr.rotation.yCoord += 5));
		keys.add(new KeyFunction("camera_rotate_up", GLFW_KEY_UP, action -> FMTB.ggr.rotation.xCoord -= 5));
		keys.add(new KeyFunction("camera_rotate_down", GLFW_KEY_DOWN, action -> FMTB.ggr.rotation.xCoord += 5));
		//
		keys.add(new KeyFunction("delete", GLFW_KEY_DELETE, action -> { if(action == GLFW_RELEASE) FMTB.MODEL.deleteSelected(); }));
		keys.add(new KeyFunction("raypick", GLFW_KEY_T, action -> { if(action == GLFW_RELEASE) RayCoastAway.doTest(true, null, false); }));
		keys.add(new KeyFunction("left_control", GLFW_KEY_LEFT_CONTROL, (action) -> FMTB.ggr.left_control_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("left_alt", GLFW_KEY_LEFT_ALT, (action) -> FMTB.ggr.left_alt_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("right_control", GLFW_KEY_RIGHT_CONTROL, (action) -> FMTB.ggr.right_control_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("right_alt", GLFW_KEY_RIGHT_ALT, (action) -> FMTB.ggr.right_alt_down = GGR.parseKeyAction(action)));
		keys.add(new KeyFunction("clipboard_copy", GLFW_KEY_C, (action) -> {
			if(action == GLFW_PRESS && GGR.isControlDown()){
				FMTB.MODEL.copyToClipboard();
			}
		}));
		keys.add(new KeyFunction("clipboard_paste", GLFW_KEY_V, (action) -> {
			if(action == GLFW_PRESS){
				FMTB.MODEL.pasteFromClipboard();
			}
		}));
		keys.add(new KeyFunction("open_polygon_tree", GLFW_KEY_P, action -> {
			if(action == GLFW_PRESS) Trees.toggle("polygon");
		}));
		keys.add(new KeyFunction("open_texture_tree", GLFW_KEY_O, action -> {
			if(action == GLFW_PRESS) Trees.toggle("textures");
		}));
		keys.add(new KeyFunction("open_helper_tree", GLFW_KEY_I, action -> {
			if(action == GLFW_PRESS) Trees.toggle("helper");
		}));
	}
	
	public static void load(){
		JsonObject obj = JsonUtil.get(new File("./keys.json"));
		if(obj.entrySet().size() == 0) return;
		if(!obj.has("format") || !obj.get("format").getAsString().equals(FORMAT)){
			log("Old keys.json format detected, skipping keys.json loading.");
			try{
				Files.copy(new File("./keys.json"), new File("./keys_old.json"));
			}
			catch(IOException e){
				log(e);
			}
			return;
		}
		for(KeyFunction func : keys){
			if(obj.has(func.name)){
				func.id = obj.get(func.name).getAsInt();
			}
		}
	}
	
	public static void save(){
		JsonObject obj = new JsonObject();
		obj.addProperty("format", FORMAT);
		for(KeyFunction func : keys){
			obj.addProperty(func.name, func.id);
		}
		JsonUtil.write(new File("./keys.json"), obj);
	}
	
	public static class KeyFunction {
		
		private String name;
		private int id, def;
		private KeyRunnable run;
		
		public KeyFunction(String name, int defid, KeyRunnable run){
			this.name = name; this.id = this.def = defid; this.run = run;
		}

		public int id(){ return id; }
		
		public int def(){ return def; }
		
		public String name(){ return name; }

		public void setId(Integer key){ this.id = key == null || key < 0 ? def : key; }
		
	}
	
	@FunctionalInterface
	public static interface KeyRunnable {
		
		public void run(int action);
	}

}
