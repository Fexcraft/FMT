package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.field.Field;
import net.fexcraft.lib.common.math.Vec3f;

/** CCR */
public class GGR {
	
    public float movemod = 1;
    public float maxlookrange = 85;
    public Vec3f pos, rotation;
    //
    public boolean w_down, s_down, d_down, a_down, r_down, f_down, space_down, shift_down, alt_down, control_down;
    
    public GGR(float x, float y, float z){
        pos = new Vec3f(x, y, z); rotation = new Vec3f(0, 0, 0);
    }

    public void apply(){
        if(rotation.yCoord / 360 > 1f){ rotation.yCoord -= 360; }
        else if(rotation.yCoord / 360 < -1f){ rotation.yCoord += 360; }
        GL11.glLoadIdentity();
        GL11.glRotatef(rotation.xCoord, 1, 0, 0);
        GL11.glRotatef(rotation.yCoord, 0, 1, 0);
        GL11.glRotatef(rotation.zCoord, 0, 0, 1);
        GL11.glTranslatef(-pos.xCoord, -pos.yCoord, -pos.zCoord);
    }

    public void pollInput(float delta){
		if(grabbed && cursor_moved){
            rotation.yCoord += (posx - oposx) * Settings.mouse_sensivity.directFloat() * delta;
            rotation.xCoord += (posy - oposy) * Settings.mouse_sensivity.directFloat() * delta;
            rotation.xCoord = Math.max(-maxlookrange, Math.min(maxlookrange, rotation.xCoord));
            cursor_moved = false;
		}
		else if(scroll_down && cursor_moved){
	        pos.xCoord += (posx - oposx) * 0.001;
	        pos.yCoord += (posy - oposy) * 0.001;
	        cursor_moved = false;
	    }
        processCameraInput(delta);
    }

    public static double posx, posy, oposx = -1, oposy = -1;
    public static boolean right_down, left_down, scroll_down, grabbed, cursor_moved;
    
	public void mouseCallback(long window, int button, int action, int mods){
        if(button == 0){
        	if(action == GLFW_PRESS){
        		left_down = true;
        	}
        	else if(action == GLFW_RELEASE){
        		if(FMTB.context.getFocusedGui() == null){
        			RayCoastAway.doTest(true, true);
        		}
        		left_down = false;
        	}
        }
        else if(button == 1){
        	if(action == GLFW_PRESS){
        		if(FMTB.context.getFocusedGui() instanceof Field == false){
            		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            		grabbed = true;
        		}
        		right_down = true;
        	}
        	else if(action == GLFW_RELEASE){
        		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        		right_down = false; grabbed = false;
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

	public void cursorPosCallback(long window, double xpos, double ypos){
		if(oposx == -1 || oposy == -1){ oposx = xpos; oposy = posy; }
		oposx = posx; oposy = posy; posx = xpos; posy = ypos; cursor_moved = true;
	}

	public void scrollCallback(long window, double xoffset, double yoffset){
		double[] zoom = rotatePoint(yoffset * 0.5f, rotation.xCoord, rotation.yCoord - 90);
        pos.xCoord += zoom[0]; pos.yCoord += zoom[1]; pos.zCoord += zoom[2];
	}

    public static double[] rotatePoint(double f, float pitch, float yaw) {
        double[] xyz = new double[]{f,0,0};
            pitch *= 0.01745329251;
            xyz[1] = -(f * Math.sin(pitch));
            //
            yaw *= 0.01745329251;
            xyz[0] = (f * Math.cos(yaw));
            xyz[2] = (f * Math.sin(yaw));
        return xyz;
    }

    public void processCameraInput(float delta){
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
		return FMTB.ggr.shift_down || FMTB.ggr.alt_down;
	}

	public static boolean iControlDown(){
		return FMTB.ggr.control_down;
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
    
}