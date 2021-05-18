package net.fexcraft.app.fyadf;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FDFObject<V> implements FDFInterface {
	
	public V value;
	
	public FDFObject(){}
	
	public FDFObject(V value){
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
		return FDFHandler.toString(this);
	}

}
