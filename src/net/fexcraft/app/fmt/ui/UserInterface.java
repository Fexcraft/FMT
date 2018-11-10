package net.fexcraft.app.fmt.ui;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.fexcraft.app.fmt.FMTGLProcess;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.ui.generic.FileChooser;

public class UserInterface {

	public static DialogBox DIALOGBOX;
	public static FileChooser FILECHOOSER;
	//
	private HashMap<String, Element> elements = new HashMap<>();
	private FMTGLProcess root;

	public UserInterface(FMTGLProcess main){
		this.root = main;
		root.setupUI(this);
	}
	
	private int width, height;

	public void render(){
		width = root.getDisplayMode().getWidth(); height = root.getDisplayMode().getHeight();
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
	        GL11.glPopMatrix();
		}
	}

	public boolean isAnyHovered(){
		return elements.values().stream().filter(pre -> pre.anyHovered()).count() > 0;
	}

	public void onButtonPress(int i){
		for(Element elm : elements.values()){
			if(elm.visible && elm.enabled /*&& elm.hovered*/){
				if(elm.onButtonClick(Mouse.getX(), root.getDisplayMode().getHeight() - Mouse.getY(), i == 0, elm.hovered)){
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
	
	public Map<String, Element> getElements(){
		return this.elements;
	}
	
}