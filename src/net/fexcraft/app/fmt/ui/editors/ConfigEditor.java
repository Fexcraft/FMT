package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.ConfigGeneral;
import net.fexcraft.app.fmt.ui.components.PreviewGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigEditor extends Editor {

	public ConfigEditor(){
		super("config_editor", "Config Editor", false);
		addComponent(new ConfigGeneral());
	}

}
