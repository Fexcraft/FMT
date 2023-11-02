package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import net.fexcraft.app.fmt.update.UpdateEvent.GroupAdded;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupRemoved;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupRenamed;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Shape;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonSorting extends EditorComponent {
	
	private SelectBox<String> box = new SelectBox<>(), type = new SelectBox<>();
	private static final String NOGROUPS = "< no groups >";
	private static final String NOPOLYSEL = "< no polygon selected >";
	protected static final String genid = "polygon.sorting";
	private TextField name;
	private NumberField TX, TY;
	
	public PolygonSorting(){
		this(180);
	}

	public PolygonSorting(int height){
		super(genid, height, false, true);
		addSortingFields();
		updcom.add(GroupAdded.class, event -> updateSelectBoxes());
		updcom.add(GroupRemoved.class, event -> updateSelectBoxes());
		updcom.add(GroupRenamed.class, event -> updateSelectBoxes());
		updcom.add(PolygonSelected.class, event -> {
			updateTypeBox();
			if(event.prevselected() < 0) return;
			if(event.selected() == 0){
				box.setSelected(0, true);
				name.getTextState().setText(NOPOLYSEL);
			}
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() > 0)){
				box.setSelected(FMT.MODEL.first_selected().group().id, true);
				name.getTextState().setText(event.polygon().name());
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
			Shape shape = Shape.get(listener.getNewValue());
			if(shape == null) return;
			ArrayList<Polygon> polis = FMT.MODEL.selected();
			for(Polygon poly : polis){
				Polygon npol = poly.convert(shape);
				if(npol != null){
					poly.group().add(npol);
					poly.group().remove(poly);
					FMT.MODEL.select(npol);
				}
			}
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

}
