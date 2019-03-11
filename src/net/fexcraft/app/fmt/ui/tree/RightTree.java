package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;
import java.util.Optional;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.lib.common.math.RGB;

public class RightTree extends Element {
	
	private static final ArrayList<RightTree> trees = new ArrayList<RightTree>();
	protected int scroll, size;

	public RightTree(String id){
		super(null, id); this.width = 308; z = -50;
		this.x = 0; this.y = 30; this.visible = false;
		trees.add(this);
	}

	@Override
	public void renderSelf(int rw, int rh){
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/button_bg");
		this.renderQuad(width - 2, y - 2, 2, height = (rh - y + 4), "ui/background");
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
	
	private static final RGB novis = new RGB(232, 211, 143), vis = new RGB(255, 194, 0);
	
	protected RGB color(boolean visible, boolean selected){
		return visible ? selected ? vis : RGB.GREEN : selected ? novis : RGB.WHITE;
	}

	public static boolean anyTreeHovered(){
		return  trees.stream().filter(pre -> pre.isHovered()).findFirst().isPresent();
	}
	
}
