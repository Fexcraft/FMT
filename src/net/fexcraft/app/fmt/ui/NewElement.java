package net.fexcraft.app.fmt.ui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.lib.common.math.RGB;

public class NewElement {

	private ArrayList<NewElement> elements = new ArrayList<>();
	private final NewElement root;
	private final String id;
	//
	private float[][][] vertexes;
	private boolean top, bot, left, right;
	private Texture texture;
	//
	private int width, height, x, xrel, y, yrel; 
	private Integer fill, border, border_fill, border_width;
	private RGB hovercolor = new RGB(218, 232, 104);
	public boolean hovered, visible, enabled;
	
	public NewElement(NewElement root, String id){
		this.root = root; this.id = id;
	}
	
	public NewElement setPosition(int x, int y){
		xrel = x; yrel = y; this.repos(); return this;
	}
	
	public NewElement setSize(int x, int y){
		width = x; height = y; return this;
	}
	
	public NewElement setBorder(int color, int color0, int width, boolean... bools){
		border = color; border_fill = color0; border_width = width;
		top = bools.length > 0 && bools[0]; bot = bools.length > 1 && bools[1];
		left = bools.length > 2 && bools[2]; right = bools.length > 3 && bools[3];
		return this.clearVertexes().clearTexture();
	}

	public NewElement clearVertexes(){
		vertexes = null; return this;
	}

	public NewElement clearTexture(){
		if(texture != null) texture.rebind(); return this;
	}

	public NewElement setColor(int color){
		fill = color; return this;
	}
	
	public NewElement repos(){
		if(root == null){ x = xrel; y = yrel; } else { x = root.x + xrel; y = root.y + yrel; }
		clearVertexes(); for(NewElement elm : elements) elm.repos(); return this;
	}

	public void render(int width, int height){
		if(!Mouse.isGrabbed()) hovered(Mouse.getX() * UserInterface.scale, height - Mouse.getY() * UserInterface.scale);
		this.renderSelfQuad();
		//
		if(this.visible){
			//if(z != 0) GL11.glTranslatef(0, 0,  z);
			this.renderSelf(width, height);
			//if(z != 0) GL11.glTranslatef(0, 0, -z);
		}
		if(this.visible && !elements.isEmpty()) for(NewElement elm : elements) elm.render(width, height);
	}
	
	/** To be overriden by extending classes. */
	public void renderSelf(int rw, int rh){
		this.renderSelfQuad();
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
	
	protected void renderSelfQuad(){
		if(texture == null || texture.rebindQ() || vertexes == null){
			int width = this.width, height = this.height;
			if(top) height += border_width; if(bot) height += border_width;
			if(left) width += border_width; if(right) width += border_width;
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			if(border != null) for(int i = 0; i < width; i++) for(int j = 0; j < height; j++) img.setRGB(i, j, border);
			{
				int xb = left ? border_width : 0, yb = top ? border_width : 0;
				int xe = right ? width - border_width : 0, ye = bot ? height - border_width : 0;
				runfill(img, xb, xe, yb, ye, fill);
			}
			if(border_width > 2){
				if(top) runfill(img, 1, width - 1, 1, border_width - 1, border_fill);
				if(bot) runfill(img, 1, width - 1, height - border_width + 1, height - 1, border_fill);
				if(left) runfill(img, 1, border_width - 1, 1, height - 1, border_fill);
				if(right) runfill(img, width - border_width + 1, width - 1, 1, height - 1, border_fill);
			}
			if(texture == null) texture = TextureManager.createTexture("elm:" + id, img); else texture.setImage(img);
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

	private void runfill(BufferedImage img, int xb, int xe, int yb, int ye, Integer fill){
		for(int i = xb; i < xe; i++) for(int j = yb; j < ye; j++) img.setRGB(i, j, fill);
	}

}
