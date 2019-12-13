package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.FontRenderer.FontType;
import net.fexcraft.lib.common.math.Time;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Crossbar extends Element {
	
	private static String text;
	private static long till;
	
	public Crossbar(){
		super(null, "crossbar", "crossbar", false);
		this.setSize(16, 16).setTexture("ui/center_marker", true);
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		if(Dialog.anyVisible()) return;
		if(text != null){
			FontRenderer.drawText(text, x = root_width / 2 - ((FontRenderer.getWidth(text, FontType.BOLD) / 2)), y = root_height / 2 - 10, FontType.BOLD);
			if(Time.getDate() > till){ text = null; } return;
		}
		this.renderQuad(x = root_width / 2 - 8, y = root_height / 2 - 8, width, height, texture);
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

	public static void show(String string, long time){
		text = string; till = time;
	}

}
