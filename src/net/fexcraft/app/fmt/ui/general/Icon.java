package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Icon extends Element {
	
	private boolean rect = false;
	
	/** Quad Icon */
	public Icon(Element root, String id, String style, String texture, int size, int x, int y){
		super(root, id, style); this.setPosition(x, y).setSize(size, size).setTexture(texture, true).setEnabled(true);
	}
	
	/** Rectangular Icon */
	public Icon(Element root, String id, String style, String texture, int sizex, int sizey, int x, int y){
		super(root, id, style); this.setPosition(x, y).setSize(sizex, sizey).setTexture(texture, true).setEnabled(true); rect = true;
	}
	
	/** Quad Icon + Hover RGB */
	public Icon(Element elm, String id, String style, String texture, int size, int x, int y, RGB hover){
		this(elm, id, style, texture, size, x, y); hovercolor = hover;
	}
	
	/** Quad Icon + Hover && Disabled RGB*/
	public Icon(Element elm, String id, String style, String texture, int size, int x, int y, RGB hover, RGB dis){
		this(elm, id, style, texture, size, x, y, hover); discolor = dis;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(hovered) (enabled ? hovercolor : discolor).glColorApply();
		if(!rect) this.renderIcon(x, y, width, texture); else this.renderQuad(x, y, width, height, texture);
		if(hovered) RGB.glColorReset();
	}
	
	@Override
	public void hovered(float mx, float my){
		super.hovered(mx, my); if(this.hovered){ for(Element elm : elements) elm.setVisible(true); }
	}

}
