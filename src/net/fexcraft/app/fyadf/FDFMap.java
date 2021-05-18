package net.fexcraft.app.fyadf;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FDFMap extends FDFObject<Map<String, FDFObject<?>>> {
	
	public FDFMap(){
		value = new LinkedHashMap<>();
	}
	
	public <V> FDFObject<V> get(String key){
		return (FDFObject<V>)value.get(key);
	}
	
	public FDFArray getArray(String key){
		return value.get(key).asArray();
	}
	
	public FDFMap getMap(String key){
		return value.get(key).asMap();
	}
	
	public FDFObject<?> add(String key, FDFObject<?> elm){
		if(key.contains(".")){
			String sub = key.substring(0, key.indexOf("."));
			if(!value.containsKey(sub)) value.put(sub, new FDFMap());
			return value.get(sub).asMap().add(key.substring(key.indexOf(".") + 1), elm);
		}
		return value.put(key, elm);
	}
	
	public FDFObject<?> rem(String key){
		return value.remove(key);
	}
	
	public boolean has(String key){
		return value.containsKey(key);
	}
	
	public boolean contains(FDFObject<?> val){
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
	
	public FDFObject<?> add(String key, String val){
		return add(key, new FDFObject<String>(val));
	}
	
	public FDFObject<?> add(String key, byte val){
		return add(key, new FDFObject<Byte>(val));
	}
	
	public FDFObject<?> add(String key, char val){
		return add(key, new FDFObject<Character>(val));
	}
	
	public FDFObject<?> add(String key, short val){
		return add(key, new FDFObject<Short>(val));
	}
	
	public FDFObject<?> add(String key, int val){
		return add(key, new FDFObject<Integer>(val));
	}
	
	public FDFObject<?> add(String key, long val){
		return add(key, new FDFObject<Long>(val));
	}
	
	public FDFObject<?> add(String key, float val){
		return add(key, new FDFObject<Float>(val));
	}
	
	public FDFObject<?> add(String key, double val){
		return add(key, new FDFObject<Double>(val));
	}
	
	public FDFObject<?> addArray(String key){
		return add(key, new FDFArray());
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
		return FDFHandler.toString(this);
	}

}
