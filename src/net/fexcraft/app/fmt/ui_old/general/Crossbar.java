package net.fexcraft.app.fmt.ui_old.general;

import net.fexcraft.app.fmt.ui_old.Dialog;
import net.fexcraft.app.fmt.ui_old.Element;
import net.fexcraft.app.fmt.utils.TextureManager;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Crossbar extends Element {
	
	public Crossbar(){
		super(null, "crossbar"); this.setLevel(0).setSize(16, 16);
		TextureManager.loadTexture("ui/center_marker", null);
		this.setTexPosSize("ui/center_marker", 0, 0, 16, 16);
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		if(Dialog.anyVisible()) return;
		this.x = root_width / 2 - 8; this.y = root_height / 2 - 8; this.renderSelfQuad();
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
