package net.fexcraft.app.fmt.ui.trees;

import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;
import com.spinyowl.legui.listener.CursorEnterEventListener;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.PreviewHandler;

import java.util.ArrayList;
import java.util.Collections;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HelperComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private ArrayList<GroupLabel> groups = new ArrayList<>();
	protected Icon visible, remove, edit;
	protected Model model;

	public HelperComponent(Model model){
		super(model.name, model.allgroups().isEmpty() ? HEIGHT : HEIGHT + model.allgroups().size() * PH + 4, true, true);
		label.getTextState().setText("" + (this.model = model).name);
		this.genFullheight();
		add(visible = new Icon((byte)2, "./resources/textures/icons/component/visible.png", () -> pin()));
		add(remove = new Icon((byte)3, "./resources/textures/icons/component/remove.png", () -> PreviewHandler.remove(model)));
		add(edit = new Icon((byte)4, "./resources/textures/icons/component/edit.png", () -> Editor.show("helper_editor")));
		updcom.add(HelperRenamed.class, event -> { if(event.model() == model) label.getTextState().setText(model.name); });
		model.allgroups().forEach(group -> addGroup(group, false));
		resize();
		update_color();
		MouseClickEventListener listener = lis -> {
			if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				PreviewHandler.select(model);
				update_color();
			}
		};
		updcom.add(HelperSelected.class, event -> update_color());
		updcom.add(HelperVisiblity.class, event -> {
			if(event.model() == model){
				update_color();
				for(GroupLabel group : groups){
					group.update_color();
				}
			}
		});
		updcom.add(GroupVisibility.class, event -> {
			if(!model.allgroups().contains(event.group())) return;
			for(GroupLabel group : groups){
				if(group.group == event.group()) group.update_color();
			}
		});
		updcom.add(GroupSelected.class, event -> {
			if(model.allgroups().contains(event.group())){
				update_color();
				for(GroupLabel group : groups){
					group.update_color();
				}
			}
		});
		updcom.add(HelperRenamed.class, event -> {
			label.getTextState().setText(event.newname());
		});
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		label.getListenerMap().addListener(MouseClickEvent.class, listener);
		//
		CursorEnterEventListener clis = lis -> {
			DisplayType type = label.isHovered() || visible.isHovered() || remove.isHovered() || edit.isHovered() ? DisplayType.MANUAL : DisplayType.NONE;
			visible.getStyle().setDisplay(type);
			remove.getStyle().setDisplay(type);
			edit.getStyle().setDisplay(type);
		};
		label.getListenerMap().addListener(CursorEnterEvent.class, clis);
		remove.getListenerMap().addListener(CursorEnterEvent.class, clis);
		visible.getListenerMap().addListener(CursorEnterEvent.class, clis);
		edit.getListenerMap().addListener(CursorEnterEvent.class, clis);
		UIUtils.hide(remove, visible, edit);
	}

	private int genFullheight(){
		return fullheight = model.allgroups().isEmpty() ? HEIGHT : HEIGHT + model.allgroups().size() * PHS + 4;
	}

	@Override
	public void minimize(Boolean bool){
		super.minimize(bool);
	}

	protected void addGroup(Group group, boolean resort){
		GroupLabel label = new GroupLabel(this).group(group).update_name().update_color();
		this.add(label);
		groups.add(label);
		if(resort){
			resize();
			minimize(minimized);
		}
	}
	
	protected void resize(){
		setSize(Editor.CWIDTH, genFullheight());
		minimize(minimized);
		for(int i = 0; i < groups.size(); i++){
			groups.get(i).sortin(i);
		}
	}

	public Model model(){
		return model;
	}

	@Override
	protected boolean move(int dir){
		if(super.move(dir)){
			try{
				int index = PreviewHandler.previews.indexOf(model);
				Collections.swap(PreviewHandler.previews, index, index + dir);
			}
			catch(Exception e){
				Logging.log(e);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public void pin(){
		model.visible = !model.visible;
		UpdateHandler.update(new HelperVisiblity(model, model.visible));
	}
	
	@Override
	public void rem(){
		if(ASK_HELPER_REMOVAL.value){
			GenericDialog.showOC(null, () -> PreviewHandler.remove(model), null, "editor.component.helper.remove", model.name);
		}
		else PreviewHandler.remove(model);
	}
	
	public static class GroupLabel extends Label {
		
		private Group group;

		public GroupLabel(HelperComponent com){
			Settings.applyBorderless(this);
			setSize(Editor.CWIDTH - 8, PH);
			Icon visi = new Icon(0, 16, 4, Editor.CWIDTH - 26, 2, "./resources/textures/icons/component/visible.png", () -> {
				group.visible = !group.visible;
				//update_color();
				UpdateHandler.update(new GroupVisibility(group, group.visible));
			});
			CursorEnterEventListener listener = lis -> {
				DisplayType type = this.isHovered() || visi.isHovered() ? DisplayType.MANUAL : DisplayType.NONE;
				visi.getStyle().setDisplay(type);
			};
			this.getListenerMap().addListener(CursorEnterEvent.class, listener);
			visi.getListenerMap().addListener(CursorEnterEvent.class, listener);
			this.getListenerMap().addListener(MouseClickEvent.class, lis -> {
				if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
					group.model.select(group);
					update_color();
				}
			});
			UIUtils.hide(visi);
			add(visi);
		}
		
		public GroupLabel group(Group group){
			this.group = group;
			return this;
		}
		
		public Group group(){
			return group;
		}
		
		public GroupLabel update_name(){
			this.getTextState().setText(" " + group.id);
			return this;
		}
		
		public GroupLabel update_color(){
			getStyle().setTextColor(group.selected ? ColorConstants.darkGray() : ColorConstants.lightGray());
			getStyle().getBackground().setColor(FMT.rgba((group.selected ? group.visible ? POLYGON_SELECTED : POLYGON_INV_SEL : group.visible ? POLYGON_NORMAL : POLYGON_INVISIBLE).value));
			return this;
		}
		
		public GroupLabel sortin(int index){
			setPosition(5, HEIGHT + 2 + (index * PHS));
			return this;
		}
		
	}

	public void update_color(){
		label.getStyle().setTextColor(ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba((model == PreviewHandler.SELECTED ? model.visible ? GROUP_SELECTED : GROUP_INV_SEL : model.visible ? GROUP_NORMAL : GROUP_INVISIBLE).value));
	}

}
