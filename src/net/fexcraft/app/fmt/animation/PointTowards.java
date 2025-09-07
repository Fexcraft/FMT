package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.V3D;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PointTowards extends Animation {

	public int axe = 0;
	public String offpiv;
	public String tarpiv;
	public V3D off = new V3D();
	public V3D tar = new V3D();
	public float addang;
	private float angle;
	private V3D here;
	private V3D ther;

	@Override
	public Animation create(JsonMap map){
		PointTowards ani = new PointTowards();
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
		ani.offpiv = map.getString("offpiv", null);
		ani.tarpiv = map.getString("tarpiv", null);
		ani.addang = map.getFloat("angle", addang);
		ani.axe = map.getInteger("axe", axe);
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
		if(tarpiv != null) map.add("tarpiv", tarpiv);
		map.add("axe", axe);
		map.add("angle", addang);
		return map;
	}

	@Override
	public void update(){
		//
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		here = group.model.getP(offpiv == null ? group.pivot : offpiv).getVec(off);
		ther = group.model.getP(tarpiv == null ? group.pivot : tarpiv).getVec(tar);
		for(Polygon poly : group){
			poly.glm.posX += here.x;
			poly.glm.posY += here.y;
			poly.glm.posZ += here.z;
		}
		switch(axe){
			case 0:{
				angle = addang;
				for(Polygon poly : group) poly.glm.rotX += angle;
				break;
			}
			case 1:{
				angle = (float)-Static.toDegrees(Math.atan2(ther.z - here.z, ther.x - here.x)) + addang;
				for(Polygon poly : group) poly.glm.rotY += angle;
				break;
			}
			case 2:{
				angle = (float)Static.toDegrees(Math.atan2(ther.y - here.y, ther.x - here.x)) + addang;
				for(Polygon poly : group) poly.glm.rotZ += angle;
				break;
			}
		}
	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){
		for(Polygon poly : group){
			poly.glm.posX -= here.x;
			poly.glm.posY -= here.y;
			poly.glm.posZ -= here.z;
		}
		switch(axe){
			case 0: for(Polygon poly : group) poly.glm.rotX -= angle; break;
			case 1: for(Polygon poly : group) poly.glm.rotY -= angle; break;
			case 2: for(Polygon poly : group) poly.glm.rotZ -= angle; break;
		}
	}

	@Override
	public String id(){
		return "fvtm:point_towards";
	}

	@Override
	public Object get(String str){
		switch(str){
			case "offset": return off;
			case "target": return tar;
			case "axe": return axe;
			case "offset_pivot": return offpiv;
			case "target_pivot": return tarpiv;
			case "add_angle": return addang;
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
			case "axe":{
				int ax = (int)(float)val;
				if(ax < 0) ax = 0;
				if(ax > 2) ax = 2;
				axe = ax;
				break;
			}
			case "offset_pivot": offpiv = val.toString(); break;
			case "target_pivot": tarpiv = val.toString(); break;
			case "add_angle": addang = (float)val; break;
		}
	}

}
