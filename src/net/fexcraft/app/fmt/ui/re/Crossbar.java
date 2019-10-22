package net.fexcraft.app.fmt.ui.re;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.NewElement;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Crossbar extends NewElement {
	
	public Crossbar(){
		super(null, "crossbar", "crossbar", false);
		this.setSize(16, 16).setTexture("ui/center_marker", true);
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		if(Dialog.anyVisible()) return;
		this.renderQuad(x = root_width / 2 - 8, y = root_height / 2 - 8, width, height, texture);
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
