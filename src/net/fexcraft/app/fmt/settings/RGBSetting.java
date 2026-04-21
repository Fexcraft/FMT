package net.fexcraft.app.fmt.settings;

import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

public class RGBSetting extends Setting<RGB> {
	
	public RGBSetting(String id, RGB def, String group){
		super(id, def, group);
	}

	public RGBSetting(String id, int def, String group){
		super(id, new RGB(def), group);
	}
	
	@Override
	public void load(JsonMap obj){
		try{
			value.packed = Integer.parseInt(obj.get(id).string_value(), 16);
		}
		catch(Exception e){
			e.printStackTrace();
			value = _default;
		}
	}
	
	@Override
	public void value(Object newval){
		if(newval instanceof RGB == false) return;
		value.packed = ((RGB)newval).packed;
	}
	
	@Override
	public void save(JsonMap obj){
		obj.add(id, Field.to6HexString(value.packed));
	}

	@Override
	public void validate(boolean apply, String string){
		//TODO
	}

}
