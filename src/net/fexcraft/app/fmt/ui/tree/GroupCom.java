package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.ASK_GROUP_REMOVAL;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class GroupCom extends TTabCom {

	protected Group group;

	public GroupCom(Group gruppo){
		group = gruppo;
	}

	@Override
	public void init(Object... args){
		super.init(group.id, group.size() * 30);
		label.onclick(ci -> {
			FMT.MODEL.select(group);
			updateLabelColor();
		});
		label.add(new Element().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 30 * 2, 1).onclick(ci -> {
			group.visible = !group.visible;
			UpdateHandler.update(new UpdateEvent.GroupVisibility(group, group.visible));
		}).hint("tree.polygon.group.visible"));
		label.add(new Element().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 30 * 3, 1).onclick(ci -> {
			if(ASK_GROUP_REMOVAL.value){
				//TODO
			}
			else FMT.MODEL.remGroup(group);
		}).hint("tree.polygon.group.remove"));
	}

	protected void updateLabelColor(){
		label.color((group.visible ? group.selected ? Settings.GROUP_SELECTED : Settings.GROUP_NORMAL : group.selected ? Settings.GROUP_INV_SEL : Settings.GROUP_INVISIBLE).value);
		label.text_color((group.selected ? col_85 : col_cd).packed);
	}

}
