package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Panel;
import com.spinyowl.legui.style.Style;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.env.PackDevEnv;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.ui.JsonEditor;
import net.fexcraft.app.fmt.ui.UVViewer;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.trees.PolygonTree;
import net.fexcraft.app.fmt.ui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.PolySelMenu;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.utils.Picker.PickTask;
import net.fexcraft.app.fmt.utils.Picker.PickType;
import net.fexcraft.lib.common.Static;

import java.util.ArrayList;

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
	public static double[] cursor_x = { 0 }, cursor_y = { 0 };
    
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
        FMT.pos.getTextState().setText(format(pos.x) + ", " + format(pos.y) + ", " + format(pos.z));
        FMT.rot.getTextState().setText(format(Math.toDegrees(hor)) + " / " + format(Math.toDegrees(ver)) + " : " + (int)fov);
        perspective(fov);
        ShaderManager.GENERAL.applyUniforms(prog -> {
			prog.use();
			def_view = glGetUniformLocation(prog.program(), "view");
			glUniformMatrix4fv(def_view, false, view.get(new float[16]));
			def_proj = glGetUniformLocation(prog.program(), "projection");
			glUniformMatrix4fv(def_proj, false, projection.get(new float[16]));
        });
    }

	public void resize(){
		perspective(fov);
	}

	public void perspective(float degree_fov){
		projection = new Matrix4f().perspective(Static.rad1 * fov, (float)FMT.WIDTH / FMT.HEIGHT, 0.1f, 1024f);
	}

	public void ortho(float scale){
		ShaderManager.UI.applyUniforms(prog -> {
			prog.use();
			def_view = glGetUniformLocation(prog.program(), "view");
			glUniformMatrix4fv(def_view, false, new Matrix4f().identity().get(new float[16]));
			def_proj = glGetUniformLocation(prog.program(), "projection");
			glUniformMatrix4fv(def_proj, false, new Matrix4f().ortho(0, FMT.WIDTH / scale, FMT.HEIGHT / scale, 0, -1000, 1000).get(new float[16]));
        });
	}

    public void pollInput(float delta){
		if(grabbed && cursor_moved0){
			if(FMT.MODEL.orient.rect()){
				hor -= (posx - oposx) * Settings.MOUSE_SENSIVITY.value * delta * 0.005;
				ver -= (posy - oposy) * Settings.MOUSE_SENSIVITY.value * delta * 0.005;
			}
			else{
				hor += (posx - oposx) * Settings.MOUSE_SENSIVITY.value * delta * 0.005;
				ver += (posy - oposy) * Settings.MOUSE_SENSIVITY.value * delta * 0.005;
			}
            ver = Math.max(-maxVR, Math.min(maxVR, ver));
            cursor_moved0 = false;
		}
		else if(scroll_down){
			pos.x += (posx - oposx) * 0.001;
			pos.y += (posy - oposy) * 0.001;
		}
		else if(Arrows.SEL > 0 && cursor_moved0){
			float f0 = (float)(posx - oposx) * Settings.ARROW_SENSIVITY.value * delta;
			float f1 = (float)(posy - oposy) * Settings.ARROW_SENSIVITY.value * delta;
			Arrows.process(Math.abs((f0 + f1) * 0.5f));
            cursor_moved0 = false;
		}
        processCameraInput(delta);
    }

    public static double posx, posy, oposx = -1, oposy = -1;
    public static boolean right_down;
	public static boolean left_down;
	public static boolean scroll_down;
	public static boolean grabbed;
	public static boolean cursor_moved0;
	public static boolean cursor_moved1;
	public static long left_timer;

	public void mouseCallback(long window, int button, int action, int mods){
        if(button == 0){
			if(action == GLFW_PRESS){
				left_down = true;
				left_timer = 0;
			}
			else if(action == GLFW_RELEASE){
				if(Arrows.SEL > 0){
					Arrows.SEL = 0;
				}
				else if(sel_panel != null){
					sp_pos = sel_panel.getPosition();
					sp_size = sel_panel.getSize();
					Picker.pick(PickType.POLYGON, PickTask.MULTISELECT, true);
				}
				else if(Settings.TESTING.value || !isOverUI()){
					if(TexturePainter.TOOL.active()){
						Picker.pick(TexturePainter.SELMODE.getPickType(), PickTask.PAINT, true);
					}
					else if(Picker.TYPE.color()) Picker.process();
					else Picker.pick(Settings.TESTING.value ? PickType.UI : Selector.TYPE, PickTask.SELECT, true);
				}
				left_down = false;
			}
        }
        else if(button == 1){
			if(Arrows.SEL > 0){
				if(action == GLFW_RELEASE) Arrows.DIR = !Arrows.DIR;
				return;
			}
			if(isControlDown()){
				if(action == GLFW_PRESS) return;
				PolySelMenu.show();
				return;
			}
			if(action == GLFW_PRESS){
				if(!isOverUI()){
					glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
					grabbed = true;
				}
				right_down = true;
			}
			else if(action == GLFW_RELEASE){
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				right_down = false;
				grabbed = false;
				cursor_moved1 = false;
			}
        }
        if(button == 2){
			if(action == GLFW_PRESS){
				scroll_down = true;
			}
			else if(action == GLFW_RELEASE){
				scroll_down = false;
			}
        }
	}

	private Panel sel_panel;
	private Vector2f sp_pos, sp_size;
	private double cx, cy;

	public Vector2f getSelPos(){
		return sp_pos;
	}

	public Vector2f getSelSiz(){
		return sp_size;
	}

	public void update(){
		if(left_down){
			if(isOverUI()) return;
			if(left_timer == 0){
				cx = posx;
				cy = posy;
			}
			if(left_timer > 5){
				if(sel_panel == null){
					sel_panel = new Panel((float)cx, (float)cy, 100, 100);
					sel_panel.getStyle().getBackground().setColor(ColorConstants.transparent());
					sel_panel.getStyle().setBorder(new SimpleLineBorder(FMT.rgba(Settings.SELECTION_LINES.value), 1));
					FMT.FRAME.getContainer().add(sel_panel);
				}
				sel_panel.setPosition((float)(cx < posx ? cx : posx), (float)(cy < posy ? cy : posy));
				sel_panel.setSize((float)(cx < posx ? posx - cx : cx - posx), (float)(cy < posy ? posy - cy : cy - posy));
			}
			left_timer++;
		}
		else{
			if(sel_panel != null){
				FMT.FRAME.getContainer().remove(sel_panel);
				sel_panel = null;
			}
		}
	}

	public static boolean isOverUI(){
		if(FMT.FRAME.getLayers().size() > 0) return true;
		glfwGetCursorPos(FMT.INSTANCE.window, cursor_x, cursor_y);
		if(cursor_y[0] < FMT.TOOLBAR.getSize().y) return true;
		if(Editor.VISIBLE_EDITOR != null){
			if(cursor_x[0] < Editor.WIDTH) return true;
			if(EditorPanel.isOverPanel(cursor_x[0], cursor_y[0])) return true;
		}
		if(Editor.VISIBLE_TREE != null && cursor_x[0] > FMT.WIDTH - Editor.WIDTH) return true;
		if(WorkspaceViewer.viewer != null && WorkspaceViewer.viewer.getStyle().getDisplay() != Style.DisplayType.NONE && overComponent(WorkspaceViewer.viewer)) return true;
		if(PackDevEnv.INSTANCE != null && PackDevEnv.INSTANCE.getStyle().getDisplay() != Style.DisplayType.NONE && overComponent(PackDevEnv.INSTANCE)) return true;
		for(FVTMConfigEditor editor : FVTMConfigEditor.INSTANCES){
			if(overComponent(editor)) return true;
		}
		for(JsonEditor editor : JsonEditor.INSTANCES){
			if(overComponent(editor)) return true;
		}
		if(UVViewer.visible()){
			if(cursor_x[0] >= UVViewer.pos().x && cursor_x[0] <= UVViewer.pos().x + UVViewer.size().x){
				if(cursor_y[0] >= UVViewer.pos().y && cursor_y[0] <= UVViewer.pos().y + UVViewer.size().y) return true;
			}
		}
		return false;
	}

	private static boolean overComponent(Component comp){
		if(cursor_x[0] >= comp.getPosition().x && cursor_x[0] <= comp.getPosition().x + comp.getSize().x){
			if(cursor_y[0] >= comp.getPosition().y && cursor_y[0] <= comp.getPosition().y + comp.getSize().y) return true;
		}
		return false;
	}

	public void cursorPosCallback(long window, double xpos, double ypos){
		if(oposx == -1 || oposy == -1){
			oposx = xpos;
			oposy = posy;
		}
		oposx = posx;
		oposy = posy;
		posx = xpos;
		posy = ypos;
		cursor_moved0 = cursor_moved1 = oposx != posx || oposy != posy;
	}

	public void scrollCallback(long window, double xoffset, double yoffset){
		if(isOverUI()){
			if(Editor.VISIBLE_TREE != Editor.POLYGON_TREE || cursor_x[0] < FMT.WIDTH - Editor.WIDTH) return;
			if(!PolygonTree.SORT_MODE) return;
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			if(groups.isEmpty()) return;
			int dir = yoffset > 0 ? -1 : 1;
			if(dir > 0){
				for(int i = groups.size() - 1; i >= 0; i--){
					FMT.MODEL.swap(groups.get(i), dir, false);
				}
			}
			else{
				for(Group group : groups){
					FMT.MODEL.swap(group, dir, false);
				}
			}
			Editor.POLYGON_TREE.reAddGroups();
			Editor.ANIM_TREE.reAddGroups();
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
		if(FMT.CONTEXT.getFocusedGui() != null){
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

	public static int mousePosX(){
		return (int)posx;
	}

	public static int mousePosY(){
		return (int)posy;
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

	private String format(double n){
		return NumberField.getFormat().format(n);
	}
    
}