package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.AltMenu;
import net.fexcraft.app.fmt.ui.general.Bottombar;
import net.fexcraft.app.fmt.ui.general.ControlsAdjuster;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.DropDown;
import net.fexcraft.app.fmt.ui.general.Exporter;
import net.fexcraft.app.fmt.ui.general.FileChooser;
import net.fexcraft.app.fmt.ui.general.HoverMenu;
import net.fexcraft.app.fmt.ui.general.SettingsBox;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.ui.general.Toolbar;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Time;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class UserInterface {

	public static float scale_x, scale_y, scale;
	public static Element SELECTED = null, DRAGGED = null;
	public static Toolbar TOOLBAR;
	public static Bottombar BOTTOMBAR;
	public static DialogBox DIALOGBOX;
	public static FileChooser FILECHOOSER;
	public static ControlsAdjuster CONTROLS;
	public static SettingsBox SETTINGSBOX;
	public static AltMenu RIGHTMENU;
	public static DropDown DROPDOWN;
	public static Exporter EXPORTER;
	//
	private ArrayList<Element> elements = new ArrayList<>();
	private FMTB root;
	//
	public static int width, height;
	private float[] clearcolor;

	public UserInterface(FMTB main){
		this.root = main; rescale();
	}
	
	public void rescale(){
		scale_x = root.getDisplayMode().getWidth();
		scale_y = root.getDisplayMode().getHeight();
		int facts = 1, uis = Settings.ui_scale(); if(uis < 0) uis = 1000;
        while(facts < uis && scale_x / (facts + 1) >= 320 && scale_y / (facts + 1) >= 240) facts++;
        scale_x = scale_x / facts; scale_y = scale_y / facts;
        scale_x = (float)Math.ceil(scale_x); scale_y = (float)Math.ceil(scale_y);
        //scale = Math.min(scale_x, scale_y);
		width = (int)scale_x; height = (int)scale_y; scale = 1f / facts;
		for(Element elm : elements){ elm.repos(); }
		for(TurboList list : FMTB.MODEL.getGroups()){
			list.button.repos(); for(PolygonWrapper wrapper : list) wrapper.button.repos();
		}
	}

	public void render(boolean bool){
		//width = root.getDisplayMode().getWidth(); height = root.getDisplayMode().getHeight();
		{
			GL11.glPushMatrix();
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
	        GL11.glOrtho(0, width, height, 0, -100, 100);
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
		}
		//
		GL11.glLoadIdentity();
		GL11.glDepthFunc(GL11.GL_ALWAYS);
		if(bool){
			tmelm.render(width, height); logintxt.render(width, height);
		}
		else{
			for(Element elm : elements) elm.render(width, height);
		}
		GL11.glDepthFunc(GL11.GL_LESS);
		//
		{
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPopMatrix();
	        GL11.glDepthFunc(GL11.GL_LEQUAL);
	        //GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
	    	if(clearcolor == null){ clearcolor = Settings.getBackGroundColor(); }
	    	GL11.glClearColor(clearcolor[0], clearcolor[1], clearcolor[2], clearcolor[3]);
	        GL11.glClearDepth(1.0);
	        GL11.glPopMatrix();
		}
	}
	
	private Element tmelm = new TextField(null, "text", "screenshot:title", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - FMTB.get().getDisplayMode().getHeight() + 4;
			this.setText((Time.getDay() % 2 == 0 ? "FMT - Fexcraft Modelling Toolbox" : "FMT - Fex's Modelling Toolbox") + (Static.dev() ? " [Developement Version]" : " [Standard Version]"), false);
			super.renderSelf(rw, rh);
		}
	};
	private Element logintxt = new TextField(null, "text", "screenshot:credits", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - FMTB.get().getDisplayMode().getHeight() + 32;
			switch(FMTB.MODEL.creators.size()){
				case 0: {
					this.setText(FMTB.MODEL.name + " - " + (SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "Guest User"), false);
					break;
				}
				case 1: {
					if(FMTB.MODEL.creators.get(0).equals(SessionHandler.getUserName())){
						this.setText(FMTB.MODEL.name + " - by " + SessionHandler.getUserName(), false);
					}
					else{
						this.setText(FMTB.MODEL.name + " - by " + String.format("%s (logged:%s)", FMTB.MODEL.creators.get(0), SessionHandler.getUserName()), false);
					}
					break;
				}
				default: {
					if(FMTB.MODEL.creators.contains(SessionHandler.getUserName())){
						this.setText(FMTB.MODEL.name + " - by " + SessionHandler.getUserName() + " (and " + (FMTB.MODEL.creators.size() - 1) + " others)", false);
					}
					else{
						this.setText(FMTB.MODEL.name + " - " + String.format("(logged:%s)", SessionHandler.getUserName()), false);
					}
					break;
				}
			}
			super.renderSelf(rw, rh);
		}
	};

	public boolean isAnyHovered(){
		boolean bool = false;
		for(Element elm : elements){ if(elm.anyHovered()){ bool = true; break; } }
		return bool;
	}

	public void onButtonPress(int i){
		if(HoverMenu.anyMenuHovered()){
			for(HoverMenu list : HoverMenu.MENUS){
				if(list.isHovered() && list.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, true)) return;
			}
		}
		else{
			Element element = null, elm0 = null;
			for(Dialog dialog : Dialog.dialogs){
				if((elm0 = (Element)dialog).visible && elm0.enabled){
					if(elm0.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, elm0.hovered)){
						return;
					} else element = elm0;
				}
			}
			for(Element elm : elements){
				if(elm instanceof Dialog == false && elm.visible && elm.enabled){
					if(elm.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, elm.hovered)){
						return;
					} else element = elm;
				}
			}
			if(element instanceof TextField == false) TextField.deselectAll();
			boolean bool = element == null ? true : element.id.equals("crossbar"); 
			if(i == 0 && bool){
				RayCoastAway.doTest(true, true);
			}
			if(GGR.iControlDown() && i == 1 && bool && !UserInterface.RIGHTMENU.visible()){
				ArrayList<PolygonWrapper> selected = FMTB.MODEL.getSelected();
				UserInterface.RIGHTMENU.show(AltMenu.Type.sel(selected.isEmpty()), Mouse.getX(), FMTB.get().getDisplayMode().getHeight() - Mouse.getY(), selected);
			}
		}
		return;
	}

	public void getDraggableElement(){
		Element element = null;
		for(Element elm : elements){
			if(elm.visible){
				element = elm.getDraggableElement(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), elm.hovered);
				if(element != null) break;
			}
		}
		if(element != null){ UserInterface.DRAGGED = element; }
	}

	public boolean onScrollWheel(int wheel){
		Element elm0 = null;
		for(Dialog dialog : Dialog.dialogs){
			if((elm0 = (Element)dialog).visible && elm0.enabled){
				if(elm0.onScrollWheel(wheel)) return true;
			}
		}
		for(Element elm : elements){
			if(elm.visible && elm.enabled){
				if(elm.onScrollWheel(wheel)) return true;
			}
		} return false;
	}

	public Element getElement(String string){
		for(Element elm : elements) if(elm.id.equals(string)) return elm; return null;
	}

	public boolean hasElement(String string){
		return getElement(string) != null;
	}
	
	public ArrayList<Element> getElements(){ return elements; }
	
}