package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.PivotGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotEditor extends Editor {

	public PivotEditor(){
		super("pivot_editor", "Pivot Editor", false);
		addComponent(new PivotGeneral());
	}

}
