package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;
import java.util.Optional;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.lib.common.math.RGB;

public class RightTree extends Element {
	
	private static final ArrayList<RightTree> trees = new ArrayList<RightTree>();
	protected int scroll, size;

	public RightTree(String id){
		super(null, id);
		this.setSize(308, 100).setLevel(-50).setPosition(0, 0);
		this.setVisible(false); trees.add(this);
	}

	@Override
	public void renderSelf(int rw, int rh){
		this.y = UserInterface.TOOLBAR.height;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/background_light");
		this.renderQuad(width - 2, y - 2, 2, height = (rh - y + 4), "ui/background_dark");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return true;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		trees.forEach(elm -> { elm.visible = elm.id.equals(id); elm.scroll = 0; });
	}
	
	public static void hideAll(){
		trees.forEach(elm -> { elm.visible = false; elm.scroll = 0; } );
	}

	public static void toggle(String string){ toggle(string, true); }

	public static void toggle(String string, boolean close){
		Optional<RightTree> opt = trees.stream().filter(pre -> pre.id.equals(string)).findFirst();
		if(close && opt.isPresent() && opt.get().visible){ hideAll(); } else{ show(string); }
	}
	
	private static final RGB sel_in = new RGB("#aa7e36");
	private static final RGB sel_vi = new RGB("#934427");
	private static final RGB def = new RGB("#0B6623");
	private static final RGB inv = new RGB("#80a073");
	//
	protected static final RGB fontcol = new RGB("#1e1e1e");
	
	protected RGB color(boolean visible, boolean selected){
		return visible ? selected ? sel_vi : def : selected ? sel_in : inv;
	}

	public static boolean anyTreeHovered(){
		return  trees.stream().filter(pre -> pre.isHovered()).findFirst().isPresent();
	}
	
}
