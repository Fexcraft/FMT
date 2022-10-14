package net.fexcraft.app.fmt.settings;

import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

public class RGBSetting extends Setting<RGB> {
	
	public RGBSetting(String id, RGB def, String group){
		super(id, def, group);
	}
	
	public RGBSetting(String id, RGB def, String group, JsonMap obj){
		super(id, def, group, obj);
	}
	
	@Override
	public void load(JsonMap obj){
		value.packed = obj.get(id, _default.packed);
	}
	
	@Override
	public void value(Object newval){
		if(newval instanceof RGB == false) return;
		value.packed = ((RGB)newval).packed;
	}
	
	@Override
	public void save(JsonMap obj){
		obj.add(id, JsonUtil.toJson(value.packed));
	}

	@Override
	public void validate(boolean apply, String string){
		//TODO
	}

}
