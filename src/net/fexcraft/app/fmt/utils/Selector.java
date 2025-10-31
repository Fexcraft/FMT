package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Vertoff;
import net.fexcraft.app.fmt.polygon.Vertoff.VOKey;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.VertexSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Picker.PickType;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.Vec3f;
import org.apache.commons.lang3.tuple.Pair;

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
		Pair<Polygon, VOKey> vo0 = FMT.MODEL.getSelectedVerts().get(0);
		Pair<Polygon, VOKey> vo1 = FMT.MODEL.getSelectedVerts().get(1);
		V3D v0 = vo0.getLeft().vertoffs.get(vo0.getRight()).cache;
		V3D v1 = vo1.getLeft().vertoffs.get(vo1.getRight()).cache;
		Pivot pi0 = FMT.MODEL.getP(vo0.getLeft().group().pivot);
		Polygon.vo_axe.setDegrees(-pi0.rot.y, -pi0.rot.z, -pi0.rot.x);
		v0 = Polygon.vo_axe.rotate(v0);
		Pivot pi1 = FMT.MODEL.getP(vo1.getLeft().group().pivot);
		Polygon.vo_axe.setDegrees(-pi1.rot.y, -pi1.rot.z, -pi1.rot.x);
		v1 = Polygon.vo_axe.rotate(v1);
		Vertoff off = vo0.getLeft().vertoffs.get(vo0.getRight());
		off.off.x = (float)(v1.x - v0.x);
		off.off.y = (float)(v1.y - v0.y);
		off.off.z = (float)(v1.z - v0.z);
		vo0.getLeft().recompile();
		Logging.bar("Offset applied if applicable.");
		FMT.MODEL.clearSelectedVerts();
	}

}
