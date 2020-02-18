package net.fexcraft.app.fmt.ui.treeold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.SettingsBox;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.Icon;
import net.fexcraft.app.fmt.ui.general.Scrollbar;
import net.fexcraft.app.fmt.ui.general.Scrollbar.Scrollable;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.lib.common.math.RGB;

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
		//if(UserInterface.TOOLBAR == null){ return this; }//skip, this call is before the UI is setup
		x = UserInterface.width - width; y = 30;//UserInterface.TOOLBAR.height + UserInterface.TOOLBAR.border_width;
		height = UserInterface.height - y; /*if(Settings.bottombar()) height -= 29;*/ clearVertexes();
		for(Element elm : elements) elm.repos(); return this;
	}
	
	@Override
	public void render(int width, int height){
		if(!FMTB.hold_right) hovered(GGR.mousePosX() * UserInterface.scale, height - GGR.mousePosY() * UserInterface.scale);
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
				/*RGB color = !drawbackground ? hovered ? hovercolor : !enabled ? discolor : RGB.BLACK : RGB.BLACK;
				if(centered){
					int x = width / 2 - (FontRenderer.getWidth(text, FontType.BOLD) / 2), y = height / 2 - 10;
					FontRenderer.drawText(text, this.x + x + (icon == null ? 0 : iconsize + 2), this.y + y, FontType.BOLD, color);
				}
				else{
					FontRenderer.drawText(text, x + texxoff + (icon == null ? 0 : iconsize + 2), y + texyoff, FontType.BOLD, color);
				}*/
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
	
	public static class OldPolygonButton extends TreeButton {

		private PolygonWrapper polygon;

		public OldPolygonButton(Element root, PolygonWrapper polygon){
			super(root, polygon.name, "tree:polygon", 296, 26, 8, 0);
			this.setColor(StyleSheet.WHITE);//.setDraggable(true);
			this.setText((this.polygon = polygon).name(), false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				public boolean processButtonClick(int mx, int my, boolean left){
					Editors.show("general"); return true;
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
					DialogBox.showYN(null, () -> {
						polygon.getTurboList().remove(polygon);
					}, null, "polygontree.remove_polygon", "#" + polygon.getTurboList().id + ":" + polygon.name());
					return true;
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
					DialogBox.showYN(null, () -> {
						anim.group.animations.remove(anim); FMTB.MODEL.updateFields();
					}, null, "tree.fvtm.remove_animation", "#" + anim.id);
					return true;
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
				Collections.swap(anim.group.animations, index, index - 1); /*GGR.resetDragging();*/ return;
			}
			if(my > 0 && (index + 1) < anim.group.animations.size()){
				Collections.swap(anim.group.animations, index, index + 1); /*GGR.resetDragging();*/ return;
			}
		}
		
	}
	
}
