package net.fexcraft.app.fmt.ui.trees;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.utils.Jsoniser;

public class PolygonTree extends Editor {
	
	private static UpdateHolder holder = new UpdateHolder();

	public PolygonTree(String name, boolean alignment){
		super(TREES.get(0), name == null ? "Polygon Tree" : name, true, alignment);
		this.addTreeIcons(0);
		holder.add(UpdateType.GROUP_ADDED, wrp -> addGroup(wrp.get(1)));
		holder.add(UpdateType.GROUP_REMOVED, wrp -> remGroup(wrp.get(1)));
		holder.add(UpdateType.MODEL_LOAD, wrp -> resizeGroups(wrp.get(0)));
		UpdateHandler.registerHolder(holder);
	}

	public PolygonTree(String key, JsonObject obj){
		this(Jsoniser.get(obj, "name", "Polygon Tree"), Jsoniser.get(obj, "alignment", true));
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

}
