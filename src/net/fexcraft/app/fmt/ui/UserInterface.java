package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class UserInterface {

	public static float scale_x, scale_y, scale;
	public static Element SELECTED = null, DRAGGED = null;
	//
	private ArrayList<Element> elements = new ArrayList<>();
	//
	public static int width, height;
	private float[] clearcolor;

	public UserInterface(){ rescale(); }
	
	public void rescale(){
		scale_x = FMTB.WIDTH;
		scale_y = FMTB.HEIGHT;
		int facts = 1, uis = 1; if(uis < 0) uis = 1000;
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
		GL11.glLoadIdentity(); RGB.glColorReset();
		GL11.glDepthFunc(GL11.GL_ALWAYS); GL11.glDisable(GL11.GL_ALPHA_TEST);
		if(bool){
			tmelm.render(width, height); logintxt.render(width, height);
		}
		else{
			for(Element elm : elements) elm.render(width, height);
		}
		GL11.glDepthFunc(GL11.GL_LESS); GL11.glEnable(GL11.GL_ALPHA_TEST);
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
			this.y = rh - FMTB.HEIGHT + 4;
			this.setText((Time.getDay() % 2 == 0 ? "FMT - Fexcraft Modelling Toolbox" : "FMT - Fex's Modelling Toolbox") + (Static.dev() ? " [Developement Version]" : " [Standard Version]"), false);
			super.renderSelf(rw, rh);
		}
	};
	private Element logintxt = new TextField(null, "text", "screenshot:credits", 4, 4, 500){
		@Override
		public void renderSelf(int rw, int rh){
			this.y = rh - FMTB.HEIGHT + 32;
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
	
}