package net.fexcraft.app.fmt.ui.trees;

import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupAdded;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupRemoved;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelLoad;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelUnload;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.json.JsonMap;

import static net.fexcraft.app.fmt.ui.EditorComponent.LW;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonTree extends Editor {

	private static UpdateCompound updcom = new UpdateCompound();
	public static boolean SORT_MODE;
	public static Label polygons;

	public PolygonTree(){
		super(Trees.POLYGON.id, "Polygon Tree", true);
		addTreeIcons(Trees.POLYGON);
		updcom.add(GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(GroupRemoved.class, event -> remGroup(event.group()));
		updcom.add(ModelLoad.class, event -> resizeGroups(event.model()));
		updcom.add(ModelUnload.class, event -> removeGroups(event.model()));
		UpdateHandler.register(updcom);
		add(polygons = new Label("", 10, 30, LW, 30));
		add(new RunButton("editor.tree.polygon.sort", 155, 60, 135, 24, () -> toggleSorting(), false));
	}

	@Override
	protected float topSpace(){
		return 90;
	}

	private void addGroup(Group group){
		this.addComponent(new GroupComponent(group));
	}
	
	private void remGroup(Group group){
		this.removeComponent(getComponent(group));
	}

	private EditorComponent getComponent(Group group){
		for(EditorComponent com : this.components){
			if(((GroupComponent)com).group() == group) return com;
		}
		return null;
	}

	private void resizeGroups(Model model){
		components.forEach(com -> ((GroupComponent)com).resize());
	}

	private void removeGroups(Model model){
		for(Group group : model.allgroups()) remGroup(group);
	}

	public void reAddGroups(){
		for(Group group : FMT.MODEL.allgroups()) remGroup(group);
		for(Group group : FMT.MODEL.allgroups()) addGroup(group);
		for(EditorComponent component : components) ((GroupComponent)component).resize();
	}

	private void toggleSorting(){
		SORT_MODE = !SORT_MODE;
		GroupComponent comp;
		if(SORT_MODE){
			for(EditorComponent component : components){
				comp = (GroupComponent)component;
				UIUtils.show(comp.sort_up, comp.sort_dw);
			}
		}
		else{
			for(EditorComponent component : components){
				comp = (GroupComponent)component;
				UIUtils.hide(comp.sort_up, comp.sort_dw);
			}
		}
	}

}
