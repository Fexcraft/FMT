package net.fexcraft.app.fmt.ui_old.tree;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui_old.FontRenderer;
import net.fexcraft.app.fmt.ui_old.UserInterface;
import net.fexcraft.app.fmt.ui_old.editor.Editor;
import net.fexcraft.app.fmt.ui_old.general.TextField;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;

public class HelperTree extends RightTree {
	
	public static GroupCompound[] trlist;
	public static int SEL = -1;
	//
	private TurboList poly;
	private int trheight;

	public HelperTree(){ super("helpertree"); }

	@Override
	public void renderSelf(int rw, int rh){
		this.y = UserInterface.TOOLBAR.height;
		this.x = rw - this.width; this.height = rh - y; trheight = 0;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/background_light");
		this.renderQuad(x - 2, y, 2, height = (rh - y + 4), "ui/background_dark");
		//
		trlist = HelperCollector.LOADED.toArray(new GroupCompound[0]); if(trlist.length == 0) SEL = -1;
		FMTB.MODEL.getCompound().values().forEach(turbo -> trheight += turbo.tempheight = 26 + (turbo.size() * 26));
		GL11.glTranslatef(0, 0,  10); int pass = 0;
		for(int i = 0; i < trlist.length; i++){
			GroupCompound model = trlist[i];
			color(model.visible, i == SEL).glColorApply();
			this.renderQuad(x + 4, y + 4 + -scroll + (pass), width - 8, 24, "ui/background_white");
			FontRenderer.drawText(model.name, x + 8, y + 6 + -scroll + (pass), 1, fontcol);
			GL11.glTranslatef(0, 0,  1);
			this.renderIcon(x + width - 114, y + 6 + -scroll + (pass), 20, "icons/group_minimize");
			this.renderIcon(x + width - 92, y + 6 + -scroll + (pass), 20, "icons/group_clone");
			this.renderIcon(x + width - 70, y + 6 + -scroll + (pass), 20, "icons/group_edit");
			this.renderIcon(x + width - 48, y + 6 + -scroll + (pass), 20, "icons/group_visible");
			this.renderIcon(x + width - 26, y + 6 + -scroll + (pass), 20, "icons/group_delete");
			GL11.glTranslatef(0, 0, -1); pass += 26;
			if(!model.minimized){
				for(int j = 0; j < model.getCompound().size(); j++){
					poly = (TurboList)model.getCompound().values().toArray()[j]; color(poly.visible, false).glColorApply();
					this.renderQuad(x + 8, y + 4 + -scroll + (pass), width - 16, 24, "ui/background_white");
					FontRenderer.drawText(j + " | " + poly.id, x + 10, y + 6 + -scroll + (pass), 1, fontcol);
					GL11.glTranslatef(0, 0,  1);
					this.renderIcon(x + width - 30, y + 6 + -scroll + (pass), 20, "icons/group_visible");
					GL11.glTranslatef(0, 0, -1); pass += 26;
				}
			}
		} this.size = pass / 26;
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
				if(mx >= x + width - 114 && mx < x + width - 94){
					trlist[j].minimized = !trlist[j].minimized; return true;
				}
				else if(mx >= x + width - 92 && mx < x + width - 72){
					GroupCompound compound = null, parent = trlist[j];
					if(parent.name.startsWith("fmtb/")){
						compound = HelperCollector.loadFMTB(parent.origin);
					}
					else if(parent.name.startsWith("frame/")){
						compound = HelperCollector.loadFrame(parent.origin);
					}
					else{
						ExImPorter porter = PorterManager.getPorterFor(parent.origin, false);
						HashMap<String, Setting> map = new HashMap<>();
						porter.getSettings(false).forEach(setting -> map.put(setting.getId(), setting));
						compound = HelperCollector.load(parent.file, porter, map);
					}
					if(compound == null){ Print.console("Error on creating clone."); return true; }
					if(parent.pos != null) compound.pos = new Vec3f(parent.pos);
					if(parent.rot != null) compound.rot = new Vec3f(parent.rot);
					if(parent.scale != null) compound.scale = new Vec3f(parent.scale);
					return true;
				}
				else if(mx >= x + width - 70 && mx < x + width - 50){
					Editor.show("preview_editor"); return true;
				}
				else if(mx >= x + width - 48 && mx < x + width - 28){
					trlist[j].visible = !trlist[j].visible; return true;
				}
				else if(mx >= x + width - 26 && mx < x + width -  6){
					HelperCollector.LOADED.remove(j); return true;
				}
				else{
					SEL = j; GroupCompound model = getSelected();
					if(model == null){
						TextField.getFieldById("helper_posx").applyChange(0);
						TextField.getFieldById("helper_posy").applyChange(0);
						TextField.getFieldById("helper_posz").applyChange(0);
						TextField.getFieldById("helper_rotx").applyChange(0);
						TextField.getFieldById("helper_roty").applyChange(0);
						TextField.getFieldById("helper_rotz").applyChange(0);
						TextField.getFieldById("helper_scalex").applyChange(0);
						TextField.getFieldById("helper_scaley").applyChange(0);
						TextField.getFieldById("helper_scalez").applyChange(0);
					}
					else{
						TextField.getFieldById("helper_posx").applyChange(model.pos == null ? 0 : model.pos.xCoord);
						TextField.getFieldById("helper_posy").applyChange(model.pos == null ? 0 : model.pos.yCoord);
						TextField.getFieldById("helper_posz").applyChange(model.pos == null ? 0 : model.pos.zCoord);
						TextField.getFieldById("helper_rotx").applyChange(model.rot == null ? 0 : model.rot.xCoord);
						TextField.getFieldById("helper_roty").applyChange(model.rot == null ? 0 : model.rot.yCoord);
						TextField.getFieldById("helper_rotz").applyChange(model.rot == null ? 0 : model.rot.zCoord);
						TextField.getFieldById("helper_scalex").applyChange(model.scale == null ? 1 : model.scale.xCoord);
						TextField.getFieldById("helper_scaley").applyChange(model.scale == null ? 1 : model.scale.yCoord);
						TextField.getFieldById("helper_scalez").applyChange(model.scale == null ? 1 : model.scale.zCoord);
					}
				}
				return true;
			}
			if(!trlist[j].minimized){
				for(int l = 0; l < trlist[j].getCompound().size(); l++){
					k++; if(k == i){
						if(mx >= x + width - 30 && mx < x + width - 10){
							trlist[j].getCompound().values().toArray(new TurboList[0])[l].visible = !trlist[j].getCompound().values().toArray(new TurboList[0])[l].visible;
							return true;
						} else return true;
					}
				}
			} k++;
		} return true;
	}

	protected boolean processScrollWheel(int wheel){
		scroll += -wheel / 10; //if(scroll < 0) scroll = 0; if(scroll > trheight) scroll = trheight - 100;
		return true;
	}

	public static GroupCompound getSelected(){
		return SEL >= trlist.length || SEL < 0 ? null : trlist[SEL];
	}
	
}
