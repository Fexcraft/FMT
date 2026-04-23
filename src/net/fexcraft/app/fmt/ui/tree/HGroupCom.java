package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HGroupCom extends Element {

	protected Group group;

	public HGroupCom(Group group){
		this.group = group;
		size(EDITOR_CONTENT - 10, 28);
	}

	@Override
	public void init(Object... args){
		text(group.id);
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(26, 26).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			group.visible = !group.visible;
			updateLabelColor();
		}).hint("tree.preview.visible").hide());
		updateLabelColor();
	}

	protected void updateLabelColor(){
		color((group.visible ? group.selected ? Settings.POLYGON_SELECTED : Settings.POLYGON_NORMAL : group.selected ? Settings.POLYGON_INV_SEL : Settings.POLYGON_INVISIBLE).value);
		text_color((group.selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

}
