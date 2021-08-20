package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.json.JsonMap;

public class TextureTree extends Editor {
	
	private static UpdateHolder holder = new UpdateHolder();

	public TextureTree(String name, boolean alignment){
		super(TREES.get(1), name == null ? "Texture Tree" : name, true, alignment);
		this.addTreeIcons(2);
		holder.add(UpdateType.TEXGROUP_ADDED, wrp -> addTexGroup(wrp.get(1)));
		holder.add(UpdateType.TEXGROUP_REMOVED, wrp -> remTexGroup(wrp.get(1)));
		holder.add(UpdateType.MODEL_LOAD, wrp -> resizeTexGroups(wrp.get(0)));
		holder.add(UpdateType.MODEL_UNLOAD, wrp -> removeTexGroups(wrp.get(0)));
		UpdateHandler.registerHolder(holder);
	}

	public TextureTree(String key, JsonMap obj){
		this(obj.get("name", "Texture Tree"), obj.get("alignment", true));
	}
	
	@Override
	protected float topSpace(){
		return 30f;
	}

	private void addTexGroup(Group group){
		this.addComponent(new GroupComponent(group));
	}
	
	private void remTexGroup(Group group){
		this.removeComponent(getComponent(group));
	}

	private EditorComponent getComponent(Group group){
		for(EditorComponent com : this.components){
			if(((GroupComponent)com).group() == group) return com;
		}
		return null;
	}

	private void resizeTexGroups(Model model){
		components.forEach(com -> ((GroupComponent)com).resize());
	}

	private void removeTexGroups(Model model){
		for(Group group : model.groups()) remTexGroup(group);
	}

}
