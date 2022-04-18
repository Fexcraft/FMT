package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;

public class FlipTools extends EditorComponent {

	public FlipTools(){
		super("polygon.fliptools", 55, false, false);
		int yoff = HEIGHT, xoff = 4, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_lr.png", () -> FMT.MODEL.flipShapeboxes(null, 0)).addTooltip(LANG_PREFIX + id + ".flip_lr"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_ud.png", () -> FMT.MODEL.flipShapeboxes(null, 1)).addTooltip(LANG_PREFIX + id + ".flip_ud"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_fb.png", () -> FMT.MODEL.flipShapeboxes(null, 2)).addTooltip(LANG_PREFIX + id + ".flip_fb"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posx.png", () -> FMT.MODEL.flipBoxPosition(null, 0)).addTooltip(LANG_PREFIX + id + ".flip_posx"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posy.png", () -> FMT.MODEL.flipBoxPosition(null, 1)).addTooltip(LANG_PREFIX + id + ".flip_posy"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posz.png", () -> FMT.MODEL.flipBoxPosition(null, 2)).addTooltip(LANG_PREFIX + id + ".flip_posz"));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posx_fb.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 2);
			FMT.MODEL.flipBoxPosition(null, 0);
		}).addTooltip(LANG_PREFIX + id + ".flip_posx_fb"));
		this.add(new Icon(7, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posy_ud.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 1);
			FMT.MODEL.flipBoxPosition(null, 1);
		}).addTooltip(LANG_PREFIX + id + ".flip_posy_ud"));
		this.add(new Icon(8, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posz_lr.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 0);
			FMT.MODEL.flipBoxPosition(null, 2);
		}).addTooltip(LANG_PREFIX + id + ".flip_posz_lr"));
	}

}
