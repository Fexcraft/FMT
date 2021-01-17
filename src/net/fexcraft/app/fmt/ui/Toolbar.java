package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;

public class Toolbar extends Panel {

	public Toolbar(){
		super(0, 0, FMT.WIDTH, 30);
		this.setFocusable(false);
		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
			getStyle().setBorderRadius(0);
			getStyle().setBorder(null);
		});
		this.add(new Icon(0, "./resources/textures/icons/toolbar/info.png", () -> {}));
		this.add(new Icon(1, "./resources/textures/icons/toolbar/settings.png", () -> {}));
		this.add(new Icon(2, "./resources/textures/icons/toolbar/profile.png", () -> {}));
		this.add(new Icon(3, "./resources/textures/icons/toolbar/save.png", () -> {}));
		this.add(new Icon(4, "./resources/textures/icons/toolbar/open.png", () -> {}));
		this.add(new Icon(5, "./resources/textures/icons/toolbar/new.png", () -> {}));
		this.add(new ToolbarMenu(0, "file",
			new Button(0, 0, ToolbarMenu.WIDTH, ToolbarMenu.HEIGHT),
			new Button(0, 28, ToolbarMenu.WIDTH, ToolbarMenu.HEIGHT),
			new Button(0, 56, ToolbarMenu.WIDTH, ToolbarMenu.HEIGHT),
			new Button(0, 84, ToolbarMenu.WIDTH, ToolbarMenu.HEIGHT)
		));
		this.add(new ToolbarMenu(1, "editors"));
		this.add(new ToolbarMenu(2, "components"));
		this.add(new ToolbarMenu(3, "utils"));
		this.add(new ToolbarMenu(4, "polygons"));
		this.add(new ToolbarMenu(5, "texture"));
		this.add(new ToolbarMenu(6, "helpers"));
		this.add(new ToolbarMenu(8, "THEME",  () -> { Settings.SELTHEME = !Settings.SELTHEME; Settings.applyTheme(); }));
		this.add(new ToolbarMenu(7, "exit", () -> { FMT.close(); }));
	}

}
