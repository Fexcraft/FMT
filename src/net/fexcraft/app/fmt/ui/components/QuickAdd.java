package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;

public class QuickAdd extends EditorComponent {

	public QuickAdd(){
		super("polygon.quick", 55, false);
		int yoff = 22, xoff = 4, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/polygon/box.png", () -> FMT.MODEL.add(null, new Box(null))));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/polygon/shapebox.png", () -> FMT.MODEL.add(null, new Shapebox(null))));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/polygon/cylinder.png", () -> {}));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/polygon/boundingbox.png", () -> {}));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/polygon/object.png", () -> {}));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/polygon/marker.png", () -> {}));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/polygon/group.png", () -> {}));
		this.add(new Icon(7, size, 2, xoff, yoff, "./resources/textures/icons/polygon/voxel.png", () -> {}));
	}

}
