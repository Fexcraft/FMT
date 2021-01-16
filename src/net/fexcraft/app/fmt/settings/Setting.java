package net.fexcraft.app.fmt.settings;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;

public class Setting<TYPE> {
	
	public final String id;
	public TYPE _default, value;
	
	public Setting(String id, TYPE def){
		this._default = value = def;
		this.id = id;
	}
	
	public Setting(String id, TYPE def, JsonObject obj){
		this(id, def);
		value = (TYPE)Jsoniser.get(obj, id, def);
	}

	public void save(JsonObject obj){
		obj.add(id, Jsoniser.toJson(value));
	}

}
