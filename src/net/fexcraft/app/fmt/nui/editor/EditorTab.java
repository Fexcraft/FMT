package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;
import net.fexcraft.app.fmt.nui.Field;
import net.fexcraft.app.fmt.nui.editor.EditorRoot.EditorMode;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorTab extends Element {

	public final String lang_prefix;
	public final EditorMode mode;
	//
	public static float FF = EDITOR_CONTENT;

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

}
