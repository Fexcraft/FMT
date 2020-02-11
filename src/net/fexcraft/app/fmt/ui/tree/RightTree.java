package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.SettingsBox;
import net.fexcraft.app.fmt.ui.FontRenderer.FontType;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.Icon;
import net.fexcraft.app.fmt.ui.general.Scrollbar;
import net.fexcraft.app.fmt.ui.general.Scrollbar.Scrollable;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;

public abstract class RightTree extends Element implements Scrollable {
	
	public static final ArrayList<RightTree> TREES = new ArrayList<RightTree>();
	protected static final RGB fontcol = new RGB("#1e1e1e");
	private static RGB sel_in_g, sel_in_p, sel_in_c, sel_vi_g, sel_vi_p, sel_vi_c;
	private static RGB def_g, def_p, def_c, inv_g, inv_p, inv_c;
	protected Scrollbar scrollbar;
	protected int fullheight;

	public RightTree(String id){
		super(null, id, id); this.setSize(308, 100).setPosition(0, 0).setVisible(false); TREES.add(this);
		this.setColor(0xff999999).setBorder(0xff000000, 0xffffffff, 1, false, false, true, false);
		this.setHoverColor(0xffffffff, false); this.repos();
		//
		sel_in_g = new RGB(StyleSheet.getColourFor("tree:group", "selected_invisible", 0xffaa7e36));
		sel_in_p = new RGB(StyleSheet.getColourFor("tree:polygon", "selected_invisible", 0xffaa7e36));
		sel_in_c = new RGB(StyleSheet.getColourFor("tree:compound", "selected_invisible", 0xffaa7e36));
		sel_vi_g = new RGB(StyleSheet.getColourFor("tree:group", "selected_visible", 0xff934427));
		sel_vi_p = new RGB(StyleSheet.getColourFor("tree:polygon", "selected_visible", 0xff934427));
		sel_vi_c = new RGB(StyleSheet.getColourFor("tree:compound", "selected_visible", 0xff934427));
		def_g = new RGB(StyleSheet.getColourFor("tree:group", "background_visible", 0xff0b6623));
		def_p = new RGB(StyleSheet.getColourFor("tree:polygon", "background_visible", 0xff0b6623));
		def_c = new RGB(StyleSheet.getColourFor("tree:compound", "background_visible", 0xff4287f5));
		inv_g = new RGB(StyleSheet.getColourFor("tree:group", "background_invisible", 0xff80a073));
		inv_p = new RGB(StyleSheet.getColourFor("tree:polygon", "background_invisible", 0xff80a073));
		inv_c = new RGB(StyleSheet.getColourFor("tree:compound", "background_invisible", 0xff80a073));
		//
		this.elements.add(scrollbar = new Scrollbar(this, true));
	}
	
	@Override
	public Element repos(){
		if(UserInterface.TOOLBAR == null){ return this; }//skip, this call is before the UI is setup
		x = UserInterface.width - width; y = UserInterface.TOOLBAR.height + UserInterface.TOOLBAR.border_width;
		height = UserInterface.height - y; /*if(Settings.bottombar()) height -= 29;*/ clearVertexes();
		for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void render(int width, int height){
		if(!FMTB.hold_right) hovered(FMTB.cursor_x * UserInterface.scale, height - FMTB.cursor_y * UserInterface.scale);
		//
		if(this.visible){ this.renderSelfQuad(); this.renderSelf(width, height); }
	}

	@Override
	public boolean processButtonClick(int x, int y, boolean left){
		return true;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		TREES.forEach(elm -> { elm.visible = elm.id.equals(id); elm.scrollbar.scrolled = 0; });
	}
	
	public static void hideAll(){
		TREES.forEach(elm -> { elm.visible = false; elm.scrollbar.scrolled = 0; } );
	}

	public static void toggle(String string){ toggle(string, true); }

	public static void toggle(String string, boolean close){
		Optional<RightTree> opt = TREES.stream().filter(pre -> pre.id.equals(string)).findFirst();
		if(close && opt.isPresent() && opt.get().visible){ hideAll(); } else{ show(string); }
	}
	
	protected static RGB colorG(boolean visible, boolean selected){
		return visible ? selected ? sel_vi_g : def_g : selected ? sel_in_g : inv_g;
	}
	
	protected static RGB colorP(boolean visible, boolean selected){
		return visible ? selected ? sel_vi_p : def_p : selected ? sel_in_p : inv_p;
	}
	
	protected static RGB colorC(boolean visible, boolean selected){
		return visible ? selected ? sel_vi_c : def_c : selected ? sel_in_c : inv_c;
	}

	public static boolean anyTreeHovered(){
		return  TREES.stream().filter(pre -> pre.isHovered()).findFirst().isPresent();
	}

	@Override
	public int getFullHeight(){
		return fullheight;
	}
	
	@Override
	public boolean refresh(){ return true; };//TODO
	
	public abstract void refreshFullHeight();
	
	public static abstract class TreeButton extends Button {
		
		private int xl, yl;
		
		public TreeButton(Element root, String id, String ss, int width, int height, int x, int y){
			super(root, id, ss, width, height, x, y); xl = x; yl = y;
		}

		@Override
		public void renderSelf(int rw, int rh){
			if(drawbackground){
				color().glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
			}
			if(text != null){
				RGB color = !drawbackground ? hovered ? hovercolor : !enabled ? discolor : RGB.BLACK : RGB.BLACK;
				if(centered){
					int x = width / 2 - (FontRenderer.getWidth(text, FontType.BOLD) / 2), y = height / 2 - 10;
					FontRenderer.drawText(text, this.x + x + (icon == null ? 0 : iconsize + 2), this.y + y, FontType.BOLD, color);
				}
				else{
					FontRenderer.drawText(text, x + texxoff + (icon == null ? 0 : iconsize + 2), y + texyoff, FontType.BOLD, color);
				}
			}
			if(icon != null){
				if(iconcolor != null) iconcolor.glColorApply();
				float y = (height - iconsize) * 0.5f;
				this.renderIcon(x + 2, this.y + y, iconsize, icon);
				if(iconcolor != null) RGB.glColorReset();
			}
		}
		
		public abstract RGB color();
		
		public abstract void update(int height, int rw, int rh);
		
		@Override
		public Element setPosition(int x, int y){
			if(xl != x || yl != y){ super.setPosition(x, y); xl = x; yl = y; } return this;
		}
		
	}
	
	public static class GroupButton extends TreeButton {
		
		private int rel; private TurboList list; private boolean compound;

		public GroupButton(Element root, TurboList list){
			super(root, list.id, "tree:group", 300, 26, 4, 0);
			this.setColor(StyleSheet.WHITE).setDraggable(true);
			this.setText((this.list = list).id, false); rel = 4;
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_minimize", 22, width - 104, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					list.minimized = !list.minimized; ((RightTree)root.getRoot()).refreshFullHeight(); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					Editor.show("model_group_editor"); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					list.visible = !list.visible; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					String str = format("modeltree.remove_group", "Remove this group?<nl>%s", list.id);
					String yes = translate("modeltree.remove_group.confirm", "Yes");
					FMTB.showDialogbox(str, yes, translate("modeltree.remove_group.cancel", "No!"), () -> {
						FMTB.MODEL.getGroups().remove(list.id);
					}, DialogBox.NOTHING); return true;
				}
			});
		}
		
		public void setAsHelperPreview(){
			elements.clear(); this.setSize(296, 26); rel = 8; compound = true;
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 26, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					list.visible = !list.visible; return true;
				}
			});
		}

		@Override
		public RGB color(){
			return colorG(list.visible, list.selected);
		}
		
		@Override
		public boolean processButtonClick(int mx, int my, boolean left){
			if(!left) return false; boolean bool = list.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
			list.selected = !bool; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = null;
			if(!compound) GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs(); return true;
		}

		@Override
		public void update(int elm_height, int rw, int rh){
			this.setPosition(rel, elm_height); id = list.id;
			if(!compound) this.setText("[" + list.size() + "] " + id, false);
			else this.setText(id, false); this.render(rw, rh);
		}
		
		@Override
		public void pullBy(int mx, int my){
			if(FMTB.MODEL.getGroups().size() < 2) return;
			int index = FMTB.MODEL.getGroups().indexOf(list);
			if(my < 0 && index > 0 && FMTB.MODEL.getGroups().get(index - 1).minimized){
				Collections.swap(FMTB.MODEL.getGroups(), index, index - 1); GGR.resetDragging(); return;
			}
			if(my > 0 && (index + 1) < FMTB.MODEL.getGroups().size() && list.minimized){
				Collections.swap(FMTB.MODEL.getGroups(), index, index + 1); GGR.resetDragging(); return;
			}
		}
		
	}
	
	public static class PolygonButton extends TreeButton {

		private PolygonWrapper polygon;

		public PolygonButton(Element root, PolygonWrapper polygon){
			super(root, polygon.name, "tree:polygon", 296, 26, 8, 0);
			this.setColor(StyleSheet.WHITE);//.setDraggable(true);
			this.setText((this.polygon = polygon).name(), false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					Editor.show("general_editor"); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					polygon.visible = !polygon.visible; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					String str = format("modeltree.remove_polygon", "Remove this polygon?<nl>%s", polygon.getTurboList().id + ":" + polygon.name());
					FMTB.showDialogbox(str, translate("modeltree.remove_polygon.confirm", "Yes"), translate("modeltree.remove_polygon.cancel", "No!"), () -> {
						polygon.getTurboList().remove(polygon);
					}, DialogBox.NOTHING); return true;
				}
			});
		}

		@Override
		public RGB color(){
			return colorP(polygon.visible, polygon.selected || polygon.getTurboList().selected);
		}
		
		@Override
		public boolean processButtonClick(int mx, int my, boolean left){
			if(!left) return false; boolean bool = polygon.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
			polygon.selected = !bool; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = polygon;
			GroupCompound.SELECTED_POLYGONS += polygon.selected ? 1 : -1; return true;
		}

		@Override
		public void update(int elm_height, int rw, int rh){
			this.setPosition(8, elm_height); this.setText(id = polygon.name(), false); this.render(rw, rh);
		}
		
	}
	
	public static class CompoundButton extends TreeButton {

		private GroupCompound compound;

		public CompoundButton(Element root, GroupCompound compound){
			super(root, compound.name, "tree:compound", 300, 26, 8, 0);
			this.setColor(StyleSheet.WHITE);//.setDraggable(true);
			this.setText((this.compound = compound).name, false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_minimize", 22, width - 130, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					compound.minimized = !compound.minimized; ((RightTree)root.getRoot()).refreshFullHeight(); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_clone", 22, width - 104, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					GroupCompound compound = null, parent = ((CompoundButton)root).compound;
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
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					Editor.show("preview_editor"); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					compound.visible = !compound.visible; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					HelperCollector.LOADED.remove(index()); return true;
				}
			});
		}

		@Override
		public RGB color(){
			return colorC(compound.visible, selected());
		}
		
		public boolean selected(){
			return HelperTree.SEL > 0 && HelperTree.SEL == index();
		}
		
		public int index(){
			return HelperCollector.LOADED.indexOf(compound);
		}
		
		@Override
		public boolean processButtonClick(int mx, int my, boolean left){
			if(!left) return false;
			if(selected()){ HelperTree.SEL = -1; }
			else{ HelperTree.SEL = index(); }
			//
			GroupCompound model = HelperTree.getSelected();
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
				TextField.getFieldById("helper_scale16x").applyChange(0);
				TextField.getFieldById("helper_scale16y").applyChange(0);
				TextField.getFieldById("helper_scale16z").applyChange(0);
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
				TextField.getFieldById("helper_scale16x").applyChange((model.scale == null ? 1 : model.scale.xCoord) * 16);
				TextField.getFieldById("helper_scale16y").applyChange((model.scale == null ? 1 : model.scale.yCoord) * 16);
				TextField.getFieldById("helper_scale16z").applyChange((model.scale == null ? 1 : model.scale.zCoord) * 16);
			}
			return true;
		}

		@Override
		public void update(int elm_height, int rw, int rh){
			this.setPosition(4, elm_height); this.setText(id = compound.name, false); this.render(rw, rh);
		}
		
	}
	
	public static class AnimationButton extends TreeButton {
		
		private int rel; private Animation anim;

		public AnimationButton(Element root, Animation anim){
			super(root, anim.id, "tree:group", 296, 26, 8, 0);
			this.setColor(StyleSheet.WHITE).setDraggable(true);
			this.setText((this.anim = anim).id, false); rel = 8;
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					SettingsBox.open("[" + anim.id + "] " + translate("editor.model_group.group.animator_settings"), anim.settings.values(), false,
						settings -> { anim.onSettingsUpdate(); /*FMTB.MODEL.updateFields();*/ }); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					anim.active = !anim.active; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					String str = format("fvtm_tree.remove", "Remove this Animation?<nl>%s", anim.id);
					String yes = translate("fvtm_tree.remove.confirm", "Yes");
					FMTB.showDialogbox(str, yes, translate("fvtm_tree.remove.cancel", "No!"), () -> {
						anim.group.animations.remove(anim); FMTB.MODEL.updateFields();
					}, DialogBox.NOTHING); return true;
				}
			});
		}

		@Override
		public RGB color(){
			return colorC(anim.active, false);
		}
		
		@Override
		public boolean processButtonClick(int mx, int my, boolean left){
			if(!left) return false; return true;
		}

		@Override
		public void update(int elm_height, int rw, int rh){
			this.setPosition(rel, elm_height); this.setText(anim.getButtonString(), false); this.render(rw, rh);
		}
		
		@Override
		public void pullBy(int mx, int my){
			if(anim.group.animations.size() < 2) return;
			int index = anim.group.animations.indexOf(anim);
			if(my < 0 && index > 0){
				Collections.swap(anim.group.animations, index, index - 1); GGR.resetDragging(); return;
			}
			if(my > 0 && (index + 1) < anim.group.animations.size()){
				Collections.swap(anim.group.animations, index, index + 1); GGR.resetDragging(); return;
			}
		}
		
	}
	
}
