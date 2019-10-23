package net.fexcraft.app.fmt.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.re.DialogBox;
import net.fexcraft.lib.common.json.JsonUtil;

public class KeyCompound {
	
	public static final ArrayList<KeyFunction> keys = new ArrayList<>();
	public static final ArrayList<KeyFunction> pressed_keys = new ArrayList<>();
	public static final ArrayList<KeyFunction> released_keys = new ArrayList<>();
	public static KeyFunction KEY_W, KEY_A, KEY_S, KEY_D, KEY_SPP, KEY_SPN, KEY_DU, KEY_DD;
	
	public static void init(){
		keys.clear();
		keys.add(KEY_W = new FunctionlessKey("move_w", Keyboard.KEY_W, false, false));
		keys.add(KEY_A = new FunctionlessKey("move_a", Keyboard.KEY_A, false, false));
		keys.add(KEY_S = new FunctionlessKey("move_s", Keyboard.KEY_S, false, false));
		keys.add(KEY_D = new FunctionlessKey("move_d", Keyboard.KEY_D, false, false));
		keys.add(KEY_SPP = new FunctionlessKey("move_speed+", Keyboard.KEY_R, false, false));
		keys.add(KEY_SPN = new FunctionlessKey("move_speed-", Keyboard.KEY_F, false, false));
		keys.add(KEY_DU = new FunctionlessKey("move_up", Keyboard.KEY_SPACE, false, false));
		keys.add(KEY_DD = new FunctionlessKey("move_down", Keyboard.KEY_LSHIFT, false, false));
		//
		keys.add(new KeyFunction("toggle_help", Keyboard.KEY_F1, true){
			@Override
			public boolean process(){
				//TODO "help" UI
				return true;
			}
		});
		keys.add(new KeyFunction("toggle_floor", Keyboard.KEY_F2, true){
			@Override public boolean process(){ Settings.toggleFloor(); return true; }
		});
		keys.add(new KeyFunction("toggle_lines", Keyboard.KEY_F3, true){
			@Override public boolean process(){ Settings.toggleLines(); return true; }
		});
		keys.add(new KeyFunction("toggle_cube", Keyboard.KEY_F4, true){
			@Override public boolean process(){ Settings.toggleCube(); return true; }
		});
		keys.add(new KeyFunction("toggle_demo", Keyboard.KEY_F5, true){
			@Override public boolean process(){ Settings.toggleDemo(); return true; }
		});
		keys.add(new KeyFunction("toggle_polygon_marker", Keyboard.KEY_F6, true){
			@Override public boolean process(){ Settings.togglePolygonMarker(); return true; }
		});
		keys.add(new KeyFunction("toggle_polygon_count", Keyboard.KEY_F7, true){
			@Override public boolean process(){ Settings.togglePolygonCount(); return true; }
		});
		keys.add(new KeyFunction("toggle_lighting", Keyboard.KEY_F8, true){
			@Override public boolean process(){ Settings.toggleLighting(); return true; }
		});
		keys.add(new KeyFunction("toggle_animations", Keyboard.KEY_F9, true){
			@Override public boolean process(){ Settings.toggleAnimations(); return true; }
		});
		//
		/*keys.add(new KeyFunction("toggle_gametest", Keyboard.KEY_F10, true){
			@Override public boolean process(){ FMTB.GAMETEST = !FMTB.GAMETEST; return true; }
		});*/
		//
		/*keys.add(new KeyFunction("toggle_fullscreen", Keyboard.KEY_F11, true){
			@Override public boolean process(){
            	try{ Display.setFullscreen(Settings.toogleFullscreen()); }
    			catch(Exception ex){ ex.printStackTrace(); } return true;
			}
		});*/
		keys.add(new KeyFunction("take_screenshot", Keyboard.KEY_F12, true){
			@Override public boolean process(){
				ImageHelper.takeScreenshot(false);
            	FMTB.showDialogbox("Screenshot taken.", "OK", "Open", DialogBox.NOTHING, () -> {
            		try{ Desktop.getDesktop().open(new File("./screenshots/")); }
            		catch(IOException e){ e.printStackTrace(); }
            	}); return true;
			}
		});
		//
		for(int i = 0; i < 9; i++){ final int j = i;
			keys.add(new KeyFunction("toggle_editor_" + i, Keyboard.KEY_1 + i, true){
				@Override public boolean process(){ Editor.toggleContainer(j); return true; }
			});
		}
		//
		keys.add(new KeyFunction("camera_rotate_left", Keyboard.KEY_LEFT, true){
			@Override public boolean process(){ FMTB.ggr.rotation.yCoord += 15; return true; }
		});
		keys.add(new KeyFunction("camera_rotate_right", Keyboard.KEY_RIGHT, true){
			@Override public boolean process(){ FMTB.ggr.rotation.yCoord -= 15; return true; }
		});
		keys.add(new KeyFunction("camera_rotate_up", Keyboard.KEY_UP, true){
			@Override public boolean process(){ FMTB.ggr.rotation.xCoord += 15; return true; }
		});
		keys.add(new KeyFunction("camera_rotate_down", Keyboard.KEY_DOWN, true){
			@Override public boolean process(){ FMTB.ggr.rotation.xCoord -= 15; return true; }
		});
		//
		keys.add(new KeyFunction("delete", Keyboard.KEY_DELETE, true){
			@Override public boolean process(){ FMTB.MODEL.deleteSelected(); return true; }
		});
		keys.add(new KeyFunction("raypick", Keyboard.KEY_T, true){
			@Override public boolean process(){ RayCoastAway.doTest(true); return true; } /* for debugging, or such */
		});
		//sorting
		pressed_keys.clear(); released_keys.clear();
		for(KeyFunction func : keys){
			if(func.keystate){ pressed_keys.add(func); }
			else{ released_keys.add(func); }
		}
	}
	
	public static void load(){
		JsonObject obj = JsonUtil.get(new File("./keys.json"));
		if(obj.entrySet().size() == 0) return;
		for(KeyFunction func : keys){
			if(obj.has(func.name)){
				func.id = obj.get(func.name).getAsInt();
			}
		}
	}
	
	public static void save(){
		JsonObject obj = new JsonObject();
		for(KeyFunction func : keys){
			obj.addProperty(func.name, func.id);
		}
		JsonUtil.write(new File("./keys.json"), obj);
	}
	
	public static abstract class KeyFunction {
		
		private String name;
		private int id, def;
		public boolean keystate;
		
		public KeyFunction(String name, int defid, boolean onpressed){
			this.name = name; this.id = this.def = defid; this.keystate = onpressed;
		}

		public int ID(){ return id; }
		
		public int def(){ return def; }
		
		public abstract boolean process();
		
		public String name(){ return name; }

		public void setID(Integer key){ this.id = key == null || key < 0 ? def : key; }
		
	}
	
	public static class FunctionlessKey extends KeyFunction {
		
		private boolean result;

		public FunctionlessKey(String name, int defid, boolean onpressed, boolean result){
			super(name, defid, onpressed); this.result = result;
		}

		@Override
		public boolean process(){ return result; }
		
	}

}
