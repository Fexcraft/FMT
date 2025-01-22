package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.PivotGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotEditor extends Editor {

	public PivotEditor(){
		super("pivot_editor", "Pivot Editor", false);
		addComponent(new PivotGeneral());
	}

}
