package net.fexcraft.app.fmt.ui.re.editor;

import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.app.fmt.ui.re.Button;

public class Container extends Button {
	
	private int elementheight;
	private boolean expanded;

	public Container(NewElement root, String id, int width, int height, int x, int y, int[] rowsizes){
		super(root, id, "editor:group", width, height, x, y);
		this.setIcon("icons/editors/minimized", height - 2);
		this.setExpanded(false); this.setBackgroundless(false);
	}
	
	@Override
	public NewElement repos(){
		clearVertexes(); elementheight = 0;
		for(NewElement elm : elements){
			elm.repos(); if(elm.x < 8) elementheight += elm.height;
		} return this;//reposition is handled by editor
	}

	public void setExpanded(boolean bool){
		this.setIcon((expanded = bool) ? "icons/editors/expanded" : "icons/editors/minimized", height - 2);
		for(NewElement elm : elements) elm.setVisible(expanded);
	}
	
	public boolean isExpanded(){
		return expanded;
	}

	public int getExpansionHeight(){
		return expanded ? elementheight : height;
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		this.setExpanded(!this.isExpanded()); return true;
	}

}
