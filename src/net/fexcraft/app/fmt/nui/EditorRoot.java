package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_WIDTH;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	public EditorRoot(){
		super();
		onResize();
	}

	@Override
	public void init(Object... args){
		add(new EditorSidePanel());
		int iinc = 35, buff = -iinc + 4, yo = 4;
		for(EditorMode value : EditorMode.values()){
			add(new Element().pos(buff += iinc, yo).size(32, 32)
				.texture("icons/editor/" + value.name().toLowerCase()).hoverable(true)
				.onclick(ci -> setMode(EditorMode.POLYGON))
				.hint("editor.mode." + value.name().toLowerCase()));
		}
	}

	private void setMode(EditorMode mode){

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
