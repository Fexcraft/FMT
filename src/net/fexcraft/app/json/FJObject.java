package net.fexcraft.app.json;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FJObject<V> implements FJson {
	
	public V value;
	
	public FJObject(){}
	
	public FJObject(V value){
		this.value = value;
	}
	
	public <VALUE> VALUE value(){
		return (VALUE)value;
	}
	
	public void value(V newval){
		this.value = newval;
	}

	@Override
	public boolean isObject(){
		return true;
	}
	
	@Override
	public String toString(){
		return FJHandler.toString(this);
	}

}
