package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_WIDTH;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	public static EditorTab[] EDITORS = new EditorTab[EditorMode.values().length];

	public EditorRoot(){
		super();
		onResize();
	}

	@Override
	public void init(Object... args){
		for(int i = 0; i < EditorMode.values().length; i++){
			add(EDITORS[i] = new EditorTab(EditorMode.values()[i]));
		}
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
