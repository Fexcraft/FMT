package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class AltMenu extends Element implements Dialog {
	
	private ArrayList<PolygonWrapper> polygons;
	public static final AltMenu MENU = new AltMenu();

	public AltMenu(){
		super(null, "alt_menu", "alt_menu"); dialogs.add(this);
		this.setSize(200, 100).setColor(0xffc7c7c7).setVisible(false);
		this.setBorder(StyleSheet.BLACK, StyleSheet.YELLOW, 3, true, true, true, true);
		this.setHoverColor(StyleSheet.WHITE, false);
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		if(!hovered){ this.reset(); } this.renderSelfQuad();
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
		for(Element elm : elements) elm.setVisible(false); elements.clear(); polygons = null; setVisible(false);
	}
	
	public void show(Type type, int x, int y, ArrayList<PolygonWrapper> selected){
		this.elements.clear(); this.xrel = x - 4; this.yrel = y - 4; height = 2; width = 20;
		for(Element elm : type.elements){
			elements.add(elm.setVisible(true)); height += elm.height;
			if(elm.width + 2 > width) width = elm.width + 2;
		}
		this.polygons = selected; this.setVisible(true).repos();
	}
	
	public static enum Type {
		
		NO_SELECTION(
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1).setText("Add Generic Box", false)
		),
		SELECTION(
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1).setText("Some Setting", false)
		);
		
		private Element[] elements;
		
		Type(Element... elements){
			this.elements = elements;
		}

		public static Type sel(boolean empty){
			return empty ? NO_SELECTION : SELECTION;
		}
		
	}

}
