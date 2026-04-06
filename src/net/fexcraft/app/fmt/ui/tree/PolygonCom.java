package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.ASK_POLYGON_REMOVAL;
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
		add(new Element().hoverable(true).texture("icons/component/visible").size(26, 26).pos(EDITOR_CONTENT - 40, 1).onclick(ci -> {
			polygon.visible = !polygon.visible;
			UpdateHandler.update(new UpdateEvent.PolygonVisibility(polygon, polygon.visible));
		}).hint("tree.polygon.polygon.visible").hide());
		add(new Element().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 70, 1).onclick(ci -> {
			if(ASK_POLYGON_REMOVAL.value){
				//TODO
			}
			else polygon.group().remove(polygon);
		}).hint("tree.polygon.polygon.remove").hide());
		updateLabelColor();
	}

	@Override
	public void hovered(boolean bool){
		super.hovered(bool);
		if(bool){
			elements.get(0).show();
			elements.get(1).show();
		}
		else{
			elements.get(0).hide();
			elements.get(1).hide();
		}
	}

	protected void updateLabelColor(){
		color((polygon.visible ? polygon.selected ? Settings.POLYGON_SELECTED : Settings.POLYGON_NORMAL : polygon.selected ? Settings.POLYGON_INV_SEL : Settings.POLYGON_INVISIBLE).value);
		text_color((polygon.selected ? col_85 : col_cd).packed);
	}

}
