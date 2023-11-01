package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.*;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class GroupEditor extends Editor {

	public GroupEditor(){
		super("group_editor", "Group Editor", false);
		if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new GroupGeneral());
	}

}
