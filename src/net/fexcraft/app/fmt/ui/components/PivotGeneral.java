package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.settings.Settings.GROUP_SUFFIX;
import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotGeneral extends EditorComponent {

	private static final String NOPIVOTSEL = "< no pivot directly selected >";
	private SelectBox<String> pivots = new SelectBox<>();
	protected static final String genid = "pivot.general";
	protected NumberField pos16x, pos16y, pos16z;
	protected NumberField posx, posy, posz;
	protected NumberField rotx, roty, rotz;
	protected TextField[] pos = new TextField[3];
	protected TextField[] rot = new TextField[3];
	private TextField name;

	public PivotGeneral(){
		super(genid, 530, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".name/id"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField(NOPIVOTSEL, L5, row(1), LW, HEIGHT, false).accept(con -> rename(con)));
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".parent"), L5, row(1), LW, HEIGHT));
		pivots.setPosition(L5, row(1));
		pivots.setSize(LW, HEIGHT);
		updcom.add(ModelLoad.class, event -> refreshPivotEntries());
		updcom.add(PivotAdded.class, event -> refreshPivotEntries());
		updcom.add(PivotRenamed.class, event -> refreshPivotEntries());
		updcom.add(PivotRemoved.class, event -> refreshPivotEntries());
		pivots.addSelectBoxChangeSelectionEventListener(listener -> {
			Pivot sel = FMT.MODEL.sel_pivot;
			if(sel == null || sel.root) return;
			sel.parent(FMT.MODEL.getP(listener.getNewValue()));
			FMT.MODEL.rerootpivots();
		});
		pivots.setVisibleCount(6);
		refreshPivotEntries();
		add(pivots);
		//
		add(new Label(translate(LANG_PREFIX + genid + ".pos16"), L5, row(1), LW, HEIGHT));
		add(pos16x = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = cons.value();
			updateFields();
		}));
		add(pos16y = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = cons.value();
			updateFields();
		}));
		add(pos16z = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = cons.value();
			updateFields();
		}));
		add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LW, HEIGHT));
		add(posx = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.x = cons.value() * 16;
			updateFields();
		}));
		add(posy = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.y = cons.value() * 16;
			updateFields();
		}));
		add(posz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.pos.z = cons.value() * 16;
			updateFields();
		}));
		add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		add(rotx = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.x = cons.value();
			updateFields();
		}));
		add(roty = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.y = cons.value();
			updateFields();
		}));
		add(rotz = new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-180, 180, true, cons -> {
			if(FMT.MODEL.sel_pivot == null) return;
			FMT.MODEL.sel_pivot.rot.z = cons.value();
			updateFields();
		}));
		updcom.add(PivotSelected.class, e -> {
			name.getTextState().setText(e.pivot().id);
			pivots.setSelected(e.pivot().root ? e.model().getRootPivot().id : e.pivot().parent().id, true);
			updateFields();
		});
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".pos_attr"), L5, row(2), LW, HEIGHT));
		this.add(pos[0] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 0)));
		this.add(pos[1] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 1)));
		this.add(pos[2] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, true, 2)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".rot_attr"), L5, row(2), LW, HEIGHT));
		this.add(rot[0] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 0)));
		this.add(rot[1] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 1)));
		this.add(rot[2] = new TextField("", L5, row(1), LW, HEIGHT, false).accept(con -> linkPivotAttr(con, false, 2)));
	}

	private void linkPivotAttr(String con, boolean pos, int axe){
		if(FMT.MODEL.sel_pivot == null) return;
		(pos ? FMT.MODEL.sel_pivot.pos_attr : FMT.MODEL.sel_pivot.rot_attr)[axe] = con;
	}

	private void updateFields(){
		if(FMT.MODEL.sel_pivot == null){
			pos16x.apply(0);
			pos16y.apply(0);
			pos16z.apply(0);
			posx.apply(0);
			posy.apply(0);
			posz.apply(0);
			rotx.apply(0);
			roty.apply(0);
			rotz.apply(0);
			for(TextField tf : pos) tf.getTextState().setText("");
			for(TextField tf : rot) tf.getTextState().setText("");
		}
		else{
			pos16x.apply(FMT.MODEL.sel_pivot.pos.x);
			pos16y.apply(FMT.MODEL.sel_pivot.pos.y);
			pos16z.apply(FMT.MODEL.sel_pivot.pos.z);
			posx.apply(FMT.MODEL.sel_pivot.pos.x * 0.0625f);
			posy.apply(FMT.MODEL.sel_pivot.pos.y * 0.0625f);
			posz.apply(FMT.MODEL.sel_pivot.pos.z * 0.0625f);
			rotx.apply(FMT.MODEL.sel_pivot.rot.x);
			roty.apply(FMT.MODEL.sel_pivot.rot.y);
			rotz.apply(FMT.MODEL.sel_pivot.rot.z);
			for(int i = 0; i < pos.length; i++) pos[i].getTextState().setText(FMT.MODEL.sel_pivot.pos_attr[i]);
			for(int i = 0; i < rot.length; i++) rot[i].getTextState().setText(FMT.MODEL.sel_pivot.rot_attr[i]);
		}
	}

	private void rename(String name){
		Pivot selected = FMT.MODEL.sel_pivot;
		if(selected == null) return;
		selected.id = name;
		UpdateHandler.update(new PivotRenamed(selected, name));
	}

	private void refreshPivotEntries(){
		while(pivots.getElements().size() > 0) pivots.removeElement(0);
		for(Pivot pivot : FMT.MODEL.pivots()){
			pivots.addElement(pivot.id);
		}
		Group group = FMT.MODEL.first_selected_group();
		if(group == null) pivots.setSelected(0, true);
		else pivots.setSelected(group.pivot, true);
	}

}
