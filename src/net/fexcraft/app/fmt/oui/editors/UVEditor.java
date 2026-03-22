package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class UVEditor extends Editor {

	public UVEditor(){
		super("uv_editor", "UV Editor", false);
		addComponent(new UVComponent());
	}

}
