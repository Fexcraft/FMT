package net.fexcraft.app.fmt.ui.generic;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.TextureManager;

public class Crossbar extends Element {
	
	public Crossbar(){
		super(null, "crossbar"); this.z = 100;
		TextureManager.loadResourcePNG("ui/center_marker");
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		this.renderQuad(root_width / 2 - 8, root_height / 2 - 8, 16, 16, "ui/center_marker");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
