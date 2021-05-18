package net.fexcraft.app.fyadf;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FDFArray extends FDFObject<List<FDFObject<?>>> {
	
	public FDFArray(){
		value = new ArrayList<>();
	}
	
	public boolean add(FDFObject<?> elm){
		return value.add(elm);
	}
	
	public boolean rem(FDFObject<?> elm){
		return value.remove(elm);
	}
	
	public FDFObject<?> get(int index){
		return value.get(index);
	}
	
	public FDFArray getArray(int index){
		return value.get(index).asArray();
	}
	
	public FDFMap getMap(int index){
		return value.get(index).asMap();
	}
	
	public FDFObject<?> rem(int index){
		return value.remove(index);
	}
	
	public FDFObject<?> set(int index, FDFObject<?> elm){
		return value.set(index, elm);
	}
	
	public boolean contains(FDFObject<?> val){
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
