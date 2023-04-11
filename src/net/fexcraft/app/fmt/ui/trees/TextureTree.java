package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.update.UpdateEvent.TexGroupAdded;
import net.fexcraft.app.fmt.update.UpdateEvent.TexGroupRemoved;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.AutoUVPositioner;
import net.fexcraft.app.json.JsonMap;

public class TextureTree extends Editor {
	
	private static UpdateCompound updcom = new UpdateCompound();

	public TextureTree(String name, boolean alignment){
		super(TREES.get(1), name == null ? "Texture Tree" : name, true, alignment);
		this.addTreeIcons(2);
		updcom.add(TexGroupAdded.class, event -> addTexGroup(event.group()));
		updcom.add(TexGroupRemoved.class, event -> remTexGroup(event.group()));
		UpdateHandler.register(updcom);
		this.add(new RunButton("editor.tree.texture.add_group", 10, 30, 135, 24, () -> TextureManager.addGroup(null, true), false));
		this.add(new RunButton("editor.tree.texture.auto_pos", 155, 30, 135, 24, () -> AutoUVPositioner.runAutoPos(), false));
		this.add(new RunButton("editor.tree.texture.reset_pos", 10, 60, 135, 24, () -> AutoUVPositioner.runReset(false), false));
		this.add(new RunButton("editor.tree.texture.reset_type", 155, 60, 135, 24, () -> AutoUVPositioner.runReset(true), false));
	}

	public TextureTree(String key, JsonMap obj){
		this(obj.get("name", "Texture Tree"), obj.get("alignment", true));
	}
	
	@Override
	protected float topSpace(){
		return 90f;
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
