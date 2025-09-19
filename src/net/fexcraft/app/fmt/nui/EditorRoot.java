package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.Logging;

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
	public void onResize(){
		size(EDITOR_WIDTH, FMT.HEIGHT - FMTInterface.toolbar.h);
		pos(0, FMTInterface.toolbar.h);
	}

}
