package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotCom extends TTabCom {

	protected Pivot pivot;

	public PivotCom(Pivot rotpoi){
		pivot = rotpoi;
	}

	@Override
	public void init(Object... args){
		super.init(pivot.id, EDITOR_CONTENT);
		onclick(ci -> {
			FMT.MODEL.select(pivot);
			updateTextColor();
		});
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 30 * 2, 1).onclick(ci -> {
			pivot.visible = !pivot.visible;
			UpdateHandler.update(new UpdateEvent.PivotVisibility(pivot, pivot.visible));
		}).hint("tree.polygon.pivot.visible").hide());
		if(!pivot.root){
			add(new HidingElm().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 30 * 3, 1).onclick(ci -> {
				if(ASK_PIVOT_REMOVAL.value){
					//TODO
				}
				else FMT.MODEL.remPivot(pivot);
			}).hint("tree.polygon.pivot.remove").hide());
		}
		pivot.groups.forEach(group -> container.add(new GroupCom(group)));
	}

	protected void orderComponents(){
		if(container.elements == null) return;
		fullheight = 5;
		GroupCom com;
		for(Element elm : container.elements){
			com = (GroupCom)elm;
			com.pos(5, fullheight);
			fullheight += com.container.visible ? com.fullheight + 35 : 35;
		}
		container.size(w, fullheight);
		container.recompile();
		((TreeTab)root.root).reorderComponents();
	}

	protected void updateTextColor(){
		boolean sel = pivot.selected();
		color((pivot.visible ? sel ? Settings.PIVOT_SELECTED : Settings.PIVOT_NORMAL : sel ? Settings.PIVOT_INV_SEL : Settings.PIVOT_INVISIBLE).value);
		text_color((sel ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

	public void addGroup(Group group){
		container.add(new GroupCom(group));
		orderComponents();
	}

	public void remGroup(Group group){
		container.remElmIf(elm -> elm instanceof GroupCom com && com.group == group);
		orderComponents();
	}

	public GroupCom getGroupCom(Group group){
		for(Element elm : container.elements){
			if(elm instanceof GroupCom com && com.group == group) return com;
		}
		return null;
	}

}
