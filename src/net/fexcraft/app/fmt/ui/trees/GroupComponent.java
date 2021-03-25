package net.fexcraft.app.fmt.ui.trees;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.style.color.ColorConstants;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.lib.common.math.RGB;

public class GroupComponent extends EditorComponent {

	public static final RGB GROUP = new RGB(0, 74, 127), POLYGON = new RGB(38, 127, 0);//TODO adjust colors, selected colors
	private static final int PH = 20, PHS = 21;
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
		Label label = new Label(polygon.name(), 5, HEIGHT + 2 + (index * PHS), Editor.CWIDTH - 5, PH);
		Settings.applyBorderless(label);
		label.getStyle().setTextColor(ColorConstants.lightGray());
		label.getStyle().getBackground().setColor(FMT.rgba(POLYGON.packed));
		this.add(label);
		this.setSize(Editor.CWIDTH, genFullheight());
		this.minimize(false);
	}
	
	private void renamePolygon(Polygon polygon){
		// TODO Auto-generated method stub
		
	}

	private void removePolygon(Polygon polygon){
		// TODO Auto-generated method stub
		
	}

	public Group group(){
		return group;
	}
	
	@Override
	public void rem(){
		GenericDialog.showOC(null, () -> { FMT.MODEL.remGroup(group); super.rem(); }, null, "editor.component.group.remove", group.id);
	}

}
