package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
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
public class GroupCom extends TTabCom {

	protected Group group;

	public GroupCom(Group gruppo){
		group = gruppo;
	}

	@Override
	public void init(Object... args){
		super.init(group.id, EDITOR_CONTENT - 5);
		onclick(ci -> {
			FMT.MODEL.select(group);
			updateTextColor();
		});
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 35 * 2, 1).onclick(ci -> {
			group.visible = !group.visible;
			UpdateHandler.update(new UpdateEvent.GroupVisibility(group, group.visible));
		}).hint("tree.polygon.group.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 35 * 3, 1).onclick(ci -> {
			if(ASK_GROUP_REMOVAL.value){
				//TODO
			}
			else FMT.MODEL.remGroup(group);
		}).hint("tree.polygon.group.remove").hide());
		group.forEach(poly -> container.add(new PolygonCom(poly)));
		orderComponents();
	}

	protected void orderComponents(){
		if(container.elements == null) return;
		fullheight = 5;
		PolygonCom com;
		for(Element elm : container.elements){
			com = (PolygonCom)elm;
			com.pos(5, fullheight);
			fullheight += 30;
		}
		container.size(w, fullheight += 5);
		container.recompile();
		((TTabCom)root.root).orderComponents();
	}

	protected void updateTextColor(){
		color((group.visible ? group.selected ? Settings.GROUP_SELECTED : Settings.GROUP_NORMAL : group.selected ? Settings.GROUP_INV_SEL : Settings.GROUP_INVISIBLE).value);
		text_color((group.selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

	public void addPolygon(Polygon poly){
		container.add(new PolygonCom(poly));
		orderComponents();
	}

	public void remPolygon(Polygon poly){
		container.remElmIf(elm -> elm instanceof PolygonCom com && com.polygon == poly);
		orderComponents();
	}


	public PolygonCom getPolyCom(Polygon poly){
		for(Element elm : container.elements){
			if(elm instanceof PolygonCom com && com.polygon == poly) return com;
		}
		return null;
	}

}
