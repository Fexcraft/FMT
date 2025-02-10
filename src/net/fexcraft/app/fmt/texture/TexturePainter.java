package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterTool;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Picker.PickType;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexturePainter {
	
	public static RGB[] CHANNELS;
	public static final RGB ERASER = new RGB(0, 0, 0).setAlpha(0);
	public static Selection SELMODE = Selection.NONE;
	public static Tool TOOL = Tool.NONE;
	public static int ACTIVE = 0;

	public static byte[] getColor(){
		return CHANNELS[ACTIVE].toByteArray();
	}

	public static void updateColor(int value, int channel, boolean upd_plt){
		CHANNELS[channel].packed = value;
		UpdateHandler.update(new PainterColor(value, channel, upd_plt));
	}
	
	public static enum Selection {
		
		NONE, PIXEL, FACE, POLYGON, GROUP;
		
		public String trs(){
			return translate("texture.painter.selection." + name().toLowerCase());
		}

		public PickType getPickType(){
			return this == FACE || this == PIXEL ? PickType.FACE : PickType.POLYGON;
		}

		public boolean face(){
			return this == FACE;
		}

		public boolean pixel(){
			return this == PIXEL;
		}
		
	}
	
	public static enum Tool {
		
		NONE, BUCKET, BRUSH, ERASER;
		
		public String trs(){
			return translate("texture.painter.tool." + name().toLowerCase());
		}

		public boolean active(){
			return this != NONE;
		}
		
	}

	public static String getToolName(){
		if(SELMODE == Selection.NONE || TOOL == Tool.NONE) return Selection.NONE.trs();
		if(SELMODE == Selection.PIXEL && TOOL == Tool.BUCKET) return SELMODE.trs() + " " + translate("texture.painter.tool.pencil");
		if(TOOL == Tool.BRUSH) return TOOL.trs();
		return SELMODE.trs() + " " + TOOL.trs();
	}

	public static void setTool(Tool tool){
		TOOL = tool;
		if(SELMODE == Selection.NONE) SELMODE = Selection.PIXEL;
		UpdateHandler.update(new PainterTool(tool, SELMODE));
	}

	public static void setSelection(Selection sel){
		SELMODE = sel;
		if(TOOL == Tool.NONE) TOOL = Tool.BUCKET;
		UpdateHandler.update(new PainterTool(TOOL, sel));
	}

	public static void paint(boolean primary, Polygon polygon, int face){
		Group group = Picker.polygon().group();
		TextureGroup tex = group.texgroup == null ? group.model.texgroup : group.texgroup;
		if(tex == null) return;
		switch(SELMODE){
			case PIXEL:
				int x = face / tex.painter.getHeight();
				int y = face % tex.painter.getHeight();
				if(x < 0 || y < 0 || x >= tex.texture.getWidth() || y >= tex.texture.getHeight()) return;
				tex.texture.set(x, y, getCurrentColor(primary));
				break;
			case FACE:
				polygon.paintTex(tex.texture, polygon.getFaceByColor(face).index(), primary);
				break;
			case POLYGON:
				for(Face f : polygon.getUVFaces()) polygon.paintTex(tex.texture, f.index(), primary);
				break;
			case GROUP:
				group.forEach(poly -> {
					for(Face f : poly.getUVFaces()) poly.paintTex(tex.texture, f.index(), primary);
				});
				break;
			case NONE:
			default:
				return;
		}
		tex.texture.rebind();
	}

	public static byte[] getCurrentColor(boolean primary){
		byte[] b = (TOOL == Tool.ERASER ? ERASER : CHANNELS[ACTIVE]).toByteArray();
		float a = (TOOL == Tool.ERASER ? ERASER : CHANNELS[ACTIVE]).alpha;
		return new byte[]{ b[0], b[1], b[2], (byte)(Math.floor(a >= 1.0f ? 255 : a * 256.0f) - 128) };
	}

	public static boolean bindTex(){
		Group group = Picker.polygon().group();
		TextureGroup tex = group.texgroup == null ? group.model.texgroup : group.texgroup;
		if(tex == null) return false;
		TextureManager.bind(tex.painter);
		return true;
	}

}
