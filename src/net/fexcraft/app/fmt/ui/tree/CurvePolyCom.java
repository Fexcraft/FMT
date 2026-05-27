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
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CurvePolyCom extends TTabCom implements GroupCom.GroupComSubElm {

	public static PolygonValue CUR_AMOUNT = new PolygonValue(PolyVal.CUR_AMOUNT);
	public static PolygonValue CUR_ACTIVE = new PolygonValue(PolyVal.CUR_ACTIVE);
	public static PolygonValue CUR_AMT_PNT = new PolygonValue(PolyVal.CUR_POINTS);
	public static PolygonValue CUR_ACT_PNT = new PolygonValue(PolyVal.CUR_ACTIVE_POINT);
	public static PolygonValue CUR_AMT_PLN = new PolygonValue(PolyVal.CUR_PLANES);
	public static PolygonValue CUR_ACT_PLN = new PolygonValue(PolyVal.CUR_ACTIVE_PLANE);
	public static PolygonValue COLOR = new PolygonValue(PolyVal.COLOR);
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
			container.lastElement().add(new HidingElm().hoverable(true)
				.texture("icons/component/add").size(24, 24).pos(container.lastElement().w - 54, 1)
				.onclick(ci -> {
					if(polygon.selected) FMT.MODEL.updateValue(CUR_AMT_PNT, null, polygon.act_curve().points.size() + 1, true);
				})
				.hint("tree.polygon.curve.add_point").hide());
			container.lastElement().add(new HidingElm().hoverable(true)
				.texture("icons/component/add").size(24, 24).pos(container.lastElement().w - 26, 1)
				.onclick(ci -> {
					if(polygon.selected) FMT.MODEL.updateValue(CUR_AMT_PLN, null, polygon.act_curve().planes.size() + 1, true);
				})
				.hint("tree.polygon.curve.add_plane").hide());
			size += 28;
			for(int i = 0; i < cur.points.size(); i++){
				boolean ap = ac && cur.active_point == i;
				int pi = i;
				container.add(new Element().pos(2, size).size(FS, FS)
					.color(cur.points.get(i).color).onclick(ci -> {
						if(!polygon.selected) return;
						try(MemoryStack stack = MemoryStack.stackPush()){
							ByteBuffer color = stack.malloc(3);
							String result = TinyFileDialogs.tinyfd_colorChooser("Choose a Color", "#" + Integer.toHexString(cur.points.get(pi).color.packed), null, color);
							if(result == null) return;
							if(polygon.act_curve() != cur){
								FMT.MODEL.updateValue(CUR_ACTIVE, null, ic, true);
							}
							if(cur.active_point != pi){
								FMT.MODEL.updateValue(CUR_ACT_PNT, null, pi, true);
							}
							FMT.MODEL.updateValue(COLOR, null, Integer.parseInt(result.replace("#", ""), 16), true);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}));
				container.add(new Element().pos(30, size).size(EDITOR_CONTENT - 45, FS)
					.text("point " + pi).defTextPos()
					.text_color(GENERIC_TEXT_0.value.packed)
					.color(ap ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_0.value)
					.onclick(ci -> {
						if(polygon.selected && ac) FMT.MODEL.updateValue(CUR_ACT_PNT, null, pi, true);
					}));
				container.lastElement().add(new HidingElm().hoverable(true)
					.texture("icons/component/remove").size(24, 24).pos(container.lastElement().w - 26, 1)
					.onclick(ci -> {
						if(polygon.selected && ac) polygon.removePoint(pi);
					})
					.hint("tree.polygon.curve.remove_point").hide());
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
						if(polygon.selected && ac) FMT.MODEL.updateValue(CUR_ACT_PLN, null, pi, true);
					}));
				container.lastElement().add(new HidingElm().hoverable(true)
					.texture("icons/component/remove").size(24, 24).pos(container.lastElement().w - 26, 1)
					.onclick(ci -> {
						if(polygon.selected && ac) polygon.removePlane(pi);
					})
					.hint("tree.polygon.curve.remove_plane").hide());
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
