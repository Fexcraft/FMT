package net.fexcraft.app.json;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FJArray extends FJObject<List<FJObject<?>>> {
	
	public FJArray(){
		value = new ArrayList<>();
	}
	
	public boolean add(FJObject<?> elm){
		return value.add(elm);
	}
	
	public boolean rem(FJObject<?> elm){
		return value.remove(elm);
	}
	
	public FJObject<?> get(int index){
		return value.get(index);
	}
	
	public FJArray getArray(int index){
		return value.get(index).asArray();
	}
	
	public FJMap getMap(int index){
		return value.get(index).asMap();
	}
	
	public FJObject<?> rem(int index){
		return value.remove(index);
	}
	
	public FJObject<?> set(int index, FJObject<?> elm){
		return value.set(index, elm);
	}
	
	public boolean contains(FJObject<?> val){
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
		return add(new FJObject<String>(val));
	}
	
	public boolean add(byte val){
		return add(new FJObject<Byte>(val));
	}
	
	public boolean add(char val){
		return add(new FJObject<Character>(val));
	}
	
	public boolean add(short val){
		return add(new FJObject<Short>(val));
	}
	
	public boolean add(int val){
		return add(new FJObject<Integer>(val));
	}
	
	public boolean add(long val){
		return add(new FJObject<Long>(val));
	}
	
	public boolean add(float val){
		return add(new FJObject<Float>(val));
	}
	
	public boolean add(double val){
		return add(new FJObject<Double>(val));
	}
	
	public boolean addArray(){
		return add(new FJArray());
	}
	
	public boolean addMap(){
		return add(new FJMap());
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
