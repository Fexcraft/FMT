package net.fexcraft.app.fmt.ui_old.general;

import net.fexcraft.app.fmt.ui_old.Element;
import net.fexcraft.app.fmt.ui_old.FontRenderer;
import net.fexcraft.lib.common.math.RGB;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Button extends Element {
	
	private RGB hovercolor = RGB.GREEN;
	private RGB discolor = RGB.RED, iconcolor = null;
	private boolean centered, drawbackground = true;
	private String text, icon;
	private int iconsize;
	//
	private static final RGB subhover = new RGB(218, 232, 104);
	
	public Button(Element root, String id, int width, int height, int x, int y){
		super(root, id); this.setPosition(this.x = root.x + x, this.y = root.y + y + (root instanceof HoverMenu ? root.getElements().size() * 28 : 0));
		this.setSize(width, height).setLevel(root.getLevel() + 1);
		this.setTexPosSize("ui/background_light", 0, 0, 64, 64);
		this.setEnabled(true); this.setupSubmenu();
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover){
		this(elm, id, w, h, x, y); hovercolor = hover;
	}
	
	public Button(Element elm, String id, int w, int h, int x, int y, RGB hover, RGB dis){
		this(elm, id, w, h, x, y, hover); discolor = dis;
	}
	
	public Button setBackgroundless(boolean bool){
		this.drawbackground = !bool; return this;
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
	
	public Button setIcon(String texture, int size){
		this.icon = texture; this.iconsize = size; return this;
	}
	
	public Button setIcon(String texture, int size, RGB color){
		this.iconcolor = color; return this.setIcon(texture, size);
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(drawbackground){
			if(hovered) (enabled ? hovercolor : discolor).glColorApply();
			this.renderSelfQuad();
			if(hovered) RGB.glColorReset();
		}
		if(text != null){
			RGB color = !drawbackground ? hovered ? hovercolor : !enabled ? discolor : RGB.BLACK : RGB.BLACK;
			if(centered){
				int x = width / 2 - (FontRenderer.getWidth(text, 1) / 2), y = height / 2 - 10;
				FontRenderer.drawText(text, this.x + x + (icon == null ? 0 : iconsize + 2), this.y + y, 1, color);
			}
			else{
				FontRenderer.drawText(text, x + 2 + (icon == null ? 0 : iconsize + 2), y + 2, 1, color);
			}
		}
		if(icon != null){
			if(iconcolor != null) iconcolor.glColorApply();
			float y = (height - iconsize) * 0.5f;
			this.renderIcon(x + 2, this.y + y, iconsize, icon);
			if(iconcolor != null) RGB.glColorReset();
		}
	}
	
	@Override
	protected void realignToRoot(int index){
		if(root instanceof HoverMenu){ this.hovercolor = subhover;
			int height = index > 0 ? root.getElements().get(index - 1).y + root.getElements().get(index - 1).height : root.y;
			this.setPosition(root.x + 2, height + 2);
		}
	}
	
	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my); if(this.hovered){ for(Element elm : elements) elm.setVisible(true); }
	}

}
