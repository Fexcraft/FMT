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
		if(value instanceof Integer) return (int)value + 0f;
		if(value instanceof Long) return (long)value + 0f;
		return (float)value;
	}

	public int integer_value(){
		if(value instanceof Number == false) return 0;
		if(value instanceof Float) return (int)(float)value;
		if(value instanceof Long) return (int)(long)value;
		return (int)value;
	}

	public long long_value(){
		if(value instanceof Number == false) return 0;
		if(value instanceof Float) return (long)(float)value;
		if(value instanceof Integer) return (long)(int)value;
		return (long)value;
	}

	public String string_value(){
		return value == null ? null : value.toString();
	}

	public boolean isNumber(){
		return value instanceof Number;
	}

}
