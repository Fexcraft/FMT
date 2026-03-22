package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.CornerUtil;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.FMT.MODEL;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;
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
	public Field name;
	public DropList group;
	public DropList polytype;
	public Field pos_x, pos_y, pos_z;
	public Field off_x, off_y, off_z;
	public Field siz_x, siz_y, siz_z;
	public Field tex_x, tex_y;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		add((sorting = new ETabCom()), lang_prefix + "sorting", 220);
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "sorting.name"));
		sorting.add((name = new Field(TEXT, FF, str -> rename(str))).pos(FO, next_y_pos(1)));
		sorting.lastElement().text(NOPOLYSEL);
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
				group.selectEntry(FMT.MODEL.first_selected().group().id);
				name.text(event.polygon().name());
				polytype.selectEntry(FMT.MODEL.first_selected().getShape().getName());
			}
		});
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "sorting.group"));
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
		add((general = new ETabCom()), lang_prefix + "general", 280);
		addGeneralElements(general, false);
		add((general_box = new ETabCom()), lang_prefix + "general", 400);
		addGeneralElements(general_box, true);
		general.visible = false;
		//
		add((shapebox = new ETabCom()), lang_prefix + "shapebox", 520);
		for(int i = 0; i < 8; i++){
			shapebox.add(new Element().pos(5, next_y_pos(i == 0 ? -1 : 1) + 5).size(20, 20).border(0xffffff).color(CornerUtil.CORNER_COLOURS[i]));
			shapebox.add(new TextElm(25, next_y_pos(0), FF - 20).translate(lang_prefix + "shapebox.corner", i));
			PolyVal val = PolyVal.values()[PolyVal.CORNER_0.ordinal() + i];
			shapebox.add((pos_x = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(val, PolyVal.ValAxe.X))).pos(F30, next_y_pos(1)));
			shapebox.add((pos_y = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(val, PolyVal.ValAxe.Y))).pos(F31, next_y_pos(0)));
			shapebox.add((pos_z = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(val, PolyVal.ValAxe.Z))).pos(F32, next_y_pos(0)));
		}
		//
		updcom.add(PolygonSelected.class, con -> {
			general.visible = false;
			general_box.visible = false;
			shapebox.visible = false;
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
					//cylinder.visible = true;
				}
				if(poly.getShape().isCurve()){
					//curve.visible = true;
				}
				if(poly.getShape().isMarker() || poly.getShape().isBoundingBox()){
					//marker.visible = true;
				}
			}
			if(!curv){
				if(!general_box.visible && !general.visible) general.visible = true;
				if(general_box.visible && general.visible) general.visible = false;
			}
			reorderComponents();
		});
	}

	private void addGeneralElements(ETabCom general, boolean box){
		if(box){
			general.add(new TextElm(0, next_y_pos(-1), FF - 20).translate(lang_prefix + "general.box_size"));
			general.add(new PosCopyButton(0, next_y_pos(0), PolyVal.SIZE));
			general.add((siz_x = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, PolyVal.ValAxe.X))).min_range(0).pos(F30, next_y_pos(1)));
			general.add((siz_y = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, PolyVal.ValAxe.Y))).min_range(0).pos(F31, next_y_pos(0)));
			general.add((siz_z = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.SIZE, PolyVal.ValAxe.Z))).min_range(0).pos(F32, next_y_pos(0)));
		}
		general.add(new TextElm(0, next_y_pos(box ? 1 : -1), FF - 20).translate(lang_prefix + "general.position"));
		general.add(new PosCopyButton(0, next_y_pos(0), PolyVal.POS));
		general.add((pos_x = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, PolyVal.ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((pos_y = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, PolyVal.ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add((pos_z = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.POS, PolyVal.ValAxe.Z))).pos(F32, next_y_pos(0)));
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
		general.add((off_x = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, PolyVal.ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((off_y = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, PolyVal.ValAxe.Y))).pos(F31, next_y_pos(0)));
		general.add((off_z = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.OFF, PolyVal.ValAxe.Z))).pos(F32, next_y_pos(0)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.rotation"));
		general.add((new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, PolyVal.ValAxe.X))).range(-180, 180).pos(F30, next_y_pos(1)));
		general.add((new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, PolyVal.ValAxe.Y))).range(-180, 180).pos(F31, next_y_pos(0)));
		general.add((new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.ROT, PolyVal.ValAxe.Z))).range(-180, 180).pos(F32, next_y_pos(0)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture"));
		general.add((tex_x = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.X))).pos(F30, next_y_pos(1)));
		general.add((tex_y = new Field(Field.FieldType.FLOAT, F3S, updcom, new PolygonValue(PolyVal.TEX, PolyVal.ValAxe.Y))).pos(F31, next_y_pos(0)));
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
			general.add(new BoolElm(F60, next_y_pos(1), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.X), updcom));
			general.add(new BoolElm(F61, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.Y), updcom));
			general.add(new BoolElm(F62, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.Z), updcom));
			general.add(new BoolElm(F63, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.X2), updcom));
			general.add(new BoolElm(F64, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.Y2), updcom));
			general.add(new BoolElm(F65, next_y_pos(0), F6S).set(new PolygonValue(PolyVal.SIDES, PolyVal.ValAxe.Z2), updcom));
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

}
