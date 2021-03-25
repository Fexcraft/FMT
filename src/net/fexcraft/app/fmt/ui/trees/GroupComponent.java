package net.fexcraft.app.fmt.ui.trees;

import java.util.ArrayList;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.lib.common.math.RGB;

public class GroupComponent extends EditorComponent {

	public static final RGB GROUP = new RGB(0, 74, 127), POLYGON = new RGB(38, 127, 0);//TODO adjust colors, selected colors
	private static final int PH = 20, PHS = 21;
	private ArrayList<PolygonLabel> polygons = new ArrayList<>();
	private Group group;
	
	public GroupComponent(Group group){
		super(group.id, HEIGHT + group.size() * PH + 4, true, true);
		label.getTextState().setText((this.group = group).id);
		this.genFullheight();
		updateholder.add(UpdateType.GROUP_RENAMED, wrp -> { if(wrp.objs[1] == group) label.getTextState().setText(group.id); });
		updateholder.add(UpdateType.POLYGON_ADDED, wrp -> { if(wrp.objs[0] == group) addPolygon(wrp.get(1), group.size() - 1); });
		updateholder.add(UpdateType.POLYGON_RENAMED, wrp -> { if(wrp.objs[0] == group) renamePolygon(wrp.get(1)); });
		updateholder.add(UpdateType.POLYGON_REMOVED, wrp -> { if(wrp.objs[0] == group) removePolygon(wrp.get(1)); });
		for(int i = 0; i < group.size(); i++){
			addPolygon(group.get(i), i);
		}
		label.getStyle().setTextColor(ColorConstants.lightGray());
		this.getStyle().getBackground().setColor(FMT.rgba(GROUP.packed));
	}

	private int genFullheight(){
		return fullheight = HEIGHT + group.size() * PHS + 4;
	}

	private void addPolygon(Polygon polygon, int index){
		PolygonLabel label = new PolygonLabel(this).polygon(polygon).sortin(index).update();
		this.add(label);
		polygons.add(label);
		sort();
	}
	
	private void sort(){
		this.setSize(Editor.CWIDTH, genFullheight());
		for(int i = 0; i < polygons.size(); i++) polygons.get(i).sortin(i);
		this.minimize(false);
	}

	private void renamePolygon(Polygon polygon){
		for(PolygonLabel label : polygons){
			if(label.polygon == polygon){
				label.update();
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
		sort();
	}

	public Group group(){
		return group;
	}
	
	@Override
	public void pin(){
		group.visible = !group.visible;
	}
	
	@Override
	public void rem(){
		if(Settings.ASK_GROUP_REMOVAL.value){
			GenericDialog.showOC(null, () -> { FMT.MODEL.remGroup(group); super.rem(); }, null, "editor.component.group.group.remove", group.id);
		}
		else{
			FMT.MODEL.remGroup(group);
			super.rem();
		}
	}
	
	public static class PolygonLabel extends Label {
		
		private Polygon polygon;

		public PolygonLabel(GroupComponent com){
			Settings.applyBorderless(this);
			setSize(Editor.CWIDTH - 5, PH);
			Icon icon = new Icon(0, 16, 4, Editor.CWIDTH - 16 - 5, 2, "./resources/textures/icons/component/remove.png", () -> {
				if(Settings.ASK_POLYGON_REMOVAL.value){
					GenericDialog.showOC(null, () -> com.group.remove(polygon), null, "editor.component.group.polygon.remove", com.group.id + ":" + polygon.name());
				}
				else com.group.remove(polygon);
			});
			CursorEnterEventListener listener = lis -> icon.getStyle().setDisplay(this.isHovered() || icon.isHovered() ? DisplayType.MANUAL : DisplayType.NONE);
			this.getListenerMap().addListener(CursorEnterEvent.class, listener);
			icon.getListenerMap().addListener(CursorEnterEvent.class, listener);
			UIUtils.hide(icon);
			this.add(icon);
		}
		
		public PolygonLabel polygon(Polygon poly){
			this.polygon = poly;
			return this;
		}
		
		public Polygon polygon(){
			return polygon;
		}
		
		public PolygonLabel update(){
			this.getTextState().setText(" " + polygon.name());
			getStyle().setTextColor(ColorConstants.lightGray());
			getStyle().getBackground().setColor(FMT.rgba(POLYGON.packed));
			return this;
		}
		
		public PolygonLabel sortin(int index){
			setPosition(5, HEIGHT + 2 + (index * PHS));
			return this;
		}
		
	}

}
