package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.ModelExports;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelEditor extends Editor {

	public ModelEditor(){
		super("model_editor", "Model Editor", false);
		addComponent(new ModelExports());
	}

}
