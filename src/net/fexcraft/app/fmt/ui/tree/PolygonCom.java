package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
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
public class PolygonCom extends Element {

	protected Polygon polygon;

	public PolygonCom(Polygon poly){
		polygon = poly;
		onclick = ci -> {
			FMT.MODEL.select(polygon);
			updateLabelColor();
		};
		size(EDITOR_CONTENT - 10, 28);
	}

	@Override
	public void init(Object... args){
		text(polygon.name());
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(26, 26).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			polygon.visible = !polygon.visible;
			UpdateHandler.update(new UpdateEvent.PolygonVisibility(polygon, polygon.visible));
		}).hint("tree.polygon.polygon.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 32 * 3, 1).onclick(ci -> {
			if(ASK_POLYGON_REMOVAL.value){
				FMT.UI.createDialog(500, 120, "tree.mode.polygon")
					.addText(0, "tree.polygon.polygon.removal")
					.addText(1, polygon.name() + " (" + polygon.getShape() + ")")
					.consumer(d -> polygon.group().remove(polygon), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else polygon.group().remove(polygon);
		}).hint("tree.polygon.polygon.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(26, 26).pos(EDITOR_CONTENT - 32 * 4, 1).onclick(ci -> {
			EditorRoot.setMode(EditorRoot.EditorMode.POLYGON);
		}).hint("tree.polygon.polygon.editor").hide());
		updateLabelColor();
	}

	protected void updateLabelColor(){
		color((polygon.visible ? polygon.selected ? Settings.POLYGON_SELECTED : Settings.POLYGON_NORMAL : polygon.selected ? Settings.POLYGON_INV_SEL : Settings.POLYGON_INVISIBLE).value);
		text_color((polygon.selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

}
