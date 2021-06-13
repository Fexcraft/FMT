package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.liquidengine.legui.component.Component;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.utils.GGR;

public class PolySelMenu {
	
	private static MenuLayer layer;
	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "polyselmenu.hide", () -> {
			FMT.MODEL.hidesel();
			layer.hide();
		}));
		components.add(new MenuButton(1, "polyselmenu.copy", () -> {
			FMT.MODEL.copySelected();
			layer.hide();
		}));
		components.add(new MenuButton(2, "polyselmenu.delete", () -> {
			FMT.MODEL.delsel();
			layer.hide();
		}));
		layer = new MenuLayer(new Vector2f((float)GGR.posx, (float)GGR.posy), components, null);
	}

	public static void show(){
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

}
