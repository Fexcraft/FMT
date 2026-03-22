package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.PreviewGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PreviewEditor extends Editor {

	public PreviewEditor(){
		super("helper_editor", "Helper Editor", false);
		addComponent(new PreviewGeneral());
	}

}
