package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.nui.editor.EditorRoot.EditorMode;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorTab extends Element {

	public final String lang_prefix;
	public final EditorMode mode;
	//
	public static float FF = EDITOR_CONTENT - 10;
	public static float FO = 5;//field offset
	//
	public static float F3S = 90;//3-row field width
	public static float F30 = 5;//3-row 1st field offset
	public static float F31 = 100;//3-row 2nd field offset
	public static float F32 = 195;//3-row 3rd field offset
	//
	public static float F6S = 40;//6-row field width
	public static float F60 = 5;
	public static float F61 = 53;
	public static float F62 = 101;
	public static float F63 = 149;
	public static float F64 = 197;
	public static float F65 = 245;
	//
	public static float FS = 26;//field height
	//
	protected UpdateCompound updcom = new UpdateCompound();
	private int next_y_elm_pos = 0;

	public EditorTab(EditorMode emode){
		super();
		mode = emode;
		lang_prefix = "editor." + emode.name().toLowerCase() + ".";
		color(col_cd);
		onResize();
	}

	public static EditorTab create(EditorMode mode){
		switch(mode){
			case POLYGON: return new PolygonEditorTab();
		}
		return new EditorTab(mode);
	}

	@Override
	public void onResize(){
		super.onResize();
		size(EDITOR_WIDTH, FMT.SCALED_HEIGHT - TOOLBAR_HEIGHT);
	}

	public int next_y_pos(int inc){
		if(inc < 0) return next_y_elm_pos = 30;
		return next_y_elm_pos += inc * 30;
	}

	public void reorderComponents(){
		if(elements == null) return;
		int incr = 5;
		for(Element elm : elements){
			if(elm instanceof ETabCom){
				elm.pos(5, incr);
				incr += elm.h + 5;
			}
		}
	}

}
