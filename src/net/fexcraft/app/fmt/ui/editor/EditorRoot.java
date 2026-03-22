package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.ui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	public static EditorTab[] EDITORS = new EditorTab[EditorMode.values().length];
	public static String NOPOLYSEL;

	public EditorRoot(){
		super();
		pos(0, TOOLBAR_HEIGHT);
		color(col_cd);
		resize();
	}

	@Override
	public void init(Object... args){
		NOPOLYSEL = Translator.translate("editor.info.no_polygon_selected");
		for(int i = 0; i < EditorMode.values().length; i++){
			add(EDITORS[i] = EditorTab.create(EditorMode.values()[i]));
			EDITORS[i].reorderComponents();
			UpdateHandler.register(EDITORS[i].updcom);
		}
		setMode(EditorMode.POLYGON);
		add(new EditorSidePanel());
	}

	public static void setMode(EditorMode mode){
		for(EditorTab editor : EDITORS) editor.hide();
		EDITORS[mode.ordinal()].show();
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.HEIGHT);
	}

	public void toggle(){
		visible = !visible;
	}

	public static enum EditorMode {

		POLYGON, MODEL, PIVOT, TEXTURE, PAINTER, PREVIEW, VARIABLE, ANIMATION

	}

}
