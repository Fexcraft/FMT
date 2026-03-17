package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.nui.Field;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.update.UpdateEvent;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.nui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditorTab extends EditorTab {

	public ETabCom sorting;
	public Field name;
	public Field pos_x, pos_y, pos_z;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		add((sorting = new ETabCom()).pos(5, 5), lang_prefix + "sorting", 300);
		sorting.add(new Element().shape(ElmShape.NONE).pos(0, 30).translate(lang_prefix + "name").size(EDITOR_CONTENT, 30).text_scale(0.9f));
		sorting.add((name = new Field(TEXT, FF, str -> rename(str))).pos(5, 60));
		updcom.add(UpdateEvent.PolygonSelected.class, event -> {
			//upd type
			if(event.prevselected() < 0) return;
			if(event.selected() == 0){
				//set 0'th group sel
				name.text(NOPOLYSEL);
			}
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() > 0)){
				// sel group (FMT.MODEL.first_selected().group().id);
				name.text(event.polygon().name());
				// set type (FMT.MODEL.first_selected().getShape().getName());
			}
		});
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
