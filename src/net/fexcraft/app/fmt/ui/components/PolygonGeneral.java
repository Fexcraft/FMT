package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Shape;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;

public class PolygonGeneral extends EditorComponent {
	
	private SelectBox<String> box = new SelectBox<>(), type = new SelectBox<>();
	private static final String NOGROUPS = "< no groups >";
	private static final String NOPOLYSEL = "< no polygon selected >";
	protected static final String genid = "polygon.general";
	private TextField name;
	private NumberField TX, TY;
	
	public PolygonGeneral(){
		this(null, 380);
		addSortingFields();
		addGeneralFields();
	}

	public PolygonGeneral(String sub, int height){
		super(genid + (sub == null ? "" : "." + sub), height, false, true);
		updateholder.add(UpdateType.GROUP_ADDED, vals -> updateSelectBoxes());
		updateholder.add(UpdateType.GROUP_REMOVED, vals -> updateSelectBoxes());
		updateholder.add(UpdateType.GROUP_RENAMED, vals -> updateSelectBoxes());
		updateholder.add(UpdateType.POLYGON_SELECTED, vals -> {
			updateTypeBox();
			int old = vals.get(1);
			if(old < 0) return;
			int size = vals.get(2);
			if(size == 0){
				box.setSelected(0, true);
				name.getTextState().setText(NOPOLYSEL);
			}
			else if(size == 1 || (old == 0 && size > 0)){
				box.setSelected(FMT.MODEL.first_selected().group().id, true);
				name.getTextState().setText(vals.get(0, Polygon.class).name());
				type.setSelected(FMT.MODEL.first_selected().getShape().getName(), true);
			}
		});
	}

	protected void addSortingFields(){
		this.add(new Label(translate(LANG_PREFIX + genid + ".name/id"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField(NOPOLYSEL, L5, row(1), LW, HEIGHT, false).accept(con -> rename(con)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".group"), L5, row(1), LW, HEIGHT));
		box.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals(NOGROUPS)) return;
			Group group = FMT.MODEL.get(listener.getNewValue());
			if(group == null) return;
			FMT.MODEL.selection_copy().forEach(poly -> {
				poly.group().remove(poly);
				group.add(poly);
			});
		});
		box.setSize(LW, HEIGHT);
		box.setPosition(L5, row(1));
		box.setVisibleCount(8);
		Settings.applyBorderless(box);
		Settings.applyBorderless(box.getSelectionButton());
		Settings.applyBorderless(box.getExpandButton());
		Settings.applyBorderlessScrollable(box.getSelectionListPanel(), false);
		updateSelectBox();
		this.add(box);
		this.add(new Label(translate(LANG_PREFIX + genid + ".shape_type"), L5, row(1), LW, HEIGHT));
		type.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals(NOPOLYSEL)) return;
			//TODO
		});
		type.setSize(LW, HEIGHT);
		type.setPosition(L5, row(1));
		type.setVisibleCount(8);
		Settings.applyBorderless(type);
		Settings.applyBorderless(type.getSelectionButton());
		Settings.applyBorderless(type.getExpandButton());
		Settings.applyBorderlessScrollable(type.getSelectionListPanel(), false);
		updateTypeBox();
		this.add(type);
	}

	protected void addGeneralFields(){
		this.add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".off"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex"), L5, row(1), LW, HEIGHT));
		this.add(TX = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.X)));
		this.add(TY = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.Y)));
		this.add(new RunButton(LANG_PREFIX + genid + ".tex_reset", F32, row(0), F3S, HEIGHT, () -> resetUV()));
	}

	private void updateSelectBoxes(){
		updateSelectBox();
		updateTypeBox();
	}

	private void updateSelectBox(){
		while(box.getElements().size() > 0) box.removeElement(0);
		if(FMT.MODEL == null || FMT.MODEL.groups().isEmpty()){
			box.addElement(NOGROUPS);
			return;
		}
		for(Group group : FMT.MODEL.groups()){
			box.addElement(group.id);
		}
	}

	private void updateTypeBox(){
		while(type.getElements().size() > 0) type.removeElement(0);
		if(FMT.MODEL == null || FMT.MODEL.first_selected() == null){
			type.addElement(NOPOLYSEL);
			return;
		}
		Shape polyshape = FMT.MODEL.first_selected().getShape();
		for(Shape shape : Shape.values()){
			if(shape.sharesConversionGroup(polyshape)) type.addElement(shape.getName());
		}
	}

	private void rename(String string){//TODO check if the validation event runs prior as supposed
		ArrayList<Polygon> polis = FMT.MODEL.selected();
		if(polis.isEmpty()) return;
		else if(polis.size() == 1){
			polis.get(0).name(string);
		}
		else{
			for(int i = 0; i < polis.size(); i++){
				polis.get(i).name(string + String.format(POLYGON_SUFFIX.value, i));
			}
		}
	}

	private void resetUV(){
		FMT.MODEL.updateValue(TX.polyval(), TX.apply(-1));
		FMT.MODEL.updateValue(TY.polyval(), TY.apply(-1));
	}

}
