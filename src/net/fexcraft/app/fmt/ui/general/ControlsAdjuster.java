/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.utils.KeyCompound;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.KeyCompound.KeyFunction;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class ControlsAdjuster extends Element implements Dialog {

	public static boolean CATCHING = false;
	//private Button next, prev;
	private int scroll = 0, hovered = -1, catched = -1;
	private KeyFunction tempkey;
	private boolean changed;
	private float tx, ty;
	
	public ControlsAdjuster(){
		super(null, "ui/controls"); TextureManager.loadTexture("ui/controls", null);
		this.visible = false; this.z = 80; this.height = 312; this.width = 512; Dialog.dialogs.add(this);
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderQuad(x = (rw / 2) - (width / 2), y = (rh / 2) - (height / 2), width, height, "ui/controls");
		if(hovered > -1){
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(1, 0.2f, 0.2f, 0.2f);
			GL11.glBegin(GL11.GL_QUADS);
			tx = x + 346; ty = y + 46 + (hovered * 32);
			GL11.glVertex2f(tx, ty);
			GL11.glVertex2f(tx + 150, ty);
			GL11.glVertex2f(tx + 150, ty + 28);
			GL11.glVertex2f(tx, ty + 28);
	        GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
		try{
			{
				FontRenderer.drawText("FMT Controls Settings", this.x + 18, this.y + 19, 3);
				FontRenderer.drawText("Pg." + scroll, this.x + 446, this.y + 19, 3);
				for(int i = 0; i < 8; i++){
					int j = i + (scroll * 8); if(j >= KeyCompound.keys.size()) continue; //break;
					FontRenderer.drawText((tempkey = KeyCompound.keys.get(j)).name(), this.x + 21, this.y + 51 + (i * 32), 3);
					String name = Keyboard.getKeyName(tempkey.ID());
					FontRenderer.drawText(this.getName(catched == i, name), this.x + 360, this.y + 51 + (i * 32), 3);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getName(boolean bool, String name){
		if(bool){
			return CATCHING ? "<{" + name + "}>" : "[" + name + "]";
		} return name;
	}
	
	@Override
	public boolean onScrollWheel(int wheel){
		if(wheel < 0) scroll++; else scroll--; if(scroll < 0) scroll = 0; hovered = -1; catched = -1; return !(CATCHING = false);
	}
	
	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		if(hovered >= 0 && hovered < 8) { catched = hovered; CATCHING = true; } return true;
	}
	
	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my);
		for(int i = 0; i < 8; i++){ int j = i * 32;
			if(mx >= (x + 346) && mx < (x + 346 + 150) && my >= (y + 46 + j) && my < (y + 46 + j + 28)){ hovered = i; return; }
		} hovered = -1;
	}
	
	public boolean show(){
		this.reset(); return this.visible = true;
	}
	
	public void reset(){
		if(changed) KeyCompound.save(); changed = false; scroll = 0; visible = false; hovered = -1; catched = -1; CATCHING = false;
	}

	@Override
	public boolean visible(){
		return visible;
	}

	public void catchKey(Integer key){ if(key == 1) key = null;
		int j = catched + (scroll * 8); tempkey = KeyCompound.keys.size() <= j || j < 0 ? null : KeyCompound.keys.get(j);
		if(tempkey != null) tempkey.setID(key); CATCHING = false; changed = true; return;
	}

}
