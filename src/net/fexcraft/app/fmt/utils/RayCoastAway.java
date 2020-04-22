package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.TextureManager.TextureGroup;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.utils.Print;

public class RayCoastAway {

	public static boolean PICKING, MOUSEOFF;
	private static ByteBuffer buffer;
	static{
		buffer = ByteBuffer.allocateDirect(4);
		buffer.order(ByteOrder.nativeOrder());
	}
	public static final int CORRECTOR = 16777216;
	public static PolygonWrapper lastsel;
	public static boolean UNLOCKED = false;

	/*public static void doTest(boolean bool){
		doTest(bool, MOUSEOFF);
	}*/

	public static void doTest(boolean bool, Boolean mouseoff, boolean pencil){
		doTest(bool, MOUSEOFF, pencil);
	}

	public static void doTest(boolean bool, boolean mouseoff, boolean pencil){
		if(bool && !PICKING){
			PICKING = true;
			MOUSEOFF = mouseoff;
			return;
		}
		if(FMTB.get() == null) return;
		//
		int width = FMTB.WIDTH, height = FMTB.HEIGHT;
		if(mouseoff){
			width = GGR.mousePosX() * 2;
			height = -(GGR.mousePosY() - FMTB.HEIGHT) * 2;
		}
		GL11.glReadPixels(width / 2, height / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		byte[] byteArray = new byte[4];
		buffer.get(byteArray);
		// Print.console((((int) byteArray[0]) & 0xFF) + " " + (((int) byteArray[1]) & 0xFF) + " " + (((int) byteArray[2]) & 0xFF));
		int id = new Color(((int)byteArray[0]) & 0xFF, ((int)byteArray[1]) & 0xFF, ((int)byteArray[2]) & 0xFF).getRGB() + CORRECTOR;
		// Print.console(id + "-ID");
		buffer.clear();
		PICKING = false;
		MOUSEOFF = false;
		if(TextureEditor.pixelMode() && pencil){
			Texture tex;
			TextureGroup group = lastsel.getTextureGroup();
			if(group == null || (tex = group.texture) == null){
				DialogBox.show(null, "dialog.button.ok", "polygon_picker.paint_bucket.toggle_off", null, () -> {
					TextureEditor.toggleBucketMode(null);
				}, "polygon_picker.paint_bucket.no_texture");
				return;
			}
			Texture calctex = TextureManager.getTexture(GroupCompound.temptexid + group.group, true);
			if(calctex == null){
				Print.console("Calculation texture not found or is not loaded or is not initialized, painting aborted.");
				return;
			}
			lastsel = null;
			BufferedImage image = calctex.getImage();
			// Print.console(id);
			for(int x = 0; x < image.getWidth(); x++){
				for(int y = 0; y < image.getHeight(); y++){
					if(new Color(image.getRGB(x, y)).getRGB() + CORRECTOR == id){
						if(TextureEditor.colorPicker()){
							TextureEditor.updateColor(tex.getImage().getRGB(x, y));
						}
						else{
							tex.getImage().setRGB(x, y, new Color(TextureEditor.CURRENTCOLOR.getColorInt()).getRGB());
							tex.rebind();
							TextureManager.saveTexture(tex);
							return;
						}
					}
					else continue;
				}
			}
			return;
		}
		PolygonWrapper wrapper = getSelected(id);
		if(wrapper == null) return;
		if(!TextureEditor.BUCKETMODE){
			boolean control = GGR.isControlDown();
			boolean state = control ? wrapper.getTurboList().selected : wrapper.selected;
			if(!GGR.isAltDown()) FMTB.MODEL.clearSelection();
			if(control){
				wrapper.getTurboList().selected = !state;
				GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
				wrapper.getTurboList().button.updateColor();
			}
			else{
				wrapper.selected = !state;
				GroupCompound.SELECTED_POLYGONS += wrapper.selected ? 1 : -1;
				wrapper.button.updateColor();
			}
			FMTB.MODEL.lastselected = control ? null : wrapper;
			FMTB.MODEL.updateFields();
		}
		else{
			if(TextureEditor.pixelMode()){
				lastsel = wrapper;
				PICKING = true;
				MOUSEOFF = mouseoff;
				return;
			}
			Texture tex;
			TextureGroup group = wrapper.getTextureGroup();
			if(group == null || (tex = group.texture) == null){
				DialogBox.show(null, "dialog.button.ok", "polygon_picker.paint_bucket.toggle_off", null, () -> {
					TextureEditor.toggleBucketMode(null);
				}, "polygon_picker.paint_bucket.no_texture");
				return;
			}
			if(TextureEditor.groupMode()){
				boolean rebind = false;
				TurboList list = wrapper.getTurboList();
				for(PolygonWrapper poly : list){
					if(poly.burnToTexture(tex.getImage(), -1)){
						rebind = true;
					}
				}
				if(rebind){
					tex.rebind();
					TextureManager.saveTexture(group.texture);
				}
			}
			else{
				if(wrapper.burnToTexture(tex.getImage(), TextureEditor.polygonMode() ? -1 : getSelectedFace(wrapper, id))){
					tex.rebind();
					TextureManager.saveTexture(group.texture);
				}
			}
		}
	}

	private static int getSelectedFace(PolygonWrapper wrapper, int id){
		for(int i = 0; i < wrapper.color.length; i++)
			if(wrapper.color[i] == id) return i;
		return -1;
	}

	private static PolygonWrapper getSelected(int id){
		for(TurboList list : FMTB.MODEL.getGroups()){
			for(PolygonWrapper wrapper : list){
				if(wrapper.color == null) continue;
				for(int col : wrapper.color){
					if(col == id) return wrapper;
				}
			}
		}
		return null;
	}

}