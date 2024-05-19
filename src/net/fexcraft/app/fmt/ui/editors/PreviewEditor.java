package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.MultiplierComponent;
import net.fexcraft.app.fmt.ui.components.PivotGeneral;
import net.fexcraft.app.fmt.ui.components.PreviewGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PreviewEditor extends Editor {

	public PreviewEditor(){
		super("helper_editor", "Helper Editor", false);
		addComponent(new MultiplierComponent());
		addComponent(new PreviewGeneral());
	}

}
