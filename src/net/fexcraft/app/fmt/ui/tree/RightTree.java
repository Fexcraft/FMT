package net.fexcraft.app.fmt.ui.tree;

import java.util.ArrayList;
import java.util.Optional;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.Icon;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.StyleSheet;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;

public abstract class RightTree extends Element {
	
	public static final ArrayList<RightTree> TREES = new ArrayList<RightTree>();
	protected static final RGB fontcol = new RGB("#1e1e1e");
	private static RGB sel_in_g, sel_in_p, sel_vi_g, sel_vi_p;
	private static RGB def_g, def_p, inv_g, inv_p;
	protected int scroll;

	public RightTree(String id){
		super(null, id, id); this.setSize(308, 100).setPosition(0, 0).setVisible(false); TREES.add(this);
		this.setColor(0xff999999).setBorder(0xff000000, 0xffffffff, 1, false, false, true, false);
		this.setHoverColor(0xffffffff, false); this.repos();
		//
		sel_in_g = new RGB(StyleSheet.getColourFor("tree:group", "selected_invisible", 0xffaa7e36));
		sel_in_p = new RGB(StyleSheet.getColourFor("tree:polygon", "selected_invisible", 0xffaa7e36));
		sel_vi_g = new RGB(StyleSheet.getColourFor("tree:group", "selected_visible", 0xff934427));
		sel_vi_p = new RGB(StyleSheet.getColourFor("tree:polygon", "selected_visible", 0xff934427));
		def_g = new RGB(StyleSheet.getColourFor("tree:group", "background_visible", 0xff0b6623));
		def_p = new RGB(StyleSheet.getColourFor("tree:polygon", "background_visible", 0xff0b6623));
		inv_g = new RGB(StyleSheet.getColourFor("tree:group", "background_invisible", 0xff80a073));
		inv_p = new RGB(StyleSheet.getColourFor("tree:polygon", "background_invisible", 0xff80a073));
	}
	
	@Override
	public Element repos(){
		x = UserInterface.width - width; y = UserInterface.TOOLBAR.height + UserInterface.TOOLBAR.border_width;
		height = UserInterface.height - y; if(Settings.bottombar()) height -= 29; clearVertexes(); return this;
	}
	
	@Override
	public void render(int width, int height){
		if(!Mouse.isGrabbed()) hovered(Mouse.getX() * UserInterface.scale, height - Mouse.getY() * UserInterface.scale);
		//
		if(this.visible){
			if(z != 0) GL11.glTranslatef(0, 0,  z);
			this.renderSelfQuad(); this.renderSelf(width, height);
			if(z != 0) GL11.glTranslatef(0, 0, -z);
		}
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return true;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		TREES.forEach(elm -> { elm.visible = elm.id.equals(id); elm.scroll = 0; });
	}
	
	public static void hideAll(){
		TREES.forEach(elm -> { elm.visible = false; elm.scroll = 0; } );
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

	public static boolean anyTreeHovered(){
		return  TREES.stream().filter(pre -> pre.isHovered()).findFirst().isPresent();
	}
	
	public static class GroupButton extends Button {
		
		private int xl, yl;
		private TurboList list;

		public GroupButton(Element root, TurboList list){
			super(root, list.id, "tree:group", 300, 26, 4, 0);
			this.setColor(StyleSheet.WHITE).setDraggable(true);
			this.setText((this.list = list).id, false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_minimize", 22, width - 104, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					list.minimized = !list.minimized; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					Editor.show("model_group_editor"); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					list.visible = !list.visible; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					FMTB.showDialogbox("Remove this group?\n" + list.id, "Yes", "No!", () -> {
						FMTB.MODEL.getGroups().remove(list.id);
					}, DialogBox.NOTHING); return true;
				}
			});
		}

		@Override
		public void renderSelf(int rw, int rh){
			if(drawbackground){
				colorG(visible, list.selected).glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
			}
			if(text != null){
				RGB color = !drawbackground ? hovered ? hovercolor : !enabled ? discolor : RGB.BLACK : RGB.BLACK;
				if(centered){
					int x = width / 2 - (FontRenderer.getWidth(text, 1) / 2), y = height / 2 - 10;
					FontRenderer.drawText(text, this.x + x + (icon == null ? 0 : iconsize + 2), this.y + y, 1, color);
				}
				else{
					FontRenderer.drawText(text, x + texxoff + (icon == null ? 0 : iconsize + 2), y + texyoff, 1, color);
				}
			}
			if(icon != null){
				if(iconcolor != null) iconcolor.glColorApply();
				float y = (height - iconsize) * 0.5f;
				this.renderIcon(x + 2, this.y + y, iconsize, icon);
				if(iconcolor != null) RGB.glColorReset();
			}
		}
		
		@Override
		protected boolean processButtonClick(int mx, int my, boolean left){
			boolean bool = list.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
			list.selected = !bool; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = null;
			return true;
		}
		
		@Override
		public Element setPosition(int x, int y){
			if(xl != x || yl != y){ super.setPosition(x, y); xl = x; yl = y; } return this;
		}
		
	}
	
	public static class PolygonButton extends Button {

		private int xl, yl;
		private PolygonWrapper polygon;

		public PolygonButton(Element root, PolygonWrapper polygon){
			super(root, polygon.name, "tree:polygon", 296, 26, 8, 0);
			this.setColor(StyleSheet.WHITE).setDraggable(true);
			this.setText((this.polygon = polygon).name(), false);
			this.setBorder(StyleSheet.BLACK, StyleSheet.BLACK, 0);
			//
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_edit", 22, width - 78, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					Editor.show("general_editor"); return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_visible", 22, width - 52, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					polygon.visible = !polygon.visible; return true;
				}
			});
			elements.add(new Icon(this, "remove", "tree:group_icon", "icons/group_delete", 22, width - 26, 2){
				@Override
				protected boolean processButtonClick(int mx, int my, boolean left){
					FMTB.showDialogbox("Remove this polygon?\n" + polygon.getTurboList().id + ":" + polygon.name(), "Yes", "No!", () -> {
						polygon.getTurboList().remove(polygon);
					}, DialogBox.NOTHING); return true;
				}
			});
		}

		@Override
		public void renderSelf(int rw, int rh){
			if(drawbackground){
				colorP(visible, polygon.selected || polygon.getTurboList().selected).glColorApply(); this.renderSelfQuad(); RGB.glColorReset();
			}
			if(text != null){
				RGB color = !drawbackground ? hovered ? hovercolor : !enabled ? discolor : RGB.BLACK : RGB.BLACK;
				if(centered){
					int x = width / 2 - (FontRenderer.getWidth(text, 1) / 2), y = height / 2 - 10;
					FontRenderer.drawText(text, this.x + x + (icon == null ? 0 : iconsize + 2), this.y + y, 1, color);
				}
				else{
					FontRenderer.drawText(text, x + texxoff + (icon == null ? 0 : iconsize + 2), y + texyoff, 1, color);
				}
			}
			if(icon != null){
				if(iconcolor != null) iconcolor.glColorApply();
				float y = (height - iconsize) * 0.5f;
				this.renderIcon(x + 2, this.y + y, iconsize, icon);
				if(iconcolor != null) RGB.glColorReset();
			}
		}
		
		@Override
		protected boolean processButtonClick(int mx, int my, boolean left){
			boolean bool = polygon.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
			polygon.selected = !bool; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = polygon;
			return true;
		}
		
		@Override
		public Element setPosition(int x, int y){
			if(xl != x || yl != y){ super.setPosition(x, y); xl = x; yl = y; } return this;
		}
		
	}
	
}
