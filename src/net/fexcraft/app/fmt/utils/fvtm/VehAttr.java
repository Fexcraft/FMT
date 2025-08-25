package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehAttr {

	public Object value;
	public Type type;

	public VehAttr(Type typ, Object val){
		type = typ;
		value = val;
	}

	public VehAttr(JsonMap map){
		type = Type.valueOf(map.getString("type", "string").toUpperCase());
		switch(type){
			case STRING -> value = map.getString("value", "");
			case BOOL -> value = map.getBoolean("value", false);
			case INT -> value = map.getInteger("value", 0);
			case FLOAT -> value = map.getFloat("value", 0f);
			case LONG -> value = map.getLong("value", 0l);
		}
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("type", type.name().toLowerCase());
		map.add("value", new JsonValue<>(value));
		return map;
	}

	public static enum Type {

		STRING, BOOL, INT, FLOAT, LONG, TRISTATE;

		public boolean number(){
			return this == INT || this == FLOAT || this == LONG;
		}

	}

}
