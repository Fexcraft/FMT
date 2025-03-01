package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.Vec3f;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Vertoff {

	public float[] color;
	public Polygon polygon;
	public Vec3f cache = new Vec3f();
	public Vector3F off = new Vector3F();

	public Vertoff(){}

	public Vertoff(JsonValue<?> value){
		off.x = value.asArray().get(0).float_value();
		off.y = value.asArray().get(1).float_value();
		off.z = value.asArray().get(2).float_value();
	}

	public void apply(Polygon poly, float[] v){
		cache.x = poly.pos.x + (v[0] += off.x);
		cache.y = poly.pos.y + (v[1] += off.y);
		cache.z = poly.pos.z + (v[2] += off.z);
	}

	public void apply(Polygon poly, Vec3f v){
		cache.x = poly.pos.x + (v.x += off.x);
		cache.y = poly.pos.y + (v.y += off.y);
		cache.z = poly.pos.z + (v.z += off.z);
	}

	public static Pair<Polygon, VOKey> getPicked(int pick){
		VOKey key = null;
		for(Map.Entry<Pair<Polygon, VOKey>, Integer> entry : Polygon.vertcolors.entrySet()){
			if(entry.getValue() == pick){
				return entry.getKey();
			}
		}
		return null;
	}

	public JsonArray save(){
		return new JsonArray(off.x, off.y, off.z);
	}

	public boolean isNull(){
		return off.x == 0f && off.y == 0f && off.z == 0f;
	}

	public static record VOKey(VOType type, int vertix, int secondary){

		@Override
		public String toString(){
			return type +"/" + vertix + "/" + secondary;
		}

		@Override
		public boolean equals(Object o){
			if(o instanceof VOKey == false) return false;
			VOKey vo = (VOKey)o;
			return type == vo.type && vertix == vo.vertix && secondary == vo.secondary;
		}

		public static VOKey parse(String key){
			String[] split = key.split("/");
			VOType type = VOType.valueOf(split[0]);
			return new VOKey(type, Integer.parseInt(split[1]), Integer.parseInt(split[2]));
		}

	}

	public static enum VOType {

		BOX_CORNER,
		CYL_INNER,
		CYL_OUTER,
		CURVE,
		;

	}

}
