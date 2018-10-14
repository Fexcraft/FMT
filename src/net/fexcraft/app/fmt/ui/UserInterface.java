package net.fexcraft.app.fmt.ui;

import java.util.HashMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.CylinderEditor;
import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.ui.editor.ShapeboxEditor;
import net.fexcraft.app.fmt.ui.generic.Crossbar;
import net.fexcraft.app.fmt.ui.generic.Toolbar;
import net.fexcraft.app.fmt.utils.TextureManager;

public class UserInterface {
	
	private HashMap<String, Element> elements = new HashMap<>();
	private FMTB root;

	public UserInterface(FMTB main){
		this.root = main;
		TextureManager.loadResourcePNG("ui/background");
		TextureManager.loadResourcePNG("ui/button_bg");
		TextureManager.loadResourcePNG("icons/group_delete");
		TextureManager.loadResourcePNG("icons/group_visible");
		TextureManager.loadResourcePNG("icons/group_edit");
		TextureManager.loadResourcePNG("icons/group_minimize");
		elements.put("crossbar", new Crossbar());
		elements.put("toolbar", new Toolbar());
		elements.put("general_editor", new GeneralEditor());
		elements.put("shapebox_editor", new ShapeboxEditor());
		elements.put("modeltree", new ModelTree());
		elements.put("cylinder_editor", new CylinderEditor());
		//
		FMTB.MODEL.updateFields();
	}
	
	private int width, height;

	public void render(){
		width = root.displaymode.getWidth(); height = root.displaymode.getHeight();
		{
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
		elements.values().forEach(elm -> elm.render(width, height));
		//
		{
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPopMatrix();
	        GL11.glDepthFunc(GL11.GL_LEQUAL);
	        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
	        GL11.glClearDepth(1.0);
		}
	}

	public boolean isAnyHovered(){
		return elements.values().stream().filter(pre -> pre.anyHovered()).count() > 0;
	}

	public void onButtonPress(int i){
		for(Element elm : elements.values()){
			if(elm.visible && elm.enabled /*&& elm.hovered*/){
				if(elm.onButtonClick(Mouse.getX(), root.displaymode.getHeight() - Mouse.getY(), i == 0, elm.hovered)){
					return;
				}
			}
		}
	}

	public boolean onScrollWheel(int wheel){
		for(Element elm : elements.values()){
			if(elm.visible && elm.enabled){
				if(elm.onScrollWheel(wheel)) return true;
			}
		} return false;
	}

	public Element getElement(String string){
		return elements.get(string);
	}

	public boolean hasElement(String string){
		return elements.containsKey(string);
	}
	
}