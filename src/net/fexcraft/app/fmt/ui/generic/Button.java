package net.fexcraft.app.fmt.ui.generic;

import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.OldElement;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public abstract class Button extends OldElement {
	
	private String texture = "ui/button_bg", text;
	private boolean centered;
	private RGB hovercolor = RGB.GREEN;
	private RGB discolor = RGB.RED;
	
	public Button(OldElement toolbar, String id, int width, int height, int x, int y){
		super(toolbar, id);
		this.width = width; this.height = height;
		this.x = parent.x + x; this.y = parent.y + y; //Relative Position
		this.setupSubmenu();
	}
	
	public Button(OldElement elm, String id, int w, int h, int x, int y, RGB hover){
		this(elm, id, w, h, x, y); hovercolor = hover;
	}
	
	public Button(OldElement elm, String id, int w, int h, int x, int y, RGB hover, RGB dis){
		this(elm, id, w, h, x, y, hover); discolor = dis;
	}

	public void setupSubmenu(){}

	public Button setText(String string, boolean centered){
		text = string; this.centered = centered;
		if(this.parent instanceof Menulist){
			int leng = FontRenderer.getWidth(text, 1);
			if(leng + 10 > this.width) this.width = leng + 10;
			for(OldElement  elm : this.parent.getElements()){
				if(elm.width < width) elm.width = this.width;
			}
			if(this.parent.width - 4 < this.width) this.parent.width = this.width + 4;
			if(this.width + 4 < this.parent.width) this.width = this.parent.width - 4;
		}
		return this;
	}

	public Button setTexture(String string){
		this.texture = string; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(hovered) (enabled ? hovercolor : discolor).glColorApply();
		this.renderQuad(x, y, width, height, texture);
		if(hovered) RGB.glColorReset();
		if(text != null){
			TextureManager.unbind();
			if(centered){
				int x = width / 2 - (FontRenderer.getWidth(text, 1) / 2), y = height / 2 - 10;
				FontRenderer.drawText(text, this.x + x, this.y + y, 1);
			}
			else{
				FontRenderer.drawText(text, x + 2, y + 2, 1);
			}
			RGB.glColorReset();
		}
	}
	
	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my);
		if(this.hovered){
			elements.values().forEach(elm -> { elm.visible = true; }); 
		}
	}
	
	public static class Empty extends Button {

		public Empty(OldElement toolbar, String id, int width, int height, int x, int y){
			super(toolbar, id, width, height, x, y);
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			return false;
		}

		@Override
		public void setupSubmenu(){
			//
		}
		
	}

}
