package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.ui.Field.FieldType;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.CornerUtil;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.FMT.MODEL;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.ui.Field.FieldType.*;
import static net.fexcraft.app.fmt.ui.Field.col_field;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOPOLYSEL;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditorTab extends EditorTab {

	public ETabCom sorting;
	public ETabCom general_box;
	public ETabCom general;
	public ETabCom shapebox;
	public ETabCom cylinder;
	public ETabCom curve;
	public ETabCom marker;
	public ETabCom vertex;
	public Field name;
	public DropList group;
	public DropList polytype;
	public Field pos_x, pos_y, pos_z;
	public Field off_x, off_y, off_z;
	public Field siz_x, siz_y, siz_z;
	public Field tex_x, tex_y;
	public Field plane_loc;
	public Field vert_key, vert_sel;
	public Field vert_x, vert_y, vert_z;
	public BoolElm plane_lit;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((sorting = new ETabCom()), lang_prefix + "sorting", 220);
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "sorting.name"));
		sorting.add((name = new Field(TEXT, FF, field -> rename(field.get_text()))).pos(FO, next_y_pos(1)));
		sorting.lastElement().text(NOPOLYSEL);
		sorting.add(new TextElm(0, next_y_pos(1), FF - 20).translate(lang_prefix + "sorting.group"));
		sorting.add(new SideButton(0, next_y_pos(0), "icons/configeditor/add").hint("editor.polygon.sorting.add_group")
			.onclick(ci -> EditorSidePanel.AddPolygon.addGroup()));
		sorting.add((group = new DropList(FF).onchange((key, val) -> {
			Group ng = (Group)val;
			FMT.MODEL.selection_copy().forEach(poly -> {
				poly.group().remove(poly);
				ng.add(poly);
			});
		})).pos(FO, next_y_pos(1)));
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "sorting.type"));
		sorting.add((polytype = new DropList(FF - (FS + 5) * 2).onchange((key, val) -> {
			Shape shape = (Shape)val;
			ArrayList<Polygon> polis = FMT.MODEL.selected();
			for(Polygon poly : polis){
				Polygon npol = poly.convert(shape);
				if(npol != null){
					poly.group().add(npol);
					poly.group().remove(poly);
					FMT.MODEL.select(npol);
				}
			}
		})).pos(FO, next_y_pos(1)));
		sorting.add(new Element().color(col_bd).size(FS, FS).pos(FF - FS - FS, next_y_pos(0)).text("Rc")
			.text_centered(true).text_scale(0.75f).hoverable(true).onclick(info -> {
				ArrayList<Polygon> polis = FMT.MODEL.selected();
				if(polis.isEmpty()) return;
				for(Polygon poly : polis){
					if(!poly.getShape().isShapebox()) continue;
					Shapebox box = (Shapebox)poly;
					box.cor0 = new Vector3F();
					box.cor1 = new Vector3F();
					box.cor2 = new Vector3F();
					box.cor3 = new Vector3F();
					box.cor4 = new Vector3F();
					box.cor5 = new Vector3F();
					box.cor6 = new Vector3F();
					box.cor7 = new Vector3F();
					box.recompile();
				}
				UpdateHandler.update(new PolygonSelected(polis.get(0), polis.size(), polis.size()));
			})
			.hint(lang_prefix + "sorting.reset_corners"));
		sorting.add(new Element().color(col_bd).size(FS, FS).pos(FF - FS + 5, next_y_pos(0)).text("Rd")
			.text_centered(true).text_scale(0.75f).hoverable(true).onclick(info -> {
				ArrayList<Polygon> polis = FMT.MODEL.selected();
				if(polis.isEmpty()) return;
				float v;
				for(Polygon poly : polis){
					if(!poly.getShape().isRectagular()) continue;
					Box box = (Box)poly;
					v = box.size.x;
					box.size.x = (int)box.size.x + ((v % 1f >= 0.5) ? 1 : 0);
					v = box.size.y;
					box.size.y = (int)box.size.y + ((v % 1f >= 0.5) ? 1 : 0);
					v = box.size.z;
					box.size.z = (int)box.size.z + ((v % 1f >= 0.5) ? 1 : 0);
					box.recompile();
				}
				UpdateHandler.update(new PolygonSelected(polis.get(0), polis.size(), polis.size()));
			}).hint(lang_prefix + "sorting.reset_size"));
		//
		container.add((vertex = new ETabCom()), lang_prefix + "vertex", 160);
		vertex.add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + "vertex.index"));
		vertex.add((vert_key = new Field(INFO, F2S, field -> {})).deg_range().pos(F20, next_y_pos(1)));
		vertex.add((vert_sel = new Field(INFO, F2S, field -> {})).deg_range().pos(F21, next_y_pos(0)));
		vertex.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "vertex.offset"));
		vertex.add((vert_x = new Field(INFO, F3S, field -> applyVertOff(field.parse_float(), ValAxe.X))).deg_range().pos(F30, next_y_pos(1)));
		vertex.add((vert_y = new Field(INFO, F3S, field -> applyVertOff(field.parse_float(), ValAxe.Y))).deg_range().pos(F31, next_y_pos(0)));
		vertex.add((vert_z = new Field(INFO, F3S, field -> applyVertOff(field.parse_float(), ValAxe.Z))).deg_range().pos(F32, next_y_pos(0)));
		//
		container.add((general = new ETabCom()), lang_prefix + "general", 280);
		addGeneralElements(general, false);
		container.add((general_box = new ETabCom()), lang_prefix + "general", 400);
		addGeneralElements(general_box, true);
		general.visible = false;
		//
		container.add((shapebox = new ETabCom()), lang_prefix + "shapebox", 520);
		for(int i = 0; i < 8; i++){
			shapebox.add(new Element().pos(5, next_y_pos(i == 0 ? -1 : 1) + 5).size(20, 20).border(0xffffff).color(CornerUtil.CORNER_COLOURS[i]));
			shapebox.add(new TextElm(25, next_y_pos(0), FF - 20).translate(lang_prefix + "shapebox.corner", i));
			PolyVal val = PolyVal.values()[PolyVal.CORNER_0.ordinal() + i];
			shapebox.add((pos_x = new Field(FLOAT, F3S, updcom, new PolygonValue(val, ValAxe.X))).pos(F30, next_y_pos(1)));
			shapebox.add((pos_y = new Field(FLOAT, F3S, updcom, new PolygonValue(val, ValAxe.Y))).pos(F31, next_y_pos(0)));
			shapebox.add((pos_z = new Field(FLOAT, F3S, updcom, new PolygonValue(val, ValAxe.Z))).pos(F32, next_y_pos(0)));
		}
		//
		container.add((cylinder = new ETabCom()), lang_prefix + "cylinder", 520);
		cylinder.add(new TextElm(F20, next_y_pos(-1), F2S).translate(lang_prefix + "cylinder.radius_outer"));
		cylinder.add(new TextElm(F21, next_y_pos(0), F2S).translate(lang_prefix + "cylinder.radius_inner"));
		cylinder.add((new Field(FLOAT, F4S, updcom, new PolygonValue(PolyVal.RADIUS_O, ValAxe.X)).min_range(0.5f)).pos(F40, next_y_pos(1)));
		cylinder.add((new Field(FLOAT, F4S, updcom, new PolygonValue(PolyVal.RADIUS_O, ValAxe.Y)).min_range(0.0f)).pos(F41, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F4S, updcom, new PolygonValue(PolyVal.RADIUS_I, ValAxe.X)).min_range(0.0f)).pos(F42, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F4S, updcom, new PolygonValue(PolyVal.RADIUS_I, ValAxe.Y)).min_range(0.0f)).pos(F43, next_y_pos(0)));
		cylinder.add(new TextElm(F20, next_y_pos(1), F2S).translate(lang_prefix + "cylinder.length"));
		cylinder.add(new TextElm(F21, next_y_pos(0), F2S).translate(lang_prefix + "cylinder.direction"));
		cylinder.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.LENGTH)).min_range(0.5f)).pos(F20, next_y_pos(1)));
		cylinder.add((new Field(FieldType.INT, F2S, updcom, new PolygonValue(PolyVal.DIRECTION)).range(0, 5)).pos(F21, next_y_pos(0)));
		cylinder.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "cylinder.segments"));
		cylinder.add((new Field(FieldType.INT, F3S, updcom, new PolygonValue(PolyVal.SEGMENTS)).range(3, 360)).pos(F30, next_y_pos(1)));
		cylinder.add((new Field(FieldType.INT, F3S, updcom, new PolygonValue(PolyVal.SEG_LIMIT)).range(0, 360)).pos(F31, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SEG_OFF)).deg_range()).pos(F32, next_y_pos(0)));
		cylinder.add(new TextElm(F20, next_y_pos(1), F2S).translate(lang_prefix + "cylinder.base_scale"));
		cylinder.add(new TextElm(F21, next_y_pos(0), F2S).translate(lang_prefix + "cylinder.top_scale"));
		cylinder.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.BASE_SCALE)).min_range(0)).pos(F20, next_y_pos(1)));
		cylinder.add((new Field(FieldType.INT, F2S, updcom, new PolygonValue(PolyVal.TOP_SCALE)).min_range(0)).pos(F21, next_y_pos(0)));
		cylinder.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "cylinder.top_offset"));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_OFF, ValAxe.X))).pos(F30, next_y_pos(1)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_OFF, ValAxe.Y))).pos(F31, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_OFF, ValAxe.Z))).pos(F32, next_y_pos(0)));
		cylinder.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "cylinder.top_rotation"));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_ROT, ValAxe.X)).deg_range()).pos(F30, next_y_pos(1)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_ROT, ValAxe.Y)).deg_range()).pos(F31, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TOP_ROT, ValAxe.Z)).deg_range()).pos(F32, next_y_pos(0)));
		cylinder.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "cylinder.faces"));
		cylinder.add(new BoolElm(F60, next_y_pos(1), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.X), updcom));
		cylinder.add(new BoolElm(F61, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Y), updcom));
		cylinder.add(new BoolElm(F62, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Z), updcom));
		cylinder.add(new BoolElm(F63, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.X2), updcom));
		cylinder.add(new BoolElm(F64, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Y2), updcom));
		cylinder.add(new BoolElm(F65, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Z2), updcom));
		cylinder.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "cylinder.radial"));
		cylinder.add((new BoolElm(F30, next_y_pos(1), F3S).set(new PolygonValue(PolyVal.RADIAL), updcom)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SEG_WIDTH))).pos(F31, next_y_pos(0)));
		cylinder.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SEG_HEIGHT))).pos(F32, next_y_pos(0)));
		//
		container.add((curve = new ETabCom()), lang_prefix + "curve", 800);
		curve.add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + "general.rotation"));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.X))).deg_range().pos(F30, next_y_pos(1)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.Y))).deg_range().pos(F31, next_y_pos(0)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.Z))).deg_range().pos(F32, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture"));
		curve.add((tex_x = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, ValAxe.X))).pos(F30, next_y_pos(1)));
		curve.add((tex_y = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, ValAxe.Y))).pos(F31, next_y_pos(0)));
		curve.add(new Element().size(F3S, FS).pos(F32, next_y_pos(0)).color(col_field)
			.translate(lang_prefix + "general.texture.reset")
			.hint(lang_prefix + "general.texture.reset_hint")
			.text_centered(true).text_autoscale()
			.onclick(ci -> {
				FMT.MODEL.updateValue(tex_x.polyval(), tex_x.set(-1), 0);
				FMT.MODEL.updateValue(tex_y.polyval(), tex_y.set(-1), 0);
			}));
		curve.add(new TextElm(0, next_y_pos(1.5f), FF).translate(lang_prefix + "curve.length"));
		curve.add((new Field(INFO, F3S, updcom, new PolygonValue(PolyVal.CUR_LENGTH))).range(0, 360).pos(F30, next_y_pos(1)));
		curve.add((new BoolElm(F31, next_y_pos(0), F3S).set(new PolygonValue(PolyVal.RADIAL), updcom)));
		curve.add((new BoolElm(F32, next_y_pos(0), F3S).set(new PolygonValue(PolyVal.DIRECTION), updcom)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.active"));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_AMOUNT))).range(2, 50).pos(F20, next_y_pos(1)));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_ACTIVE))).min_range(0).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1.5f), FF).translate(lang_prefix + "curve.points"));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_POINTS))).range(2, 50).pos(F20, next_y_pos(1)));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_ACTIVE_POINT))).min_range(0).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF - 20).translate(lang_prefix + "curve.position"));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.X))).pos(F30, next_y_pos(1)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.Y))).pos(F31, next_y_pos(0)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.Z))).pos(F32, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.color"));
		curve.add((new Field(COLOR, F2S, updcom, new PolygonValue(PolyVal.COLOR))).pos(F20, next_y_pos(1)));
		curve.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.SCALE))).range(-16, 16).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1.5f), FF).translate(lang_prefix + "curve.planes"));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_PLANES))).range(2, 50).pos(F20, next_y_pos(1)));
		curve.add((new Field(INT, F2S, updcom, new PolygonValue(PolyVal.CUR_ACTIVE_PLANE))).min_range(0).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.size"));
		curve.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.SIZE, ValAxe.Y))).min_range(0).pos(F20, next_y_pos(1)));
		curve.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.SIZE, ValAxe.Z))).min_range(0).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.offset"));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.X))).pos(F30, next_y_pos(1)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.Y))).pos(F31, next_y_pos(0)));
		curve.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.Z))).pos(F32, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.location"));
		curve.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.PLANE_ROT))).deg_range().pos(F20, next_y_pos(1)));
		curve.add((plane_loc = new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.PLANE_LOC))).min_range(0).pos(F21, next_y_pos(0)));
		curve.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "curve.literal"));
		curve.add((plane_lit = new BoolElm(F20, next_y_pos(1), F2S).set(new PolygonValue(PolyVal.PLANE_LOC_LIT), updcom)));
		curve.add(new Element().size(F2S, FS).pos(F21, next_y_pos(0)).color(col_field)
			.translate(lang_prefix + "curve.reset")
			.hint(lang_prefix + "curve.reset_hint")
			.text_centered(true).text_autoscale()
			.onclick(ci -> {
				ArrayList<Polygon> sel = FMT.MODEL.selected();
				for(Polygon poly : sel){
					if(!poly.getShape().isCurve()) continue;
					CurvePolygon curv = (CurvePolygon)poly;
					Curve cu = curv.act_curve();
					int size = cu.planes.size() - 1;
					double loc = cu.litloc ? cu.path.length / size : 1f / size;
					for(int i = 0; i < cu.planes.size(); i++){
						cu.planes.get(i).location = loc * i;//(i + 1);
					}
					cu.compilePath();
					curv.recompile();
					UpdateHandler.update(new UpdateEvent.PolygonValueEvent(poly, plane_loc.polyval(), true));
					UpdateHandler.update(new UpdateEvent.PolygonValueEvent(poly, plane_lit.polyval(), true));
				}
			}));
 		//
		container.add((marker = new ETabCom()), lang_prefix + "marker", 220);
		marker.add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + "marker.color"));
		marker.add((new Field(COLOR, FF, updcom, new PolygonValue(PolyVal.COLOR))).pos(FO, next_y_pos(1)));
		marker.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "marker.scale"));
		marker.add((new Field(FLOAT, F2S, updcom, new PolygonValue(PolyVal.SCALE)).range(-256, 256)).pos(F20, next_y_pos(1)));
		marker.add((new BoolElm(F21, next_y_pos(0), F2S).set(new PolygonValue(PolyVal.DETACHED), updcom)));
		marker.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "marker.seat"));
		marker.add((new BoolElm(F30, next_y_pos(1), F3S).set(new PolygonValue(PolyVal.BIPED), updcom)));
		marker.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.BIPED_ANGLE)).deg_range()).pos(F31, next_y_pos(0)));
		marker.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.BIPED_SCALE)).range(-128, 128)).pos(F32, next_y_pos(0)));
		//
		updcom.add(UpdateEvent.GroupAdded.class, event -> updateLists());
		updcom.add(UpdateEvent.GroupRemoved.class, event -> updateLists());
		updcom.add(UpdateEvent.GroupRenamed.class, event -> updateLists());
		updcom.add(PolygonSelected.class, event -> {
			updatePolyTypeList();
			if(event.prevselected() < 0) return;
			if(event.selected() == 0){
				group.selectEntry(0);
				name.text(NOPOLYSEL);
			}
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() > 0)){
				group.selectKey(FMT.MODEL.first_selected().group().id);
				name.text(event.polygon().name());
				polytype.selectKey(FMT.MODEL.first_selected().getShape().getName());
			}
		});
		updcom.add(PolygonSelected.class, con -> {
			general.visible = false;
			general_box.visible = false;
			shapebox.visible = false;
			cylinder.visible = false;
			marker.visible = false;
			vertex.visible = false;
			ArrayList<Polygon> polys = FMT.MODEL.selected();
			boolean curv = true;
			for(Polygon poly : polys){
				if(!poly.getShape().isCurve()) curv = false;
				if(!curv){
					if(poly.getShape().isRectagular()){
						general_box.visible = true;
					}
					else{
						general.visible = true;
					}
				}
				if(poly.getShape().isShapebox()){
					shapebox.visible = true;
				}
				if(poly.getShape().isCylinder()){
					cylinder.visible = true;
				}
				if(poly.getShape().isCurve()){
					curve.visible = true;
				}
				if(poly.getShape().isMarker() || poly.getShape().isBoundingBox()){
					marker.visible = true;
				}
			}
			if(!curv){
				if(!general_box.visible && !general.visible) general.visible = true;
				if(general_box.visible && general.visible) general.visible = false;
			}
			reorderComponents();
		});
		updcom.add(UpdateEvent.VertexSelected.class, e -> {
			vert_sel.text(e.selected());
			if(e.selected() <= 0){
				vert_key.text("");
				vert_x.text(0);
				vert_y.text(0);
				vert_z.text(0);
			}
			else{
				Vertoff vo = e.pair().getLeft().vertoffs.get(e.pair().getRight());
				vert_key.text(e.pair().getRight().toString());
				vert_x.text(vo.off.x);
				vert_y.text(vo.off.y);
				vert_z.text(vo.off.z);
			}
		});
	}

	private void addGeneralElements(ETabCom general, boolean box){
		if(box){
			general.add(new TextElm(0, next_y_pos(-1), FF - 20).translate(lang_prefix + "general.box_size"));
			general.add(new PosCopyButton(0, next_y_pos(0), PolyVal.SIZE));
			general.add((siz_x = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, ValAxe.X))).min_range(0).pos(F30, next_y_pos(1)));
			general.add((siz_y = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, ValAxe.Y))).min_range(0).pos(F31, next_y_pos(0)));
			general.add((siz_z = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, ValAxe.Z))).min_range(0).pos(F32, next_y_pos(0)));
		}
		general.add(new TextElm(0, next_y_pos(box ? 1 : -1), FF - 20).translate(lang_prefix + "general.position"));
		general.add(new PosCopyButton(0, next_y_pos(0), PolyVal.POS));
		general.add((pos_x = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((pos_y = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add((pos_z = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, ValAxe.Z))).pos(F32, next_y_pos(0)));
		general.add(new TextElm(0, next_y_pos(1), FF - 45).translate(lang_prefix + "general.offset"));
		general.add(new PosCopyButton(0, next_y_pos(0), PolyVal.OFF));
		general.add(new SideButton(1, next_y_pos(0), "icons/polygon/marker").hint("editor.polygon.general.offset.center_box")
			.onclick(ci -> {
				if(MODEL.selected().isEmpty()) return;
				for(Polygon polygon : MODEL.selected()){
					FMT.MODEL.updateValue(off_x.polyval(), null, polygon.getValue(siz_x.polyval()) * -0.5f, true);
					FMT.MODEL.updateValue(off_y.polyval(), null, polygon.getValue(siz_y.polyval()) * -0.5f, true);
					FMT.MODEL.updateValue(off_z.polyval(), null, polygon.getValue(siz_z.polyval()) * -0.5f, true);
				}
			}));
		general.add((off_x = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((off_y = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add((off_z = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, ValAxe.Z))).pos(F32, next_y_pos(0)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.rotation"));
		general.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.X))).deg_range().pos(F30, next_y_pos(1)));
		general.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.Y))).deg_range().pos(F31, next_y_pos(0)));
		general.add((new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, ValAxe.Z))).deg_range().pos(F32, next_y_pos(0)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture"));
		general.add((tex_x = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((tex_y = new Field(FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add(new Element().size(F3S, FS).pos(F32, next_y_pos(0)).color(col_field)
			.translate(lang_prefix + "general.texture.reset")
			.hint(lang_prefix + "general.texture.reset_hint")
			.text_centered(true).text_autoscale()
			.onclick(ci -> {
				FMT.MODEL.updateValue(tex_x.polyval(), tex_x.set(-1), 0);
				FMT.MODEL.updateValue(tex_y.polyval(), tex_y.set(-1), 0);
			}));
		if(box){
			general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.box_faces"));
			general.add(new BoolElm(F60, next_y_pos(1), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.X), updcom));
			general.add(new BoolElm(F61, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Y), updcom));
			general.add(new BoolElm(F62, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Z), updcom));
			general.add(new BoolElm(F63, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.X2), updcom));
			general.add(new BoolElm(F64, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Y2), updcom));
			general.add(new BoolElm(F65, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, ValAxe.Z2), updcom));
		}
	}

	private void updateLists(){
		group.clear();
		if(FMT.MODEL != null && FMT.MODEL.allgroups().size() > 0){
			for(Group mg : FMT.MODEL.allgroups()){
				group.addEntry(mg.id, mg);
			}
			group.selectEntry(0);
		}
		updatePolyTypeList();
	}

	private void updatePolyTypeList(){
		polytype.clear();
		if(FMT.MODEL != null && FMT.MODEL.first_selected() != null){
			Shape polyshape = FMT.MODEL.first_selected().getShape();
			for(Shape shape : Shape.values()){
				if(shape.sharesConversionGroup(polyshape)){
					polytype.addEntry(shape.getName(), shape);
				}
			}
		}
	}

	private void rename(String str){
		ArrayList<Polygon> polis = FMT.MODEL.selected();
		if(polis.isEmpty()) return;
		else if(polis.size() == 1){
			polis.get(0).name(str);
		}
		else{
			for(int i = 0; i < polis.size(); i++){
				polis.get(i).name(str + String.format(POLYGON_SUFFIX.value, i));
			}
		}
	}

	private void applyVertOff(float v, ValAxe a){
		if(FMT.MODEL.getSelectedVerts().isEmpty()) return;
		Pair<Polygon, Vertoff.VOKey> pair = FMT.MODEL.getSelectedVerts().get(0);
		Vertoff vo = pair.getLeft().vertoffs.get(pair.getRight());
		switch(a){
			case X -> vo.off.x = v;
			case Y -> vo.off.y = v;
			case Z -> vo.off.z = v;
		}
		pair.getLeft().recompile();
	}

}
