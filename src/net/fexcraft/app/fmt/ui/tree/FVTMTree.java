package net.fexcraft.app.fmt.ui.tree;

import org.lwjgl.input.Mouse;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class FVTMTree extends RightTree {

	public static FVTMTree TREE = new FVTMTree();
	public static int SEL = -1;
	private int elm_height;

	private FVTMTree(){ super("fvtm_tree"); TREE = this; }

	@Override
	public void renderSelf(int rw, int rh){
		elm_height = 4; elements.clear(); if(FMTB.MODEL.getGroups().size() == 0) SEL = -1; elm_height -= scrollbar.scrolled; boolean bool;
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(list.animations.size() == 0) continue;
			if((bool = elm_height < 4) && list.aminimized){ elm_height += 28; continue; } if(elm_height > height) break;
			if(!bool){ list.button.update(elm_height, rw, rh); elm_height += 28; elements.add(list.button); }
			if(list.aminimized) continue;
			for(Animation anim : list.animations){
				if(elm_height < 4){ elm_height += 28; continue; } if(elm_height > height) break;
				anim.button.update(elm_height, rw, rh); elm_height += 28; elements.add(anim.button);
			}
		}
		elements.add(scrollbar.repos()); scrollbar.render(rw, rh);
	}

	@Override
	public void hovered(float mx, float my){
		super.hovered(mx, my);
	}

	public boolean processScrollWheel(int wheel){
		this.modifyScroll(-wheel / (Mouse.isButtonDown(1) ? 1 : 10)); return true;
	}
	
	public void modifyScroll(int amount){
		scrollbar.scrolled += amount; if(scrollbar.scrolled < 0) scrollbar.scrolled = 0;
	}

	public static TurboList getSelected(){
		return SEL >= FMTB.MODEL.getGroups().size() || SEL < 0 ? null : FMTB.MODEL.getGroups().get(SEL);
	}

	@Override
	public void refreshFullHeight(){
		int full = 4; for(TurboList list : FMTB.MODEL.getGroups()) full += list.aminimized ? 28 : (list.animations.size() * 28) + 28; this.fullheight = full;
	}
	
}
