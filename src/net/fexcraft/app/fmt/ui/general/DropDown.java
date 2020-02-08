package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Scrollbar.Scrollable;
import net.fexcraft.app.fmt.utils.StyleSheet;

public class DropDown extends Element implements Dialog, Scrollable {
	
	public static final DropDown INST = new DropDown();
	private int fullheight;
	private Scrollbar scrollbar;
	private Element temproot;

	public DropDown(){
		super(null, "dropdown", "dropdown", false); dialogs.add(this);
		this.setSize(200, 100).setColor(0xffc7c7c7).setVisible(false);
		this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, false, true, true, true);
		this.setHoverColor(StyleSheet.WHITE, false);
		this.elements.add(scrollbar = new Scrollbar(this, false));
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered && !temproot.isHovered() && !scrollbar.isHovered() && !FMTB.ggr.isDragging()){ this.reset(); } this.renderSelfQuad();
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
		for(Element elm : elements) if(elm != scrollbar) elm.dispose(); elements.clear(); setVisible(false);
	}
	
	public void show(Element root, ArrayList<Element> elements){
		this.reset(); this.temproot = root; this.elements = elements; fullheight = 0; scrollbar.scrolled = 0;
		this.xrel = root.x + 1; this.yrel = root.y + root.height; height = 2; width = root.width - 2; int hei = 1;
		for(Element elm : elements){
			elm.setVisible(true); fullheight += elm.height;
			if(yrel + height + elm.height < UserInterface.height) height += elm.height;
			if(elm.width > width) width = elm.width;
			if(elm.width < width) elm.width = width;
			elm.yrel = hei; hei += elm.height;
		}
		this.elements.add(scrollbar); this.setVisible(true).repos();
	}
	
	public static class Button extends net.fexcraft.app.fmt.ui.general.Button {

		public Button(Element root, String id, String style, int width, int height, int x, int y){
			super(root, id, style, width, height, x, y); border = null; border_width = 0;
			border_fill = null; top = bot = left = right = false; gentex = true;
			this.setHoverColor(0xff4287f5, true);
			this.clearVertexes().clearTexture();
		}
		
	}

	@Override
	public int getFullHeight(){
		return fullheight;
	}

	@Override
	public boolean refresh(){
		int head = (int)-scrollbar.scrolled;
		for(Element element : elements){
			if(element == scrollbar || element == root) continue;
			element.yrel = head; element.setVisible(true);
			if(head < 0) element.setVisible(false);
			if(head + 28 > height) element.setVisible(false);
			head += 26; element.repos();
		} scrollbar.repos().setVisible(true); return true;
	}
	
	@Override
	public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
		if(scrollbar.onButtonClick(x, y, left, scrollbar.isHovered())) return true;
		return super.onButtonClick(x, y, left, hovered);
	}

	@Override
	public boolean processScrollWheel(int wheel){
		int amount = -wheel / (Mouse.isButtonDown(1) ? 1 : 10); scrollbar.scrolled += amount;
		if(scrollbar.scrolled < 0) scrollbar.scrolled = 0; return refresh();
	}

}
