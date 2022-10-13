package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TexturePainter {
	
	public static final RGB PRIMARY = RGB.BLACK.copy();
	public static final RGB SECONDARY = RGB.WHITE.copy();
	public static Selection SELMODE = Selection.NONE;
	public static Tool TOOL = Tool.NONE;
	
	public static byte[] getCurrentColor(boolean primary){
		return (primary ? PRIMARY : SECONDARY).toByteArray();
	}

	public static byte[] getPrimaryColor(){
		return PRIMARY.toByteArray();
	}
	
	public static byte[] getSecondaryColor(){
		return SECONDARY.toByteArray();
	}

	public static void updateColor(Integer value, boolean primary){
		if(primary) PRIMARY.packed = value;
		else SECONDARY.packed = value;
		UpdateHandler.update(UpdateType.PAINTER_COLOR, value, primary);
	}
	
	public static enum Selection {
		
		NONE, PIXEL, FACE, POLYGON, GROUP;
		
		public String trs(){
			return translate("texture.painter.selection." + name().toLowerCase());
		}
		
	}
	
	public static enum Tool {
		
		NONE, BUCKET, BRUSH, ERASER;
		
		public String trs(){
			return translate("texture.painter.tool." + name().toLowerCase());
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
		UpdateHandler.update(UpdateType.PAINTER_TOOL, tool, SELMODE);
	}

	public static void setSelection(Selection sel){
		SELMODE = sel;
		if(TOOL == Tool.NONE) TOOL = Tool.BUCKET;
		UpdateHandler.update(UpdateType.PAINTER_TOOL, TOOL, sel);
	}

}
