package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CurvePolyCom extends TTabCom implements GroupCom.GroupComSubElm {

	public static PolygonValue CUR_ACTIVE = new PolygonValue(PolyVal.CUR_ACTIVE);
	public static PolygonValue CUR_ACT_PNT = new PolygonValue(PolyVal.CUR_ACTIVE_POINT);
	public static PolygonValue CUR_ACT_PLN = new PolygonValue(PolyVal.CUR_ACTIVE_PLANE);
	protected CurvePolygon polygon;

	public CurvePolyCom(Polygon poly){
		polygon = (CurvePolygon)poly;
	}

	@Override
	public void init(Object... args){
		super.init(polygon.name(), EDITOR_CONTENT - 10);
		onclick = ci -> {
			FMT.MODEL.select(polygon);
			updateLabelColor();
		};
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(26, 26).pos(EDITOR_CONTENT - 34 * 2, 1).onclick(ci -> {
			polygon.visible = !polygon.visible;
			UpdateHandler.update(new UpdateEvent.PolygonVisibility(polygon, polygon.visible));
		}).hint("tree.polygon.polygon.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 34 * 3, 1).onclick(ci -> {
			if(ASK_POLYGON_REMOVAL.value){
				FMT.UI.createDialog(500, 120, "tree.mode.polygon")
					.addText(0, "tree.polygon.polygon.removal")
					.addText(1, polygon.name() + " (" + polygon.getShape() + ")")
					.consumer(d -> polygon.group().remove(polygon), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else polygon.group().remove(polygon);
		}).hint("tree.polygon.polygon.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(26, 26).pos(EDITOR_CONTENT - 34 * 4, 1).onclick(ci -> {
			EditorRoot.setMode(EditorRoot.EditorMode.POLYGON);
		}).hint("tree.polygon.polygon.editor").hide());
		updateLabelColor();
		container.hide();
	}

	@Override
	protected void orderComponents(){
		refresh();
	}

	@Override
	public Polygon polygon(){
		return polygon;
	}

	@Override
	public void refresh(){
		container.clearElements(false);
		int size = 2, c = 0;
		for(Curve cur : polygon.curves){
			boolean ac = polygon.active == c;
			int ic = c;
			container.add(new Element().pos(2, size).size(EDITOR_CONTENT - 14, FS)
				.text("curve " + c).defTextPos()
				.text_color(ac ? GENERIC_TEXT_1.value.packed : GENERIC_TEXT_2.value.packed)
				.color(ac ? POLYGON_SELECTED.value : GENERIC_BACKGROUND_2.value)
				.onclick(ci -> {
					if(polygon.selected) FMT.MODEL.updateValue(CUR_ACTIVE, null, ic, true);
				}));
			size += 28;
			for(int i = 0; i < cur.points.size(); i++){
				boolean ap = ac && cur.active_point == i;
				int pi = i;
				container.add(new Element().pos(2, size).size(FS, FS)
					.color(cur.points.get(i).color));//TODO open color-picker on click
				container.add(new Element().pos(30, size).size(EDITOR_CONTENT - 45, FS)
					.text("point " + pi).defTextPos()
					.text_color(GENERIC_TEXT_0.value.packed)
					.color(ap ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_0.value)
					.onclick(ci -> {
						if(polygon.selected) FMT.MODEL.updateValue(CUR_ACT_PNT, null, pi, true);
					}));
				size += 28;
			}
			for(int i = 0; i < cur.planes.size(); i++){
				boolean ap = ac && cur.active_segment == i;
				int pi = i;
				container.add(new Element().pos(5, size).size(EDITOR_CONTENT - 18, FS)
					.text("plane " + pi).defTextPos()
					.text_color(GENERIC_TEXT_0.value.packed)
					.color(ap ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_0.value)
					.onclick(ci -> {
						if(polygon.selected) FMT.MODEL.updateValue(CUR_ACT_PLN, null, pi, true);
					}));
				size += 28;
			}
			c++;
		}
		container.size(EDITOR_CONTENT - 10, size);
		container.recompile();
	}

	@Override
	public float height(){
		return h + (container.visible ? container.h : 0);
	}

	@Override
	public void updateLabelColor(){
		color((polygon.visible ? polygon.selected ? Settings.POLYGON_SELECTED : Settings.POLYGON_NORMAL : polygon.selected ? Settings.POLYGON_INV_SEL : Settings.POLYGON_INVISIBLE).value);
		text_color((polygon.selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

	@Override
	protected void updateTextColor(){
		updateLabelColor();
	}

	@Override
	protected void minimized_changed(){
		((TTabCom)root.root).orderComponents();
	}

}
