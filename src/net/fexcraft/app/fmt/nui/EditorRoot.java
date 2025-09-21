package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorRoot extends Element {

	public static int EDITOR_WIDTH = 300;

	public EditorRoot(){
		super();
		onResize();
	}

	@Override
	public void init(Object... args){
		add(new EditorSidePanel());
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.HEIGHT - FMTInterface.TOOLBAR_HEIGHT);
		pos(0, FMTInterface.TOOLBAR_HEIGHT);
	}

}
