package net.fexcraft.app.fmt.ui.general;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class AltMenu extends Element implements Dialog {
	
	@SuppressWarnings("unused")
	private ArrayList<PolygonWrapper> polygons;
	public static final AltMenu MENU = new AltMenu();

	public AltMenu(){
		super(null, "alt_menu", "alt_menu", false); dialogs.add(this);
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
		this.elements.clear(); this.xrel = x; this.yrel = y; height = 2; width = 20; int hei = 1;
		for(Element elm : type.elements){
			elements.add(elm.setVisible(true)); height += elm.height;
			if(elm.width + 2 > width) width = elm.width + 2;
			elm.yrel = hei; hei += elm.height;
		}
		this.polygons = selected; this.setVisible(true).repos();
	}
	
	public static enum Type {
		
		NO_SELECTION(
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_box").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_box")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_shapebox").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_shapebox")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_cylinder").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_cylinder")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_group").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_group")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_marker").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("shapelist").getElement("menu").getElement("add_marker")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("utils").getElement("menu").getElement("reset").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("utils").getElement("menu").getElement("reset")).getText(), false)
		),
		SELECTION(
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("editor").getElement("menu").getElement("copy_selection").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("editor").getElement("menu").getElement("copy_selection")).getText(), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					FMTB.MODEL.clearSelection(); return true;
				}
			}.setText(translate("alt_menu.selection.deselect_all", "Clear Selection"), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					FMTB.MODEL.deleteSelected(); return true;
				}
			}.setText(translate("alt_menu.selection.delete_selected", "Delete Selection"), false),
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					Editor.show("general_editor"); return true;
				}
			}.setText(translate("alt_menu.selection.open_editor", "Open Editor"), false),
			//
			new Button(MENU, "alt_button", "alt_menu:button", 200, 28, 1, 1){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					return UserInterface.TOOLBAR.getElement("utils").getElement("menu").getElement("reset").processButtonClick(x, y, left);
				}
			}.setText(((Button)UserInterface.TOOLBAR.getElement("utils").getElement("menu").getElement("reset")).getText(), false)
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
