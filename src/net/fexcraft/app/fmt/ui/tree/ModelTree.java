package net.fexcraft.app.fmt.ui.tree;

import org.lwjgl.input.Mouse;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.GroupCompound.GroupList;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class ModelTree extends RightTree {
	
	public static ModelTree TREE = new ModelTree();
	public static long COUNT = 0;
	private int elm_height, head;
	private String translation;
	private GroupList groups;
	//
	private static Button polygoncount;

	private ModelTree(){
		super("modeltree"); translation = translate("modeltree.polygons", "Polygons: ");
		polygoncount = new Button(this, "polygoncount", "modeltree:polygoncount", 300, 26, 4, 4).setText("PolygonCount", false);
		polygoncount.setHoverColor(StyleSheet.YELLOW, true).setEnabled(false);
	}

	@Override
	public void renderSelf(int rw, int rh){
		head = elm_height = 4; groups = FMTB.MODEL.getGroups(); elements.clear(); COUNT = FMTB.MODEL.countTotalMRTs();
		if(Settings.polygonCount()){
			polygoncount.setText(translation + COUNT, false).setPosition(4, elm_height).repos();
			elm_height += polygoncount.height + 4; elements.add(polygoncount);
		}
		elm_height -= scrollbar.scrolled; boolean bool;
		for(TurboList list : groups){
			if((bool = elm_height < head) && list.minimized){ elm_height += 28; continue; } if(elm_height > height) break;
			if(!bool){ list.button.update(elm_height, rw, rh); elm_height += 28; elements.add(list.button); }
			if(list.minimized) continue;
			for(PolygonWrapper wrapper : list){
				if(elm_height < head){ elm_height += 28; continue; } if(elm_height > height) break;
				wrapper.button.update(elm_height, rw, rh); elm_height += 28; elements.add(wrapper.button);
			}
		}
		if(Settings.polygonCount()){ polygoncount.renderSelf(rw, rh); }
		elements.add(scrollbar.repos()); scrollbar.render(rw, rh);
	}

	@Override
	public void hovered(float mx, float my){
		super.hovered(mx, my);
	}

	public boolean processScrollWheel(int wheel){
		modifyScroll(-wheel / (Mouse.isButtonDown(1) ? 1 : 10)); return true;
	}
	
	public void modifyScroll(int amount){
		scrollbar.scrolled += amount; if(scrollbar.scrolled < 0) scrollbar.scrolled = 0;
	}

	@Override
	public void refreshFullHeight(){
		int full = 4; groups = FMTB.MODEL.getGroups();
		//if(Settings.polygonCount()) full += polygoncount.height + 4;
		for(TurboList list : groups) full += list.minimized ? 28 : (list.size() * 28) + 28;
		this.fullheight = full;
	}
	
}
