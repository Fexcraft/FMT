package net.fexcraft.app.fmt.ui;

import java.io.File;

import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.Logging;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};
	private UpdateHolder holder;

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.applyBorderless(this);
		holder = new UpdateHolder();
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> Logging.log("test")));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", NOTHING));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", NOTHING));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", NOTHING));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", NOTHING));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", NOTHING));
		this.add(new ToolbarMenu(0, "file",
			new MenuButton(0, "file.new"),
			new MenuButton(1, "file.open"),
			new ToolbarMenu(-2, "file.recent",
				new MenuButton(0, "file.recent.none"),
				new MenuButton(1, "file.recent.none"),
				new MenuButton(2, "file.recent.none"),
				new MenuButton(3, "file.recent.none"),
				new MenuButton(4, "file.recent.none"),
				new MenuButton(5, "file.recent.none"),
				new MenuButton(6, "file.recent.none"),
				new MenuButton(7, "file.recent.none"),
				new MenuButton(8, "file.recent.none"),
				new MenuButton(9, "file.recent.none")
			).setLayerPreShow(layer -> {
				//TODO
			}),
			new MenuButton(3, "file.save"),
			new MenuButton(4, "file.save_as"),
			new MenuButton(5, "file.import"),
			new MenuButton(6, "file.export"),
			new MenuButton(7, "file.settings"),
			new MenuButton(8, "file.donate"),
			new MenuButton(9, "file.exit", () -> FMT.close())
		));
		this.add(new ToolbarMenu(1, "editors",
			new MenuButton(0, "editors.new")
		));
		holder.add(UpdateType.EDITOR_CREATED, wrap -> {
			Editor editor = wrap.get(0);
			if(editor.tree) return;
			ToolbarMenu menu = ToolbarMenu.MENUS.get("editors");
			MenuButton button = new MenuButton(menu.components.size(), editor.id, editor.name);
			button.addListener(() -> editor.toggle());
			menu.components.add(button);
			menu.layer.regComponent(button);
			menu.layer.refreshSize();
		});
		holder.add(UpdateType.EDITOR_REMOVED, wrap -> {
			//TODO
		});
		this.add(new ToolbarMenu(2, "utils"));
		this.add(new ToolbarMenu(3, "polygons",
			new MenuButton(0, "polygons.add_box", () -> QuickAdd.addBox()),
			new MenuButton(1, "polygons.add_shapebox", () -> QuickAdd.addShapebox()),
			new MenuButton(2, "polygons.add_cylinder"),
			new MenuButton(3, "polygons.add_boundingbox"),
			new MenuButton(4, "polygons.add_object"),
			new MenuButton(5, "polygons.add_marker"),
			new MenuButton(6, "polygons.add_group", () -> QuickAdd.addGroup()),
			new MenuButton(7, "polygons.add_voxel")
		));
		this.add(new ToolbarMenu(4, "texture"));
		this.add(new ToolbarMenu(5, "helpers"));
		this.add(new ToolbarMenu(6, "project",
			new MenuButton(0, "project.open"),
			new MenuButton(1, "project.settings"),
			//new MenuButton(2, "project.import"),
			//new MenuButton(3, "project.export"),
			new MenuButton(2, "project.close")
		));
		this.add(new ToolbarMenu(7, "exit", () -> FMT.close()));
		UpdateHandler.registerHolder(holder);
	}

	public static void addRecent(File file){
		// TODO Auto-generated method stub
	}

}
