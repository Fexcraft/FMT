package net.fexcraft.app.fmt.ui;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.ui.components.FolderComponent;
import net.fexcraft.app.fmt.utils.GGR;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;

public class FileEditMenu {
	
	private static MenuLayer layer;
	private static FolderComponent component;
	private static FolderComponent.DirComponent directory;
	private static File file;
	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "fileeditmenu.asjson", () -> {
			new JsonEditor(file);
			layer.hide();
		}));
		components.add(new MenuButton(1, "fileeditmenu.rename", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(2, "fileeditmenu.copy", () -> {
			//
			layer.hide();
		}));
		components.add(new MenuButton(3, "fileeditmenu.delete", () -> {
			file.delete();
			component.genView();
			layer.hide();
		}));
		layer = new MenuLayer(null, new Vector2f((float)GGR.posx, (float)GGR.posy), components, null){
			@Override
			public boolean timed(){
				return true;
			}
		};
	}

	public static void show(FolderComponent folcom, FolderComponent.DirComponent dircom, File fl){
		component = folcom;
		directory = dircom;
		file = fl;
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

}
