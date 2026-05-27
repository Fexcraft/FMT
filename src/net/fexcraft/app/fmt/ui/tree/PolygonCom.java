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
public class PolygonCom extends Element implements GroupCom.GroupComSubElm {

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
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 32 * 3, 1)
			.onclick(ci -> FMT.MODEL.askAndRemove(polygon)).hint("tree.polygon.polygon.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(26, 26).pos(EDITOR_CONTENT - 32 * 4, 1)
			.onclick(ci -> EditorRoot.setMode(EditorRoot.EditorMode.POLYGON)).hint("tree.polygon.polygon.editor").hide());
		updateLabelColor();
	}

	@Override
	public Polygon polygon(){
		return polygon;
	}

	@Override
	public void refresh(){}

	@Override
	public float height(){
		return h;
	}

}
