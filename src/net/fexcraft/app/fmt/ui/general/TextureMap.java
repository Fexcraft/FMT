/**
 * 
 */
package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureMap extends Element implements Dialog {
	
	private Icon exit, apply, move_left, move_right, move_up, move_down, reset;
	private PolygonWrapper selected;
	private Boolean orientation;
	private int offx, offy;
	private float zoom;
	
	public TextureMap(){
		super(null, "texture_map", "texture_map"); this.setSize(520, 520).setDraggable(true).setColor(0xff80adcc);
		this.setVisible(false).setPosition(0, 0).setHoverColor(0xffffffff, false); Dialog.dialogs.add(this);
		this.setBorder(0xff000000, 0xff3458eb, 5, true, true, true, true); orientation = null;
		//
		this.elements.add(exit = new Icon(this, "exit", "texture_map:button", "icons/texture_map/close", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				((TextureMap)root).reset(); return true;
			}
		});
		this.elements.add(apply = new Icon(this, "apply", "texture_map:button", "icons/texture_map/apply", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				return true;
			}
		});
		this.elements.add(reset = new Icon(this, "rest", "texture_map:button", "icons/texture_map/reset", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				offx = 0; offy = 0; return true;
			}
		});
		this.elements.add(move_left = new Icon(this, "move_left", "texture_map:button", "icons/texture_map/move_left", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				offx += left ? 1 : 16; return true;
			}
		});
		this.elements.add(move_right = new Icon(this, "move_right", "texture_map:button", "icons/texture_map/move_right", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				offx += left ? -1 : -16; return true;
			}
		});
		this.elements.add(move_up = new Icon(this, "move_up", "texture_map:button", "icons/texture_map/move_up", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				offy += left ? -1 : -16; return true;
			}
		});
		this.elements.add(move_down = new Icon(this, "move_down", "texture_map:button", "icons/texture_map/move_down", 32, 0, 0){
			@Override public boolean processButtonClick(int x, int y, boolean left){
				offy += left ? 1 : 16; return true;
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
		this.renderSelfQuad(); String texture = FMTB.MODEL.texture == null ? "null" : FMTB.MODEL.texture;
		renderQuad(x + 4, y + 4, orientation == null || orientation ? 512 : 256, orientation == null || !orientation ? 512 : 256, "dotted");
		renderQuad(x + 4, y + 4, orientation == null || orientation ? 512 : 256, orientation == null || !orientation ? 512 : 256, texture);
		if(FMTB.MODEL.getLastSelected() != selected){
			selected = FMTB.MODEL.getLastSelected();
			//gentex
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
		if(FMTB.MODEL.textureSizeX == FMTB.MODEL.textureSizeY) orientation = null;
		else orientation = FMTB.MODEL.textureSizeX > FMTB.MODEL.textureSizeY;
		if(orientation == null){
			height = 556; width = 520;
			//
			apply.xrel = 4; apply.yrel = 520;
			reset.xrel = 40; reset.yrel = 520;
			move_right.xrel = 76; move_right.yrel = 520;
			move_left.xrel = 112; move_left.yrel = 520;
			move_up.xrel = 148; move_up.yrel = 520;
			move_down.xrel = 184; move_down.yrel = 520;
			exit.xrel = 484; exit.yrel = 520;
		}
		else if(orientation){
			height = 300; width = 520;
			//
			apply.xrel = 4; apply.yrel = 264;
			reset.xrel = 40; reset.yrel = 264;
			move_right.xrel = 76; move_right.yrel = 264;
			move_left.xrel = 112; move_left.yrel = 264;
			move_up.xrel = 148; move_up.yrel = 264;
			move_down.xrel = 184; move_down.yrel = 264;
			exit.xrel = 484; exit.yrel = 264;
		}
		else{
			height = 556; width = 264;
			//
			apply.xrel = 4; apply.yrel = 520;
			reset.xrel = 40; reset.yrel = 520;
			move_right.xrel = 76; move_right.yrel = 520;
			move_left.xrel = 112; move_left.yrel = 520;
			move_up.xrel = 148; move_up.yrel = 520;
			move_down.xrel = 184; move_down.yrel = 520;
			exit.xrel = 228; exit.yrel = 520;
		}
		this.visible = true; this.repos();
	}

	@Override
	public void reset(){
		this.visible = false; orientation = null;
	}

}
