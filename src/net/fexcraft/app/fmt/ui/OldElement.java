package net.fexcraft.app.fmt.ui;

import java.util.Collection;
import java.util.TreeMap;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import net.fexcraft.app.fmt.utils.TextureManager;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public abstract class OldElement {
	
	protected TreeMap<String, OldElement> elements = new TreeMap<>();
	protected OldElement parent;
	public int width, height, x, y, z = 0;
	public boolean hovered, enabled = true, visible = true;
	public String id;
	
	public OldElement(OldElement parent, String id){
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
		for(OldElement elm : elements.values()){
			if(elm.anyHovered()) return true;
		} return false;
	}

	public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
		boolean bool = false;
		for(OldElement elm : elements.values()){
			//System.out.println(this.id + " -> " + elm.id);
			if(elm.visible && elm.enabled /*&& elm.hovered*/){
				if(bool = elm.onButtonClick(x, y, left, elm.hovered)) break;
			}
		}
		return (bool || hovered) && processButtonClick(x, y, left);
	}

	protected abstract boolean processButtonClick(int x, int y, boolean left);
	
	public OldElement setLevel(int i){ this.z = i; return this; }
	
	public OldElement getElement(String id){
		return this.elements.get(id);
	}

	public boolean onScrollWheel(int wheel){
		boolean bool = false;
		for(OldElement elm : elements.values()){
			if(elm.visible && elm.enabled){
				if(bool = elm.onScrollWheel(wheel)) break;
			}
		}
		return bool || (hovered && processScrollWheel(wheel));
	}

	protected boolean processScrollWheel(int wheel){ return false; }

	public OldElement setEnabled(boolean bool){
		this.enabled = bool; return this;
	}

	public boolean isHovered(){
		return hovered;
	}

	public Collection<OldElement> getElements(){
		return elements.values();
	}

}
