package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.StyleSheet;

public class Scrollbar extends Element {
	
	private static final int WIDTH = 12;
	//
	//private Scroll scroll;
	protected Scrollable element;
	private boolean opposite;
	public float scrolled;

	public Scrollbar(Element root, boolean opp){
		super(root, root.getId() + ":scrollbar", root.getId() + ":scrollbar");
		element = (Scrollable)root; width = WIDTH; height = root.height;
		this.elements.add(/*scroll = */new Scroll(this));
		this.setColor(0xff6e6e6e).setHoverColor(StyleSheet.WHITE, false);;
		opposite = opp; xrel = opp ? root.x - width : root.width; 
	}

	@Override
	public Element repos(){
		visible = element.getFullHeight() > root.height;
		xrel = opposite ? -width : root.width; height = root.height; super.repos(); return this;
	}
	
	public static interface Scrollable {
		
		public int getFullHeight();
		
	}
	
	public static class Scroll extends Element {
		
		private Scrollbar scrollbar;
		private float rat;

		private Scroll(Scrollbar root){
			super(root, root.getRoot().getId() + ":scroll", root.getRoot().getId() + ":scroll");
			width = WIDTH; height = calcHeight(); scrollbar = root;
			this.setDraggable(true).setColor(0xffcdcdcd).setHoverColor(0xfffcba03, false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.WHITE, 1, true, true, true, true);
		}
		
		@Override
		public Element repos(){
			height = calcHeight(); super.repos(); return this;
		}
		
		@Override
		public void render(int rw, int rh){
			y = root.y + percentalPosition(); super.render(rw, rh);
		}

		private int percentalPosition(){
			return (int)((scrollbar.scrolled / scrollbar.element.getFullHeight()) * scrollbar.height);
		}
		
		@Override
		public void hovered(float mouseX, float mouseY){
			this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
		}

		private int calcHeight(){
			try{
				rat = scrollbar.getRoot().height * 100 / scrollbar.element.getFullHeight();
				float ret = scrollbar.height * (rat *= 0.01f); return ret < 20 ? 20 : (int)ret;
			}
			catch(Exception e){
				return 100;
			}
		}
		
		@Override
		public void pullBy(int mx, int my){
			scrollbar.scrolled += my * (scrollbar.element.getFullHeight() / scrollbar.height); if(scrollbar.scrolled < 0) scrollbar.scrolled = 0;
			if(scrollbar.scrolled > scrollbar.element.getFullHeight()) scrollbar.scrolled = scrollbar.element.getFullHeight();
		}
		
	}

}
