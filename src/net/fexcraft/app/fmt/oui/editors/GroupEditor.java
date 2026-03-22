package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class GroupEditor extends Editor {

	public GroupEditor(){
		super("group_editor", "Group Editor", false);
		addComponent(new GroupGeneral());
	}

}
