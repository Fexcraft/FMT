package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Button extends Element {
	
	private RGB hovercolor = RGB.GREEN;
	private RGB discolor = RGB.RED;
	private boolean centered;
	private String text;
	
	public Button(Element root, String id, int width, int height, int x, int y){
		super(root, id); this.setPosition(this.x = root.x + x, this.y = root.y + y + (root instanceof HoverMenu ? root.getElements().size() * 28 : 0));
		this.setSize(width, height).setLevel(root.getLevel() - 1);
		this.setTexPosSize("ui/background_light", 0, 0, 64, 64);
		this.setEnabled(true); this.setupSubmenu();
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover){
		this(elm, id, w, h, x, y); hovercolor = hover;
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover, RGB dis){
		this(elm, id, w, h, x, y, hover); discolor = dis;
	}

	/** To be overridden. **/
	public void setupSubmenu(){}

	public Button setText(String string, boolean centered){
		text = string; this.centered = centered;
		if(this.root instanceof HoverMenu){
			int leng = FontRenderer.getWidth(text, 1);
			if(leng + 10 > this.width) this.width = leng + 10;
			for(Element  elm : this.root.getElements()){
				if(elm.width < width) elm.width = this.width;
			}
			if(this.root.width - 4 < this.width) this.root.width = this.width + 4;
			if(this.width + 4 < this.root.width) this.width = this.root.width - 4;
		}
		return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(hovered) (enabled ? hovercolor : discolor).glColorApply();
		this.renderSelfQuad();
		if(hovered) RGB.glColorReset();
		if(text != null){
			if(centered){
				int x = width / 2 - (FontRenderer.getWidth(text, 1) / 2), y = height / 2 - 10;
				FontRenderer.drawText(text, this.x + x, this.y + y, 1);
			}
			else{
				FontRenderer.drawText(text, x + 2, y + 2, 1);
			}
		}
	}
	
	@Override
	protected void realignToRoot(int index){
		if(root instanceof HoverMenu){
			int height = index > 0 ? root.getElements().get(index - 1).y + root.getElements().get(index - 1).height : root.y;
			this.setPosition(root.x + 2, height + 2);
		}
	}
	
	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my); if(this.hovered){ for(Element elm : elements) elm.setVisible(true); }
	}
	
	public static class Default extends Button {

		public Default(Element toolbar, String id, int width, int height, int x, int y){
			super(toolbar, id, width, height, x, y);
		}

		@Override
		public void setupSubmenu(){
			//
		}
		
	}

}
