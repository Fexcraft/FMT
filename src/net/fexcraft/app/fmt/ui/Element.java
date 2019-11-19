package net.fexcraft.app.fmt.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;

public class Element {

	protected ArrayList<Element> elements = new ArrayList<>();
	protected /*final*/ String id, stylegroup;
	protected final Element root;
	//
	protected float[][][] vertexes;
	protected boolean top, bot, left, right;
	protected Texture texture;
	//
	public int width, height, x, xrel, y, yrel, border_width; 
	protected Integer fill, border, border_fill;
	protected RGB hovercolor, discolor;
	protected boolean hovered, visible = true, enabled = true, draggable, gentex = true;
	
	public Element(Element root, String id, String stylegroup){
		this(root, id, stylegroup, true);
	}
	
	public Element(Element root, String id, String stylegroup, boolean inithover){
		this.root = root; this.id = id; this.stylegroup = stylegroup; if(inithover) this.setHoverColor(null, false);
	}
	
	public Element setPosition(int x, int y){
		xrel = x; yrel = y; this.repos(); return this;
	}
	
	public Element setSize(int x, int y){
		width = x; height = y; return this;
	}
	
	public Element setTexture(String texture, boolean load){
		if(load && TextureManager.getTexture(texture, true) == null) TextureManager.loadTexture(texture, null);
		this.texture = TextureManager.getTexture(texture, true); gentex = false; return this;
	}
	
	public Element setEnabled(boolean bool){
		this.enabled = bool; return this;
	}

	public Element setVisible(boolean bool){
		this.visible = bool; return this;
	}
	
	/** top - 0, bot - 1, left - 2, right - 3 */
	public Element setBorder(int color, int color0, int width, boolean... bools){
		border = StyleSheet.getColourFor(stylegroup, "border", color); border_width = width;
		border_fill = StyleSheet.getColourFor(stylegroup, "border_fill", color0);
		top = bools.length > 0 && bools[0]; bot = bools.length > 1 && bools[1];
		left = bools.length > 2 && bools[2]; right = bools.length > 3 && bools[3];
		gentex = true; return this.clearVertexes().clearTexture();
	}
	
	public Element setHoverColor(Integer hover, boolean dis){
		if(!dis) hovercolor = new RGB(StyleSheet.getColourFor(stylegroup, "hovered", hover == null ? 0xffdae868 : hover, hover != null));
		else discolor = new RGB(StyleSheet.getColourFor(stylegroup, "disabled", hover == null ? 0xffeb4034 : hover, hover != null));
		return this;
	}

	public Element clearVertexes(){
		vertexes = null; return this;
	}

	public Element clearTexture(){
		if(texture != null) texture.rebind(); return this;
	}

	public Element setColor(int color){
		fill = StyleSheet.getColourFor(stylegroup, "background", color); return this;
	}
	
	public Element repos(){
		if(root == null){ x = xrel; y = yrel; } else { x = root.x + xrel; y = root.y + yrel; }
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}
	
	public void hovered(float mouseX, float mouseY){
		if(vertexes == null){ this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height; }
		else{
			this.hovered = mouseX >= vertexes[0][0][0] && mouseX < vertexes[0][2][0]
				&& mouseY >= vertexes[0][0][1] && mouseY < vertexes[0][2][1];
			/*if(hovered){
				Print.console("MV: " + mouseX + " " + mouseY);
				Print.console("XV: " + vertexes[0][0][0] + " " + vertexes[0][2][0]);
				Print.console("YV: " + vertexes[0][0][1] + " " + vertexes[0][2][1]);
			}*/
		}
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public boolean isEnabled(){
		return enabled;
	}
	
	public boolean isHovered(){
		return hovered;
	}

	public void render(int width, int height){
		if(!Mouse.isGrabbed()) hovered(Mouse.getX() * UserInterface.scale, height - Mouse.getY() * UserInterface.scale);
		//
		if(this.visible){
			this.renderSelf(width, height);
		}
		if(this.visible && !elements.isEmpty()) for(Element elm : elements) elm.render(width, height);
	}
	
	/** To be overriden by extending classes. */
	public void renderSelf(int rw, int rh){
		this.renderSelfQuad();
	}
	
	protected void renderSelfQuad(){
		if(texture == null || texture.rebindQ() || vertexes == null){
			int width = this.width, height = this.height; gentex = true;
			if(top) height += border_width; if(bot) height += border_width;
			if(left) width += border_width; if(right) width += border_width;
			if(texture == null || texture.rebindQ()){
				BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				if(border != null) for(int i = 0; i < width; i++) for(int j = 0; j < height; j++) img.setRGB(i, j, border);
				{
					int xb = left ? border_width : 0, yb = top ? border_width : 0;
					int xe = right ? width - border_width : width, ye = bot ? height - border_width : height;
					runfill(img, xb, xe, yb, ye, fill);
				}
				if(border_width > 2){
					if(top) runfill(img, 1, width - 1, 1, border_width - 1, border_fill);
					if(bot) runfill(img, 1, width - 1, height - border_width + 1, height - 1, border_fill);
					if(left) runfill(img, 1, border_width - 1, 1, height - 1, border_fill);
					if(right) runfill(img, width - border_width + 1, width - 1, 1, height - 1, border_fill);
				}
				if(texture == null) texture = TextureManager.createTexture("elm:" + id, img); else texture.setImage(img);
			}
			//
			float x = this.x, y = this.y; if(top) y -= border_width; if(left) x -= border_width; vertexes = new float[2][][];
			vertexes[0] = new float[][]{ { x, y }, { x + width, y }, { x + width, y + height }, { x, y + height } };
			vertexes[1] = new float[][]{ { 0, 0 }, { 1, 0 }, { 1, 1 }, { 0, 1 } };
		}
		if(hovered) hovercolor.glColorApply();
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		for(int j = 0; j < 4; j++){
			GL11.glTexCoord2f(vertexes[1][j][0], vertexes[1][j][1]);
			GL11.glVertex2f(vertexes[0][j][0], vertexes[0][j][1]);
		}
        GL11.glEnd();
        if(hovered) RGB.glColorReset();
	}

	protected void runfill(BufferedImage img, int xb, int xe, int yb, int ye, Integer fill){
		for(int i = xb; i < xe; i++) for(int j = yb; j < ye; j++) img.setRGB(i, j, fill);
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
	
	protected void renderQuad(int x, int y, int width, int height, Texture texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + height);
        GL11.glEnd();
	}
	
	protected void renderIcon(float x, float y, int sz, String texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + sz, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + sz, y + sz);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + sz);
        GL11.glEnd();
	}
	
	protected void renderIcon(float x, float y, int sz, Texture texture){
		TextureManager.bindTexture(texture);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0); GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0); GL11.glVertex2f(x + sz, y);
		GL11.glTexCoord2f(1, 1); GL11.glVertex2f(x + sz, y + sz);
		GL11.glTexCoord2f(0, 1); GL11.glVertex2f(x, y + sz);
        GL11.glEnd();
	}

	public boolean onButtonClick(int x, int y, boolean left, boolean hovered){
		boolean bool = false;
		for(Element elm : elements){
			if(elm.visible && elm.enabled){
				if(bool = elm.onButtonClick(x, y, left, elm.hovered)) break;
			}
		}
		return bool ? true : hovered ? processButtonClick(x, y, left) : false;
	}

	public Element getDraggableElement(int mx, int my, boolean hovered){
		Element element = null;
		for(Element elm : elements){
			if(!elm.visible) continue;
			if((element = elm.getDraggableElement(mx, my, elm.hovered)) != null) break;
		}
		return element != null ? element : hovered && isDraggable() ? this : null;
	}
	
	/** To be overridden. **/
	public boolean processButtonClick(int x, int y, boolean left){
		return false;
	}

	public boolean anyHovered(){
		if(hovered) return true; boolean bool = false;
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
	public boolean processScrollWheel(int wheel){ return false; }
	
	public ArrayList<Element> getElements(){
		return elements;
	}

	public String getId(){
		return id;
	}
	
	public boolean isSelected(){
		return UserInterface.SELECTED == this;
	}
	
	public boolean select(){
		UserInterface.SELECTED = this; return isSelected();
	}

	public boolean deselect(){
		if(isSelected()) UserInterface.SELECTED = null; return UserInterface.SELECTED == null;
	}

	public Element getElement(String string){
		for(Element elm : elements) if(elm.id.equals(string)) return elm; return null;
	}
	
	public boolean isDraggable(){
		return draggable;
	}
	
	public Element setDraggable(boolean bool){
		draggable = bool; return this;
	}

	public Element getRoot(){
		return root;
	}
	
	public static String translate(String str){
		return Translator.translate(str);
	}
	
	public static String translate(String str, String fill){
		return Translator.translate(str, fill);
	}
	
	public static String format(String str, String fill, Object... objs){
		return Translator.format(str, fill, objs);
	}

	public void dispose(){
		if(gentex && texture != null && texture.getGLID() != null) GL11.glDeleteTextures(texture.getGLID());
		if(!elements.isEmpty()) for(Element elm : elements) elm.dispose(); return;
	}

	public void pullBy(int mx, int my){
		xrel += mx; yrel += my; this.repos();
	}

}
