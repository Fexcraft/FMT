package net.fexcraft.app.fmt.ui.tree;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class ModelTree extends RightTree {
	
	private TurboList[] trlist;
	private PolygonWrapper poly;
	private int trheight;
	private long count;

	public ModelTree(){
		super("modeltree");
	}

	@Override
	public void renderSelf(int rw, int rh){
		this.y = UserInterface.TOOLBAR.height;
		this.x = rw - this.width; this.height = rh - y; trheight = 0;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/background_light");
		this.renderQuad(x - 2, y, 2, height = (rh - y + 4), "ui/background_dark");
		//
		trlist = (TurboList[])FMTB.MODEL.getCompound().values().toArray(new TurboList[]{});
		FMTB.MODEL.getCompound().values().forEach(turbo -> trheight += turbo.tempheight = 26 + (turbo.size() * 26));
		GL11.glTranslatef(0, 0,  10); int pass = 0;
		if(Settings.polygonCount()){
			this.renderQuad(x + 4, y + 4 + -scroll + (pass), width - 8, 24, "ui/background_dark");
			FontRenderer.drawText("Polygons: " + count, x + 8, y + 5 + -scroll + (pass), 1, fontcol);
			pass += 26; count = 0;
		}
		for(int i = 0; i < trlist.length; i++){
			TurboList list = trlist[i]; count += list.size();
			color(list.visible, list.selected).glColorApply();
			this.renderQuad(x + 4, y + 4 + -scroll + (pass), width - 8, 24, "ui/background_white");
			FontRenderer.drawText((Settings.polygonCount() ? "[" + list.size() + "] " : "") + list.id, x + 8, y + 5 + -scroll + (pass), 1, fontcol);
			GL11.glTranslatef(0, 0,  1);
			this.renderIcon(x + width - 92, y + 6 + -scroll + (pass), 20, "icons/group_minimize");
			this.renderIcon(x + width - 70, y + 6 + -scroll + (pass), 20, "icons/group_edit");
			this.renderIcon(x + width - 48, y + 6 + -scroll + (pass), 20, "icons/group_visible");
			this.renderIcon(x + width - 26, y + 6 + -scroll + (pass), 20, "icons/group_delete");
			GL11.glTranslatef(0, 0, -1); pass += 26;
			if(!list.minimized){
				for(int j = 0; j < list.size(); j++){
					poly = list.get(j); color(poly.visible, poly.selected || list.selected).glColorApply();
					this.renderQuad(x + 8, y + 4 + -scroll + (pass), width - 16, 24, "ui/background_white");
					FontRenderer.drawText(j + " | " + poly.name(), x + 10, y + 5 + -scroll + (pass), 1, fontcol);
					GL11.glTranslatef(0, 0,  1);
					this.renderIcon(x + width - 74, y + 6 + -scroll + (pass), 20, "icons/group_edit");
					this.renderIcon(x + width - 52, y + 6 + -scroll + (pass), 20, "icons/group_visible");
					this.renderIcon(x + width - 30, y + 6 + -scroll + (pass), 20, "icons/group_delete");
					GL11.glTranslatef(0, 0, -1); pass += 26;
				}
			}
		} this.size = pass;
		GL11.glTranslatef(0, 0, -10);
	}

	@Override
	public void hovered(int mx, int my){
		super.hovered(mx, my);
	}

	@Override
	protected boolean processButtonClick(int mx, int my, boolean left){
		if(!left || !(mx >= x + 8 && mx < x + width - 8 && my >= y + 4 && my < y + height - 8)) return false;
		int myy = my - (y + 4 + -scroll); int i = myy / 26; int k = 0; if(Settings.polygonCount()) i -= 1;
		for(int j = 0; j < trlist.length; j++){
			if(k == i){
				if(mx >= x + width - 92 && mx < x + width - 72){
					trlist[j].minimized = !trlist[j].minimized; return true;
				}
				else if(mx >= x + width - 70 && mx < x + width - 50){
					Editor.show("model_group_editor"); return true;
				}
				else if(mx >= x + width - 48 && mx < x + width - 28){
					trlist[j].visible = !trlist[j].visible; return true;
				}
				else if(mx >= x + width - 26 && mx < x + width -  6){
					String id = trlist[j].id;
					FMTB.showDialogbox("Remove this group?\n" + id, "Yes", "No!", () -> {
						FMTB.MODEL.getCompound().remove(id);
					}, DialogBox.NOTHING);
					return true;
				}
				else{
					boolean bool = trlist[j].selected;
					if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
					trlist[j].selected = !bool; FMTB.MODEL.updateFields();
					FMTB.MODEL.lastselected = null;
				}
				return true;
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
							String id = trlist[j].id; PolygonWrapper poly = trlist[j].get(l);
							FMTB.showDialogbox("Remove this polygon?\n" + id + ":" + poly.name(), "Yes", "No!", () -> {
								FMTB.MODEL.getCompound().get(id).remove(poly);
							}, DialogBox.NOTHING);
							return true;
						}
						else{
							boolean bool = trlist[j].get(l).selected;
							if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
							trlist[j].get(l).selected = !bool; FMTB.MODEL.updateFields();
							FMTB.MODEL.lastselected = trlist[j].get(l);
						}
						return true;
					}
				}
			} k++;
		} return true;
	}

	protected boolean processScrollWheel(int wheel){
		this.modifyScroll(-wheel / (Mouse.isButtonDown(1) ? 1 : 10)); return true;
	}
	
	public void modifyScroll(int amount){
		if(size < height) return; scroll += amount; if(scroll < 0) scroll = 0; if(scroll + height >= size) scroll = size - height + 26;
	}
	
}
