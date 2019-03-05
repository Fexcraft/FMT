package net.fexcraft.app.fmt.ui.generic;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public abstract class Button extends Element {
	
	private String text, texture = "ui/button_bg";
	private boolean centered;
	private RGB hovercolor = RGB.GREEN;
	private RGB discolor = RGB.RED;
	
	public Button(Element toolbar, String id, int width, int height, int x, int y){
		super(toolbar, id);
		this.width = width; this.height = height;
		this.x = parent.x + x; this.y = parent.y + y; //Relative Position
		this.setupSubmenu();
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover){
		this(elm, id, w, h, x, y); hovercolor = hover;
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover, RGB dis){
		this(elm, id, w, h, x, y, hover); discolor = dis;
	}

	public void setupSubmenu(){}

	public Button setText(String string, boolean centered){
		this.text = string; this.centered = centered; return this;
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
			/*if(centered){
				int x = width / 2 - (font.getWidth(text) / 2);
				int y = height / 2 - (font.getHeight(text) / 2);
				font.drawString(this.x + x, this.y + y, text, Color.black);
			}
			else{
				font.drawString(x + 2, y + 2, text, Color.black);
			}*///TODO
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

		public Empty(Element toolbar, String id, int width, int height, int x, int y){
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
