package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.json.JsonMap;

public class TextureTree extends Editor {
	
	private static UpdateHolder holder = new UpdateHolder();

	public TextureTree(String name, boolean alignment){
		super(TREES.get(1), name == null ? "Texture Tree" : name, true, alignment);
		this.addTreeIcons(2);
		holder.add(UpdateType.TEXGROUP_ADDED, wrp -> addTexGroup(wrp.get(0)));
		holder.add(UpdateType.TEXGROUP_REMOVED, wrp -> remTexGroup(wrp.get(0)));
		UpdateHandler.registerHolder(holder);
		this.add(new RunButton("editor.tree.texture.add_group", 7.5f, 30, 90, 24, () -> TextureManager.addGroup(null, true), false));
		this.add(new RunButton("editor.tree.texture.auto_pos", 105, 30, 90, 24, () -> {}, false));//TODO
		this.add(new RunButton("editor.tree.texture.reset_pos", 202.5f, 30, 90, 24, () -> {}, false));//TODO
	}

	public TextureTree(String key, JsonMap obj){
		this(obj.get("name", "Texture Tree"), obj.get("alignment", true));
	}
	
	@Override
	protected float topSpace(){
		return 60f;
	}

	private void addTexGroup(TextureGroup group){
		this.addComponent(new TexGroupComponent(group));
	}
	
	private void remTexGroup(TextureGroup group){
		this.removeComponent(getComponent(group));
	}

	private EditorComponent getComponent(TextureGroup group){
		for(EditorComponent com : this.components){
			if(((TexGroupComponent)com).texgroup() == group) return com;
		}
		return null;
	}

}
