package net.fexcraft.app.fmt.ui.trees;

import static net.fexcraft.app.fmt.settings.Settings.*;

import java.util.ArrayList;
import java.util.Collections;

import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.utils.Logging;
import org.liquidengine.legui.style.color.ColorConstants;

public class GroupComponent extends EditorComponent {

	private static final int PH = 20, PHS = 21;
	private ArrayList<PolygonLabel> polygons = new ArrayList<>();
	private Group group;
	
	public GroupComponent(Group group){
		super(group.id, group.isEmpty() ? HEIGHT : HEIGHT + group.size() * PH + 4, true, true);
		label.getTextState().setText((this.group = group).id);
		this.genFullheight();
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
	}

	private int genFullheight(){
		return fullheight = group.isEmpty() ? HEIGHT : HEIGHT + group.size() * PHS + 4;
	}

	@Override
	protected void minimize(Boolean bool){
		super.minimize(bool);
		group.minimized = minimized;
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
		for(int i = 0; i < polygons.size(); i++){
			polygons.get(i).sortin(i);
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
				int index = FMT.MODEL.groups().indexOf(group);
				Collections.swap(FMT.MODEL.groups(), index, index + dir);
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
			setSize(Editor.CWIDTH - 8, PH);
			Icon icon = new Icon(0, 16, 4, Editor.CWIDTH - 26, 2, "./resources/textures/ievent/component/remove.png", () -> {
				if(ASK_POLYGON_REMOVAL.value){
					GenericDialog.showOC(null, () -> com.group.remove(polygon), null, "editor.component.group.polygon.remove", com.group.id + ":" + polygon.name());
				}
				else com.group.remove(polygon);
			});
			Icon visi = new Icon(0, 16, 4, Editor.CWIDTH - 46, 2, "./resources/textures/ievent/component/visible.png", () -> {
				polygon.visible = !polygon.visible;
				//update_color();
				UpdateHandler.update(new PolygonVisibility(polygon, polygon.visible));
			});
			CursorEnterEventListener listener = lis -> {
				DisplayType type = this.isHovered() || icon.isHovered() || visi.isHovered() ? DisplayType.MANUAL : DisplayType.NONE;
				icon.getStyle().setDisplay(type);
				visi.getStyle().setDisplay(type);
			};
			this.getListenerMap().addListener(CursorEnterEvent.class, listener);
			icon.getListenerMap().addListener(CursorEnterEvent.class, listener);
			visi.getListenerMap().addListener(CursorEnterEvent.class, listener);
			this.getListenerMap().addListener(MouseClickEvent.class, lis -> {
				if(lis.getAction() == MouseClickAction.CLICK && lis.getButton() == MouseButton.MOUSE_BUTTON_LEFT){
					polygon.group().model.select(polygon);
					update_color();
				}
			});
			UIUtils.hide(icon, visi);
			this.add(icon);
			this.add(visi);
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
		
		public PolygonLabel sortin(int index){
			setPosition(5, HEIGHT + 2 + (index * PHS));
			return this;
		}
		
	}

	public void update_color(){
		label.getStyle().setTextColor(group.selected ? ColorConstants.darkGray() : ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba((group.selected ? group.visible ? GROUP_SELECTED : GROUP_INV_SEL : group.visible ? GROUP_NORMAL : GROUP_INVISIBLE).value));
	}

}
