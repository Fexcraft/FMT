package net.fexcraft.app.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JsonArray extends JsonObject<List<JsonObject<?>>> {
	
	public JsonArray(){
		value = new ArrayList<>();
	}
	
	public boolean add(JsonObject<?> elm){
		return value.add(elm);
	}
	
	public boolean rem(JsonObject<?> elm){
		return value.remove(elm);
	}
	
	public JsonObject<?> get(int index){
		return value.get(index);
	}
	
	public JsonArray getArray(int index){
		return value.get(index).asArray();
	}
	
	public JsonMap getMap(int index){
		return value.get(index).asMap();
	}
	
	public JsonObject<?> rem(int index){
		return value.remove(index);
	}
	
	public JsonObject<?> set(int index, JsonObject<?> elm){
		return value.set(index, elm);
	}
	
	public boolean contains(JsonObject<?> val){
		return value.contains(val);
	}
	
	@Override
	public boolean isArray(){
		return true;
	}
	
	@Override
	public boolean isObject(){
		return false;
	}
	
	public boolean add(String val){
		return add(new JsonObject<String>(val));
	}
	
	public boolean add(byte val){
		return add(new JsonObject<Byte>(val));
	}
	
	public boolean add(char val){
		return add(new JsonObject<Character>(val));
	}
	
	public boolean add(short val){
		return add(new JsonObject<Short>(val));
	}
	
	public boolean add(int val){
		return add(new JsonObject<Integer>(val));
	}
	
	public boolean add(long val){
		return add(new JsonObject<Long>(val));
	}
	
	public boolean add(float val){
		return add(new JsonObject<Float>(val));
	}
	
	public boolean add(double val){
		return add(new JsonObject<Double>(val));
	}
	
	public boolean add(boolean val){
		return add(new JsonObject<Boolean>(val));
	}
	
	public boolean addArray(){
		return add(new JsonArray());
	}
	
	public boolean addMap(){
		return add(new JsonMap());
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

	public List<JsonObject<?>> elements(){
		return value;
	}
	
	@Override
	public String toString(){
		return JsonHandler.toString(this);
	}

}
