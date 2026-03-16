package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.nui.Field;
import net.fexcraft.app.fmt.polygon.Polygon;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.nui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.settings.Settings.POLYGON_SUFFIX;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditorTab extends EditorTab {

	public static Field POLY_NAME;
	public static Field POS_X, POS_Y, POS_Z;

	public PolygonEditorTab(){
		super(EditorRoot.EditorMode.POLYGON);
	}

	@Override
	public void init(Object... objs){
		add(new Element().pos(5, 5).translate(lang_prefix + "name").color(col_bd).size(EDITOR_CONTENT, 30));
		add((POLY_NAME = new Field(FF, str -> rename(str))).pos(5, 40));
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
