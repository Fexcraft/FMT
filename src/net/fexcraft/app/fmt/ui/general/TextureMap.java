/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import static net.fexcraft.app.fmt.ui.general.TextureMap.Orientation.HEIGHT;
import static net.fexcraft.app.fmt.ui.general.TextureMap.Orientation.NONE;
import static net.fexcraft.app.fmt.ui.general.TextureMap.Orientation.WIDTH;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureMap extends Element implements Dialog {
	
	private Icon exit, apply, move_left, move_right, move_up, move_down, reset;
	private PolygonWrapper selected;
	private Orientation orient;
	private float[][][] texs;
	private int oldx, oldy;
	//private float zoom;
	
	public TextureMap(){
		super(null, "texture_map", "texture_map"); this.setSize(520, 520).setDraggable(true).setColor(0xff80adcc);
		this.setVisible(false).setPosition(0, 0).setHoverColor(0xffffffff, false); Dialog.dialogs.add(this);
		this.setBorder(0xff000000, 0xff3458eb, 5, true, true, true, true); orient = NONE;
		//
		this.elements.add(exit = new Icon(this, "exit", "texture_map:button", "icons/texture_map/close", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				((TextureMap)root).reset(); return true;
			}
		});
		this.elements.add(apply = new Icon(this, "apply", "texture_map:button", "icons/texture_map/apply", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				Texture tex = TextureManager.getTexture(FMTB.MODEL.texture, false);
            	selected.burnToTexture(tex.getImage(), null);
            	TextureManager.saveTexture(FMTB.MODEL.texture);
            	tex.reload(); return true;
			}
		});
		this.elements.add(reset = new Icon(this, "rest", "texture_map:button", "icons/texture_map/reset", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				selected.textureX = oldx; selected.textureY = oldy; selected.recompile(); return true;
			}
		});
		this.elements.add(move_left = new Icon(this, "move_left", "texture_map:button", "icons/texture_map/move_left", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				selected.textureX += left ? 1 : 16; if(selected.textureX > FMTB.MODEL.textureSizeX) selected.textureX = FMTB.MODEL.textureSizeX; selected.recompile(); return true;
			}
		});
		this.elements.add(move_right = new Icon(this, "move_right", "texture_map:button", "icons/texture_map/move_right", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				selected.textureX += left ? -1 : -16; if(selected.textureX < 0) selected.textureX = 0; selected.recompile(); return true;
			}
		});
		this.elements.add(move_up = new Icon(this, "move_up", "texture_map:button", "icons/texture_map/move_up", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				selected.textureY += left ? -1 : -16; if(selected.textureY > FMTB.MODEL.textureSizeY) selected.textureY = FMTB.MODEL.textureSizeY; selected.recompile(); return true;
			}
		});
		this.elements.add(move_down = new Icon(this, "move_down", "texture_map:button", "icons/texture_map/move_down", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				selected.textureY += left ? 1 : 16; if(selected.textureY < 0) selected.textureY = 0; selected.recompile(); return true;
			}
		});
	}
	
	@Override
	public Element repos(){
		x = (UserInterface.width - width) / 2 + xrel; y = (UserInterface.height - height) / 2 + yrel;
		clearVertexes(); for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void renderSelf(int rw, int rh) {
		this.renderSelfQuad(); String texture = FMTB.MODEL.texture == null ? "dotted" : FMTB.MODEL.texture;
		renderQuad(x + 4, y + 4, orient.x, orient.y, "dotted");
		renderQuad(x + 4, y + 4, orient.x, orient.y, texture);
		float sx = FMTB.MODEL.textureSizeX / (float)orient.x, sy = FMTB.MODEL.textureSizeY / (float)orient.y;
		if(FMTB.MODEL.getLastSelected() != selected){
			selected = FMTB.MODEL.getLastSelected(); texs = selected == null ? null : selected.texpos;
			if(selected == null){ oldx = oldy = 0; } else{ oldx = selected.textureX; oldy = selected.textureY; }
		} if(texs == null) return;
		TextureManager.bindTexture("null");
		for(float[][] tex : texs){
			renderQuad(x + 4 + (int)((tex[0][0] + selected.textureX) / sx), y + 4 + (int)((tex[0][1] + selected.textureY) / sy),
				(int)((tex[1][0] - tex[0][0]) / sx), (int)((tex[1][1] - tex[0][1]) / sy), "bordered");
		}
	}
	
	@Override
	public boolean onScrollWheel(int wheel){
		//
		return true;
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		//
		return true;
	}

	@Override
	public boolean visible(){
		return visible;
	}
	
	public void show(){
		if(visible){ this.reset(); return; }
		if(FMTB.MODEL.textureSizeX == FMTB.MODEL.textureSizeY) orient = NONE;
		else orient = FMTB.MODEL.textureSizeX > FMTB.MODEL.textureSizeY ? WIDTH : HEIGHT;
		switch(orient){
			case NONE: default:
				height = 556; width = 520;
				//
				apply.xrel = 4; apply.yrel = 520;
				reset.xrel = 40; reset.yrel = 520;
				move_right.xrel = 76; move_right.yrel = 520;
				move_left.xrel = 112; move_left.yrel = 520;
				move_up.xrel = 148; move_up.yrel = 520;
				move_down.xrel = 184; move_down.yrel = 520;
				exit.xrel = 484; exit.yrel = 520;
				break;
			case WIDTH:
				height = 300; width = 520;
				//
				apply.xrel = 4; apply.yrel = 264;
				reset.xrel = 40; reset.yrel = 264;
				move_right.xrel = 76; move_right.yrel = 264;
				move_left.xrel = 112; move_left.yrel = 264;
				move_up.xrel = 148; move_up.yrel = 264;
				move_down.xrel = 184; move_down.yrel = 264;
				exit.xrel = 484; exit.yrel = 264;
				break;
			case HEIGHT:
				height = 556; width = 264;
				//
				apply.xrel = 4; apply.yrel = 520;
				reset.xrel = 40; reset.yrel = 520;
				move_right.xrel = 76; move_right.yrel = 520;
				move_left.xrel = 112; move_left.yrel = 520;
				move_up.xrel = 148; move_up.yrel = 520;
				move_down.xrel = 184; move_down.yrel = 520;
				exit.xrel = 228; exit.yrel = 520;
				break;
			
		}
		this.visible = true; this.repos();
	}

	@Override
	public void reset(){
		this.visible = false; orient = NONE;
	}
	
	public static enum Orientation {
		
		NONE(512, 512), WIDTH(512, 256), HEIGHT(256, 512);
		
		public int x, y;
		
		Orientation(int x, int y){
			this.x = x; this.y = y;
		}
		
	}

}
