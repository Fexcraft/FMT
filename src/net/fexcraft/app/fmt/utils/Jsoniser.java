package net.fexcraft.app.fmt.utils;

import static java.lang.String.format;

import org.joml.Vector3f;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;

/**
 * A fresher JSON Utility than the one in FCL.
 * 
 * @author Ferdinand Calo' (FEX___96)
 */
public class Jsoniser {
	
	public static Vector3f getVector(JsonMap obj, String format, float def){
		return new Vector3f(obj.get(format(format, "x"), def), obj.get(format(format, "y"), def), obj.get(format(format, "z"), def));
	}

	public static JsonObject<?> toJson(Object val){
		if(val instanceof Double) return new JsonObject<Float>((float)val);
		if(val instanceof Float) return new JsonObject<Float>((float)val);
		if(val instanceof Long) return new JsonObject<Long>((long)val);
		if(val instanceof Integer) return new JsonObject<Integer>((int)val);
		if(val instanceof Boolean) return new JsonObject<Boolean>((boolean)val);
		//
		return new JsonObject<String>((String)val);
	}
	
}
