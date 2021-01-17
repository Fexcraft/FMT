package net.fexcraft.app.fmt.ui;

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
	}

}
