package net.fexcraft.app.fmt.ui.tree;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.function.Consumer;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.SettingsBox;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class SubTreeGroup extends Panel {

	private TreeGroup root;
	private TreeBase base;
	private TurboList list;
	private PolygonWrapper polygon;
	private Animation animation;
	private String text;
	private int textindex;
	private Label label;

	public SubTreeGroup(TreeBase base){
		super(0, 0, base.getSize().x - 22, 20);
		this.base = base;
		this.add(label = new Label("group-label", 0, 0, (int)getSize().x, 20));
		Consumer<Boolean> con;
		Settings.THEME_CHANGE_LISTENER.add(con = bool -> {
			label.getStyle().setFont("roboto-bold");
			label.getStyle().setPadding(0, 0, 0, 5);
			label.getStyle().setBorderRadius(0);
			// if(bool){
			label.getStyle().setTextColor(ColorConstants.darkGray());
			// }
			// else{
			// label.getStyle().setTextColor(ColorConstants.lightGray());
			// }
			getStyle().getBorder().setEnabled(false);
			if(animation != null || list != null || polygon != null || text != null) updateColor();
		});
		con.accept(Settings.darktheme());
	}

	public SubTreeGroup(TreeBase base, PolygonWrapper wrapper){
		this(base);
		polygon = wrapper;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
			DialogBox.showYN(null, () -> {
				polygon.getTurboList().remove(polygon);
			}, null, "tree.polygon.remove_polygon", "#" + polygon.getTurboList().id + ":" + polygon.name());
		}, "delete"));
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
			polygon.visible = !polygon.visible;
			updateColor();
		}, "visibility"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
			Editors.show("general");
		}, "edit"));
		label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(polygon == null || listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
			boolean sell = polygon.selected;
			if(!GGR.isShiftDown()){
				FMTB.MODEL.clearSelection();
			}
			polygon.selected = !sell;
			FMTB.MODEL.updateFields();
			FMTB.MODEL.lastselected = polygon;
			updateColor();
			GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
		});
	}

	public SubTreeGroup(TreeBase base, TurboList group){
		this(base);
		list = group;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_visible", () -> {
			list.visible = !list.visible;
			updateColor();
		}, "visibility"));
	}

	public SubTreeGroup(TreeBase base, Animation anim){
		this(base);
		this.animation = anim;
		updateColor();
		this.add(new TreeIcon((int)getSize().x - 20, 0, "group_delete", () -> {
			DialogBox.showYN(null, () -> {
				animation.button.removeFromSubTree();
				animation.group.animations.remove(animation);
				animation.group.abutton.recalculateSize();
				FMTB.MODEL.updateFields();
			}, null, "tree.fvtm.remove_animation", "#" + animation.id);
		}, "delete"));
		this.add(new TreeIcon((int)getSize().x - 42, 0, "group_visible", () -> {
			animation.active = !animation.active;
			updateColor();
		}, "visibility"));
		this.add(new TreeIcon((int)getSize().x - 64, 0, "group_edit", () -> {
			SettingsBox.open("[" + animation.id + "] " + Translator.translate("editor.model_group.group.animator_settings"), animation.settings.values(), false, settings -> {
				animation.onSettingsUpdate();
				update();
			});
		}, "edit"));
	}

	public SubTreeGroup(TreeBase base, int index, String string, Runnable runnable){
		this(base);
		this.text = string;
		this.textindex = index;
		label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() != CLICK || listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT) return;
			runnable.run();
		});
	}

	public void removeFromSubTree(){
		if(root == null) return;
		root.remove(this);
	}

	public TreeGroup subtree(){
		return root;
	}

	public TreeBase tree(){
		return base;
	}

	public Component update(){
		label.getTextState().setText(text != null ? Translator.translate(text) : animation != null ? animation.getButtonString() : list == null ? polygon.name() : list.id);
		return this;
	}

	public void updateColor(){
		if(text != null) label.getStyle().getBackground().setColor(FMTB.rgba(0x28a148));
		else if(animation != null) label.getStyle().getBackground().setColor(FMTB.rgba(animation.active ? 0x28a148 : 0x6bbf81));
		else if(list == null) label.getStyle().getBackground().setColor(FMTB.rgba(polygon.selected ? polygon.visible ? 0xa37a18 : 0xd6ad4b : polygon.visible ? 0x28a148 : 0x6bbf81));
		else label.getStyle().getBackground().setColor(FMTB.rgba(list.selected ? list.visible ? 0xa37a18 : 0xd6ad4b : list.visible ? 0x28a148 : 0x6bbf81));
	}

	public void refreshPosition(){
		if(text != null){
			this.setPosition(10, (textindex * 22) + 22);
			return;
		}
		if(animation != null){
			this.setPosition(10, (animation.group.animations.indexOf(animation) * 22) + 22);
			return;
		}
		if(list == null){
			this.setPosition(10, (polygon.getTurboList().indexOf(polygon) * 22) + 22);
		}
		else{
			this.setPosition(10, (root.compound.getGroups().indexOf(list) * 22) + 22);
		}
	}

	public SubTreeGroup setRoot(TreeGroup button){
		root = button;
		root.add(this.update());
		button.recalculateSize();
		refreshPosition();
		show();
		return this;
	}

	public void toggle(){
		if(isVisible()) hide();
		else show();
	}

	public void toggle(boolean bool){
		if(!bool) hide();
		else show();
	}

	public void hide(){
		this.getStyle().setDisplay(DisplayType.NONE);
	}

	public void show(){
		this.getStyle().setDisplay(DisplayType.MANUAL);
	}

}