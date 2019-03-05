package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.utils.Print;

public class RayCoastAway {
	
	public static boolean PICKING, MOUSEOFF;
	private static ByteBuffer buffer;
	static { buffer = ByteBuffer.allocateDirect(4); buffer.order(ByteOrder.nativeOrder()); }
	
	public static void doTest(boolean bool){
		doTest(bool, MOUSEOFF);
	}
	
	public static void doTest(boolean bool, boolean mouseoff){
		/*if(!Settings.rayPicking()) return;*/ if(bool && !PICKING){ PICKING = true; MOUSEOFF = mouseoff; return; } if(FMTB.get() == null) return;
		//
		int width = FMTB.get().getDisplayMode().getWidth(), height = FMTB.get().getDisplayMode().getHeight();
		if(mouseoff){ width = Mouse.getX() * 2; height = Mouse.getY() * 2; }
		GL11.glReadPixels(width / 2, height / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		byte[] byteArray = new byte[4]; buffer.get(byteArray);
		//Print.console((((int) byteArray[0]) & 0xFF) + " " + (((int) byteArray[1]) & 0xFF) + " "  + (((int) byteArray[2]) & 0xFF));
		int id = new Color(((int) byteArray[0]) & 0xFF, ((int) byteArray[1]) & 0xFF, ((int) byteArray[2]) & 0xFF).getRGB() + 16777216;
		//Print.console(id + "-ID");
		buffer.clear(); PICKING = false; MOUSEOFF = false;
		if(TextureEditor.pixelMode()){
			Texture tex;
			if(FMTB.MODEL.texture == null || (tex = TextureManager.getTexture(FMTB.MODEL.texture, true)) == null){
				FMTB.showDialogbox("No Texture loaded.", "Cannot use Paint Pencil.", "ok", "toggle off", DialogBox.NOTHING, () -> { TextureEditor.toggleBucketMode(null); });
				return;
			}
			Texture calctex = TextureManager.getTexture(GroupCompound.temptexid, true);
			if(calctex == null){
				Print.console("Calculation texture not found or is not loaded or is not initialized, painting aborted."); return;
			} BufferedImage image = calctex.getImage();
			//Print.console(id);
			for(int x = 0; x < image.getWidth(); x++){
				for(int y = 0; y < image.getHeight(); y++){
					if(new Color(image.getRGB(x, y)).getRGB() + 16777216 == id){
						if(TextureEditor.colorPicker()){
							TextureEditor.CURRENTCOLOR.packed = tex.getImage().getRGB(x, y);
							((TextureEditor)Editor.get("texture_editor")).updateFields();
						}
						else{
							tex.getImage().setRGB(x, y, new Color(TextureEditor.CURRENTCOLOR.packed).getRGB()); tex.rebind();
							TextureManager.saveTexture(FMTB.MODEL.texture); return;
						}
					} else continue;
				}
			}
			return;
		}
		PolygonWrapper wrapper = getSelected(id);
		if(wrapper == null) return;
		if(!TextureEditor.BUCKETMODE){
			boolean control = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
			boolean state = control ? wrapper.getTurboList().selected : wrapper.selected;
			if(!Keyboard.isKeyDown(Keyboard.KEY_LMENU)) FMTB.MODEL.clearSelection();
			if(control){ wrapper.getTurboList().selected = !state; }
			else{ wrapper.selected = !state; }
			FMTB.MODEL.lastselected = control ? null : wrapper;
			FMTB.MODEL.updateFields();
		}
		else{
			Texture tex;
			if(FMTB.MODEL.texture == null || (tex = TextureManager.getTexture(FMTB.MODEL.texture, true)) == null){
				FMTB.showDialogbox("No Texture loaded.", "Cannot use Paint Bucket.", "ok", "toggle off", DialogBox.NOTHING, () -> { TextureEditor.toggleBucketMode(null); });
				return;
			}
			if(TextureEditor.groupMode()){
				boolean rebind = false; TurboList list = wrapper.getTurboList();
				for(PolygonWrapper poly : list){
					if(poly.burnToTexture(tex.getImage(), -1)){ rebind = true; }
				}
				if(rebind){
					tex.rebind(); TextureManager.saveTexture(FMTB.MODEL.texture);
				}
			}
			else{
				if(wrapper.burnToTexture(tex.getImage(), TextureEditor.polygonMode() ? -1 : getSelectedFace(wrapper, id))){
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