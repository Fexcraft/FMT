package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.StyleSheet;

//TODO ADD SCROLL FOR LARGER DROP DOWNS
public class DropDown extends Element implements Dialog {
	
	public static final DropDown INST = new DropDown();
	private Element temproot;

	public DropDown(){
		super(null, "dropdown", "dropdown", false); dialogs.add(this);
		this.setSize(200, 100).setColor(0xffc7c7c7).setVisible(false);
		this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, false, true, true, true);
		this.setHoverColor(StyleSheet.WHITE, false);
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !temproot.isHovered()){ this.reset(); } this.renderSelfQuad();
	}
	
	@Override
	public Element repos(){
		return super.repos();
	}

	@Override
	public boolean visible(){
		return isVisible();
	}

	@Override
	public void reset(){
		for(Element elm : elements) elm.dispose(); elements.clear(); setVisible(false);
	}
	
	public void show(Element root, ArrayList<Element> elements){
		this.reset(); this.temproot = root; this.elements = elements;
		this.xrel = root.x + 1; this.yrel = root.y + root.height; height = 2; width = root.width - 2; int hei = 1;
		for(Element elm : elements){
			elm.setVisible(true); height += elm.height;
			if(elm.width > width) width = elm.width;
			if(elm.width < width) elm.width = width;
			elm.yrel = hei; hei += elm.height;
		}
		this.setVisible(true).repos();
	}
	
	public static class Button extends net.fexcraft.app.fmt.ui.general.Button {

		public Button(Element root, String id, String style, int width, int height, int x, int y){
			super(root, id, style, width, height, x, y); border = null; border_width = 0;
			border_fill = null; top = bot = left = right = false; gentex = true;
			this.clearVertexes().clearTexture();
		}
		
	}

}
