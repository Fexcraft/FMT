package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.DropList;
import net.fexcraft.app.fmt.nui.Field;
import net.fexcraft.app.fmt.nui.TextElm;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.update.UpdateEvent;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.nui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditorTab extends EditorTab {

	public ETabCom sorting;
	public Field name;
	public DropList group;
	public Field pos_x, pos_y, pos_z;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		add((sorting = new ETabCom()).pos(5, 5), lang_prefix + "sorting", 300);
		sorting.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "name").text_scale(0.9f));
		sorting.add((name = new Field(TEXT, FF, str -> rename(str))).pos(FO, next_y_pos(1)));
		updcom.add(UpdateEvent.GroupAdded.class, event -> updateLists());
		updcom.add(UpdateEvent.GroupRemoved.class, event -> updateLists());
		updcom.add(UpdateEvent.GroupRenamed.class, event -> updateLists());
		updcom.add(UpdateEvent.PolygonSelected.class, event -> {
			//upd type
			if(event.prevselected() < 0) return;
			if(event.selected() == 0){
				group.selectEntry(0);
				name.text(NOPOLYSEL);
			}
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() > 0)){
				group.selectEntry(FMT.MODEL.first_selected().group().id);
				name.text(event.polygon().name());
				// set type (FMT.MODEL.first_selected().getShape().getName());
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
	}

	private void updateLists(){
		group.clear();
		if(FMT.MODEL != null && FMT.MODEL.allgroups().size() > 0){
			for(Group mg : FMT.MODEL.allgroups()){
				group.addEntry(mg.id, mg);
			}
			group.selectEntry(0);
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
