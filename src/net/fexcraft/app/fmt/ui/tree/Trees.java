package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;

import org.liquidengine.legui.component.Frame;

import net.fexcraft.app.fmt.FMTB;


public class Trees {
	
	public static final ArrayList<TreeBase> trees = new ArrayList<>();
	public static TreeBase polygon, helper, fvtm, textures;
	
	public static void initializeTrees(Frame frame){
		frame.getContainer().add(polygon = new TreeBase("polygon"));
		frame.getContainer().add(textures = new TreeBase("textures"));
		frame.getContainer().add(helper = new TreeBase("helper"));
		frame.getContainer().add(fvtm = new TreeBase("fvtm"));
		for(TreeBase base : trees) base.addIcons();
	}
	
	public static void hideAll(){
		for(TreeBase tree : trees) tree.hide();
	}
	
	public static void show(String type){
		hideAll();
		switch(type){
			case "polygon": polygon.show(); break;
			case "helper": case "preview":
			case "helper_preview": helper.show(); break;
			case "fvtm": fvtm.show(); break;
			case "textures": case "texture":
			case "tex": textures.show(); break;
			default: break;
		}
	}

	public static void toggle(String string){
		TreeBase base = get(string); if(base.isVisible()) base.hide(); else show(string);
	}
	
	private static TreeBase get(String string){
		for(TreeBase base : trees) if(base.id.equals(string)) return base; return null;
	}

	public static boolean anyVisible(){
		for(TreeBase tree : trees) if(tree.isVisible()) return true; return false;
	}
	
	public static TreeBase getVisible(){
		for(TreeBase tree : trees) if(tree.isVisible()) return tree; return null;
	}

	public static void resize(int width, int height){
		for(TreeBase tree : trees){
			tree.setPosition(FMTB.WIDTH - 304, 30);
			tree.setSize(tree.getSize().x, FMTB.HEIGHT - 30);
			tree.scrollable.setSize(tree.scrollable.getSize().x, FMTB.HEIGHT - 90);
			tree.reOrderGroups();
		}
	}

	public static void updateCounters(){
		for(TreeBase tree : trees) tree.updateCounter();
	}

}
