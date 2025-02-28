package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Vertoff;
import net.fexcraft.app.fmt.utils.Picker.PickType;
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
		Pair<Polygon, Vertoff.VOKey> vo0 = FMT.MODEL.getSelectedVerts().get(0);
		Pair<Polygon, Vertoff.VOKey> vo1 = FMT.MODEL.getSelectedVerts().get(1);
		if(!vo0.getLeft().getShape().isShapebox()) return;
		Vec3f v0 = vo0.getLeft().vertoffs.get(vo0.getRight()).cache;
		Vec3f v1 = vo1.getLeft().vertoffs.get(vo1.getRight()).cache;
		Pivot pi0 = FMT.MODEL.getP(vo0.getLeft().group().pivot);
		Polygon.vo_axe.setAngles(-pi0.rot.y, -pi0.rot.z, -pi0.rot.x);
		v0 = Polygon.vo_axe.get(v0);
		Pivot pi1 = FMT.MODEL.getP(vo1.getLeft().group().pivot);
		Polygon.vo_axe.setAngles(-pi1.rot.y, -pi1.rot.z, -pi1.rot.x);
		v1 = Polygon.vo_axe.get(v1);
		Vertoff off = vo0.getLeft().vertoffs.get(vo0.getRight());
		off.off.x = v0.x - v1.x;
		off.off.y = v0.y - v1.y;
		off.off.z = v0.z - v1.z;
		vo0.getLeft().recompile();
		FMT.MODEL.getSelectedVerts().clear();
	}

}
