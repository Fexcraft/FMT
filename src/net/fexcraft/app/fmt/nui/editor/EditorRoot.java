package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.nui.Element;

import static net.fexcraft.app.fmt.nui.FMTInterface.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	public static EditorTab[] EDITORS = new EditorTab[EditorMode.values().length];

	public EditorRoot(){
		super();
		pos(0, TOOLBAR_HEIGHT);
		color(col_cd);
		onResize();
	}

	@Override
	public void init(Object... args){
		for(int i = 0; i < EditorMode.values().length; i++){
			add(EDITORS[i] = EditorTab.create(EditorMode.values()[i]));
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
