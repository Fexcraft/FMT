package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTGLProcess;
import net.fexcraft.app.fmt.ui_old.general.ControlsAdjuster;
import net.fexcraft.app.fmt.ui_old.general.DialogBox;
import net.fexcraft.app.fmt.ui_old.general.HoverMenu;
import net.fexcraft.app.fmt.ui_old.general.NFC;
import net.fexcraft.app.fmt.ui_old.general.SettingsBox;
import net.fexcraft.app.fmt.ui_old.general.TextField;
import net.fexcraft.app.fmt.ui_old.general.Toolbar;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.Settings;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class UserInterface extends AbstractElement {

	public static float scale_x, scale_y, divscal;
	public static int scale;
	public static Element SELECTED = null;
	public static Toolbar TOOLBAR;
	public static DialogBox DIALOGBOX;
	public static NFC FILECHOOSER;
	public static ControlsAdjuster CONTROLS;
	public static SettingsBox SETTINGSBOX;
	//
	private ArrayList<Element> elements = new ArrayList<>();
	private FMTGLProcess root;
	private float[] clearcolor;

	public UserInterface(FMTGLProcess main){
		super(null, "root"); this.root = main; root.setupUI(this); this.rescale();
		this.clearcolor = Settings.getBackGroundColor();
		//
		elements.add(new Element(this, "test0").setPosition(10, 10).setSize(100, 20));
	}
	
	public void rescale(){
		for(Element elm : elements) elm.rescale();
		scale_x = root.getDisplayMode().getWidth();
		scale_y = root.getDisplayMode().getHeight();
		scale = 1; int uis = Settings.ui_scale(); if(uis < 0) uis = 1000;
        while(scale < uis && scale_x / (scale + 1) >= 320 && scale_y / (scale + 1) >= 240) scale++;
        scale_x = scale_x / scale; scale_y = scale_y / scale;
        scale_x = (float)Math.ceil(scale_x); scale_y = (float)Math.ceil(scale_y);
		width = (int)scale_x; height = (int)scale_y; divscal = 1f / scale;
	}

	public void render(boolean screenshot){
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
		if(screenshot){
			//tmelm.render(width, height); logintxt.render(width, height);
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
	    	GL11.glClearColor(clearcolor[0], clearcolor[1], clearcolor[2], clearcolor[3]);
	        GL11.glClearDepth(1.0);
	        GL11.glPopMatrix();
		}
	}
	
	/*private Element tmelm = new TextField(null, "text", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - FMTB.get().getDisplayMode().getHeight() + 4;
			this.setText((Time.getDay() % 2 == 0 ? "FMT - Fexcraft Modelling Toolbox" : "FMT - Fex's Modelling Toolbox") + (Static.dev() ? " [Developement Version]" : " [Standard Version]"), false);
			super.renderSelf(rw, rh);
		}
	};
	private Element logintxt = new TextField(null, "text", 4, 4, 500){
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
	};*/

	public boolean isAnyHovered(){
		boolean bool = false;
		for(Element elm : elements){ if(elm.anyHovered()){ bool = true; break; } }
		return bool;
	}

	public void onButtonPress(int i){
		if(HoverMenu.anyMenuHovered()){
			for(HoverMenu list : HoverMenu.arrlist){
				if(list.hovered && list.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, true)) return;
			}
		}
		else{
			Element element = null;
			for(Element elm : elements){
				if(elm.visible && elm.enabled){
					if(elm.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, elm.hovered)){
						return;
					} else element = elm;
				}
			}
			if(element instanceof TextField == false) TextField.deselectAll();
			if(i == 0 && (element == null ? true : element.id.equals("crossbar"))){
				RayCoastAway.doTest(true, true);
			}
		}
	}

	public boolean onScrollWheel(int wheel){
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

	@Override
	public AbstractElement getRoot(){
		return null;
	}
	
}