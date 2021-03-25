package net.fexcraft.app.fmt.settings;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.lib.common.math.RGB;

public class RGBSetting extends Setting<RGB> {
	
	public RGBSetting(String id, RGB def, String group){
		super(id, def, group);
	}
	
	public RGBSetting(String id, RGB def, String group, JsonObject obj){
		super(id, def, group, obj);
	}
	
	@Override
	public void load(JsonObject obj){
		value.packed = Jsoniser.get(obj, id, _default.packed);
	}
	
	@Override
	public void value(RGB newval){
		value.packed = newval.packed;
	}
	
	@Override
	public void save(JsonObject obj){
		obj.add(id, Jsoniser.toJson(value.packed));
	}

	@Override
	public void validate(boolean apply, String string){
		//TODO
	}

}
