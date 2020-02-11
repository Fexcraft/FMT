package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterface;
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
    	processMouseInput(delta);
        processMoveInput(delta);
        //acceptInputKeyboard();
    }

	public static boolean clickedL, clickedR; private boolean panning, dragging;
    private int wheel, oldMouseX =- 1, oldMouseY = -1;

    public void processMouseInput(float delta){
        /*if(clickedR && !Mouse.isButtonDown(1)){
            Mouse.setGrabbed(false);//fix mouse grab sticking
        }
        if(clickedL && !Mouse.isButtonDown(0)){
            UserInterface.DRAGGED = null; dragging = true;
        }
        if(Mouse.isGrabbed()){*/
            //TODO rotation.yCoord += FMTB.cdiffx * Settings.mouse_sensivity.directFloat() * delta;
            //TODO rotation.xCoord += -FMTB.cdiffy * Settings.mouse_sensivity.directFloat() * delta;
            //TODO rotation.xCoord = Math.max(-maxlookrange, Math.min(maxlookrange, rotation.xCoord));
            //FMTB.cdiffx = FMTB.cdiffy = 0;
            //
        	//if(Mouse.isButtonDown(0) && !clickedL) RayCoastAway.doTest(true, false); clickedL = Mouse.isButtonDown(0);
        /*}
        else{
        	if(!Mouse.isInsideWindow()) return;
        	if(Mouse.isButtonDown(0) && !clickedL) root.UI.onButtonPress(0); clickedL = Mouse.isButtonDown(0);
        	if(Mouse.isButtonDown(1) && !clickedR) root.UI.onButtonPress(1); clickedR = Mouse.isButtonDown(1);
        	if((wheel = Mouse.getDWheel()) != 0){
        		if(!root.UI.onScrollWheel(wheel)){
                    double[] zoom = rotatePoint(wheel * 0.005f, rotation.xCoord, rotation.yCoord - 90);
                    pos.xCoord += zoom[0]; pos.yCoord += zoom[1]; pos.zCoord += zoom[2];
        		}
        	}
        }
        //
        if(Mouse.isInsideWindow()){
        	if(Mouse.isButtonDown(1) && !RightTree.anyTreeHovered() && !UserInterface.RIGHTMENU.visible()){
        		Mouse.setGrabbed(true);
        	}
        	if(Mouse.isButtonDown(0) && dragging){
        		if(UserInterface.DRAGGED != null){
        			UserInterface.DRAGGED.pullBy(Mouse.getDX(), -Mouse.getDY());
        		}
        		else{
        			root.UI.getDraggableElement();
        			if(UserInterface.DRAGGED == null) dragging = false;
        		}
        	}
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !ControlsAdjuster.CATCHING){
        	if(Mouse.isGrabbed()){ Mouse.setGrabbed(false); return; }
            root.reset(true); Mouse.setGrabbed(false); TextureEditor.reset();
        }*/
    }
    
    public static void resetDragging(){
    	UserInterface.DRAGGED = null; FMTB.ggr.dragging = false;
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

    public void processMoveInput(float delta){
        /*if(Mouse.isButtonDown(2)){
            if(oldMouseX == -1){
                oldMouseX = Mouse.getX();
                oldMouseY = Mouse.getY();
            }
            //offset it because if we do it every frame it doesn't always work, but every few frames is fine.
            pos.xCoord += (Mouse.getX() - oldMouseX) * 0.001;
            pos.yCoord += (Mouse.getY() - oldMouseY) * 0.001;
            panning = true;
        }
        else if(panning){
            oldMouseX =- 1; panning = false;
        }*/
        //
    	//if(!Mouse.isGrabbed()) return;
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
    
}