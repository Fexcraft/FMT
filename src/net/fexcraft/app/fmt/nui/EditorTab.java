package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;

import static net.fexcraft.app.fmt.nui.FMTInterface.EDITOR_WIDTH;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorTab extends Element {

	public final EditorRoot.EditorMode mode;

	public EditorTab(EditorRoot.EditorMode emode){
		super();
		mode = emode;
		pos(0, 40);
		//color(RGB.random());
		onResize();
	}

	@Override
	public void onResize(){
		super.onResize();
		size(EDITOR_WIDTH, FMT.HEIGHT - 40);
	}

}
