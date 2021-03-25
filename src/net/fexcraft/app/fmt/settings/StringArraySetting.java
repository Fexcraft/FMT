package net.fexcraft.app.fmt.settings;

import com.google.gson.JsonObject;

public class StringArraySetting extends Setting<String> {
	
	protected String[] vals;

	public StringArraySetting(String id, String def, String group, String... vals){
		super(id, def, group);
		this.vals = vals;
	}
	
	public StringArraySetting(String id, String def, String group, JsonObject obj, String... vals){
		super(id, def, group, obj);
		this.vals = vals;
	}

	@Override
	public void validate(boolean apply, String string){
		//TODO
	}

}
