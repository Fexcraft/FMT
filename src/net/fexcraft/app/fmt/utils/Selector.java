package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Vertoff;
import net.fexcraft.app.fmt.polygon.Vertoff.VOSelection;
import net.fexcraft.app.fmt.utils.Picker.PickType;
import net.fexcraft.lib.common.math.V3D;

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
		return TYPE != PickType.VERTEX;
	}

	public static void move(){
		if(FMT.MODEL.getSelectedVerts().size() < 2) return;
		VOSelection vo0 = FMT.MODEL.getSelectedVerts().get(0);
		VOSelection vo1 = FMT.MODEL.getSelectedVerts().get(1);
		V3D v0 = vo0.vertoff().cache;
		V3D v1 = vo1.vertoff().cache;
		Pivot pi0 = FMT.MODEL.getP(vo0.polygon().group().pivot);
		Polygon.vo_axe.setDegrees(-pi0.rot.y, -pi0.rot.z, -pi0.rot.x);
		v0 = Polygon.vo_axe.rotate(v0);
		Pivot pi1 = FMT.MODEL.getP(vo1.polygon().group().pivot);
		Polygon.vo_axe.setDegrees(-pi1.rot.y, -pi1.rot.z, -pi1.rot.x);
		v1 = Polygon.vo_axe.rotate(v1);
		Vertoff off = vo0.vertoff();
		off.off.x = (float)(v1.x - v0.x);
		off.off.y = (float)(v1.y - v0.y);
		off.off.z = (float)(v1.z - v0.z);
		vo0.polygon().recompile();
		Logging.bar("Offset applied if applicable.");
		FMT.MODEL.clearSelectedVerts();
	}

}
