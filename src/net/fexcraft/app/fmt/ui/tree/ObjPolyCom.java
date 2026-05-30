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
import net.fexcraft.lib.common.math.RGB;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.nio.ByteBuffer;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;
import static net.fexcraft.app.fmt.ui.tree.CurvePolyCom.COLOR;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ObjPolyCom extends TTabCom implements GroupCom.GroupComSubElm {

	public static PolygonValue VERTICES = new PolygonValue(PolyVal.VERTICES);
	public static PolygonValue OBJ_FACES = new PolygonValue(PolyVal.OBJ_FACES);
	public static PolygonValue VERT_ACT = new PolygonValue(PolyVal.VERT_ACTIVE);
	public static PolygonValue OBJ_FACE_ACT = new PolygonValue(PolyVal.OBJ_FACE_ACTIVE);
	public static PolygonValue OBJ_FACE_VRT = new PolygonValue(PolyVal.OBJ_FACE_VERTEX);
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
		add(new HidingElm().hoverable(true).texture("icons/component/add").size(26, 26).pos(EDITOR_CONTENT - 34 * 5, 1)
			.onclick(ci -> polygon.addFace()).hint("tree.polygon.object.add_face").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/add").size(26, 26).pos(EDITOR_CONTENT - 34 * 6, 1)
			.onclick(ci -> polygon.addVertex()).hint("tree.polygon.object.add_vertex").hide());
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
			container.add(new Element().pos(2, size).size(FS, FS)
				.color(polygon.vertoffs.get(polygon.vectors.get(vi)).color).onclick(ci -> {
					if(!polygon.selected) return;
					int col = 0xffffff;
					if(ci.button() == 1){
						col = RGB.random().packed;
					}
					else{
						try(MemoryStack stack = MemoryStack.stackPush()){
							ByteBuffer color = stack.malloc(3);
							String result = TinyFileDialogs.tinyfd_colorChooser("Choose a Color", "#" + Integer.toHexString(polygon.vertoffs.get(polygon.vectors.get(vi)).color), null, color);
							if(result == null) return;
							col = Integer.parseInt(result.replace("#", ""), 16);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					}
					refresh();//if(polygon.selvec != vi) FMT.MODEL.updateValue(VERT_ACT, null, vi, true);
					polygon.vertoffs.get(polygon.vectors.get(vi)).color = col;
					polygon.vertoffs.get(polygon.vectors.get(vi)).arr_color = new RGB(col).toFloatArray();
					FMT.MODEL.updateValue(COLOR, null, col, true);
				}));
			container.add(new Element().pos(30, size).size(EDITOR_CONTENT - 45, FS)
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
			container.lastElement().add(new HidingElm().hoverable(true)
				.texture("icons/component/remove").size(24, 24).pos(container.lastElement().w - 26, 1)
				.onclick(ci -> {
					if(polygon.selected && av) polygon.removeVertex(vi);
				})
				.hint("tree.polygon.object.remove_vertex").hide());
			size += 28;
		}
		for(int f = 0; f < polygon.faces.size(); f++){
			boolean af = polygon.selfac == f;
			PolyObject.ObjFace face = polygon.faces.get(f);
			int fi = f;
			for(int vi = 0; vi < face.vecs.length; vi++){
				int v = vi;
				container.add(new Element().pos(2 + vi * 20, size).size(20, FS)
					.color(polygon.vertoffs.get(polygon.vectors.get(face.vecs[v])).color)
					.onclick(ci -> {
						int i = face.vecs[v] + (ci.button() == 0 ? 1 : -1);
						if(i >= polygon.vectors.size()) i = 0;
						if(i < 0) i = polygon.vectors.size() - 1;
						face.vecs[v] = i;
						polygon.recompile();
						refresh();
					}).text(face.vecs[v]).text_autoscale());
			}
			container.add(new Element().pos(85, size).size(EDITOR_CONTENT - 100, FS)
				.text("face " + f).defTextPos()
				.text_color(GENERIC_TEXT_0.value.packed)
				.color(af ? POLYGON_INV_SEL.value : GENERIC_BACKGROUND_1.value)
				.onclick(ci -> {
					if(polygon.selected) FMT.MODEL.updateValue(OBJ_FACE_ACT, null, fi, true);
				}));
			container.lastElement().add(new HidingElm().hoverable(true)
				.texture("icons/component/remove").size(24, 24).pos(container.lastElement().w - 26, 1)
				.onclick(ci -> {
					if(polygon.selected && af) polygon.removeFace(fi);
				})
				.hint("tree.polygon.object.remove_face").hide());
			container.lastElement().add(new HidingElm().hoverable(true)
				.texture("icons/component/adjust").size(24, 24).pos(container.lastElement().w - 52, 1)
				.onclick(ci -> {
					if(polygon.selected && af){
						polygon.toggleTriangleQuad(fi);
						FMT.MODEL.updateValue(OBJ_FACE_ACT, null, fi, true);
					}
				})
				.hint("tree.polygon.object.triangle_quad").hide());
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
