package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.CurvePolygon;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyObject;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
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
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			group.visible = !group.visible;
			UpdateHandler.update(new UpdateEvent.GroupVisibility(group, group.visible));
		}).hint("tree.polygon.group.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 32 * 3, 1).onclick(ci -> {
			if(ASK_GROUP_REMOVAL.value){
				FMT.UI.createDialog(500, 120, "tree.mode.polygon")
					.addText(0, "tree.polygon.group.removal")
					.addText(1, group.id + " (" + group.size() + " polygons)")
					.consumer(d -> FMT.MODEL.remGroup(group), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else FMT.MODEL.remGroup(group);
		}).hint("tree.polygon.group.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(28, 28).pos(EDITOR_CONTENT - 32 * 4, 1).onclick(ci -> {
			EditorRoot.setMode(EditorRoot.EditorMode.GROUP);
		}).hint("tree.polygon.group.editor").hide());
		group.forEach(poly -> container.add(genNew(poly)));
		orderComponents();
		if(group.minimized) hide();
	}

	@Override
	protected void minimized_changed(){
		group.minimized = !container.visible;
	}

	protected void orderComponents(){
		fullheight = container.visible ? 5 : 0;
		if(container.elements != null){
			for(Element elm : container.elements){
				elm.pos(5, fullheight);
				((GroupComSubElm)elm).refresh();
				fullheight += ((GroupComSubElm)elm).height() + 2;
			}
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
		container.add(genNew(poly));
		orderComponents();
	}

	public Element genNew(Polygon poly){
		if(poly instanceof CurvePolygon) return new CurvePolyCom(poly);
		if(poly instanceof PolyObject) return new ObjPolyCom(poly);
		return new PolygonCom(poly);
	}

	public void remPolygon(Polygon poly){
		container.remElmIf(elm -> elm instanceof GroupComSubElm com && com.polygon() == poly);
		orderComponents();
	}

	public GroupComSubElm getPolyCom(Polygon poly){
		for(Element elm : container.elements){
			if(elm instanceof GroupComSubElm com && com.polygon() == poly) return com;
		}
		return null;
	}

	public static interface GroupComSubElm {

		public Polygon polygon();

		public void refresh();

		public float height();

		public default void updateLabelColor(){
			((Element)this).color((polygon().visible ? polygon().selected ? Settings.POLYGON_SELECTED : Settings.POLYGON_NORMAL : polygon().selected ? Settings.POLYGON_INV_SEL : Settings.POLYGON_INVISIBLE).value);
			((Element)this).text_color((polygon().selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
		}

	}

}
