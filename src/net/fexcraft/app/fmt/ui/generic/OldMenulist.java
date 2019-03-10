package net.fexcraft.app.fmt.ui.generic;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.OldElement;

public abstract class OldMenulist extends OldElement {
	
	public static final ArrayList<OldMenulist> arrlist = new ArrayList<>();

	public OldMenulist(OldElement parent, String id, int width, int height, int x, int y){
		super(parent, id); this.width = width; this.height = height;
		this.x = x; this.y = y; this.visible = false; this.addButtons();
		OldMenulist.arrlist.add(this);
	}

	public abstract void addButtons();

	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !parent.hovered) visible = false;
		this.height = 2;
		for(OldElement elm : elements.values()){
			height += elm.height + 2;
		}
		this.renderQuad(x, y, width, height, "ui/background");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}
	
	public static boolean anyMenuHovered(){
		return arrlist.stream().filter(pre -> pre.isHovered() && pre.visible).findFirst().isPresent();
	}

}
