package net.fexcraft.app.fmt.ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.app.fmt.wrappers.GroupCompound.Selection;

public class ModelTree extends RightTree {
	
	private TurboList[] trlist;
	private PolygonWrapper poly;
	private int trheight;

	public ModelTree(){ super("modeltree"); }

	@Override
	public void renderSelf(int rw, int rh){
		this.x = rw - this.width; this.height = rh - 30; trheight = 0;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/button_bg");
		this.renderQuad(x - 2, y, 2, height = (rh - y + 4), "ui/background");
		//
		trlist = (TurboList[])FMTB.MODEL.getCompound().values().toArray(new TurboList[]{});
		FMTB.MODEL.getCompound().values().forEach(turbo -> trheight += turbo.tempheight = 26 + (turbo.size() * 26));
		GL11.glTranslatef(0, 0,  10); int pass = 0;
		for(int i = 0; i < trlist.length; i++){
			TurboList list = trlist[i];
			color(list.visible, isSelected(list)).glColorApply();
			this.renderQuad(x + 4, y + 4 + -scroll + (pass), width - 8, 24, "ui/background"); TextureManager.unbind();
			font.drawString(x + 8, y + 6 + -scroll + (pass), list.id, Color.white); RGB.glColorReset();
			GL11.glTranslatef(0, 0,  1);
			this.renderIcon(x + width - 92, y + 6 + -scroll + (pass), "icons/group_minimize");
			this.renderIcon(x + width - 70, y + 6 + -scroll + (pass), "icons/group_edit");
			this.renderIcon(x + width - 48, y + 6 + -scroll + (pass), "icons/group_visible");
			this.renderIcon(x + width - 26, y + 6 + -scroll + (pass), "icons/group_delete");
			GL11.glTranslatef(0, 0, -1); pass += 26;
			if(!list.minimized){
				for(int j = 0; j < list.size(); j++){
					poly = list.get(j); color(poly.visible, isSelected(list, j)).glColorApply();
					this.renderQuad(x + 8, y + 4 + -scroll + (pass), width - 16, 24, "ui/background"); TextureManager.unbind();
					font.drawString(x + 10, y + 6 + -scroll + (pass), j + " | " + poly.name(), Color.white); RGB.glColorReset();
					GL11.glTranslatef(0, 0,  1);
					this.renderIcon(x + width - 74, y + 6 + -scroll + (pass), "icons/group_edit");
					this.renderIcon(x + width - 52, y + 6 + -scroll + (pass), "icons/group_visible");
					this.renderIcon(x + width - 30, y + 6 + -scroll + (pass), "icons/group_delete");
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
					Editor.show("group_editor"); return true;
				}
				else if(mx >= x + width - 48 && mx < x + width - 28){
					trlist[j].visible = !trlist[j].visible; return true;
				}
				else if(mx >= x + width - 26 && mx < x + width -  6){
					FMTB.MODEL.getCompound().remove(trlist[j].id); return true;
				}
				else{
					if(isSelected(trlist[j])){
						FMTB.MODEL.deselectGroup(trlist[j].id);
					}
					else{
						FMTB.MODEL.selectGroup(trlist[j].id);
					}
				}
				return false;
			}
			if(!trlist[j].minimized){
				for(int l = 0; l < trlist[j].size(); l++){
					k++; if(k == i){
						if(mx >= x + width - 74 && mx < x + width - 54){
							Editor.show("general_editor"); return true;
						}
						else if(mx >= x + width - 52 && mx < x + width - 32){
							trlist[j].get(l).visible = !trlist[j].get(l).visible; return true;
						}
						else if(mx >= x + width - 30 && mx < x + width - 10){
							trlist[j].remove(l); return true;
						}
						else{
							if(isSelected(trlist[j], l)){
								FMTB.MODEL.deselect(trlist[j].id, l);
							}
							else{
								FMTB.MODEL.select(trlist[j].id, l);
							}
						}
						return false;
					}
				}
			} k++;
		} return false;
	}

	private boolean isSelected(TurboList list){
		for(Selection sel : FMTB.MODEL.getSelected()){
			if(sel.group.equals(list.id)) return true;
		}
		return false;
	}
	
	private boolean isSelected(TurboList list, int poly){
		for(Selection sel : FMTB.MODEL.getSelected()){
			if(sel.group.equals(list.id) && sel.element == poly) return true;
		}
		return false;
	}

	protected boolean processScrollWheel(int wheel){
		scroll += -wheel / (Mouse.isButtonDown(1) ? 1 : 10); //if(scroll < 0) scroll = 0; if(scroll > trheight) scroll = trheight - 100;
		return true;
	}
	
}
