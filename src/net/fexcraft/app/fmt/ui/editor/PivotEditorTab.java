package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOPIVOTSEL;
import static net.fexcraft.lib.common.Static.sixteenth;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotEditorTab extends EditorTab {

	public ETabCom general;
	private Field name;
	private Field pos16x, pos16y, pos16z;
	private Field posx, posy, posz;
	private Field rotx, roty, rotz;
	private DropList<Pivot> pivots;

	public PivotEditorTab(){
		super(EditorRoot.EditorMode.PIVOT);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((general = new ETabCom()), lang_prefix + "general", 350);
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
		general.add((pos16x = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((pos16y = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((pos16z = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.pos"));
		general.add((posx = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = field.parse_float() * 16;
		})).pos(F30, next_y_pos(1)));
		general.add((posy = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = field.parse_float() * 16;
		})).pos(F31, next_y_pos(0)));
		general.add((posz = new Field(Field.FieldType.FLOAT, F3S).consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = field.parse_float() * 16;
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.rot"));
		general.add((rotx = new Field(Field.FieldType.FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((roty = new Field(Field.FieldType.FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((rotz = new Field(Field.FieldType.FLOAT, F3S).deg_range().consumer(field -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
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
		}
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

}
