package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class RayCoastAway {
	
	public static boolean PICKING, MOUSEOFF;
	private static ByteBuffer buffer;
	static { buffer = ByteBuffer.allocateDirect(4); buffer.order(ByteOrder.nativeOrder()); }
	
	public static void doTest(boolean bool){
		doTest(bool, MOUSEOFF);
	}
	
	public static void doTest(boolean bool, boolean mouseoff){
		if(!Settings.rayPicking()) return; if(bool && !PICKING){ PICKING = true; MOUSEOFF = mouseoff; return; }
		//
		int width = FMTB.get().getDisplayMode().getWidth(), height = FMTB.get().getDisplayMode().getHeight();
		if(mouseoff){ width = Mouse.getX() * 2; height = Mouse.getY() * 2; }
		GL11.glReadPixels(width / 2, height / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		byte[] byteArray = new byte[4]; buffer.get(byteArray);
		//Print.console((((int) byteArray[0]) & 0xFF) + " " + (((int) byteArray[1]) & 0xFF) + " "  + (((int) byteArray[2]) & 0xFF));
		int id = new Color(((int) byteArray[0]) & 0xFF, ((int) byteArray[1]) & 0xFF, ((int) byteArray[2]) & 0xFF).getRGB() + 16777216;
		//Print.console(id + "-ID");
		buffer.clear(); PICKING = false; MOUSEOFF = false;
		PolygonWrapper wrapper = getSelected(id);
		if(wrapper == null) return;
		if(!TextureEditor.BUCKETMODE){
			boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
			boolean state = control ? wrapper.getList().selected : wrapper.selected;
			if(!Keyboard.isKeyDown(Keyboard.KEY_LMENU)) FMTB.MODEL.clearSelection();
			if(control){ wrapper.getList().selected = !state; }
			else{ wrapper.selected = !state; }
			FMTB.MODEL.updateFields();
		}
		else{
			Texture tex;
			if(FMTB.MODEL.texture == null || (tex = TextureManager.getTexture(FMTB.MODEL.texture, true)) == null){
				FMTB.showDialogbox("No Texture loaded.", "Cannot use Paint Bucket.", "ok", "toggle off", DialogBox.NOTHING, () -> { TextureEditor.toggleBucketMode(null); });
				return;
			}
			if(TextureEditor.PMODE == TextureEditor.PaintMode.GROUP){
				boolean rebind = false; TurboList list = wrapper.getList();
				for(PolygonWrapper poly : list){
					if(poly.burnToTexture(tex.getImage(), -1)){ rebind = true; }
				}
				if(rebind){
					tex.rebind(); TextureManager.saveTexture(FMTB.MODEL.texture);
				}
			}
			else{
				if(wrapper.burnToTexture(tex.getImage(), TextureEditor.PMODE == TextureEditor.PaintMode.POLYGON ? -1 : getSelectedFace(wrapper, id))){
					tex.rebind(); TextureManager.saveTexture(FMTB.MODEL.texture);
				}
			}
		}
	}

	private static int getSelectedFace(PolygonWrapper wrapper, int id){
		for(int i = 0; i < wrapper.color.length; i++) if(wrapper.color[i] == id) return i; return -1;
	}

	private static PolygonWrapper getSelected(int id){
		for(TurboList list : FMTB.MODEL.getCompound().values()){
			for(PolygonWrapper wrapper : list){
				if(wrapper.color == null) continue;
				for(int col : wrapper.color){
					if(col == id) return wrapper;
				}
			}
		} return null;
	}

}