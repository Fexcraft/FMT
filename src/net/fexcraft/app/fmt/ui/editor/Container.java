package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.utils.GGR;

public class Container extends Button {
	
	private int elementheight;
	private boolean expanded;

	public Container(Element root, String id, int width, int height, int x, int y, int[] rowsizes){
		super(root, id, "editor:container", width, height, x, y);
		this.setIcon("icons/editors/minimized", height - 2);
		this.setExpanded(false); this.setBackgroundless(false).setColor(0xff95ad9c);
	}
	
	@Override
	public Element repos(){
		clearVertexes(); elementheight = 4;
		for(Element elm : elements){
			elm.repos(); if(elm.xrel < 20) elementheight += elm.height + 4;
		} return this;//reposition is handled by editor
	}
	
	@Override
	public void render(int width, int height){
		if(!FMTB.hold_right) hovered(GGR.mousePosX() * UserInterface.scale, height - GGR.mousePosY() * UserInterface.scale);
		this.renderSelf(width, height); if(expanded) for(Element elm : elements) elm.render(width, height);
	}

	public void setExpanded(boolean bool){
		this.setIcon((expanded = bool) ? "icons/editors/expanded" : "icons/editors/minimized", height - 2);
		for(Element elm : elements) elm.setVisible(expanded); root.repos();
	}
	
	public boolean isExpanded(){
		return expanded;
	}

	public int getExpansionHeight(){
		return height + (expanded ? elementheight : 0);
	}
	
	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		this.setExpanded(!expanded); return true;
	}

	public int getLastElementYSize(int i){
		if(elements.isEmpty()) return i; return elements.get(elements.size() - 1).height + i;
	}

	/** Specifically for the Group Animations Container. */
	public void addSubElements(){}

}
