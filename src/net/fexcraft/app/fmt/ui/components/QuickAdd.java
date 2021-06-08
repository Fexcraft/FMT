package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Cylinder;
import net.fexcraft.app.fmt.polygon.Marker;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;

public class QuickAdd extends EditorComponent {

	public QuickAdd(){
		super("polygon.quick", 55, false, false);
		int yoff = HEIGHT, xoff = 4, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/polygon/box.png", () -> addBox()).addTooltip(LANG_PREFIX + id + ".add_box"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/polygon/shapebox.png", () -> addShapebox()).addTooltip(LANG_PREFIX + id + ".add_shapebox"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/polygon/cylinder.png", () -> addCylinder()).addTooltip(LANG_PREFIX + id + ".add_cylinder"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/polygon/boundingbox.png", () -> {}).addTooltip(LANG_PREFIX + id + ".add_boundingbox"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/polygon/object.png", () -> {}).addTooltip(LANG_PREFIX + id + ".add_object"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/polygon/marker.png", () -> addMarker()).addTooltip(LANG_PREFIX + id + ".add_marker"));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/polygon/group.png", () -> addGroup()).addTooltip(LANG_PREFIX + id + ".add_group"));
		this.add(new Icon(7, size, 2, xoff, yoff, "./resources/textures/icons/polygon/voxel.png", () -> {}).addTooltip(LANG_PREFIX + id + ".add_voxel"));
	}

	public static void addBox(){
		FMT.MODEL.add(null, new Box(null));
	}

	public static void addShapebox(){
		FMT.MODEL.add(null, new Shapebox(null));
	}

	public static void addGroup(){
		String name = "group" + FMT.MODEL.groups().size();
		while(FMT.MODEL.contains(name)) name += "0";
		FMT.MODEL.addGroup(name);
	}

	public static void addCylinder(){
		FMT.MODEL.add(null, new Cylinder(null));
	}

	public static void addMarker(){
		FMT.MODEL.add(null, new Marker(null));
	}

}
