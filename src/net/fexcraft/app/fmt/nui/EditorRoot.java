package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_WIDTH;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	private static EditorTab[] editors = new EditorTab[EditorMode.values().length];

	public EditorRoot(){
		super();
		onResize();
	}

	@Override
	public void init(Object... args){
		add(new EditorSidePanel());
		int iinc = 35, buff = -iinc + 4, yo = 4, ti = 0;
		for(EditorMode mode : EditorMode.values()){
			add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/editor/" + mode.name().toLowerCase()).hoverable(true)
				.onclick(ci -> setMode(mode))
				.hint("editor.mode." + mode.name().toLowerCase()));
			add(editors[ti++] = new EditorTab(mode));
		}
	}

	private void setMode(EditorMode mode){
		for(EditorTab editor : editors) editor.hide();
		editors[mode.ordinal()].show();
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
