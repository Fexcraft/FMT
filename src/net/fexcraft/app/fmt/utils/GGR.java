package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.ShaderManager.Uniform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.utils.Picker.PickTask;
import net.fexcraft.lib.common.Static;

/** CCR */
public class GGR {

	public float movemod = 1;
    public float maxVR = Static.rad90 - Static.rad5;
    public Vector3f pos;
    //
    public boolean w_down, s_down, d_down, a_down, r_down, f_down, space_down, shift_down;
    public boolean left_alt_down, left_control_down, right_alt_down, right_control_down;
    public boolean zoomed;
    //
	private static int def_view, def_proj;
	private static Matrix4f view, projection;
	private float fov = 45f;
	public float hor, ver;
	private Vector3f dir = new Vector3f(), right = new Vector3f();
    
    public GGR(){
		boolean bool = FMT.MODEL.orient.rect();
		float y = bool ? 75 : -75;
		float z = bool ? 75 : -75;
		pos = new Vector3f(75, y, z);
		hor = bool ? -Static.rad135 : -Static.rad45;
		ver = bool ? -Static.rad30 : Static.rad30;
		view = new Matrix4f().lookAt(new Vector3f(4, 3, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
		perspective(45);
    }

	public void apply(){
        dir = new Vector3f(
            (float)Math.cos(ver) * (float)Math.sin(hor),
            (float)Math.sin(ver),
            (float)Math.cos(ver) * (float)Math.cos(hor)
        );
        right = new Vector3f(
			(float)Math.sin(hor - 3.14f / 2.0f),
            0,
            (float)Math.cos(hor - 3.14f / 2.0f)
        );
        Vector3f up = dir.cross(right, new Vector3f());
        view = new Matrix4f().lookAt(
            pos,
            new Vector3f(pos).add(dir),
            FMT.MODEL.orient.rect() ? up.mul(-1) : up
        );
        //FMT.pos.getTextState().setText(format(pos.x) + ", " + format(pos.y) + ", " + format(pos.z));
        //FMT.rot.getTextState().setText(format(Math.toDegrees(hor)) + " / " + format(Math.toDegrees(ver)) + " : " + (int)fov);
        perspective(fov);
		ShaderManager.GENERAL.use();
		def_view = ShaderManager.GENERAL.getUniform(Uniform.VIEW);
		glUniformMatrix4fv(def_view, false, view.get(new float[16]));
		def_proj = ShaderManager.GENERAL.getUniform(Uniform.PROJECTION);
		glUniformMatrix4fv(def_proj, false, projection.get(new float[16]));
    }

	public void resize(){
		perspective(fov);
	}

	public void perspective(float degree_fov){
		projection = new Matrix4f().perspective(Static.rad1 * fov, (float)FMT.WIDTH / FMT.HEIGHT, 0.1f, 1024f);
	}

	public void ortho(float scale){
		ShaderManager.UI.use();
		def_view = ShaderManager.UI.getUniform(Uniform.VIEW);
		glUniformMatrix4fv(def_view, false, new Matrix4f().identity().get(new float[16]));
		def_proj = ShaderManager.UI.getUniform(Uniform.PROJECTION);
		glUniformMatrix4fv(def_proj, false, new Matrix4f().ortho(0, FMT.WIDTH / scale, FMT.HEIGHT / scale, 0, -1000, 1000).get(new float[16]));
	}

    public void pollInput(float delta){
		if(grabbed && cursor_moved0){
			if(FMT.MODEL.orient.rect()){
				hor -= apos_x * Settings.MOUSE_SENSITIVITY.value * delta * 0.005;
				ver -= apos_y * Settings.MOUSE_SENSITIVITY.value * delta * 0.005;
			}
			else{
				hor += apos_x * Settings.MOUSE_SENSITIVITY.value * delta * 0.005;
				ver += apos_y * Settings.MOUSE_SENSITIVITY.value * delta * 0.005;
			}
            ver = Math.max(-maxVR, Math.min(maxVR, ver));
            cursor_moved0 = false;
			apos_x = apos_y = 0;
		}
		else if(scroll_down){
			pos.x += apos_x * 0.001;
			pos.y += apos_y * 0.001;
		}
		if(left_down && Element.HOVERED != null && cursor_moved0){
			Element.HOVERED.onDrag((float)apos_x, (float)apos_y);
			cursor_moved0 = false;
			apos_x = apos_y = 0;
		}
        processCameraInput(delta);
    }

	public static double cpos_x, cpos_y;//current cursor pos
	public static double opos_x = -1, opos_y = -1;//old cursor pos
	public static double apos_x, apos_y;//accumulator
	public static double spos_x, spos_y;//selection start pos
    public static boolean right_down;
	public static boolean left_down;
	public static boolean scroll_down;
	public static boolean grabbed;
	public static boolean cursor_moved0;
	//public static boolean cursor_moved1;
	public static long left_timer;

	public void mouseCallback(long window, int button, int action, int mods){
        if(button == 0){
			if(action == GLFW_PRESS){
				left_down = true;
				left_timer = 0;
				apos_x = apos_y = 0;
			}
			else if(action == GLFW_RELEASE){
				/*if(sel_panel != null){
					sp_pos = sel_panel.getPosition();
					sp_size = sel_panel.getSize();
					Picker.pick(PickType.POLYGON, PickTask.MULTISELECT, true);
				}//TODO
				else*/ if(!isOverUI()){
					if(TexturePainter.TOOL.active()){
						Picker.pick(TexturePainter.SELMODE.getPickType(), PickTask.PAINT, true);
					}
					else if(Picker.TYPE.color()) Picker.process();
					else Picker.pick(Selector.TYPE, PickTask.SELECT, true);
				}
				else{
					FMT.UI.click(cpos_x, cpos_y, 0);
				}
				left_down = false;
			}
        }
        else if(button == 1){
			if(isControlDown()){
				if(action == GLFW_PRESS) return;
				//PolySelMenu.show();
				return;
			}
			if(action == GLFW_PRESS){
				if(!isOverUI()){
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
					grabbed = true;
					apos_x = apos_y = 0;
				}
				right_down = true;
			}
			else if(action == GLFW_RELEASE){
				if(isOverUI()) FMT.UI.click(cpos_x, cpos_y, 1);
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				right_down = false;
				grabbed = false;
				//cursor_moved1 = false;
			}
        }
        if(button == 2){
			if(action == GLFW_PRESS){
				scroll_down = true;
				apos_x = apos_y = 0;
			}
			else if(action == GLFW_RELEASE){
				scroll_down = false;
			}
        }
	}

	public static void updateHoveredElement(){
		Element hov = grabbed ? null : FMT.UI.getElmAt(cpos_x, cpos_y);
		Element.HOVER_TIMER += FMT.delta;
		if(Element.HOVERED != null && Element.HOVERED != hov){
			Element.HOVERED.hovered(false);
			Element.HOVER_TIMER = 0;
		}
		if(hov == null){
			Element.HOVERED = null;
			Element.HOVER_TIMER = 0;
		}
		else{
			hov.hovered(true);
			Element.HOVERED = hov;
		}
	}

	//private Panel sel_panel;
	//private Vector2f sp_pos, sp_size;

	/*public Vector2f getSelPos(){
		return sp_pos;
	}

	public Vector2f getSelSiz(){
		return sp_size;
	}*/

	public void update(){
		if(left_down){
			if(isOverUI()) return;
			if(left_timer == 0){
				spos_x = cpos_x;
				spos_y = cpos_y;
			}
			if(left_timer > 5){
				/*if(sel_panel == null){
					sel_panel = new Panel((float)spos_x, (float)spos_y, 100, 100);
					sel_panel.getStyle().getBackground().setColor(ColorConstants.transparent());
					//sel_panel.getStyle().setBorder(new SimpleLineBorder(FMT.rgba(Settings.SELECTION_LINES.value), 1));
					//FMT.FRAME.getContainer().add(sel_panel);
				}
				sel_panel.setPosition((float)(spos_x < cpos_x ? spos_x : cpos_x), (float)(spos_y < cpos_y ? spos_y : cpos_y));
				sel_panel.setSize((float)(spos_x < cpos_x ? cpos_x - spos_x : spos_x - cpos_x), (float)(spos_y < cpos_y ? cpos_y - spos_y : spos_y - cpos_y));*/
				//TODO
			}
			left_timer++;
		}
		else{
			/*if(sel_panel != null){
				//FMT.FRAME.getContainer().remove(sel_panel);
				sel_panel = null;
			}*/
		}
	}

	public static boolean isOverUI(){
		return Element.HOVERED != null || Element.isSelectedAField();
	}

	public void cursorPosCallback(long window, double xpos, double ypos){
		if(opos_x == -1){
			opos_x = xpos;
			opos_y = ypos;
		}
		opos_x = cpos_x;
		opos_y = cpos_y;
		apos_x += xpos - cpos_x;
		apos_y += ypos - cpos_y;
		cpos_x = xpos;
		cpos_y = ypos;
		cursor_moved0 = /*cursor_moved1 =*/ opos_x != cpos_x || opos_y != cpos_y;
	}

	public void scrollCallback(long window, double xoffset, double yoffset){
		if(Element.HOVERED != null){
			Element.HOVERED.scroll(xoffset, yoffset);
			return;
		}
		double s = yoffset * Settings.SCROLL_SPEED.value;
		if(r_down) s *= 10;
		if(f_down) s *= 0.1;
        pos.x += s * Math.sin(hor);
        pos.y += s * Math.sin(ver);
        pos.z += s * Math.cos(hor);
	}

	public void toggleZoom(){
		if(isOverUI()) return;
		perspective(fov = ((zoomed = !zoomed) ? Settings.ZOOM_LEVEL.value : 45));
	}

	public void processCameraInput(float delta){
		if(Element.HOVERED != null){
			w_down = s_down = d_down = a_down = r_down = f_down = space_down = shift_down = false;
		}
        boolean front = w_down;
        boolean back  = s_down;
        boolean right = d_down;
        boolean left  = a_down;
        boolean speedp = r_down;
        boolean speedm = f_down;
        boolean up   = space_down;
        boolean down = shift_down;
        float nspeed, fbs;
        if(speedp) nspeed = Settings.MOVE_SPEED.value * 5;
        else if(speedm) nspeed = Settings.MOVE_SPEED.value / 2;
        else nspeed = Settings.MOVE_SPEED.value;
        nspeed *= delta;
		if(movemod != 1f) nspeed *= movemod;
		fbs = nspeed;
		if(FMT.MODEL.orient.rect()) nspeed = -nspeed;
        if(up) pos.y -= nspeed;
        if(down) pos.y += nspeed;
        if(back){
			pos.sub(new Vector3f(dir).mul(fbs, 0, fbs));
        }
        if(front){
			pos.add(new Vector3f(dir).mul(fbs, 0, fbs));
        }
        if(left){
			pos.add(new Vector3f(this.right).mul(nspeed, 0, nspeed));
        }
        if(right){
			pos.sub(new Vector3f(this.right).mul(nspeed, 0, nspeed));
        }
    }

	public static boolean isShiftDown(){
		return FMT.CAM.shift_down || isAltDown();
	}

	public static boolean isAltDown(){
		return FMT.CAM.left_alt_down || FMT.CAM.right_alt_down;
	}

	public static boolean isControlDown(){
		return FMT.CAM.left_control_down || FMT.CAM.right_control_down;
	}

	public static boolean parseKeyAction(int action){
		return action == GLFW_RELEASE ? false : action == GLFW_PRESS || action == GLFW_REPEAT;
	}

	public static int xCursor(){
		return (int)cpos_x;
	}

	public static int yCursor(){
		return (int)cpos_y;
	}

	public static float xCursorUI(){
		return (float)(cpos_x / Settings.UI_SCALE.value);
	}

	public static float yCursorUI(){
		return (float)(cpos_y / Settings.UI_SCALE.value);
	}

	public void reset(){
		boolean bool = FMT.MODEL.orient.rect();
		float y = bool ? 75 : -75;
		float z = bool ? 75 : -75;
		pos = new Vector3f(75, y, z);
		hor = bool ? -Static.rad135 : -Static.rad45;
		ver = bool ? -Static.rad30 : Static.rad30;
		movemod = 1f;
		w_down = s_down = a_down = d_down = false;
		space_down = shift_down = false;
	}

	public float fov(){
		return fov;
	}

	public void fov(float fov){
		this.fov = fov;
	}
    
}