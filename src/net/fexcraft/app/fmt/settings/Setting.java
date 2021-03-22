package net.fexcraft.app.fmt.settings;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;

public class Setting<TYPE> {
	
	public final String id, group;
	public TYPE _default, value;
	
	public Setting(String id, TYPE def, String group){
		this._default = value = def;
		this.group = group;
		this.id = id;
		Settings.register(group, id, this);
	}
	
	public Setting(String id, TYPE def, String group, JsonObject obj){
		this(id, def, group);
		load(Jsoniser.getSubObj(obj, group));
		Settings.register(group, id, this);
	}
	
	public void load(JsonObject obj){
		value = (TYPE)Jsoniser.get(obj, id, _default);
	}

	public <VALUE> VALUE value(){
		return (VALUE)value;
	}
	
	public void value(TYPE newval){
		this.value = newval;
	}

	public void save(JsonObject obj){
		obj.add(id, Jsoniser.toJson(value));
	}

	public void validate(boolean apply, String string){
		// TODO Auto-generated method stub
	}

	public boolean toggle(){
		if(value instanceof Boolean){
			Object obj = !(Boolean)value;
			value((TYPE)obj);
		}
		return value();
	}

}
