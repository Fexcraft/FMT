package net.fexcraft.app.json;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FJMap extends FJObject<Map<String, FJObject<?>>> {
	
	public FJMap(){
		value = new LinkedHashMap<>();
	}
	
	public <V> FJObject<V> get(String key){
		return (FJObject<V>)value.get(key);
	}
	
	public FJArray getArray(String key){
		return value.get(key).asArray();
	}
	
	public FJMap getMap(String key){
		return value.get(key).asMap();
	}
	
	public FJObject<?> add(String key, FJObject<?> elm){
		if(key.contains(".")){
			String sub = key.substring(0, key.indexOf("."));
			if(!value.containsKey(sub)) value.put(sub, new FJMap());
			return value.get(sub).asMap().add(key.substring(key.indexOf(".") + 1), elm);
		}
		return value.put(key, elm);
	}
	
	public FJObject<?> rem(String key){
		return value.remove(key);
	}
	
	public boolean has(String key){
		return value.containsKey(key);
	}
	
	public boolean contains(FJObject<?> val){
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
	
	public FJObject<?> add(String key, String val){
		return add(key, new FJObject<String>(val));
	}
	
	public FJObject<?> add(String key, byte val){
		return add(key, new FJObject<Byte>(val));
	}
	
	public FJObject<?> add(String key, char val){
		return add(key, new FJObject<Character>(val));
	}
	
	public FJObject<?> add(String key, short val){
		return add(key, new FJObject<Short>(val));
	}
	
	public FJObject<?> add(String key, int val){
		return add(key, new FJObject<Integer>(val));
	}
	
	public FJObject<?> add(String key, long val){
		return add(key, new FJObject<Long>(val));
	}
	
	public FJObject<?> add(String key, float val){
		return add(key, new FJObject<Float>(val));
	}
	
	public FJObject<?> add(String key, double val){
		return add(key, new FJObject<Double>(val));
	}
	
	public FJObject<?> addArray(String key){
		return add(key, new FJArray());
	}
	
	public FJObject<?> addMap(String key){
		return add(key, new FJMap());
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
	
	@Override
	public String toString(){
		return FJHandler.toString(this);
	}

}
