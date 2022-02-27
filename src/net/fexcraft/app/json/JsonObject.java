package net.fexcraft.app.json;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JsonObject<V> implements FJson {
	
	public V value;
	
	public JsonObject(){}
	
	public JsonObject(V value){
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
		return JsonHandler.toString(this);
	}

	public float float_value(){
		if(value instanceof Number == false) return 0;
		return ((Number)value).floatValue();
	}

	public int integer_value(){
		if(value instanceof Number == false) return 0;
		return ((Number)value).intValue();
	}

	public long long_value(){
		if(value instanceof Number == false) return 0;
		return ((Number)value).longValue();
	}

	public String string_value(){
		return value == null ? null : value.toString();
	}

	public boolean isNumber(){
		return value instanceof Number;
	}

	public boolean bool(){
		return (Boolean)value;
	}

}
