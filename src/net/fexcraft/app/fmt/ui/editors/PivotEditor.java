package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.GroupGeneral;
import net.fexcraft.app.fmt.ui.components.MultiplierComponent;
import net.fexcraft.app.fmt.ui.components.PivotGeneral;
import net.fexcraft.app.fmt.ui.components.QuickAdd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotEditor extends Editor {

	public PivotEditor(){
		super("pivot_editor", "Pivot Editor", false);
		addComponent(new MultiplierComponent());
		addComponent(new PivotGeneral());
	}

}
