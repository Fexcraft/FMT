package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Translator extends Animation {

	private static String[] keys = new String[]{
		"x", "y", "z", "speed_x", "speed_y", "speed_z",
		"loop", "attr_bool", "attr"
	};
	public float x, y, z;
	public float sx, sy, sz;
	public boolean loop;
	public boolean attrbool;
	public String attr;

	@Override
	public Animation create(JsonMap map){
		Translator ani = new Translator();
		ani.x = map.getFloat("x", 0);
		ani.y = map.getFloat("y", 0);
		ani.z = map.getFloat("z", 0);
		ani.sx = map.getFloat("sx", 0);
		ani.sy = map.getFloat("sy", 0.1f);
		ani.sz = map.getFloat("sz", 0);
		ani.loop = map.getBoolean("loop", false);
		ani.attrbool = map.getBoolean("attr_bool", true);
		ani.attr = map.getString("attr", "");
		return ani;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("x", x);
		map.add("y", y);
		map.add("z", z);
		map.add("sx", sx);
		map.add("sy", sy);
		map.add("sz", sz);
		map.add("loop", loop);
		map.add("attr_bool", attrbool);
		map.add("attr", attr);
		return map;
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		for(Polygon poly : group){
			poly.pos.x += x;
			poly.pos.y += y;
			poly.pos.z += z;
		}
	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){
		for(Polygon poly : group){
			poly.pos.x -= x;
			poly.pos.y -= y;
			poly.pos.z -= z;
		}
	}

	@Override
	public String id(){
		return "fvtm:translator";
	}

	@Override
	public Object get(String str){
		switch(str){
			case "x": return x;
			case "y": return y;
			case "z": return z;
			case "speed_x": return sx;
			case "speed_y": return sy;
			case "speed_z": return sz;
			case "loop": return loop;
			case "attr_bool": return attrbool;
			case "attr": return attr;
		}
		return null;
	}

	@Override
	public void set(String str, Object val){
		switch(str){
			case "x": x = (float)val; break;
			case "y": y = (float)val; break;
			case "z": z = (float)val; break;
			case "speed_x": sx = (float)val; break;
			case "speed_y": sy = (float)val; break;
			case "speed_z": sz = (float)val; break;
			case "loop": loop = Boolean.parseBoolean(val.toString()); break;
			case "attr_bool": attrbool = Boolean.parseBoolean(val.toString()); break;
			case "attr": attr = val.toString(); break;
		}
	}

}
