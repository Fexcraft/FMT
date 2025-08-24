package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.V3D;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PointTowards extends Animation {

	public int axe = 0;
	public String tarpiv;
	public V3D off = new V3D();
	public V3D tar = new V3D();

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
		ani.tarpiv = map.getString("tarpiv", null);
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
		arr = new JsonArray();
		arr.add(tar.x);
		arr.add(tar.y);
		arr.add(tar.z);
		map.add("tar", arr);
		map.add("axe", axe);
		if(tarpiv != null) map.add("tarpiv", tarpiv);
		return map;
	}

	@Override
	public void update(){
		//
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		//
	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){
		//
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
			case "target_pivot": return tarpiv;
		}
		return null;
	}

	@Override
	public void set(String str, Object val){
		switch(str){
			case "off.x": off.x = (float)val; break;
			case "off.y": off.y = (float)val; break;
			case "off.z": off.z = (float)val; break;
			case "tar.x": tar.x = (float)val; break;
			case "tar.y": tar.y = (float)val; break;
			case "tar.z": tar.z = (float)val; break;
			case "axe":{
				int ax = (int)(float)val;
				if(ax < 0) ax = 0;
				if(ax > 2) ax = 2;
				axe = ax;
				break;
			}
			case "target_pivot": tarpiv = val.toString(); break;
		}
	}

}
