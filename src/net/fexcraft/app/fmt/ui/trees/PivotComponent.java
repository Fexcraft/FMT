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
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Logging;

import java.util.ArrayList;
import java.util.Collections;

import static net.fexcraft.app.fmt.settings.Settings.*;

public class PivotComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private ArrayList<GroupLabel> groups = new ArrayList<>();
	protected Icon visible, remove;
	private Pivot pivot;

	public PivotComponent(Pivot pivot){
		super(pivot.id, pivot.groups.isEmpty() ? HEIGHT : HEIGHT + pivot.groups.size() * PH + 4, true, true);
		label.getTextState().setText((this.pivot = pivot).id);
		this.genFullheight();
		add(visible = new Icon((byte)2, "./resources/textures/icons/component/visible.png", () -> pin()));
		add(remove = new Icon((byte)3, "./resources/textures/icons/component/remove.png", () -> FMT.MODEL.remPivot(pivot)));
		updcom.add(PivotRenamed.class, event -> { if(event.pivot() == pivot) label.getTextState().setText(pivot.id); });
		updcom.add(GroupAdded.class, event -> { if(pivot.isin(event.group())) addGroup(event.group(), true); });
		updcom.add(GroupRenamed.class, event -> { if(pivot.isin(event.group())) renameGroup(event.group()); });
		updcom.add(GroupRemoved.class, event -> { if(pivot.isin(event.group())) removeGroup(event.group()); });
		//pivot.groups.forEach(group -> addGroup(group, false));
		update_color();
		//if(!group.visible) UIUtils.hide(this);
		MouseClickEventListener listener = lis -> {
			if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				//FMT.MODEL.select(pivot);
				update_color();
			}
		};
		updcom.add(PivotVisibility.class, event -> {
			if(event.pivot() == pivot){
				update_color();
				for(GroupLabel group : groups){
					group.update_color();
				}
			}
		});
		updcom.add(GroupVisibility.class, event -> {
			if(!pivot.groups.contains(event.group())) return;
			for(GroupLabel group : groups){
				if(group.group == event.group()) group.update_color();
			}
		});
		updcom.add(GroupSelected.class, event -> {
			if(pivot.groups.contains(event.group())){
				update_color();
				for(GroupLabel group : groups){
					group.update_color();
				}
			}
		});
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		label.getListenerMap().addListener(MouseClickEvent.class, listener);
	}

	private int genFullheight(){
		return fullheight = pivot.groups.isEmpty() ? HEIGHT : HEIGHT + pivot.groups.size() * PHS + 4;
	}

	@Override
	public void minimize(Boolean bool){
		super.minimize(bool);
		pivot.minimized = minimized;
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
		minimize(pivot.minimized);
		for(int i = 0; i < groups.size(); i++){
			groups.get(i).sortin(i);
		}
	}

	private void renameGroup(Group group){
		for(GroupLabel label : groups){
			if(label.group == group){
				label.update_name();
				break;
			}
		}
	}

	private void removeGroup(Group group){
		for(GroupLabel label : groups){
			if(label.group == group){
				groups.remove(label);
				remove(label);
				break;
			}
		}
		resize();
		minimize(minimized);
	}

	public Pivot pivot(){
		return pivot;
	}

	@Override
	protected boolean move(int dir){
		if(super.move(dir)){
			try{
				int index = FMT.MODEL.pivots().indexOf(this.pivot);
				Collections.swap(FMT.MODEL.pivots(), index, index + dir);
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
		pivot.visible = !pivot.visible;
		//update_color();
		UpdateHandler.update(new PivotVisibility(pivot, pivot.visible));
	}
	
	@Override
	public void rem(){
		if(ASK_GROUP_REMOVAL.value){
			GenericDialog.showOC(null, () -> FMT.MODEL.remPivot(pivot), null, "editor.component.group.group.remove", pivot.id);
		}
		else FMT.MODEL.remPivot(pivot);
	}
	
	public static class GroupLabel extends Label {
		
		private Group group;

		public GroupLabel(PivotComponent com){
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
			this.add(visi);
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
		this.getStyle().getBackground().setColor(FMT.rgba((pivot.visible ? GROUP_NORMAL : GROUP_INVISIBLE).value));
	}

}
