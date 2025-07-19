package net.fexcraft.app.fmt.ui.trees;

import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;
import com.spinyowl.legui.listener.CursorEnterEventListener;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private static final int EMS = 120;
	private ArrayList<AnimationLabel> animations = new ArrayList<>();
	protected Icon visible;
	protected Icon remove;
	protected Icon sort_up;
	protected Icon sort_dw;
	protected Icon add;
	private Group group;

	public AnimComponent(Group group){
		super(group.id, group.isEmpty() ? HEIGHT : HEIGHT + group.animations.size() * PH + 4, true, true);
		label.getTextState().setText((this.group = group).id);
		this.genFullheight();
		add(add = new Icon((byte)2, "./resources/textures/icons/component/add.png", this::openAdd));
		add(visible = new Icon((byte)3, "./resources/textures/icons/component/visible.png", this::pin));
		add(remove = new Icon((byte)4, "./resources/textures/icons/component/remove.png", () -> FMT.MODEL.remGroup(group)));
		add(sort_dw = new Icon((byte)5, "./resources/textures/icons/component/move_down.png", () -> FMT.MODEL.swap(group, 1, true)));
		add(sort_up = new Icon((byte)6, "./resources/textures/icons/component/move_up.png", () -> FMT.MODEL.swap(group, -1, true)));
		updcom.add(GroupRenamed.class, event -> { if(event.group() == group) label.getTextState().setText(group.id); });
		group.animations.forEach(anim -> addAnimation(anim, false));
		update_color();
		MouseClickEventListener listener = lis -> {
			if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				group.model.select(group);
				update_color();
			}
		};
		updcom.add(GroupSelected.class, event -> {
			if(event.group() == group){
				update_color();
				for(AnimationLabel anim : animations){
					anim.update_color();
				}
			}
		});
		updcom.add(GroupVisibility.class, event -> {
			if(event.group() == group){
				update_color();
				for(AnimationLabel anim : animations){
					anim.update_color();
				}
			}
		});
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		label.getListenerMap().addListener(MouseClickEvent.class, listener);
		//
		CursorEnterEventListener clis = lis -> {
			DisplayType type = label.isHovered() || add.isHovered() || visible.isHovered() || remove.isHovered() ? DisplayType.MANUAL : DisplayType.NONE;
			visible.getStyle().setDisplay(type);
			remove.getStyle().setDisplay(type);
			add.getStyle().setDisplay(type);
		};
		label.getListenerMap().addListener(CursorEnterEvent.class, clis);
		remove.getListenerMap().addListener(CursorEnterEvent.class, clis);
		visible.getListenerMap().addListener(CursorEnterEvent.class, clis);
		add.getListenerMap().addListener(CursorEnterEvent.class, clis);
		//
		UIUtils.hide(remove, visible, add);
		if(!PolygonTree.SORT_MODE) UIUtils.hide(sort_up, sort_dw);
	}

	private int genFullheight(){
		return fullheight = (group.isEmpty() ? HEIGHT : HEIGHT + group.animations.size() * PHS + 4);
	}

	@Override
	public void minimize(Boolean bool){
		super.minimize(bool);
		group.minimized = minimized;
	}

	public void openAdd(){
		Dialog dialog = new Dialog("Select an Animation", 420, 70);
		SelectBox<String> select = new SelectBox<>(10, 10, 400, 30);
		for(FvtmTypes.ProgRef str : FvtmTypes.PROGRAMS){
			select.addElement(str.name());
		}
		select.addSelectBoxChangeSelectionEventListener(event -> {
			dialog.close();
		});
		select.setVisibleCount(8);
		dialog.getContainer().add(select);
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	private void addAnimation(Animation anim, boolean resort){
		AnimationLabel label = new AnimationLabel(this).set(anim).update_name().update_color();
		this.add(label);
		animations.add(label);
		if(resort){
			resize();
			minimize(minimized);
		}
	}
	
	protected void resize(){
		setSize(Editor.CWIDTH, genFullheight());
		minimize(group.minimized);
		resort();
	}

	protected void resort(){
		for(int i = 0; i < animations.size(); i++){
			animations.get(i).sortin(i, 0);
		}
	}

	private void removeAnimation(Animation anim){
		for(AnimationLabel label : animations){
			if(label.anim == anim){
				animations.remove(label);
				remove(label);
				break;
			}
		}
		resize();
		minimize(minimized);
	}

	public Group group(){
		return group;
	}
	
	@Override
	public void pin(){
		group.visible = !group.visible;
		UpdateHandler.update(new GroupVisibility(group, group.visible));
	}
	
	@Override
	public void rem(){
		if(ASK_GROUP_REMOVAL.value){
			GenericDialog.showOC(null, () -> FMT.MODEL.remGroup(group), null, "editor.component.group.group.remove", group.id);
		}
		else FMT.MODEL.remGroup(group);
	}
	
	public static class AnimationLabel extends Label {
		
		private Animation anim;

		public AnimationLabel(AnimComponent com){
			Settings.applyBorderless(this);
			setSize(Editor.CWIDTH - 8, PH);
			Icon remo = new Icon(0, 16, 4, Editor.CWIDTH - 26, 2, "./resources/textures/icons/component/remove.png", () -> {
				com.group.remove(anim);
			});
			Icon visi = new Icon(0, 16, 4, Editor.CWIDTH - 46, 2, "./resources/textures/icons/component/visible.png", () -> {
				anim.enabled = !anim.enabled;
				update_color();
			});
			Icon edit = new Icon(0, 16, 4, Editor.CWIDTH - 66, 2, "./resources/textures/icons/component/edit.png", () -> {
				//TODO
			});
			CursorEnterEventListener listener = lis -> {
				DisplayType type = this.isHovered() || remo.isHovered() || visi.isHovered() || edit.isHovered() ? DisplayType.MANUAL : DisplayType.NONE;
				remo.getStyle().setDisplay(type);
				visi.getStyle().setDisplay(type);
				edit.getStyle().setDisplay(type);
			};
			this.getListenerMap().addListener(CursorEnterEvent.class, listener);
			remo.getListenerMap().addListener(CursorEnterEvent.class, listener);
			visi.getListenerMap().addListener(CursorEnterEvent.class, listener);
			edit.getListenerMap().addListener(CursorEnterEvent.class, listener);
			UIUtils.hide(remo, visi, edit);
			add(remo);
			add(visi);
			add(edit);
		}
		
		public AnimationLabel set(Animation poly){
			this.anim = poly;
			return this;
		}
		
		public Animation animation(){
			return anim;
		}
		
		public AnimationLabel update_name(){
			this.getTextState().setText(" " + anim.id());
			return this;
		}
		
		public AnimationLabel update_color(){
			getStyle().setTextColor(anim.enabled ? ColorConstants.darkGray() : ColorConstants.lightGray());
			getStyle().getBackground().setColor(FMT.rgba((anim.enabled ? POLYGON_NORMAL : POLYGON_INVISIBLE).value));
			return this;
		}
		
		public AnimationLabel sortin(int index, int offset){
			setPosition(5, HEIGHT + 2 + (index * PHS) + offset);
			return this;
		}
		
	}

	public void update_color(){
		label.getStyle().setTextColor(group.selected ? ColorConstants.darkGray() : ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba((group.selected ? group.visible ? GROUP_SELECTED : GROUP_INV_SEL : group.visible ? GROUP_NORMAL : GROUP_INVISIBLE).value));
	}

}
