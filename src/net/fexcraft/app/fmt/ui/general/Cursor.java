package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Cursor extends Element {
	
	public Cursor(){
		super(null, "cursor", "cursor", false);
		this.setSize(16, 16).setTexture("icons/cursor/default", true);
		TextureManager.loadTexture("icons/cursor/dragging", null);
		TextureManager.loadTexture("icons/cursor/camera_rotate", null);
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		if(!Settings.internal_cursor()) return; x = FMTB.cursor_x; y = FMTB.HEIGHT - FMTB.cursor_y;
		this.renderQuad(x, y, width, height, "icons/cursor/" + (FMTB.hold_right ? "camera_rotate" : FMTB.ggr.isDragging() ? "dragging" : "default"));
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
