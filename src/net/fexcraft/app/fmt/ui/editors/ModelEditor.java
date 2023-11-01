package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.GroupGeneral;
import net.fexcraft.app.fmt.ui.components.ModelExports;
import net.fexcraft.app.fmt.ui.components.ModelGeneral;
import net.fexcraft.app.fmt.ui.components.MultiplierComponent;
import net.fexcraft.app.fmt.ui.components.QuickAdd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelEditor extends Editor {

	public ModelEditor(){
		super("model_editor", "Model Editor", false);
		if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new ModelGeneral());
		addComponent(new ModelExports());
	}

}
