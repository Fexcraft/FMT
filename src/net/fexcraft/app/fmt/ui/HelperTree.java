package net.fexcraft.app.fmt.ui;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.app.fmt.wrappers.GroupCompound;

public class HelperTree extends RightTree {
	
	private GroupCompound[] trlist;
	private TurboList poly;
	private int trheight;

	public HelperTree(){ super("helpertree"); }

	@Override
	public void renderSelf(int rw, int rh){
		this.x = rw - this.width; this.height = rh - 30; trheight = 0;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/button_bg");
		this.renderQuad(x - 2, y, 2, height = (rh - y + 4), "ui/background");
		//
		trlist = HelperCollector.LOADED.toArray(new GroupCompound[0]);
		FMTB.MODEL.getCompound().values().forEach(turbo -> trheight += turbo.tempheight = 26 + (turbo.size() * 26));
		GL11.glTranslatef(0, 0,  10); int pass = 0;
		for(int i = 0; i < trlist.length; i++){
			GroupCompound model = trlist[i];
			color(model.visible, false).glColorApply();
			this.renderQuad(x + 4, y + 4 + -scroll + (pass), width - 8, 24, "ui/background"); TextureManager.unbind();
			font.drawString(x + 8, y + 6 + -scroll + (pass), model.name, Color.white); RGB.glColorReset();
			GL11.glTranslatef(0, 0,  1);
			this.renderIcon(x + width - 92, y + 6 + -scroll + (pass), "icons/group_minimize");
			this.renderIcon(x + width - 70, y + 6 + -scroll + (pass), "icons/group_edit");
			this.renderIcon(x + width - 48, y + 6 + -scroll + (pass), "icons/group_visible");
			this.renderIcon(x + width - 26, y + 6 + -scroll + (pass), "icons/group_delete");
			GL11.glTranslatef(0, 0, -1); pass += 26;
			if(!model.minimized){
				for(int j = 0; j < model.getCompound().size(); j++){
					poly = (TurboList)model.getCompound().values().toArray()[j]; color(poly.visible, false).glColorApply();
					this.renderQuad(x + 8, y + 4 + -scroll + (pass), width - 16, 24, "ui/background"); TextureManager.unbind();
					font.drawString(x + 10, y + 6 + -scroll + (pass), j + " | " + poly.id, Color.white); RGB.glColorReset();
					GL11.glTranslatef(0, 0,  1);
					this.renderIcon(x + width - 30, y + 6 + -scroll + (pass), "icons/group_visible");
					GL11.glTranslatef(0, 0, -1); pass += 26;
				}
			}
		}
		GL11.glTranslatef(0, 0, -10);
	}

	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my);
	}

	@Override
	protected boolean processButtonClick(int mx, int my, boolean left){
		if(!(mx >= x + 8 && mx < x + width - 8 && my >= y + 4 && my < y + height - 8)) return false;
		int myy = my - (y + 4 + -scroll); int i = myy / 26; int k = 0;
		for(int j = 0; j < trlist.length; j++){
			if(k == i){
				if(mx >= x + width - 92 && mx < x + width - 72){
					trlist[j].minimized = !trlist[j].minimized; return true;
				}
				else if(mx >= x + width - 70 && mx < x + width - 50){
					Editor.show("helper_editor"); return true;
				}
				else if(mx >= x + width - 48 && mx < x + width - 28){
					trlist[j].visible = !trlist[j].visible; return true;
				}
				else if(mx >= x + width - 26 && mx < x + width -  6){
					HelperCollector.LOADED.remove(j); return true;
				}
				else{
					//
				}
				return false;
			}
			if(!trlist[j].minimized){
				for(int l = 0; l < trlist[j].getCompound().size(); l++){
					k++; if(k == i){
						if(mx >= x + width - 30 && mx < x + width - 10){
							trlist[j].getCompound().values().toArray(new TurboList[0])[l].visible = !trlist[j].getCompound().values().toArray(new TurboList[0])[l].visible;
							return true;
						} else return false;
					}
				}
			} k++;
		} return false;
	}

	protected boolean processScrollWheel(int wheel){
		scroll += -wheel / 10; //if(scroll < 0) scroll = 0; if(scroll > trheight) scroll = trheight - 100;
		return true;
	}
	
}
