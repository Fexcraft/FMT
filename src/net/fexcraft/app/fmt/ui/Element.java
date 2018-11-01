package net.fexcraft.app.fmt.ui;

import java.awt.Font;
import java.util.TreeMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;

import net.fexcraft.app.fmt.utils.TextureManager;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public abstract class Element {

	protected static TrueTypeFont font = new TrueTypeFont(new Font("Cambria", Font.BOLD, 16), true);
	protected static TrueTypeFont mono = new TrueTypeFont(new Font("Courier", Font.BOLD, 16), true);
	/*static{ try{ InputStream inputStream	= ResourceLoader.getResourceAsStream("./resources/font/custom.ttf"); Font awt = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(20f); font = new TrueTypeFont(awt, false); } catch(Exception e){ e.printStackTrace(); }}*/
	//
	protected TreeMap<String, Element> elements = new TreeMap<>();
	protected Element parent;
	public int width, height, x, y, z = 0;
	public boolean hovered, enabled = true, visible = true;
	public String id;
	
	public Element(Element parent, String id){
		this.parent = parent; this.id = id;
	}

	/** @param parent may be null **/
	public void render(int root_width, int root_height){
		if(!Mouse.isGrabbed()) this.hovered(Mouse.getX(), root_height - Mouse.getY());
		if(this.visible){
			if(z != 0) GL11.glTranslatef(0, 0,  z);
			GL11.glDepthFunc(GL11.GL_ALWAYS);
			this.renderSelf(root_width, root_height);
			GL11.glDepthFunc(GL11.GL_LESS);
			if(z != 0) GL11.glTranslatef(0, 0, -z);
		}
		if(this.visible && !elements.isEmpty()) elements.values().forEach(elm -> elm.render(root_width, root_height));
	}
	
	public abstract void renderSelf(int rw, int rh);
	
	protected void renderQuad(int x, int y, int width, int height, String texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + height);
        GL11.glEnd();
	}
	
	protected void renderIcon(int x, int y, String texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + 20, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + 20, y + 20);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + 20);
        GL11.glEnd();
	}
	
	public void hovered(int mouseX, int mouseY){
		this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public boolean anyHovered(){
		if(this.hovered) return true;
		for(Element elm : elements.values()){
			if(elm.anyHovered()) return true;
		} return false;
	}

	public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
		boolean bool = false;
		for(Element elm : elements.values()){
			//System.out.println(this.id + " -> " + elm.id);
			if(elm.visible && elm.enabled /*&& elm.hovered*/){
				if(bool = elm.onButtonClick(x, y, left, elm.hovered)) break;
			}
		}
		return (bool || hovered) && processButtonClick(x, y, left);
	}

	protected abstract boolean processButtonClick(int x, int y, boolean left);
	
	public Element setLevel(int i){ this.z = i; return this; }
	
	public Element getElement(String id){
		return this.elements.get(id);
	}

	public boolean onScrollWheel(int wheel){
		boolean bool = false;
		for(Element elm : elements.values()){
			if(elm.visible && elm.enabled){
				if(bool = elm.onScrollWheel(wheel)) break;
			}
		}
		return bool || (hovered && processScrollWheel(wheel));
	}

	protected boolean processScrollWheel(int wheel){ return false; }

}
