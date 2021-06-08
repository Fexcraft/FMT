package net.fexcraft.app.fmt.settings;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.json.JsonMap;

public class StringArraySetting extends Setting<String> {
	
	protected String[] vals;

	public StringArraySetting(String id, String def, String group, String... vals){
		super(id, def, group);
		this.vals = vals;
	}
	
	public StringArraySetting(String id, String def, String group, JsonMap obj, String... vals){
		super(id, def, group, obj);
		this.vals = vals;
	}

	@Override
	public void validate(boolean apply, String string){
		//TODO
	}

	@Override
	public Component createField(Component root, UpdateHolder holder, int x, int y, int w, int h){
		SelectBox<String> box = new SelectBox<>(x, y, w, h);
		for(String str : vals) box.addElement(str);
		box.addSelectBoxChangeSelectionEventListener(lis -> value(lis.getNewValue()));
		Settings.applyMenuTheme(box);
		box.setSelected(value, true);
		return box;
	}

}
