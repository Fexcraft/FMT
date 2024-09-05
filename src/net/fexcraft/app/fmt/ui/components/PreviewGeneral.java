package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.PosCopyIcon;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.PreviewHandler;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PreviewGeneral extends EditorComponent {

	private static final String NOHELPERSEL = "< no helper directly selected >";
	protected static final String genid = "helper.general";
	protected NumberField pos16x, pos16y, pos16z;
	protected NumberField posx, posy, posz;
	protected NumberField rotx, roty, rotz;
	protected NumberField sclx, scly, sclz;
	private TextField name;

	public PreviewGeneral(){
		super(genid, 300, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".name/id"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField(NOHELPERSEL, L5, row(1), LW, HEIGHT, false).accept(con -> rename(con)));
		//
		add(new Label(translate(LANG_PREFIX + genid + ".pos16"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, () -> new float[]{ pos16x.value() * .0625f, pos16y.value() * .0625f, pos16z.value() * .0625f }));
		add(pos16x = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.x = cons.value();
			updateFields();
		}));
		add(pos16y = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.y = cons.value();
			updateFields();
		}));
		add(pos16z = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.z = cons.value();
			updateFields();
		}));
		add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, () -> new float[]{ posx.value() * .0625f, posy.value() * .0625f, posz.value() * .0625f }));
		add(posx = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.x = cons.value() * 16;
			updateFields();
		}));
		add(posy = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.y = cons.value() * 16;
			updateFields();
		}));
		add(posz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.pos.z = cons.value() * 16;
			updateFields();
		}));
		add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		add(rotx = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.x = cons.value();
			updateFields();
		}));
		add(roty = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.y = cons.value();
			updateFields();
		}));
		add(rotz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.rot.z = cons.value();
			updateFields();
		}));
		add(new Label(translate(LANG_PREFIX + genid + ".scale"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, () -> new float[]{ sclx.value() * .0625f, scly.value() * .0625f, sclz.value() * .0625f }));
		add(sclx = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.x = cons.value();
			updateFields();
		}));
		add(scly = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.y = cons.value();
			updateFields();
		}));
		add(sclz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(PreviewHandler.SELECTED == null) return;
			PreviewHandler.SELECTED.scl.z = cons.value();
			updateFields();
		}));
		updcom.add(HelperSelected.class, e -> updateFields());
	}

	private void updateFields(){
		Model model = PreviewHandler.SELECTED;
		if(model == null){
			name.getTextState().setText(NOHELPERSEL);
			pos16x.apply(0);
			pos16y.apply(0);
			pos16z.apply(0);
			posx.apply(0);
			posy.apply(0);
			posz.apply(0);
			rotx.apply(0);
			roty.apply(0);
			rotz.apply(0);
			sclx.apply(0);
			scly.apply(0);
			sclz.apply(0);
		}
		else{
			name.getTextState().setText(model.name.substring(model.name.indexOf("/") + 1));
			pos16x.apply(model.pos.x);
			pos16y.apply(model.pos.y);
			pos16z.apply(model.pos.z);
			posx.apply(model.pos.x * 0.0625f);
			posy.apply(model.pos.y * 0.0625f);
			posz.apply(model.pos.z * 0.0625f);
			rotx.apply(model.rot.x);
			roty.apply(model.rot.y);
			rotz.apply(model.rot.z);
			sclx.apply(model.scl.x);
			scly.apply(model.scl.y);
			sclz.apply(model.scl.z);
		}
		UpdateHandler.update(new HelperChanged(model));
	}

	private void rename(String name){
		Model selected = PreviewHandler.SELECTED;
		if(selected == null) return;
		selected.name = selected.name.substring(0, selected.name.indexOf("/") + 1) + name;
		UpdateHandler.update(new HelperRenamed(selected, selected.name));
	}

}
