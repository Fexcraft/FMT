package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.PreviewHandler;

import static net.fexcraft.app.fmt.ui.Field.FieldType.FLOAT;
import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOPREVIEWSEL;
import static net.fexcraft.lib.common.Static.sixteenth;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PreviewEditorTab extends EditorTab {

	public ETabCom general;
	private Field name;
	private Field pos16x, pos16y, pos16z;
	private Field posx, posy, posz;
	private Field rotx, roty, rotz;
	private Field sclx, scly, sclz;

	public PreviewEditorTab(){
		super(EditorRoot.EditorMode.PREVIEW);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		scrollable.container.add((general = new ETabCom("general")), lang_prefix + "general", 350);
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.name"));
		general.add((name = new Field(TEXT, FF, field -> {
			if(PreviewHandler.SELECTED == null) return;
			Model selected = PreviewHandler.SELECTED;
			selected.name = selected.name.substring(0, selected.name.indexOf("/") + 1) + name.get_text();
			UpdateHandler.update(new UpdateEvent.HelperRenamed(selected, selected.name));
		})).pos(FO, next_y_pos(1)));
		//
		general.add(new TextElm(0, next_y_pos(1.5f), FF - 75).translate(lang_prefix + "general.pos16"));
		general.add(new PosCopyButton(0, next_y_pos(0), null, () -> {
			return new float[]{ PreviewHandler.SELECTED.pos.x, PreviewHandler.SELECTED.pos.y, PreviewHandler.SELECTED.pos.z };
		}));
		general.add(new PosPasteButton(1, next_y_pos(0), null, arr -> {
			PreviewHandler.SELECTED.pos.x = arr[0];
			PreviewHandler.SELECTED.pos.y = arr[1];
			PreviewHandler.SELECTED.pos.z = arr[2];
			pos16x.set(arr[0]);
			pos16y.set(arr[1]);
			pos16z.set(arr[2]);
			posx.set(arr[0] * sixteenth);
			posy.set(arr[1] * sixteenth);
			posz.set(arr[2] * sixteenth);
		}));
		general.add((pos16x = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((pos16y = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((pos16z = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF - 75).translate(lang_prefix + "general.pos"));
		general.add(new PosCopyButton(0, next_y_pos(0), null, () -> {
			return new float[]{ PreviewHandler.SELECTED.pos.x * sixteenth, PreviewHandler.SELECTED.pos.y * sixteenth, PreviewHandler.SELECTED.pos.z * sixteenth };
		}));
		general.add(new PosPasteButton(1, next_y_pos(0), null, arr -> {
			PreviewHandler.SELECTED.pos.x = arr[0] * 16;
			PreviewHandler.SELECTED.pos.y = arr[1] * 16;
			PreviewHandler.SELECTED.pos.z = arr[2] * 16;
			pos16x.set(arr[0] * 16);
			pos16y.set(arr[1] * 16);
			pos16z.set(arr[2] * 16);
			posx.set(arr[0]);
			posy.set(arr[1]);
			posz.set(arr[2]);
		}));
		general.add((posx = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.x = field.parse_float() * 16;
		})).pos(F30, next_y_pos(1)));
		general.add((posy = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.y = field.parse_float() * 16;
		})).pos(F31, next_y_pos(0)));
		general.add((posz = new Field(FLOAT, F3S).consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.z = field.parse_float() * 16;
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF - 75).translate(lang_prefix + "general.rot"));
		general.add(new PosCopyButton(0, next_y_pos(0), null, () -> {
			return new float[]{ PreviewHandler.SELECTED.rot.x, PreviewHandler.SELECTED.rot.y, PreviewHandler.SELECTED.rot.z };
		}));
		general.add(new PosPasteButton(1, next_y_pos(0), null, arr -> {
			PreviewHandler.SELECTED.rot.x = arr[0];
			PreviewHandler.SELECTED.rot.y = arr[1];
			PreviewHandler.SELECTED.rot.z = arr[2];
			rotx.set(arr[0]);
			roty.set(arr[1]);
			rotz.set(arr[2]);
		}));
		general.add((rotx = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((roty = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((rotz = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		general.add(new TextElm(0, next_y_pos(1), FF - 75).translate(lang_prefix + "general.scale"));
		general.add(new PosCopyButton(0, next_y_pos(0), null, () -> {
			return new float[]{ PreviewHandler.SELECTED.scl.x, PreviewHandler.SELECTED.scl.y, PreviewHandler.SELECTED.scl.z };
		}));
		general.add(new PosPasteButton(1, next_y_pos(0), null, arr -> {
			PreviewHandler.SELECTED.scl.x = arr[0];
			PreviewHandler.SELECTED.scl.y = arr[1];
			PreviewHandler.SELECTED.scl.z = arr[2];
			sclx.set(arr[0]);
			scly.set(arr[1]);
			sclz.set(arr[2]);
		}));
		general.add((sclx = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.x = field.parse_float();
		})).pos(F30, next_y_pos(1)));
		general.add((scly = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.y = field.parse_float();
		})).pos(F31, next_y_pos(0)));
		general.add((sclz = new Field(FLOAT, F3S).deg_range().consumer(field -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.z = field.parse_float();
		})).pos(F32, next_y_pos(0)));
		//
		updcom.add(UpdateEvent.HelperAdded.class, e -> updateFields());
		updcom.add(UpdateEvent.HelperRemoved.class, e -> updateFields());
		updcom.add(UpdateEvent.HelperSelected.class, e -> updateFields());
		updateFields();
	}

	private void updateFields(){
		if(PreviewHandler.SELECTED == null){
			name.text(NOPREVIEWSEL);
			pos16x.set(0);
			pos16y.set(0);
			pos16z.set(0);
			posx.set(0);
			posy.set(0);
			posz.set(0);
			rotx.set(0);
			roty.set(0);
			rotz.set(0);
			sclx.set(0);
			scly.set(0);
			sclz.set(0);
		}
		else{
			Model prv = PreviewHandler.SELECTED;
			name.text(prv.name);
			pos16x.set(prv.pos.x);
			pos16y.set(prv.pos.y);
			pos16z.set(prv.pos.z);
			posx.set(prv.pos.x * sixteenth);
			posy.set(prv.pos.y * sixteenth);
			posz.set(prv.pos.z * sixteenth);
			rotx.set(prv.rot.x);
			roty.set(prv.rot.y);
			rotz.set(prv.rot.z);
			sclx.set(prv.scl.x);
			scly.set(prv.scl.y);
			sclz.set(prv.scl.z);
		}
	}

}
