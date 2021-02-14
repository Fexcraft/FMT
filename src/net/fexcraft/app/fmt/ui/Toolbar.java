package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;

public class Toolbar extends Panel {
	
	public static final Runnable NOTHING = () -> {};

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
			getStyle().setBorderRadius(0);
			getStyle().setBorder(null);
		});
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", NOTHING));
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
			).setLayerPreShow(() -> {
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
		this.add(new ToolbarMenu(1, "editors"));
		this.add(new ToolbarMenu(2, "components"));
		this.add(new ToolbarMenu(3, "utils"));
		this.add(new ToolbarMenu(4, "polygons"));
		this.add(new ToolbarMenu(5, "texture"));
		this.add(new ToolbarMenu(6, "helpers"));
		this.add(new ToolbarMenu(7, "project",
			new MenuButton(0, "project.open"),
			new MenuButton(1, "project.settings"),
			new MenuButton(2, "project.import"),
			new MenuButton(3, "project.export"),
			new MenuButton(4, "project.close")
		));
		this.add(new ToolbarMenu(8, "THEME",  () -> { Settings.SELTHEME = !Settings.SELTHEME; Settings.applyTheme(); }));
		//this.add(new ToolbarMenu(8, "exit", () -> { FMT.close(); }));
	}

}
