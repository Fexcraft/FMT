package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.utils.texture.Texture;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class RayCoastAway {

	public static boolean PICKING, MOUSEOFF;
	private static ByteBuffer picker;
	static{
		picker = ByteBuffer.allocateDirect(4);
		picker.order(ByteOrder.nativeOrder());
	}
	public static final int CORRECTOR = 16777216;
	public static PolygonWrapper lastsel;

	/*public static void doTest(boolean bool){
		doTest(bool, MOUSEOFF);
	}*/

	public static void doTest(boolean bool, Boolean mouseoff, boolean pencil){
		doTest(bool, MOUSEOFF, pencil);
	}

	public static void doTest(boolean bool, boolean mouseoff, boolean pencil){
		if(bool && !PICKING){
			if(TextureEditor.colorPicker()){
				byte[] picked = getPicked(mouseoff);
				picked[0] -= 128;
				picked[1] -= 128;
				picked[2] -= 128;
				TextureEditor.updateColor(picked, null);
				return;
			}
			PICKING = true;
			MOUSEOFF = mouseoff;
			return;
		}
		//
		byte[] picked = getPicked(mouseoff);
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
				log("Calculation texture not found or is not loaded or is not initialized, painting aborted.");
				return;
			}
			lastsel = null;
			// log(id);
			for(int x = 0; x < calctex.getWidth(); x++){
				for(int y = 0; y < calctex.getHeight(); y++){
					byte[] calc = calctex.get(x, y);
					if(calc[0] == picked[0] && calc[1] == picked[1] && calc[2] == picked[2]){
						/*if(TextureEditor.colorPicker()){
							byte[] arr = tex.get(x, y);
							arr[0] -= 128;
							arr[1] -= 128;
							arr[2] -= 128;
							TextureEditor.updateColor(arr, null);
						}
						else{*/
							//log(x + " " + y);
							tex.set(x, y, TextureEditor.CURRENTCOLOR.toByteArray());
							tex.rebind();
							//TXO tex.save();
							return;
						/*}*/
					}
					else continue;
				}
			}
			return;
		}
		picked[0] += -128;
		picked[1] += -128;
		picked[2] += -128;
		PolygonWrapper wrapper = getSelected(picked);
		if(wrapper == null) return;
		if(!TextureEditor.isPaintActive()){
			select(wrapper);
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
					if(poly.burnToTexture(tex, -1)){
						rebind = true;
					}
				}
				if(rebind){
					tex.rebind();
					//TXO group.texture.save();
				}
			}
			else{
				if(wrapper.burnToTexture(tex, TextureEditor.polygonMode() ? -1 : wrapper.getUnShiftedIndex(getSelectedFace(wrapper, picked)))){
					tex.rebind();
					//TXO group.texture.save();
				}
			}
		}
	}
	
	public static void select(PolygonWrapper wrapper){
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

	private static byte[] getPicked(boolean mouseoff){
		int width = FMTB.WIDTH, height = FMTB.HEIGHT;
		if(mouseoff){
			width = GGR.mousePosX() * 2;
			height = -(GGR.mousePosY() - FMTB.HEIGHT) * 2;
		}
		GL11.glReadPixels(width / 2, height / 2, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, picker);
		byte[] picked = new byte[4];
		picker.get(picked);
		// log((((int) byteArray[0]) & 0xFF) + " " + (((int) byteArray[1]) & 0xFF) + " " + (((int) byteArray[2]) & 0xFF));
		// log(id + "-ID");
		picker.clear();
		return picked;
	}

	private static int getSelectedFace(PolygonWrapper wrapper, byte[] picked){
		for(int i = 0; i < wrapper.color.length; i++)
			if(wrapper.color[i][0] == picked[0] && wrapper.color[i][1] == picked[1] && wrapper.color[i][2] == picked[2]) return i;
		return -1;
	}

	private static PolygonWrapper getSelected(byte[] picked){
		for(TurboList list : FMTB.MODEL.getGroups()){
			for(PolygonWrapper wrapper : list){
				if(wrapper.color == null) continue;
				for(byte[] col : wrapper.color){
					if(col[0] == picked[0] && col[1] == picked[1] && col[2] == picked[2]) return wrapper;
				}
			}
		}
		return null;
	}

}