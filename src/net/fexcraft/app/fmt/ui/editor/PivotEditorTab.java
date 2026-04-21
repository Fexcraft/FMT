package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.ui.BoolElm;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.ui.Field.FieldType.FLOAT;
import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOPIVOTSEL;
import static net.fexcraft.lib.common.Static.sixteenth;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotEditorTab extends EditorTab {

	public ETabCom general;
	public ETabCom attrlinks;
	private Field name;
	private Field pos16x, pos16y, pos16z;
	private Field posx, posy, posz;
	private Field rotx, roty, rotz;
	private Field[] pos_attr = new Field[3];
	private Field[] rot_attr = new Field[3];
	private DropList<Pivot> pivots;
	private BoolElm rootrot;

	public PivotEditorTab(){
		super(EditorRoot.EditorMode.PIVOT);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((general = new ETabCom()), lang_prefix + "general", 410);
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.id"));
		general.add((name = new Field(TEXT, FF, field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.id = name.get_text();
			UpdateHandler.update(new UpdateEvent.PivotRenamed(FMT.MODEL.sel_pivot, name.get_text()));
		})).pos(FO, next_y_pos(1)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.pivot"));
		general.add((pivots = new DropList<>(FF)).pos(FO, next_y_pos(1)));
		pivots.onchange((key, val) -> {
			if(FMT.MODEL.sel_pivot == null || FMT.MODEL.sel_pivot.root) return;
			FMT.MODEL.sel_pivot.parent(val);
			FMT.MODEL.rerootpivots();
		});
		//
		general.add(new TextElm(0, next_y_pos(1.5f), FF).translate(lang_prefix + "general.pos16"));
		general.add((pos16x = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((pos16y = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((pos16z = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.pos"));
		general.add((posx = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = field.parse_float() * 16;
		})).pos(F30, next_y_pos(1)));
		general.add((posy = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = field.parse_float() * 16;
		})).pos(F31, next_y_pos(0)));
		general.add((posz = new Field(FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = field.parse_float() * 16;
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.rot"));
		general.add((rotx = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((roty = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((rotz = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.root_rot"));
		general.add((rootrot = new BoolElm(FO, next_y_pos(1), FF)).set(
			() -> FMT.MODEL.sel_pivot != null && FMT.MODEL.sel_pivot.root_rot,
			e -> {
				if(FMT.MODEL.sel_pivot == null) return;
				FMT.MODEL.sel_pivot.root_rot = e;
			}
		));
		//
		container.add((attrlinks = new ETabCom()), lang_prefix + "attr_links", 280);
		attrlinks.add(new TextElm(0, next_y_pos(-1), FF).translate(lang_prefix + "attr_links.pos"));
		attrlinks.add((pos_attr[0] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "pos", 0))).pos(FO, next_y_pos(1)));
		attrlinks.add((pos_attr[1] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "pos", 1))).pos(FO, next_y_pos(1)));
		attrlinks.add((pos_attr[2] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "pos", 2))).pos(FO, next_y_pos(1)));
		attrlinks.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "attr_links.rot"));
		attrlinks.add((rot_attr[0] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "rot", 0))).pos(FO, next_y_pos(1)));
		attrlinks.add((rot_attr[1] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "rot", 1))).pos(FO, next_y_pos(1)));
		attrlinks.add((rot_attr[2] = new Field(TEXT, FF, field -> linkAttr(field.get_text(), "rot", 2))).pos(FO, next_y_pos(1)));
		//
		updcom.add(UpdateEvent.ModelLoad.class, event -> updateFields());
		updcom.add(UpdateEvent.PivotAdded.class, event -> updateFields());
		updcom.add(UpdateEvent.PivotRenamed.class, event -> updateFields());
		updcom.add(UpdateEvent.PivotRemoved.class, event -> updateFields());
		updcom.add(UpdateEvent.PivotSelected.class, e -> updateFields());
		updateFields();
	}

	private void updateFields(){
		if(FMT.MODEL.sel_pivot == null){
			name.text(NOPIVOTSEL);
			refreshPivots(null);
			pos16x.set(0);
			pos16y.set(0);
			pos16z.set(0);
			posx.set(0);
			posy.set(0);
			posz.set(0);
			rotx.set(0);
			roty.set(0);
			rotz.set(0);
			for(Field field : pos_attr) field.text("");
			for(Field field : rot_attr) field.text("");
		}
		else{
			Pivot piv = FMT.MODEL.sel_pivot;
			name.text(piv.id);
			refreshPivots(piv);
			pos16x.set(piv.pos.x);
			pos16y.set(piv.pos.y);
			pos16z.set(piv.pos.z);
			posx.set(piv.pos.x * sixteenth);
			posy.set(piv.pos.y * sixteenth);
			posz.set(piv.pos.z * sixteenth);
			rotx.set(piv.rot.x);
			roty.set(piv.rot.y);
			rotz.set(piv.rot.z);
			for(int i = 0; i < pos_attr.length; i++){
				pos_attr[i].text(piv.pos_attr[i]);
				rot_attr[i].text(piv.rot_attr[i]);
			}
		}
		rootrot.updtexcol();
	}

	private void refreshPivots(Pivot except){
		pivots.clear();
		if(except == null || except.root) return;
		for(Pivot pv : FMT.MODEL.pivots()){
			if(pv == except) continue;
			pivots.addEntry(pv.id, pv);
		}
		pivots.selectKey(except.parentid);
	}

	private void linkAttr(String text, String type, int axe){
		if(FMT.MODEL.sel_pivot == null) return;
		switch(type){
			case "pos": FMT.MODEL.sel_pivot.pos_attr[axe] = text;
			case "rot": FMT.MODEL.sel_pivot.rot_attr[axe] = text;
		}
	}

}
