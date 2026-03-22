package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.ConfigGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigEditor extends Editor {

	public ConfigEditor(){
		super("config_editor", "Config Editor", false);
		addComponent(new ConfigGeneral());
	}

}
