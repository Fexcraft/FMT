package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector3f;

import java.util.Arrays;

import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CurvePlane {

	public Vector3f size = new Vector3f(1);
	public Vector3f offset = new Vector3f(0);
	public boolean[] sides = new boolean[4];
	public Vector3f cor0, cor1, cor2, cor3;//, rot;
	public float location, rot;

	public CurvePlane(float loc){
		cor0 = new Vector3f();
		cor1 = new Vector3f();
		cor2 = new Vector3f();
		cor3 = new Vector3f();
		//rot = new Vector3f();
		location = loc;
	}

	public CurvePlane(CurvePlane seg, boolean loc){
		cor0 = new Vector3f(seg.cor0);
		cor1 = new Vector3f(seg.cor1);
		cor2 = new Vector3f(seg.cor2);
		cor3 = new Vector3f(seg.cor3);
		//rot = new Vector3f(seg.rot);
		rot = seg.rot;
		size.set(seg.size);
		offset.set(seg.offset);
		location = seg.location + (loc ? 0 : 1);
		sides = Arrays.copyOf(seg.sides, 4);
	}

	public CurvePlane(JsonMap map){
		size.x = map.get("width", 1f);
		size.y = map.get("height", 1f);
		size.z = map.get("depth", 1f);
		if(map.has("sides_off")){
			JsonArray array = map.getArray("sides_off");
			for(int i = 0; i < sides.length; i++){
				if(i >= array.size()) break;
				sides[i] = array.get(i).value();
			}
		}
		cor0 = getVector(map, "%s0", 0);
		cor1 = getVector(map, "%s1", 0);
		cor2 = getVector(map, "%s2", 0);
		cor3 = getVector(map, "%s3", 0);
		//rot = getVector(map, "rot%s", 0);
		rot = map.getFloat("rot", 0);
		offset = getVector(map, "off%s", 0);
		location = map.getFloat("loc", 0);
	}

	public Vector3f[] corners(){
		return new Vector3f[]{cor0, cor1, cor2, cor3};
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("width", size.x);
		map.add("height", size.y);
		map.add("depth", size.z);
		boolean anysides = false;
		for(boolean bool : sides) if(bool) anysides = true;
		if(anysides){
			JsonArray array = new JsonArray();
			for(boolean bool : sides) array.add(bool);
			map.add("sides_off", array);
		}
		setVector(map, "%s0", cor0);
		setVector(map, "%s1", cor1);
		setVector(map, "%s2", cor2);
		setVector(map, "%s3", cor3);
		//setVector(map, "rot%s", rot);
		map.add("rot", rot);
		setVector(map, "off%s", offset);
		map.add("loc", location);
		return map;
	}

}
