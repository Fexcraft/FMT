package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.V3D;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PointPivotTowards extends Animation {

	public String loc_piv;
	public String tow_piv;
	public String loc_bas;
	public V3D loff = new V3D();
	public V3D toff = new V3D();
	public V3D ang = new V3D();
	private Pivot loc;
	private Pivot bas;
	private Pivot tow;
	private V3D here;
	private V3D ther;

	@Override
	public Animation create(JsonMap map){
		PointPivotTowards ani = new PointPivotTowards();
		if(map.has("loc_off")){
			JsonArray arr = map.getArray("loc_off");
			ani.loff.x = arr.get(0).float_value();
			ani.loff.y = arr.get(1).float_value();
			ani.loff.z = arr.get(2).float_value();
		}
		if(map.has("tow_off")){
			JsonArray arr = map.getArray("tow_off");
			ani.toff.x = arr.get(0).float_value();
			ani.toff.y = arr.get(1).float_value();
			ani.toff.z = arr.get(2).float_value();
		}
		if(map.has("ang")){
			JsonArray arr = map.getArray("ang");
			ani.ang.x = arr.get(0).float_value();
			ani.ang.y = arr.get(1).float_value();
			ani.ang.z = arr.get(2).float_value();
		}
		ani.loc_piv = map.getString("loc_piv", null);
		ani.loc_bas = map.getString("loc_bas", null);
		ani.tow_piv = map.getString("tow_piv", null);
		return ani;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		JsonArray arr = new JsonArray();
		arr.add(loff.x);
		arr.add(loff.y);
		arr.add(loff.z);
		map.add("loc_off", arr);
		arr = new JsonArray();
		arr.add(toff.x);
		arr.add(toff.y);
		arr.add(toff.z);
		map.add("tow_off", arr);
		arr = new JsonArray();
		arr.add(ang.x);
		arr.add(ang.y);
		arr.add(ang.z);
		map.add("ang", arr);
		if(loc_piv != null) map.add("loc_piv", loc_piv);
		if(loc_bas != null) map.add("loc_bas", loc_bas);
		if(tow_piv != null) map.add("tow_piv", tow_piv);
		return map;
	}

	@Override
	public void update(){
		//
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		if(loc_piv == null || loc_bas == null || tow_piv == null) return;
		loc = group.model.getP(loc_piv);
		bas = group.model.getP(loc_bas);
		tow = group.model.getP(tow_piv);
		if(loc == null || bas == null || tow == null) return;
		//
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
			case "local_offset": return loff;
			case "towards_offset": return toff;
			case "add_angle": return ang;
			case "local_pivot": return loc_piv;
			case "local_base": return loc_bas;
			case "towards_pivot": return tow_piv;
		}
		return null;
	}

	@Override
	public void set(String str, Object val){
		switch(str){
			case "local_offset.x": loff.x = (float)val; break;
			case "local_offset.y": loff.y = (float)val; break;
			case "local_offset.z": loff.z = (float)val; break;
			case "towards_offset.x": toff.x = (float)val; break;
			case "towards_offset.y": toff.y = (float)val; break;
			case "towards_offset.z": toff.z = (float)val; break;
			case "add_angle.x": ang.x = (float)val; break;
			case "add_angle.y": ang.y = (float)val; break;
			case "add_angle.z": ang.z = (float)val; break;
			case "local_pivot": loc_piv = val.toString(); break;
			case "local_base": loc_bas = val.toString(); break;
			case "towards_pivot": tow_piv = val.toString(); break;
		}
	}

}
