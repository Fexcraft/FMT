package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.V3D;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PointPivotTowards extends Animation {

	public String offpiv;
	public String tarpiv;
	public V3D off = new V3D();
	public V3D tar = new V3D();
	public V3D ang = new V3D();
	private Pivot offp;
	private Pivot tarp;
	private V3D here;
	private V3D ther;

	@Override
	public Animation create(JsonMap map){
		PointPivotTowards ani = new PointPivotTowards();
		if(map.has("off")){
			JsonArray arr = map.getArray("off");
			ani.off.x = arr.get(0).float_value();
			ani.off.y = arr.get(1).float_value();
			ani.off.z = arr.get(2).float_value();
		}
		if(map.has("tar")){
			JsonArray arr = map.getArray("tar");
			ani.tar.x = arr.get(0).float_value();
			ani.tar.y = arr.get(1).float_value();
			ani.tar.z = arr.get(2).float_value();
		}
		if(map.has("ang")){
			JsonArray arr = map.getArray("ang");
			ani.ang.x = arr.get(0).float_value();
			ani.ang.y = arr.get(1).float_value();
			ani.ang.z = arr.get(2).float_value();
		}
		ani.offpiv = map.getString("offpiv", null);
		ani.tarpiv = map.getString("tarpiv", null);
		return ani;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		JsonArray arr = new JsonArray();
		arr.add(off.x);
		arr.add(off.y);
		arr.add(off.z);
		map.add("off", arr);
		if(offpiv != null) map.add("offpiv", offpiv);
		arr = new JsonArray();
		arr.add(tar.x);
		arr.add(tar.y);
		arr.add(tar.z);
		map.add("tar", arr);
		arr = new JsonArray();
		arr.add(ang.x);
		arr.add(ang.y);
		arr.add(ang.z);
		map.add("ang", arr);
		if(tarpiv != null) map.add("tarpiv", tarpiv);
		return map;
	}

	@Override
	public void update(){
		//
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		if(offpiv == null || tarpiv == null) return;
		offp = group.model.getP(offpiv);
		tarp = group.model.getP(tarpiv);
		if(offp == null || tarp == null) return;
		here = offp.getVec(off);
		ther = tarp.getVec(tar).sub(here);
		offp.rot.y = (float)(Static.toDegrees(-Math.atan2(ther.z, ther.x)) + ang.y);
		offp.rot.z = (float)(Static.toDegrees(Math.atan2(Math.sqrt(ther.x * ther.x + ther.z * ther.z), -ther.y)) + ang.z);
		//ther = offp.getVec(off.add(0, 0, 1)).sub(here);
		//offp.rot.x = (float)(Static.toDegrees(-Math.atan2(ther.y, Math.sqrt(ther.x * ther.x + ther.z * ther.z))) + ang.x);
	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){
		//
	}

	@Override
	public String id(){
		return "fvtm:point_pivot_towards";
	}

	@Override
	public Object get(String str){
		switch(str){
			case "offset": return off;
			case "target": return tar;
			case "add_angle": return ang;
			case "offset_pivot": return offpiv;
			case "target_pivot": return tarpiv;
		}
		return null;
	}

	@Override
	public void set(String str, Object val){
		switch(str){
			case "offset.x": off.x = (float)val; break;
			case "offset.y": off.y = (float)val; break;
			case "offset.z": off.z = (float)val; break;
			case "target.x": tar.x = (float)val; break;
			case "target.y": tar.y = (float)val; break;
			case "target.z": tar.z = (float)val; break;
			case "add_angle.x": ang.x = (float)val; break;
			case "add_angle.y": ang.y = (float)val; break;
			case "add_angle.z": ang.z = (float)val; break;
			case "offset_pivot": offpiv = val.toString(); break;
			case "target_pivot": tarpiv = val.toString(); break;
		}
	}

}
