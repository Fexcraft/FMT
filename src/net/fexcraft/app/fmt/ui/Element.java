package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;

public class Element {
	
	protected ArrayList<Element> elements = new ArrayList<>();
	protected Element root;
	public int x, y, z = -100, width, height, xoff, yoff;
	public float tx, ty, tsx, tsy, tex_width, tex_height;
	protected boolean hovered, enabled, visible;
	public String id, texture = "null";
	public Texture teximg;
	
	public Element(Element root, String id){
		this.root = root; this.id = id; this.visible = true; this.enabled = true; teximg = TextureManager.getNullTex();
	}
	
	public Element setPosition(int x, int y){
		this.x = x; this.y = y; for(int i = 0; i < elements.size(); i++) elements.get(i).realignToRoot(i); return this;
	}
	
	/** To be overridden. **/
	protected void realignToRoot(int index){}

	public Element setLevel(int z){
		this.z = z; return this;
	}

	public int getLevel(){ return z; }
	
	public Element setSize(int width, int height){
		this.width = width; this.height = height; return this;
	}
	
	public Element setOffset(int xoff, int yoff){
		this.xoff = xoff; this.yoff = yoff; return this;
	}
	
	public Element setVisible(boolean bool){
		this.visible = bool; return this;
	}

	public boolean isVisible(){
		return visible;
	}
	
	public Element setEnabled(boolean bool){
		this.enabled = bool; return this;
	}

	public boolean isEnabled(){
		return enabled;
	}
	
	public boolean isSelected(){
		return UserInterface.SELECTED == this;
	}
	
	public boolean select(){
		UserInterface.SELECTED = this; return true;
	}
	
	public boolean isHovered(){
		return hovered;
	}
	
	/** Set Texture, Position and Size. **/
	public Element setTexPosSize(String texture, int tx, int ty, int tw, int th){
		this.texture = texture; this.teximg = TextureManager.getTexture(texture, false);
		this.tsx = 1f / teximg.getWidth(); this.tsy = 1f / teximg.getHeight();
		this.tx = tx; this.ty = ty; this.tex_width = tw; this.tex_height = th; return this;
	}

	public void setTexOnly(String texture){
		this.texture = texture; this.teximg = TextureManager.getTexture(texture, false);
	}
	
	public List<Element> getElements(){
		return elements;
	}
	
	public Element getElement(String id){
		for(Element elm : elements) if(elm.id.equals(id)) return elm; return null;
	}
	
	protected void renderSelfQuad(){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		float tx = this.tx * tsx, ty = this.ty * tsy;
		float x = (this.x + xoff) * UserInterface.XSCALE, y = (this.y + yoff) * UserInterface.YSCALE;
		float w = this.width * UserInterface.XSCALE, h = height * UserInterface.YSCALE;
		GL11.glTexCoord2f(tx, ty);
			GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(tx + (tex_width * tsx), ty);
			GL11.glVertex2f(x + w, y);
		GL11.glTexCoord2f(tx + (tex_width * tsx), ty + (tex_height * tsy));
			GL11.glVertex2f(x + w, y + h);
		GL11.glTexCoord2f(tx, ty + (tex_height * tsy));
			GL11.glVertex2f(x, y + h);
        GL11.glEnd();
	}
	
	protected void renderQuad(int x, int y, int width, int height, String texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + height);
        GL11.glEnd();
	}
	
	protected void renderIcon(int x, int y, int sz, String texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + sz, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + sz, y + sz);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + sz);
        GL11.glEnd();
	}

	public void render(int root_width, int root_height){
		if(!Mouse.isGrabbed()) this.hovered(Mouse.getX(), root_height - Mouse.getY());
		if(this.visible){
			if(z != 0) GL11.glTranslatef(0, 0,  z);
			this.renderSelf(root_width, root_height);
			if(z != 0) GL11.glTranslatef(0, 0, -z);
		}
		if(this.visible && !elements.isEmpty()) for(Element elm : elements) elm.render(root_width, root_height);
	}
	
	public void hovered(int mouseX, int mouseY){
		this.hovered = mouseX >= (x + xoff) && mouseX < (x + xoff) + width && mouseY >= (y + yoff) && mouseY < (y + yoff) + height;
	}
	
	/** To be overriden by extending classes. */
	public void renderSelf(int rw, int rh){
		this.renderSelfQuad();
	}

	public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
		boolean bool = false;
		for(Element elm : elements){
			if(elm.visible && elm.enabled){
				if(bool = elm.onButtonClick(x, y, left, elm.hovered)) break;
			}
		}
		return bool || hovered ? processButtonClick(x, y, left) : false;
	}
	
	/** To be overridden. **/
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

	public boolean anyHovered(){
		if(this.isHovered()) return true; boolean bool = false;
		for(Element elm : elements){ if(elm.anyHovered()){ bool = true; break; } } return bool;
	}

	public boolean onScrollWheel(int wheel){
		boolean bool = false;
		for(Element elm : elements){
			if(elm.visible && elm.enabled){
				if(bool = elm.onScrollWheel(wheel)) break;
			}
		}
		return bool || (hovered && processScrollWheel(wheel));
	}
	
	/** To be overridden. **/
	protected boolean processScrollWheel(int wheel){ return false; }

}
