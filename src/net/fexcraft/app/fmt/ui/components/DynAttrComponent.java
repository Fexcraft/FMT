package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.utils.fvtm.VehAttr;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DynAttrComponent extends EditorComponent {

	public DynAttrComponent(String key, VehAttr attr){
		super("variable.dynamic", 60, false, true);
		label.getTextState().setText(key);

	}

}
