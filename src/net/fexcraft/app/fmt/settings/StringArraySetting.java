package net.fexcraft.app.fmt.settings;

import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
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
	public Component createField(Component root, UpdateCompound updcom, int x, int y, int w, int h){
		SelectBox<String> box = new SelectBox<>(x, y, w, h);
		for(String str : vals) box.addElement(str);
		box.addSelectBoxChangeSelectionEventListener(lis -> value(lis.getNewValue()));
		Settings.applyMenuTheme(box);
		box.setSelected(value, true);
		return box;
	}

	public void setElms(String[] array){
		vals = array;
	}

}
