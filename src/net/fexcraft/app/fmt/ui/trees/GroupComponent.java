package net.fexcraft.app.fmt.ui.trees;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;
import java.util.Collections;

import com.spinyowl.legui.component.Panel;
import com.spinyowl.legui.component.SelectBox;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;
import com.spinyowl.legui.listener.CursorEnterEventListener;
import com.spinyowl.legui.listener.MouseClickEventListener;
import com.spinyowl.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.utils.Logging;
import com.spinyowl.legui.style.color.ColorConstants;

public class GroupComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private static final int EMS = 120;
	private ArrayList<PolygonLabel> polygons = new ArrayList<>();
	private PivotComponent root;
	protected Icon visible;
	protected Icon remove;
	protected Icon edit;
	protected Icon sort_up;
	protected Icon sort_dw;
	private Group group;
	//
	private boolean editmode;
	private Panel editpanel;
	private TextField name;
	private SelectBox<String> pivots = new SelectBox<>();
	
	public GroupComponent(Group group, PivotComponent picom){
		super(group.id, picom == null ? 0 : 4, group.isEmpty() ? HEIGHT : HEIGHT + group.size() * PH + 4, true, true);
		root = picom;
		label.getTextState().setText((this.group = group).id);
		this.genFullheight();
		add(visible = new Icon(this, 2, "./resources/textures/icons/component/visible.png", () -> pin()));
		add(remove = new Icon(this, 3, "./resources/textures/icons/component/remove.png", () -> FMT.MODEL.remGroup(group)));
		add(edit = new Icon(this, 4, "./resources/textures/icons/component/edit.png", () -> toggleEditMode()));
		add(sort_dw = new Icon(this, 5, "./resources/textures/icons/component/move_down.png", () -> FMT.MODEL.swap(group, 1, true)));
		add(sort_up = new Icon(this, 6, "./resources/textures/icons/component/move_up.png", () -> FMT.MODEL.swap(group, -1, true)));
		updcom.add(GroupRenamed.class, event -> { if(event.group() == group) label.getTextState().setText(group.id); });
		updcom.add(PolygonAdded.class, event -> { if(event.group() == group) addPolygon(event.polygon(), true); });
		updcom.add(PolygonRenamed.class, event -> { if(event.polygon().group() == group) renamePolygon(event.polygon()); });
		updcom.add(PolygonRemoved.class, event -> { if(event.group() == group) removePolygon(event.polygon()); });
		group.forEach(poly -> addPolygon(poly, false));
		update_color();
		//if(!group.visible) UIUtils.hide(this);
		MouseClickEventListener listener = lis -> {
			if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
				group.model.select(group);
				update_color();
			}
		};
		updcom.add(GroupSelected.class, event -> {
			if(event.group() == group){
				update_color();
				for(PolygonLabel poly : polygons){
					poly.update_color();
				}
			}
		});
		updcom.add(GroupVisibility.class, event -> {
			if(event.group() == group){
				update_color();
				for(PolygonLabel poly : polygons){
					poly.update_color();
				}
			}
		});
		updcom.add(PolygonSelected.class, event -> {
			if(event.polygon().group() != group) return;
			for(PolygonLabel poly : polygons){
				if(poly.polygon == event.polygon()) poly.update_color();
			}
		});
		updcom.add(PolygonVisibility.class, event -> {
			if(event.polygon().group() != group) return;
			for(PolygonLabel poly : polygons){
				if(poly.polygon == event.polygon()) poly.update_color();
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
		//
		UIUtils.hide(remove, visible, edit);
		if(!PolygonTree.SORT_MODE) UIUtils.hide(sort_up, sort_dw);
	}

	private void toggleEditMode(){
		editmode = !editmode;
		if(editmode && editpanel == null){
			initEditPanel();
		}
		UIUtils.show(editmode, editpanel);
		resize();
		(root == null ? editor : root.editor).alignComponents();
	}

	private int genFullheight(){
		minheight = editmode ? HEIGHT + EMS : HEIGHT;
		return fullheight = (group.isEmpty() ? HEIGHT : HEIGHT + group.size() * PHS + 4) + (editmode ? EMS : 0);
	}

	@Override
	public void minimize(Boolean bool){
		super.minimize(bool);
		group.minimized = minimized;
		if(root != null){
			root.resize();
		}
	}

	private void addPolygon(Polygon polygon, boolean resort){
		PolygonLabel label = new PolygonLabel(this).polygon(polygon).update_name().update_color();
		this.add(label);
		polygons.add(label);
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
		int off = editmode ? EMS : 0;
		for(int i = 0; i < polygons.size(); i++){
			polygons.get(i).sortin(i, off);
		}
	}

	private void renamePolygon(Polygon polygon){
		for(PolygonLabel label : polygons){
			if(label.polygon == polygon){
				label.update_name();
				break;
			}
		}
	}

	private void removePolygon(Polygon polygon){
		for(PolygonLabel label : polygons){
			if(label.polygon == polygon){
				polygons.remove(label);
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
	protected boolean move(int dir){
		if(super.move(dir)){
			try{
				Pivot pivot = FMT.MODEL.getP(group.pivot);
				int index = pivot.groups.indexOf(group);
				Collections.swap(pivot.groups, index, index + dir);
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
		group.visible = !group.visible;
		//update_color();
		UpdateHandler.update(new GroupVisibility(group, group.visible));
	}
	
	@Override
	public void rem(){
		if(ASK_GROUP_REMOVAL.value){
			GenericDialog.showOC(null, () -> FMT.MODEL.remGroup(group), null, "editor.component.group.group.remove", group.id);
		}
		else FMT.MODEL.remGroup(group);
	}
	
	public static class PolygonLabel extends Label {
		
		private Polygon polygon;

		public PolygonLabel(GroupComponent com){
			Settings.applyBorderless(this);
			setSize(com.getSize().x - 8, PH);
			Icon remo = new Icon(0, 16, 4, Editor.CWIDTH - 26, 2, "./resources/textures/icons/component/remove.png", () -> {
				if(ASK_POLYGON_REMOVAL.value){
					GenericDialog.showOC(null, () -> com.group.remove(polygon), null, "editor.component.group.polygon.remove", com.group.id + ":" + polygon.name());
				}
				else com.group.remove(polygon);
			});
			Icon visi = new Icon(0, 16, 4, Editor.CWIDTH - 46, 2, "./resources/textures/icons/component/visible.png", () -> {
				polygon.visible = !polygon.visible;
				//update_color();
				UpdateHandler.update(new PolygonVisibility(polygon, polygon.visible));
			});
			Icon edit = new Icon(0, 16, 4, Editor.CWIDTH - 66, 2, "./resources/textures/icons/component/edit.png", () -> {
				Editor.show("polygon_editor");
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
			this.getListenerMap().addListener(MouseClickEvent.class, lis -> {
				if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
					polygon.group().model.select(polygon);
					update_color();
				}
			});
			UIUtils.hide(remo, visi, edit);
			add(remo);
			add(visi);
			add(edit);
		}
		
		public PolygonLabel polygon(Polygon poly){
			this.polygon = poly;
			return this;
		}
		
		public Polygon polygon(){
			return polygon;
		}
		
		public PolygonLabel update_name(){
			this.getTextState().setText(" " + polygon.name());
			return this;
		}
		
		public PolygonLabel update_color(){
			getStyle().setTextColor(polygon.selected ? ColorConstants.darkGray() : ColorConstants.lightGray());
			getStyle().getBackground().setColor(FMT.rgba((polygon.selected ? polygon.visible ? POLYGON_SELECTED : POLYGON_INV_SEL : polygon.visible ? POLYGON_NORMAL : POLYGON_INVISIBLE).value));
			return this;
		}
		
		public PolygonLabel sortin(int index, int offset){
			setPosition(5, HEIGHT + 2 + (index * PHS) + offset);
			return this;
		}
		
	}

	public void update_color(){
		label.getStyle().setTextColor(group.selected ? ColorConstants.darkGray() : ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba((group.selected ? group.visible ? GROUP_SELECTED : GROUP_INV_SEL : group.visible ? GROUP_NORMAL : GROUP_INVISIBLE).value));
	}

	private void initEditPanel(){
		editpanel = new Panel(5, HEIGHT + 5, LW, EMS - 10);
		editpanel.add(new Label(translate(LANG_PREFIX + "group.general.name/id"), L5, row(0), LWS, HEIGHT));
		editpanel.add(name = new TextField(group.id, L5, row(1), LWS - 30, HEIGHT, false));
		editpanel.add(new RunButton(">>", L5 + LWS - 25, row(0), HEIGHT, HEIGHT, this::rename));
		updcom.add(GroupRenamed.class, e -> {
			if(e.group().equals(group)) label.getTextState().setText(group.id);
		});
		//
		editpanel.add(new Label(translate(LANG_PREFIX + "group.general.pivot"), L5, row(1), LWS, HEIGHT));
		editpanel.add(pivots);
		pivots.setPosition(L5, row(1));
		pivots.setSize(LWS, HEIGHT);
		updcom.add(ModelLoad.class, event -> refreshPivotEntries());
		updcom.add(PivotAdded.class, event -> refreshPivotEntries());
		updcom.add(PivotRenamed.class, event -> refreshPivotEntries());
		updcom.add(PivotRemoved.class, event -> refreshPivotEntries());
		pivots.addSelectBoxChangeSelectionEventListener(lis -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			if(!groups.contains(group)){
				if(groups.size() > 0) return;
				groups.add(0, group);
			}
			Pivot pivot = FMT.MODEL.getP(lis.getNewValue());
			for(Group gr : groups){
				Pivot old = FMT.MODEL.getP(gr.pivot);
				if(old != null) old.groups.remove(gr);
				gr.pivot = pivot.id;
				pivot.groups.add(gr);
				UpdateHandler.update(new PivotChanged(gr, pivot));
			}
		});
		pivots.setVisibleCount(6);
		refreshPivotEntries();
		//
		add(editpanel);
	}

	private void rename(){
		String string = name.getTextState().getText();
		ArrayList<Group> groups = FMT.MODEL.selected_groups();
		if(groups.size() > 1 && groups.contains(group)){
			for(int i = 0; i < groups.size(); i++){
				groups.get(i).reid(string + String.format(GROUP_SUFFIX.value, i));
			}
		}
		else group.reid(string);
	}

	private void refreshPivotEntries(){
		while(pivots.getElements().size() > 0) pivots.removeElement(0);
		for(Pivot pivot : FMT.MODEL.pivots()){
			pivots.addElement(pivot.id);
		}
		pivots.setSelected(group.pivot, true);
	}

}
