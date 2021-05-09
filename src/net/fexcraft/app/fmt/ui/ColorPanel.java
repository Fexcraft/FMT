package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.lib.common.math.RGB;

public class ColorPanel extends Panel {

	public ColorPanel(float x, float y, float w, float h, RGB color){
		super(x, y, w, h);
		this.getStyle().getBackground().setColor(FMT.rgba(color));
	}

}
