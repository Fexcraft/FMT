package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;
import java.util.Optional;

import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.RGB;

@Deprecated
public class RightTree extends NewElement {
	
	public static final ArrayList<RightTree> TREES = new ArrayList<RightTree>();
	protected int scroll, size;

	public RightTree(String id){
		super(null, id, id); this.setSize(308, 100).setPosition(0, 0, -50).setVisible(false); TREES.add(this);
		this.setColor(0xff999999).setBorder(0xff000000, 0xffffffff, 1, false, false, true, false);
		this.setHoverColor(0xffffffff, false); this.repos();
	}
	
	@Override
	public NewElement repos(){
		x = UserInterface.width - width; y = UserInterface.TOOLBAR.height + UserInterface.TOOLBAR.border_width;
		height = UserInterface.height - y; if(Settings.bottombar()) height -= 29; clearVertexes(); return this;
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return true;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		TREES.forEach(elm -> { elm.visible = elm.id.equals(id); elm.scroll = 0; });
	}
	
	public static void hideAll(){
		TREES.forEach(elm -> { elm.visible = false; elm.scroll = 0; } );
	}

	public static void toggle(String string){ toggle(string, true); }

	public static void toggle(String string, boolean close){
		Optional<RightTree> opt = TREES.stream().filter(pre -> pre.id.equals(string)).findFirst();
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
		return  TREES.stream().filter(pre -> pre.isHovered()).findFirst().isPresent();
	}
	
}
