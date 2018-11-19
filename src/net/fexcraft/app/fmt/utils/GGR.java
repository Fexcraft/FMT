package net.fexcraft.app.fmt.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.FMTGLProcess;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.ui.tree.RightTree;
import net.fexcraft.lib.common.math.Vec3f;

/** CCR */
public class GGR {
	
    public float movespeed = 1.5f;//0.5f;
    public float maxlookrange = 85;
    public float sensivity = 1.0f;//= 0.05f;
    public Vec3f pos, rotation;
    private final FMTGLProcess root;
    
    public GGR(FMTGLProcess root, int x, int y, int z){
        pos = new Vec3f(x, y, z); this.root = root;
        rotation = new Vec3f(0, 0, 0);
    }
    
    public GGR(FMTGLProcess root, float x, float y, float z){
        pos = new Vec3f(x, y, z); this.root = root;
        rotation = new Vec3f(0, 0, 0);
    }

    public void apply(){
        if(rotation.yCoord / 360 > 1){ rotation.yCoord -= 360; }
        else if(rotation.yCoord / 360 < -1){ rotation.yCoord += 360; }
        GL11.glLoadIdentity();
        GL11.glRotatef(rotation.xCoord, 1, 0, 0);
        GL11.glRotatef(rotation.yCoord, 0, 1, 0);
        GL11.glRotatef(rotation.zCoord, 0, 0, 1);
        GL11.glTranslatef(-pos.xCoord, -pos.yCoord, -pos.zCoord);
    }

    public void pollInput(float delta){
        acceptMouseInput(delta);
        if(!TextField.anySelected()) acceptInputMove(delta);
        acceptInputKeyboard();
    }
    
    private void acceptInputKeyboard(){
    	while(Keyboard.next()){
    		int key = Keyboard.getEventKey();
    		if(Keyboard.getEventKeyState()){//"pressed"
    	        if(TextField.anySelected()){
    	        	TextField field = TextField.getSelected();
    	        	if(field != null){
    	        		for(int i = 2; i < 12; i++){
    	        			if(key == i) field.onInput(key, getKeyName(i));
    	        		}
    	        		for(int i = 16; i < 26; i++){
    	        			if(key == i) field.onInput(key, getKeyName(i));
    	        		}
    	        		for(int i = 30; i < 39; i++){
    	        			if(key == i) field.onInput(key, getKeyName(i));
    	        		}
    	        		for(int i = 44; i < 51; i++){
    	        			if(key == i) field.onInput(key, getKeyName(i));
    	        		}
    	        		if(key == Keyboard.KEY_BACK){
    	        			field.onBackSpace();
    	        		}
    	        		if(key == Keyboard.KEY_RETURN){
    	        			field.onReturn();
    	        		}
    	        		if(key == Keyboard.KEY_MINUS){
    	        			field.onInput(key, "-");
    	        		}
    	        		if(key == Keyboard.KEY_PERIOD){
    	        			field.onInput(key, ".");
    	        		}
    	        		if(key == Keyboard.KEY_SPACE){
    	        			field.onInput(key, " ");
    	        		}
    	        	}
    	        }
    	        else{
    	            if(key == Keyboard.KEY_F1){ /*//TODO help UI*/ }
    	            if(key == Keyboard.KEY_F2){ Settings.toggleFloor(); }
    	            if(key == Keyboard.KEY_F3){ Settings.toggleLines(); }
    	            if(key == Keyboard.KEY_F4){ Settings.toggleCube(); }
    	            if(key == Keyboard.KEY_F5){ Settings.toggleDemo(); }
    	            if(key == Keyboard.KEY_F6){ Settings.togglePolygonMarker(); }
    	            //
    	            if(key == Keyboard.KEY_1){ Editor.toggle("general_editor", false); }
    	            if(key == Keyboard.KEY_2){ Editor.toggle("shapebox_editor", false); }
    	            if(key == Keyboard.KEY_3){ Editor.toggle("cylinder_editor", false); }
    	            if(key == Keyboard.KEY_4){ Editor.toggle("group_editor", false); }
    	            if(key == Keyboard.KEY_5){ Editor.toggle("model_editor", false); }
    	            if(key == Keyboard.KEY_F11){
    	            	try{ Display.setFullscreen(Settings.toogleFullscreen()); }
    	    			catch(Exception ex){ ex.printStackTrace(); }
    	            }
    	            //
    	            if(key == Keyboard.KEY_LEFT){ this.rotation.yCoord += 15; }
    	            if(key == Keyboard.KEY_RIGHT){ this.rotation.yCoord -= 15; }
    	            if(key == Keyboard.KEY_UP){ this.rotation.xCoord += 15; }
    	            if(key == Keyboard.KEY_DOWN){ this.rotation.xCoord -= 15; }
    	            //
    	            if(key == Keyboard.KEY_SYSRQ || key == Keyboard.KEY_F12){
    	            	ImageHelper.takeScreenshot(false);
    	            	FMTB.showDialogbox("Screenshot taken.", "", "OK", "Open", DialogBox.NOTHING, () -> {
    	            		try{ Desktop.getDesktop().open(new File("./screenshots/")); }
    	            		catch(IOException e){ e.printStackTrace(); }
    	            	});
    	            }
    	        }
    		}
    		else{//"released"
    			
    		}
    	}
	}

	private String getKeyName(int i){
		return GGR.isShiftDown() ? Keyboard.getKeyName(i) : Keyboard.getKeyName(i).toLowerCase();
	}

	private boolean clickedL, clickedR, panning;
    private int wheel, oldMouseX=-1,oldMouseY=-1;

    public void acceptMouseInput(float delta){
        if(clickedR && !Mouse.isButtonDown(1)){
            Mouse.setGrabbed(false);//fix mouse grab sticking
        }
        if(Mouse.isGrabbed()){
            rotation.yCoord += Mouse.getDX() * sensivity * delta;
            rotation.xCoord += -Mouse.getDY() * sensivity * delta;
            rotation.xCoord = Math.max(-maxlookrange, Math.min(maxlookrange, rotation.xCoord));
        }
        else{
        	if(!Mouse.isInsideWindow()) return;
        	if(Mouse.isButtonDown(0) && !clickedL) root.getUserInterface().onButtonPress(0); clickedL = Mouse.isButtonDown(0);
        	if(Mouse.isButtonDown(1) && !clickedR) root.getUserInterface().onButtonPress(1); clickedR = Mouse.isButtonDown(1);
        	if((wheel = Mouse.getDWheel()) != 0){
        		if(!root.getUserInterface().onScrollWheel(wheel)){
                    double[] zoom = rotatePoint(wheel * 0.005f, rotation.xCoord, rotation.yCoord - 90);
                    pos.xCoord += zoom[0]; pos.yCoord += zoom[1]; pos.zCoord += zoom[2];
        		}
        	}
        }
        //
        if((Mouse.isInsideWindow() && /*Keyboard.isKeyDown(Keyboard.KEY_E) ||*/ Mouse.isButtonDown(1) && !RightTree.anyTreeHovered())){
            Mouse.setGrabbed(true);
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            root.reset(); Mouse.setGrabbed(false);
        }
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

    public void acceptInputMove(float delta){
        if(Mouse.isButtonDown(2)){
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
        }
        //
    	//if(!Mouse.isGrabbed()) return;
        if(RightTree.anyTreeHovered()) return;
        boolean front = Keyboard.isKeyDown(Keyboard.KEY_W);
        boolean back = Keyboard.isKeyDown(Keyboard.KEY_S);
        boolean right = Keyboard.isKeyDown(Keyboard.KEY_D);
        boolean left = Keyboard.isKeyDown(Keyboard.KEY_A);
        boolean speedp = Keyboard.isKeyDown(Keyboard.KEY_R);
        boolean speedm = Keyboard.isKeyDown(Keyboard.KEY_F);
        boolean up = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
        boolean down = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        float nspeed;
        if(speedp) nspeed = movespeed * 5;
        else if(speedm) nspeed = movespeed / 2;
        else nspeed = movespeed;
        nspeed *= delta;
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
		return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
	}
    
}