package net.fexcraft.app.fmt.ui.re;

import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Icon extends NewElement {
	
	public Icon(NewElement root, String id, String style, String texture, int size, int x, int y){
		super(root, id, style); this.setPosition(x, y, root.z + 1).setSize(size, size).setTexture(texture, true).setEnabled(true);
	}
	
	public Icon(NewElement elm, String id, String style, String texture, int size, int x, int y, RGB hover){
		this(elm, id, style, texture, size, x, y); hovercolor = hover;
	}
	
	public Icon(NewElement elm, String id, String style, String texture, int size, int x, int y, RGB hover, RGB dis){
		this(elm, id, style, texture, size, x, y, hover); discolor = dis;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(hovered) (enabled ? hovercolor : discolor).glColorApply();
		this.renderIcon(x, y, width, texture);
		if(hovered) RGB.glColorReset();
	}
	
	@Override
	public void hovered(float mx, float my){
		super.hovered(mx, my); if(this.hovered){ for(NewElement elm : elements) elm.setVisible(true); }
	}

}
