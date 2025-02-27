package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.VertexOffset;
import net.fexcraft.app.fmt.utils.Picker.PickType;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Selector {

	public static boolean SHOW_VERTICES = true;
	public static PickType TYPE = PickType.POLYGON;

	public static void set(PickType sel){
		TYPE = sel;
	}

	public static boolean showCorners(){
		return TYPE != PickType.VERTEX && !SHOW_VERTICES;
	}

	public static void move(){
		if(FMT.MODEL.getSelectedVerts().size() < 2) return;
		VertexOffset vert0 = FMT.MODEL.getSelectedVerts().get(0);
		VertexOffset vert1 = FMT.MODEL.getSelectedVerts().get(0);
		if(!vert0.polygon.getShape().isShapebox()) return;

		FMT.MODEL.getSelectedVerts().clear();
	}

}
