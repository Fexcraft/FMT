package net.fexcraft.app.fmt.ui.trees;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.utils.Jsoniser;

public class PolygonTree extends Editor {
	
	private static UpdateHolder holder = new UpdateHolder();

	public PolygonTree(String name, boolean alignment){
		super(TREES.get(0), name == null ? "Polygon Tree" : name, true, alignment);
		holder.add(UpdateType.GROUP_ADDED, (x, y, z) -> addGroup());
		holder.add(UpdateType.GROUP_REMOVED, (x, y, z) -> remGroup());
		UpdateHandler.registerHolder(holder);
		addComponent(new EditorComponent("tree.test.group", 200, true, true));
		addComponent(new EditorComponent("tree.test.group", 200, true, true));
		addComponent(new EditorComponent("tree.test.group", 200, true, true));
		addComponent(new EditorComponent("tree.test.group", 200, true, true));
		addComponent(new EditorComponent("tree.test.group", 200, true, true));
	}

	public PolygonTree(String key, JsonObject obj){
		this(Jsoniser.get(obj, "name", "Polygon Tree"), Jsoniser.get(obj, "alignment", true));
	}

	private void addGroup(){
		// TODO Auto-generated method stub
	}
	
	private void remGroup(){
		// TODO Auto-generated method stub
	}

}
