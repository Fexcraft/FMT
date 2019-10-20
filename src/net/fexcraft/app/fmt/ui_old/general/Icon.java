package net.fexcraft.app.fmt.ui_old.general;

import net.fexcraft.app.fmt.ui_old.Element;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Icon extends Element {
	
	protected RGB hovercolor = RGB.GREEN;
	protected RGB discolor = RGB.RED;
	
	public Icon(Element root, String id, String texture, int width, int height, int x, int y){
		super(root, id); this.setPosition(this.x = root.x + x, this.y = root.y + y);
		this.setSize(width, height).setLevel(root.getLevel() + 1);
		this.setTexPosSize(texture, 0, 0, 32, 32); this.setEnabled(true);
	}
	
	public Icon(Element elm, String id, String texture, int w, int h, int x, int y, RGB hover){
		this(elm, id, texture, w, h, x, y); hovercolor = hover;
	}
	
	public Icon(Element elm, String id, String texture, int w, int h, int x, int y, RGB hover, RGB dis){
		this(elm, id, texture, w, h, x, y, hover); discolor = dis;
	}

	public Icon setTexture(String string){
		this.texture = string; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(hovered) (enabled ? hovercolor : discolor).glColorApply();
		this.renderSelfQuad();
		if(hovered) RGB.glColorReset();
	}
	
	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my); if(this.hovered){ for(Element elm : elements) elm.setVisible(true); }
	}

}
