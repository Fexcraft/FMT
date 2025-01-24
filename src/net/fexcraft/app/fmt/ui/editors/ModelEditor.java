package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.ModelExports;
import net.fexcraft.app.fmt.ui.components.ModelGeneral;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelEditor extends Editor {

	public ModelEditor(){
		super("model_editor", "Model Editor", false);
		addComponent(new ModelGeneral());
		addComponent(new ModelExports());
	}

}
