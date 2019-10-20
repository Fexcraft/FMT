package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.lib.common.math.RGB;

public abstract class ContainerButton extends Button {
	
	private boolean expanded;
	private int elementheight = 0;
	private int[] rowsizes, rowheight;
	private static final RGB rgb = new RGB("#6385a0");

	public ContainerButton(Element root, String id, int width, int height, int x, int y, int[] rowsizes){
		super(root, id, width, height, x, y);
		this.setIcon("icons/editors/minimized", height - 2);
		this.setTexPosSize("ui/background_light", 0, 0, 64, 64);
		if(rowsizes != null){
			this.initRowData(rowsizes); this.addSubElements(); this.initHeight(); 
		}
		this.setExpanded(false); this.setBackgroundless(true);
	}

	protected void initRowData(int[] rowsizes){
		this.rowsizes = rowsizes; rowheight = new int[rowsizes.length];
	}
	
	protected void initHeight(){
		int[] rowpass = new int[rowsizes.length];
		elementheight = height + 2;
		for(Element elm : elements){
			if(rowpass[elm.row] < elm.height) rowpass[elm.row] = elm.height;
		}
		for(int i = 0; i < rowheight.length; i++){
			rowheight[i] = elementheight; elementheight += rowpass[i] + 4;
		}
	}
	
	public abstract void addSubElements();

	public void setExpanded(boolean bool){
		this.setIcon((expanded = bool) ? "icons/editors/expanded" : "icons/editors/minimized", height - 2);
		for(Element elm : elements) elm.setVisible(expanded);
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
	
	@Override
	public void renderSelf(int rw, int rh){
		rgb.glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
		super.renderSelf(rw, rh);
		for(Element elm : elements){
			elm.width = ((root.width - (rowsizes[elm.row] * 4)) - 8) / rowsizes[elm.row];
			elm.x = x + (elm.col * (elm.width + 4));
			elm.y = y + rowheight[elm.row]; 
		}
	}
	
	@Override
	public void hovered(int mx, int my){
		this.hovered = mx >= (x + xoff) && mx < (x + xoff) + width && my >= (y + yoff) && my < (y + yoff) + height;
	}

}
