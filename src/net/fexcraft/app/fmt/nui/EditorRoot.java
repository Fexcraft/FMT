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
	}

	@Override
	public void onResize(){
		size(EDITOR_WIDTH, FMT.HEIGHT);
	}

	public void toggle(){
		visible = !visible;
	}

}
