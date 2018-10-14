package net.fexcraft.app.fmt.ui.generic;

import net.fexcraft.app.fmt.ui.Element;

public abstract class Menulist extends Element {

	public Menulist(Element parent, String id, int width, int height, int x, int y){
		super(parent, id);
		this.width = width; this.height = height;
		this.x = x; this.y = y;
		this.visible = false;
		this.addButtons();
	}

	public abstract void addButtons();

	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !parent.hovered) visible = false;
		this.height = 2;
		for(Element elm : elements.values()){
			height += elm.height + 2;
		}
		this.renderQuad(x, y, width, height, "ui/background");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

}
