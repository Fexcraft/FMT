package net.fexcraft.app.fmt.texture;

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

}
