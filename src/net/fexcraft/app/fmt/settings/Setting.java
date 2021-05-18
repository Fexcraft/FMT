package net.fexcraft.app.fmt.settings;

import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.json.JsonMap;

public class Setting<TYPE> {
	
	public final String id, group;
	public TYPE _default, value;
	
	public Setting(String id, TYPE def, String group){
		this._default = value = def;
		this.group = group;
		this.id = id;
		Settings.register(group, id, this);
	}
	
	public Setting(String id, TYPE def, String group, JsonMap obj){
		this(id, def, group);
		load(obj.getMap(group));
		//Settings.register(group, id, this);
	}
	
	public void load(JsonMap obj){
		value = (TYPE)obj.get(id, _default);
	}

	public <VALUE> VALUE value(){
		return (VALUE)value;
	}
	
	public void value(TYPE newval){
		this.value = newval;
	}

	public void save(JsonMap obj){
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
