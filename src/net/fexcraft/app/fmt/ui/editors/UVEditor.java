package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class UVEditor extends Editor {

	public UVEditor(){
		super("uv_editor", "UV Editor", false);
		if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new UVComponent());
	}

}
