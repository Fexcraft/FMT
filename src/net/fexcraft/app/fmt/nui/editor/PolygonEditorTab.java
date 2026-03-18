package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.DropList;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.nui.Field;
import net.fexcraft.app.fmt.nui.TextElm;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.nui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.nui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.nui.editor.EditorRoot.NOPOLYSEL;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditorTab extends EditorTab {

	public ETabCom sorting;
	public Field name;
	public DropList group;
	public DropList polytype;
	public Field pos_x, pos_y, pos_z;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		add((sorting = new ETabCom()).pos(5, 5), lang_prefix + "sorting", 220);
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "name").text_scale(0.9f));
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
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "group").text_scale(0.9f));
		sorting.add((group = new DropList(FF).onchange((key, val) -> {
			Group ng = (Group)val;
			FMT.MODEL.selection_copy().forEach(poly -> {
				poly.group().remove(poly);
				ng.add(poly);
			});
		})).pos(FO, next_y_pos(1)));
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "type").text_scale(0.9f));
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
