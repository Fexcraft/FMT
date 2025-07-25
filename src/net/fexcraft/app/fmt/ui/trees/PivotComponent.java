package net.fexcraft.app.fmt.ui.trees;

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
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Logging;

import java.util.ArrayList;
import java.util.Collections;

import static net.fexcraft.app.fmt.settings.Settings.*;

public class PivotComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private ArrayList<GroupComponent> groups = new ArrayList<>();
	protected Icon visible, remove, edit;
	private Pivot pivot;

	public PivotComponent(Pivot pivot){
		super(pivot.id, pivot.groups.isEmpty() ? HEIGHT : HEIGHT + pivot.groups.size() * PH + 4, true, true);
		label.getTextState().setText((this.pivot = pivot).id);
		this.genFullheight();
		add(visible = new Icon(this, 2, "./resources/textures/icons/component/visible.png", () -> pin()));
		add(remove = new Icon(this, 3, "./resources/textures/icons/component/remove.png", () -> FMT.MODEL.remPivot(pivot)));
		add(edit = new Icon(this, 4, "./resources/textures/icons/component/edit.png", () -> Editor.show("pivot_editor")));
		updcom.add(PivotRenamed.class, event -> { if(event.pivot() == pivot) label.getTextState().setText(pivot.id); });
		updcom.add(GroupAdded.class, event -> { if(pivot.isin(event.group())) addGroup(event.group(), true); });
		//updcom.add(GroupRenamed.class, event -> { if(pivot.isin(event.group())) renameGroup(event.group()); });
		updcom.add(GroupRemoved.class, event -> { if(pivot.isin(event.group())) removeGroup(event.group()); });
		//pivot.groups.forEach(group -> addGroup(group, false));
		update_color();
		//if(!group.visible) UIUtils.hide(this);
		MouseClickEventListener listener = lis -> {
			if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				FMT.MODEL.select(pivot);
				update_color();
			}
		};
		updcom.add(PivotSelected.class, event -> update_color());
		updcom.add(PivotVisibility.class, event -> {
			if(event.pivot() == pivot){
				update_color();
				for(GroupComponent group : groups){
					group.update_color();
				}
			}
		});
		updcom.add(GroupVisibility.class, event -> {
			if(!pivot.groups.contains(event.group())) return;
			for(GroupComponent group : groups){
				if(group.group() == event.group()) group.update_color();
			}
		});
		updcom.add(GroupSelected.class, event -> {
			if(pivot.groups.contains(event.group())){
				update_color();
				for(GroupComponent group : groups){
					group.update_color();
				}
			}
		});
		updcom.add(PivotChanged.class, event -> {
			removeGroup(event.group());
			if(pivot == event.pivot()){
				addGroup(event.group(), true);
			}
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
		fullheight = HEIGHT;
		if(pivot.groups.isEmpty()) return fullheight;
		fullheight += 2;
		for(GroupComponent group : groups){
			group.setPosition(2, fullheight);
			group.resort();
			fullheight += group.getSize().y + 2;
		}
		return fullheight += 2;
	}

	@Override
	public void minimize(Boolean bool){
		super.minimize(bool);
		pivot.minimized = minimized;
	}

	protected void addGroup(Group group, boolean resort){
		GroupComponent comp = new GroupComponent(group, this);
		add(comp);
		groups.add(comp);
		comp.minimize(true);
		UpdateHandler.register(comp.getUpdCom());
		if(resort){
			resize();
			minimize(minimized);
		}
	}

	protected void resize(){
		setSize(Editor.CWIDTH, genFullheight());
		minimize(pivot.minimized);
	}

	private void removeGroup(Group group){
		for(GroupComponent label : groups){
			if(label.group() == group){
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
		if(ASK_PIVOT_REMOVAL.value){
			GenericDialog.showOC(null, () -> FMT.MODEL.remPivot(pivot), null, "editor.component.pivot.pivot.remove", pivot.id);
		}
		else FMT.MODEL.remPivot(pivot);
	}

	public void update_color(){
		label.getStyle().setTextColor(pivot == FMT.MODEL.sel_pivot ? ColorConstants.darkGray() : ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba((pivot == FMT.MODEL.sel_pivot ? pivot.visible ? PIVOT_SELECTED : PIVOT_INV_SEL : pivot.visible ? PIVOT_NORMAL : PIVOT_INVISIBLE).value));
	}

}
