package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.utils.Print;

public class RayCoastAway {
	
	public static boolean PICKING;
	private static ByteBuffer buffer;
	//
	public static void setup(){
		buffer = ByteBuffer.allocateDirect(4); buffer.order(ByteOrder.nativeOrder());
	}
	
	public static void doTest(boolean bool){
		if(!Settings.rayPicking()) return; if(bool && !PICKING){ PICKING = true; return; }
		//
		int width = FMTB.get().getDisplayMode().getWidth(), height = FMTB.get().getDisplayMode().getHeight();
		GL11.glReadPixels(width / 2, height / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		byte[] byteArray = new byte[4]; buffer.get(byteArray);
		Print.console((((int) byteArray[0]) & 0xFF) + " " + (((int) byteArray[1]) & 0xFF) + " "  + (((int) byteArray[2]) & 0xFF));
		int id = new Color(((int) byteArray[0]) & 0xFF, ((int) byteArray[1]) & 0xFF, ((int) byteArray[2]) & 0xFF).getRGB() + 16777216;
		Print.console(id + "-ID"); buffer.clear(); PICKING = false;
		for(TurboList list : FMTB.MODEL.getCompound().values()){
			for(PolygonWrapper wrapper : list){
				if(wrapper.color == id){
					boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
					boolean state = control ? wrapper.getList().selected : wrapper.selected;
					if(!Keyboard.isKeyDown(Keyboard.KEY_LMENU)) FMTB.MODEL.clearSelection();
					if(control){ wrapper.getList().selected = !state; }
					else{ wrapper.selected = !state; }
					FMTB.MODEL.updateFields();
				}
			}
		}
	}

}