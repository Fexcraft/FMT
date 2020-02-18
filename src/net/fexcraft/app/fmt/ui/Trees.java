package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.Editors.SPVSL;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;


public class Trees {
	
	public static final ArrayList<TreeBase> trees = new ArrayList<>();
	public static TreeBase polygon, helper;
	
	public static void initializeTrees(Frame frame){
		frame.getContainer().add(polygon = new TreeBase("polygon"));
		frame.getContainer().add(helper = new TreeBase("helper"));
		//temporary
		//polygon.show();
	}
	
	public static void hideAll(){
		for(TreeBase tree : trees) tree.hide();
	}
	
	public static void show(String type){
		hideAll();
		switch(type){
			case "polygon": polygon.show(); break;
			case "helper": case "preview":
			case "helper_preview": helper.show(); break;
			default: break;
		}
	}

	public static void toggle(String string){
		TreeBase base = get(string); if(base.isVisible()) base.hide(); else show(string);
	}
	
	private static TreeBase get(String string){
		for(TreeBase base : trees) if(base.id.equals(string)) return base; return null;
	}

	public static boolean anyVisible(){
		for(TreeBase tree : trees) if(tree.isVisible()) return true; return false;
	}
	
	public static TreeBase getVisible(){
		for(TreeBase tree : trees) if(tree.isVisible()) return tree; return null;
	}
	
	public static class TreeBase extends Panel {

		private ArrayList<TreeGroup> groups = new ArrayList<>();
		public ScrollablePanel scrollable;
		public String counterlabel;
		public Label counter; 
		public final String id;
		
		public TreeBase(String name){
			super(FMTB.WIDTH - 304, 30, 304, FMTB.HEIGHT - 30); trees.add(this); id = name;
			super.add(counter = new Label((counterlabel = Editors.translate("tree." + id + ".counter")) + "0", 4, 1, 100, 24));
			counter.getStyle().setFontSize(24f);
	        scrollable = new ScrollablePanel(0, 28, 304, FMTB.HEIGHT - 60);
	        scrollable.getStyle().getBackground().setColor(1, 1, 1, 1);
	        scrollable.setHorizontalScrollBarVisible(false);
	        scrollable.getContainer().setSize(296, FMTB.HEIGHT - 60);
	        scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
	        scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
	        super.add(scrollable); this.hide();
		}

		public void toggle(){
			if(isVisible()) hide(); else show();
		}
		
		public void hide(){
			this.getStyle().setDisplay(DisplayType.NONE);
		}
		
		public void show(){
			this.getStyle().setDisplay(DisplayType.MANUAL);
		}
		
		public boolean addSub(Component com){
			if(com instanceof TreeGroup) groups.add((TreeGroup)com);
			return scrollable.getContainer().add(com);
		}

		public void reOrderGroups(){
			float size = 2; for(TreeGroup tree : groups) size += tree.getSize().y + 2;
			scrollable.getContainer().setSize(scrollable.getSize().x, size > FMTB.HEIGHT - 60 ? size : FMTB.HEIGHT - 60); size = 2;
			for(TreeGroup tree : groups){ tree.setPosition(0, size); size += tree.getSize().y + 2; }
		}

		public void updateCounter(){
			switch(this.id){
				case "polygon": counter.getTextState().setText(counterlabel + FMTB.MODEL.countTotalMRTs()); break;
				case "helper": counter.getTextState().setText(counterlabel + HelperCollector.LOADED.size()); break;
				case "fvtm": counter.getTextState().setText(counterlabel + FMTB.MODEL.getGroups().size()); break;
				default: return;
			}
		}

		public void clear(){
			scrollable.getContainer().removeIf(filter -> true); groups.clear(); reOrderGroups();
		}

		public int groupAmount(){
			return groups.size();
		}
		
	}
	
	public static class TreeIcon extends ImageView {
		
		public TreeIcon(int x, int y, String adress, MouseClickEventListener listener){
			super(new BufferedImage("./resources/textures/icons/" + adress + ".png"));
			setSize(20, 20); setPosition(x, y); getStyle().setBorderRadius(0);
			this.getListenerMap().addListener(MouseClickEvent.class, listener);
		}
		
		public TreeIcon(int x, int y, String adress, Runnable run){
			this(x, y, adress, (MouseClickEventListener)event -> { if(event.getAction() == CLICK){ run.run(); } });
		}
		
	}
	
	public static class TreeGroup extends Panel {
		
		private GroupCompound compound;
		private TurboList list;
		private TreeBase tree;
		private Label label;

		public TreeGroup(TreeBase base){
			super(0, 0, base.getSize().x - 12, 20); tree = base;
			this.add(label = new Label("group-label", 0, 0, (int)getSize().x, 20));
			label.getStyle().setFont("roboto-bold");
			label.getStyle().setPadding(0, 0, 0, 5);
			label.getStyle().setBorderRadius(0);
		}
		
		public TreeGroup(TreeBase base, TurboList group){
			this(base); list = group; updateColor();
			this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
				DialogBox.showYN(null, () -> { FMTB.MODEL.getGroups().remove(list.id); }, null, "tree.polygon.remove_group", "#" + list.id);
			}));
			this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
				list.visible = !list.visible; updateColor();
			}));
			this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
				Editors.show("group");
			}));
			this.add(new TreeIcon((int)getSize().x - 86, 0, "group_minimize", () -> toggle(!list.minimized)));
			label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
				boolean sell = list.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
				list.selected = !sell; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = null; updateColor();
				GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
			});
			this.recalculateSize();
		}
		
		public TreeGroup(TreeBase base, GroupCompound group){
			this(base); compound = group; updateColor();
			this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
				HelperCollector.LOADED.remove(index()); this.removeFromTree(); tree.reOrderGroups();
			}));
			this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
				compound.visible = !compound.visible; updateColor();
			}));
			this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
				Editors.show("helper_preview");
			}));
			this.add(new TreeIcon((int)getSize().x - 86, 0, "group_clone", () -> {
				GroupCompound newcomp = null, parent = compound;
				if(parent.name.startsWith("fmtb/")){
					newcomp = HelperCollector.loadFMTB(parent.origin);
				}
				else if(parent.name.startsWith("frame/")){
					newcomp = HelperCollector.loadFrame(parent.origin);
				}
				else{
					ExImPorter porter = PorterManager.getPorterFor(parent.origin, false);
					HashMap<String, Setting> map = new HashMap<>();
					porter.getSettings(false).forEach(setting -> map.put(setting.getId(), setting));
					newcomp = HelperCollector.load(parent.file, porter, map);
				}
				if(newcomp == null){ Print.console("Error on creating clone."); return; }
				if(parent.pos != null) newcomp.pos = new Vec3f(parent.pos);
				if(parent.rot != null) newcomp.rot = new Vec3f(parent.rot);
				if(parent.scale != null) newcomp.scale = new Vec3f(parent.scale);
			}));
			this.add(new TreeIcon((int)getSize().x - 108, 0, "group_minimize", () -> toggle(!compound.minimized)));
			label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
				if(selected()){ HelperCollector.SELECTED = -1; }
				else{ HelperCollector.SELECTED = index(); }
				/*GroupCompound model = HelperCollector.getSelected();
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
				}*/
				updateColor();
			});
			this.recalculateSize();
		}
		
		public void toggle(boolean bool){
			if(list == null) compound.minimized = bool; else list.minimized = bool; recalculateSize();
			getChildComponents().forEach(con -> {
				if(con instanceof SubTreeGroup){
					((SubTreeGroup)con).toggle(!bool);
				}
			});
		}
		
		public void recalculateSize(){
			if(list != null){
				this.setSize(this.getSize().x, list.minimized ? 20 : (list.size() * 22) + 20);
			}
			else{
				this.setSize(this.getSize().x, compound.minimized ? 20 : (compound.getGroups().size() * 22) + 20);
			}
			tree.reOrderGroups();
		}

		public void removeFromTree(){
			tree.scrollable.getContainer().remove(this); tree.groups.remove(this);
		}
		
		public TreeBase tree(){
			return tree;
		}

		public Component update(){
			label.getTextState().setText(list == null ? compound.name : list.id); return this;
		}
		
		public void updateColor(){
			if(list == null) label.getStyle().getBackground().setColor(FMTB.rgba(selected() ? compound.visible ? 0xa37a18 : 0xd6ad4b : compound.visible ? 0x28a148 : 0x6bbf81));
			else label.getStyle().getBackground().setColor(FMTB.rgba(list.selected ? list.visible ? 0xa37a18 : 0xd6ad4b : list.visible ? 0x28a148 : 0x6bbf81));
		}
		
		public boolean selected(){
			return HelperCollector.SELECTED > 0 && HelperCollector.SELECTED == index();
		}
		
		public int index(){
			return HelperCollector.LOADED.indexOf(compound);
		}
		
	}
	
	public static class SubTreeGroup extends Panel {
		
		private TreeGroup root;
		private TreeBase base;
		private TurboList list;
		private PolygonWrapper polygon;
		private Label label;

		public SubTreeGroup(TreeBase base){
			super(0, 0, base.getSize().x - 22, 20); this.base = base;
			this.add(label = new Label("group-label", 0, 0, (int)getSize().x, 20));
			label.getStyle().setFont("roboto-bold");
			label.getStyle().setPadding(0, 0, 0, 5);
			label.getStyle().setBorderRadius(0);
		}
		
		public SubTreeGroup(TreeBase base, PolygonWrapper wrapper){
			this(base); polygon = wrapper; updateColor();
			this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
				DialogBox.showYN(null, () -> { polygon.getTurboList().remove(polygon); }, null, "tree.polygon.remove_polygon", "#" + polygon.getTurboList().id + ":" + polygon.name());
			}));
			this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
				polygon.visible = !polygon.visible; updateColor();
			}));
			this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
				Editors.show("general");
			}));
			label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
				boolean sell = list.selected; if(!GGR.isShiftDown()){ FMTB.MODEL.clearSelection(); }
				list.selected = !sell; FMTB.MODEL.updateFields(); FMTB.MODEL.lastselected = null; updateColor();
				GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
			});
		}
		
		public SubTreeGroup(TreeBase base, TurboList group){
			this(base); list = group; updateColor();
			this.add(new TreeIcon((int)getSize().x - 20, 0, "group_visible", () -> {
				list.visible = !list.visible; updateColor();
			}));
		}

		public void removeFromSubTree(){
			if(root == null) return; root.remove(this);
		}
		
		public TreeGroup subtree(){
			return root;
		}
		
		public TreeBase tree(){
			return base;
		}

		public Component update(){
			label.getTextState().setText(list == null ? polygon.name() : list.id); return this;
		}
		
		public void updateColor(){
			if(list == null) label.getStyle().getBackground().setColor(FMTB.rgba(polygon.selected ? polygon.visible ? 0xa37a18 : 0xd6ad4b : polygon.visible ? 0x28a148 : 0x6bbf81));
			else label.getStyle().getBackground().setColor(FMTB.rgba(list.selected ? list.visible ? 0xa37a18 : 0xd6ad4b : list.visible ? 0x28a148 : 0x6bbf81));
		}

		public void refreshPosition(){
			if(list == null){
				this.setPosition(10, (polygon.getTurboList().indexOf(polygon) * 22) + 22);
			}
			else{
				this.setPosition(10, (root.compound.getGroups().indexOf(list) * 22) + 22);
			}
		}

		public void setRoot(TreeGroup button){
			root = button; root.add(this.update()); button.recalculateSize(); refreshPosition(); show();
		}

		public void toggle(){
			if(isVisible()) hide(); else show();
		}

		public void toggle(boolean bool){
			if(!bool) hide(); else show();
		}
		
		public void hide(){
			this.getStyle().setDisplay(DisplayType.NONE);
		}
		
		public void show(){
			this.getStyle().setDisplay(DisplayType.MANUAL);
		}
		
	}
	
	public static class GroupButton extends Button {

		public GroupButton(String string, int x, int y, int w, int h){
			super(string, x, y, w, h);
		}
		
	}

	public static void resize(int width, int height){
		for(TreeBase tree : trees){
			tree.setPosition(FMTB.WIDTH - 304, 30);
			tree.setSize(tree.getSize().x, FMTB.HEIGHT - 30);
			tree.scrollable.setSize(tree.scrollable.getSize().x, FMTB.HEIGHT - 60);
			tree.reOrderGroups();
		}
	}

	public static void updateCounters(){
		for(TreeBase tree : trees) tree.updateCounter();
	}

}
