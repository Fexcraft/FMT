package net.fexcraft.app.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JsonMap extends JsonObject<Map<String, JsonObject<?>>> {
	
	public JsonMap(){
		value = new LinkedHashMap<>();
	}
	
	public <V> JsonObject<V> get(String key){
		return (JsonObject<V>)value.get(key);
	}
	
	public JsonArray getArray(String key){
		return value.get(key).asArray();
	}

	public List<JsonObject<?>> getArrayElements(String key){
		return getArray(key).elements();
	}
	
	public JsonMap getMap(String key){
		if(!value.containsKey(key)) addMap(key);
		return value.get(key).asMap();
	}
	
	public JsonObject<?> add(String key, JsonObject<?> elm){
		if(key.contains(".")){
			String sub = key.substring(0, key.indexOf("."));
			if(!value.containsKey(sub)) value.put(sub, new JsonMap());
			return value.get(sub).asMap().add(key.substring(key.indexOf(".") + 1), elm);
		}
		return value.put(key, elm);
	}
	
	public JsonObject<?> rem(String key){
		return value.remove(key);
	}
	
	public boolean has(String key){
		return value.containsKey(key);
	}
	
	public boolean contains(JsonObject<?> val){
		return value.containsValue(val);
	}
	
	@Override
	public boolean isMap(){
		return true;
	}
	
	@Override
	public boolean isObject(){
		return false;
	}
	
	public JsonObject<?> add(String key, String val){
		return add(key, new JsonObject<String>(val));
	}
	
	public JsonObject<?> add(String key, byte val){
		return add(key, new JsonObject<Byte>(val));
	}
	
	public JsonObject<?> add(String key, char val){
		return add(key, new JsonObject<Character>(val));
	}
	
	public JsonObject<?> add(String key, short val){
		return add(key, new JsonObject<Short>(val));
	}
	
	public JsonObject<?> add(String key, int val){
		return add(key, new JsonObject<Integer>(val));
	}
	
	public JsonObject<?> add(String key, long val){
		return add(key, new JsonObject<Long>(val));
	}
	
	public JsonObject<?> add(String key, float val){
		return add(key, new JsonObject<Float>(val));
	}
	
	public JsonObject<?> add(String key, double val){
		return add(key, new JsonObject<Double>(val));
	}

	public JsonObject<?> add(String key, boolean val){
		return add(key, new JsonObject<Boolean>(val));
	}
	
	public JsonObject<?> addArray(String key){
		return add(key, new JsonArray());
	}
	
	public JsonObject<?> addMap(String key){
		return add(key, new JsonMap());
	}
	
	public int size(){
		return value.size();
	}
	
	public boolean empty(){
		return value.size() == 0;
	}
	
	public boolean not_empty(){
		return value.size() > 0;
	}

	public Set<Entry<String, JsonObject<?>>> entries(){
		return value.entrySet();
	}
	
	@Override
	public String toString(){
		return JsonHandler.toString(this);
	}

	public <V> V get(String key, V def){
		return value.containsKey(key) ? value.get(key).value() : def;
	}

	public float getFloat(String key, float def){
		return value.containsKey(key) ? value.get(key).float_value() : def;
	}

	public int getInteger(String key, int def){
		return value.containsKey(key) ? value.get(key).integer_value() : def;
	}

	public long getLong(String key, long def){
		return value.containsKey(key) ? value.get(key).long_value() : def;
	}

	public String getString(String key, String def){
		return value.containsKey(key) ? value.get(key).string_value() : def;
	}

	public boolean getBoolean(String key, boolean def){
		return value.containsKey(key) ? value.get(key).value() : def;
	}

}
