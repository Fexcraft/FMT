package net.fexcraft.app.fmt.ui;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.ui.components.FolderComponent;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;

public class JsonEditorMenu {
	
	private static MenuLayer layer;
	private static JsonEditor jsoneditor;
	private static String idxkey;
	private static JsonObject root, elm;

	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "jsoneditormenu.insert", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(1, "jsoneditormenu.add_before", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(2, "jsoneditormenu.add_after", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(3, "jsoneditormenu.rename", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(4, "jsoneditormenu.copy", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(5, "jsoneditormenu.delete", () -> {
			//
			layer.hide();
		}));
		layer = new MenuLayer(null, new Vector2f((float)GGR.posx, (float)GGR.posy), components, null){
			@Override
			public boolean timed(){
				return true;
			}
		};
	}

	public static void show(JsonEditor editor, JsonObject map, String key, JsonObject value){
		jsoneditor = editor;
		root = map;
		idxkey = key;
		elm = value;
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

}
