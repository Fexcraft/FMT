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
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fieds.NumberField;
import net.fexcraft.app.fmt.ui.fieds.TextField;

public class PolygonGeneral extends EditorComponent {
	
	private SelectBox<String> box = new SelectBox<>();
	private static final String NOGROUPS = "< no groups >";

	public PolygonGeneral(){
		super("polygon.general", 280, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".name/id"), L5, HEIGHT + R0, LW, HEIGHT));
		this.add(new TextField("", L5, HEIGHT + R1, LW, HEIGHT, false).accept(con -> rename(con)));
		this.add(new Label(translate(LANG_PREFIX + id + ".group"), L5, HEIGHT + R2, LW, HEIGHT));
		box.addSelectBoxChangeSelectionEventListener(listener -> {
			if(listener.getNewValue().equals(NOGROUPS)) return;
			Group group = FMT.MODEL.get(listener.getNewValue());
			if(group == null) return;
			FMT.MODEL.selected().forEach(poly -> poly.group(group));
		});
		box.setSize(LW, HEIGHT);
		box.setPosition(L5, HEIGHT + R3);
		box.setVisibleCount(8);
		Settings.applyBorderless(box);
		Settings.applyBorderless(box.getSelectionButton());
		Settings.applyBorderless(box.getExpandButton());
		Settings.applyBorderlessScrollable(box.getSelectionListPanel(), false);
		updateSelectBox();
		this.add(box);
		this.add(new Label(translate(LANG_PREFIX + id + ".pos"), L5, HEIGHT + R4, LW, HEIGHT));
		this.add(new NumberField(this, F30, HEIGHT + R5, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.X)));
		this.add(new NumberField(this, F31, HEIGHT + R5, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Y)));
		this.add(new NumberField(this, F32, HEIGHT + R5, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + id + ".off"), L5, HEIGHT + R6, LW, HEIGHT));
		this.add(new NumberField(this, F30, HEIGHT + R7, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.X)));
		this.add(new NumberField(this, F31, HEIGHT + R7, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Y)));
		this.add(new NumberField(this, F32, HEIGHT + R7, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + id + ".rot"), L5, HEIGHT + R8, LW, HEIGHT));
		this.add(new NumberField(this, F30, HEIGHT + R9, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.X)));
		this.add(new NumberField(this, F31, HEIGHT + R9, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.Y)));
		this.add(new NumberField(this, F32, HEIGHT + R9, 90, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.ROT, ValAxe.Z)));
		updateholder.add(UpdateType.GROUP_ADDED, vals -> updateSelectBox());
		updateholder.add(UpdateType.GROUP_REMOVED, vals -> updateSelectBox());
		updateholder.add(UpdateType.GROUP_RENAMED, vals -> updateSelectBox());
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

}
