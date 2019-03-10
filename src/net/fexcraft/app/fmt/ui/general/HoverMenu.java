package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.Element;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public abstract class HoverMenu extends Element {
	
	public static final ArrayList<HoverMenu> arrlist = new ArrayList<>();

	public HoverMenu(Element root, String id, int width){
		super(root, id); this.setSize(width, 2).setLevel(root.getLevel());
		this.setPosition(root.x - 2, root.y + root.height + 2);
		this.setTexPosSize("ui/background_dark", 0, 0, 64, 64);
		this.addButtons(); HoverMenu.arrlist.add(this);
	}

	public abstract void addButtons();

	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !root.isHovered()) visible = false;
		this.height = 2;
		for(Element elm : elements){ height += elm.height + 2; }
		this.renderSelfQuad();
	}
	
	@Override
	protected void realignToRoot(int index){
		this.setPosition(root.x - 2, root.y + root.height + 2);
	}
	
	public static boolean anyMenuHovered(){
		for(HoverMenu menu : arrlist) if(menu.hovered && menu.visible) return true; return false;
	}

}
