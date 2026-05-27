package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import org.apache.commons.lang3.tuple.Pair;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ObjPolyCom extends TTabCom implements GroupCom.GroupComSubElm {

	public static PolygonValue VERTICES = new PolygonValue(PolyVal.VERTICES);
	public static PolygonValue OBJ_FACES = new PolygonValue(PolyVal.OBJ_FACES);
	public static PolygonValue VERT_ACT = new PolygonValue(PolyVal.VERT_ACTIVE);
	public static PolygonValue OBJ_FACE_ACT = new PolygonValue(PolyVal.OBJ_FACE_ACTIVE);
	protected PolyObject polygon;

	public ObjPolyCom(Polygon poly){
		polygon = (PolyObject)poly;
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
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 34 * 3, 1)
			.onclick(ci -> FMT.MODEL.askAndRemove(polygon)).hint("tree.polygon.polygon.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(26, 26).pos(EDITOR_CONTENT - 34 * 4, 1)
			.onclick(ci -> EditorRoot.setMode(EditorRoot.EditorMode.POLYGON)).hint("tree.polygon.polygon.editor").hide());
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
		int size = 2;
		for(int v = 0; v < polygon.vectors.size(); v++){
			boolean av = polygon.selvec == v;
			int vi = v;
			container.add(new Element().pos(2, size).size(EDITOR_CONTENT - 14, FS)
				.text("vector " + v).defTextPos()
				.text_color(GENERIC_TEXT_0.value.packed)
				.color(av ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_1.value)
				.onclick(ci -> {
					if(polygon.selected){
						FMT.MODEL.updateValue(VERT_ACT, null, vi, true);
						FMT.MODEL.clearSelectedVerts();
						FMT.MODEL.select(Pair.of(polygon, polygon.vectors.get(vi)));
					}
				}));
			size += 28;
		}
		for(int f = 0; f < polygon.faces.size(); f++){
			boolean af = polygon.selfac == f;
			int fi = f;
			container.add(new Element().pos(2, size).size(EDITOR_CONTENT - 14, FS)
				.text("face " + f).defTextPos()
				.text_color(GENERIC_TEXT_0.value.packed)
				.color(af ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_1.value)
				.onclick(ci -> {
					if(polygon.selected) FMT.MODEL.updateValue(OBJ_FACE_ACT, null, fi, true);
				}));
			size += 28;
		}
		container.size(EDITOR_CONTENT - 10, size);
		container.recompile();
	}

	@Override
	public float height(){
		return h + (container.visible ? container.h : 0);
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
