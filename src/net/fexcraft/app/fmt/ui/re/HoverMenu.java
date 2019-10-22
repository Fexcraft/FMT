package net.fexcraft.app.fmt.ui.re;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.NewElement;

public abstract class HoverMenu extends NewElement {
	
	public static final ArrayList<HoverMenu> MENUS = new ArrayList<>();

	public HoverMenu(NewElement root, String id, int width){
		super(root, id, "hovermenu"); MENUS.add(this); this.setSize(width, 0).setColor(0xffc7c7c7).setVisible(false); this.addButtons();
	}
	
	public abstract void addButtons();
	
	@Override
	public NewElement setVisible(boolean bool){
		for(NewElement elm : elements) elm.setVisible(bool); this.visible = bool; return this;
	}
	
	public static boolean anyMenuHovered(){
		for(HoverMenu menu : MENUS) if(menu.hovered && menu.visible) return true; return false;
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !root.isHovered()){ this.setVisible(false); }
		for(NewElement elm : elements){ height += elm.height + 2; }
		this.renderSelfQuad();
	}
	
	public NewElement repos(){
		x = root.x + xrel; y = root.y + yrel + root.height; clearVertexes(); height = 0; for(NewElement elm : elements) elm.repos(); return this;
	}

}
