package net.fexcraft.app.fmt.ui.trees;

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

public class PolygonTree extends Editor {
	
	private static UpdateCompound updcom = new UpdateCompound();

	public PolygonTree(){
		super(TREES.get(0), "Polygon Tree", true);
		this.addTreeIcons(0);
		updcom.add(GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(GroupRemoved.class, event -> remGroup(event.group()));
		updcom.add(ModelLoad.class, event -> resizeGroups(event.model()));
		updcom.add(ModelUnload.class, event -> removeGroups(event.model()));
		UpdateHandler.register(updcom);
	}
	
	@Override
	protected float topSpace(){
		return 60f;
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

}
