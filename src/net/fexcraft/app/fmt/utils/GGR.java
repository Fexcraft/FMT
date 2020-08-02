package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.MenuEntry;
import net.fexcraft.app.fmt.ui.TexViewBox;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;

/** CCR */
public class GGR {
	
    public float movemod = 1;
    public float maxlookrange = 85;
    public Vec3f pos, rotation, orbit;
    public Axis3DL orbital = new Axis3DL();
    public float distance;
    //
    public boolean w_down, s_down, d_down, a_down, r_down, f_down, space_down, shift_down;
    public boolean left_alt_down, left_control_down, right_alt_down, right_control_down;
    
    public GGR(float x, float y, float z){
        pos = new Vec3f(x, y, z);
        rotation = new Vec3f(0, 0, 0);
        orbit = new Vec3f(0, 0, 0);
    }

    public void apply(){
        if(rotation.yCoord / 360 > 1f){ rotation.yCoord -= 360; }
        else if(rotation.yCoord / 360 < -1f){ rotation.yCoord += 360; }
        GL11.glLoadIdentity();
        if(Settings.orbital_camera()){
            Vec3f vec = orbital.getRelativeVector(0, 0, distance);
            vec.xCoord += -orbit.xCoord;
            vec.yCoord += -orbit.yCoord;
            vec.zCoord += -orbit.zCoord;
            GL11.glRotatef(-rotation.xCoord, 1, 0, 0);
            GL11.glRotatef(-rotation.yCoord, 0, 1, 0);
            GL11.glRotatef(-rotation.zCoord, 0, 0, 1);
            if(Settings.oldrot()) GL11.glRotatef(-180, 1, 0, 0);
            GL11.glTranslatef(vec.xCoord, vec.yCoord, vec.zCoord);
            if(Settings.oldrot()) GL11.glRotatef(180, 1, 0, 0);
        }
        else{
            GL11.glRotatef(rotation.xCoord, 1, 0, 0);
            GL11.glRotatef(rotation.yCoord, 0, 1, 0);
            GL11.glRotatef(rotation.zCoord, 0, 0, 1);
            GL11.glTranslatef(-pos.xCoord, -pos.yCoord, -pos.zCoord);
        }
    }

    public void pollInput(float delta){
		if(grabbed && cursor_moved){
        	double x = posx - oposx, y = posy - oposy;
        	if(x < FMTB.WIDTH && y < FMTB.HEIGHT){
                if(Settings.orbital_camera()){
                    rotation.yCoord -= x * Settings.mouse_sensivity.directFloat() * delta;
                    rotation.xCoord += y * Settings.mouse_sensivity.directFloat() * delta;
                    rotation.xCoord = Math.max(-maxlookrange, Math.min(maxlookrange, rotation.xCoord));
                	orbital.setAngles(rotation.yCoord, 0, -rotation.xCoord);
                }
                else{
                    rotation.yCoord += x * Settings.mouse_sensivity.directFloat() * delta;
                    rotation.xCoord += y * Settings.mouse_sensivity.directFloat() * delta;
                    rotation.xCoord = Math.max(-maxlookrange, Math.min(maxlookrange, rotation.xCoord));
                }
        	}
            cursor_moved = false;
		}
		else if(scroll_down && cursor_moved){
	        pos.xCoord += (posx - oposx) * 0.001;
	        pos.yCoord += (posy - oposy) * 0.001;
	        cursor_moved = false;
	    }
        processCameraInput(delta);
        if(left_down && TextureEditor.pixelMode() && Settings.dragPainting() && isNotOverUI()){
        	RayCoastAway.doTest(true, true, false);
        }
    }

    public static double posx, posy, oposx = -1, oposy = -1;
    public static boolean right_down, left_down, scroll_down, grabbed, cursor_moved;
    
	public void mouseCallback(long window, int button, int action, int mods){
        if(button == 0){
        	if(action == GLFW_PRESS){
        		left_down = true;
        	}
        	else if(action == GLFW_RELEASE){
        		if(isNotOverUI()){
        			RayCoastAway.doTest(true, true, false);
        		}
        		left_down = false;
        	}
        }
        else if(button == 1){
        	if(action == GLFW_PRESS){
        		if(isNotOverUI()){
            		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            		grabbed = true;
        		}
        		right_down = true;
        	}
        	else if(action == GLFW_RELEASE){
        		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        		right_down = false;
        		grabbed = false;
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

	public static boolean isNotOverUI(){
		if(FMTB.frame.getLayers().size() > 0) return false;
		double[] x = { 0 }, y = { 0 };
		glfwGetCursorPos(FMTB.window, x, y);
		if(y[0] < 30) return false;
		if(Editors.anyVisible() && x[0] < 304) return false;
		if(Trees.anyVisible() && x[0] > (FMTB.WIDTH - 304)) return false;
		if(MenuEntry.anyHovered()) return false;
		if(TexViewBox.isOpen()){
			if(x[0] >= TexViewBox.pos().x && x[0] < TexViewBox.pos().x + TexViewBox.size().x){
				if(y[0] >= TexViewBox.pos().y && y[0] < TexViewBox.pos().y + TexViewBox.size().y) return false;
			}
		}
		return true;
	}

	public void cursorPosCallback(long window, double xpos, double ypos){
		if(oposx == -1 || oposy == -1){ oposx = xpos; oposy = posy; }
		oposx = posx; oposy = posy; posx = xpos; posy = ypos; cursor_moved = true;
	}

	public void scrollCallback(long window, double xoffset, double yoffset){
		if(posy > 30 && ((posx < 304 && Editors.anyVisible()) || (posx > FMTB.WIDTH - 304 && Trees.anyVisible()))) return;
		if(Settings.orbital_camera()){
			distance -= yoffset * (movemod / 2);
			return;
		}
		double[] zoom = rotatePoint(yoffset * 0.5f, rotation.xCoord, rotation.yCoord - 90);
        pos.xCoord += zoom[0]; pos.yCoord += zoom[1]; pos.zCoord += zoom[2];
	}

    public static double[] rotatePoint(double f, float pitch, float yaw) {
        double[] xyz = new double[]{ f, 0, 0 };
        pitch *= 0.01745329251;
        xyz[1] = -(f * Math.sin(pitch));
        //
        yaw *= 0.01745329251;
        xyz[0] = (f * Math.cos(yaw));
        xyz[2] = (f * Math.sin(yaw));
        return xyz;
    }

    public void processCameraInput(float delta){
    	if(!isNotOverUI()){
    		w_down = s_down = d_down = a_down = r_down = f_down = space_down = shift_down = false;
    	}
		if(Settings.orbital_camera()){
			if(Settings.center_on_part()){
				PolygonWrapper wrapper = FMTB.MODEL.getFirstSelection();
				if(wrapper != null){
					orbit.xCoord = wrapper.pos.xCoord * Static.sixteenth;
					orbit.yCoord = wrapper.pos.yCoord * Static.sixteenth;
					orbit.zCoord = wrapper.pos.zCoord * Static.sixteenth;
				}
				else{
					orbit.xCoord = orbit.yCoord = orbit.zCoord = 0;
				}
			}
			return;
		}
        boolean front = w_down;
        boolean back  = s_down;
        boolean right = d_down;
        boolean left  = a_down;
        boolean speedp = r_down;
        boolean speedm = f_down;
        boolean up   = space_down;
        boolean down = shift_down;
        float nspeed;
        if(speedp) nspeed = Settings.movespeed.directFloat() * 5;
        else if(speedm) nspeed = Settings.movespeed.directFloat() / 2;
        else nspeed = Settings.movespeed.directFloat();
        nspeed *= delta; if(movemod != 1f) nspeed *= movemod;
        if(up) pos.yCoord += nspeed;
        if(down) pos.yCoord -= nspeed;
        if(back){
            pos.xCoord -= Math.sin(Math.toRadians(rotation.yCoord)) * nspeed;
            pos.zCoord += Math.cos(Math.toRadians(rotation.yCoord)) * nspeed;
        }
        if(front){
            pos.xCoord += Math.sin(Math.toRadians(rotation.yCoord)) * nspeed;
            pos.zCoord -= Math.cos(Math.toRadians(rotation.yCoord)) * nspeed;
        }
        if(left){
            pos.xCoord += Math.sin(Math.toRadians(rotation.yCoord - 90)) * nspeed;
            pos.zCoord -= Math.cos(Math.toRadians(rotation.yCoord - 90)) * nspeed;
        }
        if(right){
            pos.xCoord += Math.sin(Math.toRadians(rotation.yCoord + 90)) * nspeed;
            pos.zCoord -= Math.cos(Math.toRadians(rotation.yCoord + 90)) * nspeed;
        }
    }

	public static boolean isShiftDown(){
		return FMTB.ggr.shift_down || isAltDown();
	}

	public static boolean isAltDown(){
		return FMTB.ggr.left_alt_down || FMTB.ggr.right_alt_down;
	}

	public static boolean isControlDown(){
		return FMTB.ggr.left_control_down || FMTB.ggr.right_control_down;
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
		pos = new Vec3f(0, 4, 4);
		rotation = new Vec3f(45, 0, 0);
		orbit = new Vec3f(0, 0, 0);
		movemod = 1f;
		w_down = s_down = a_down = d_down = space_down = shift_down = false;
	}
    
}