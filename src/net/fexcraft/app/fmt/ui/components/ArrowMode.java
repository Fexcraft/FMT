package net.fexcraft.app.fmt.ui.components;

import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;

public class ArrowMode extends EditorComponent {

	public ArrowMode(){
		super("arrow.mode", 55, false, false);
		int yoff = HEIGHT, xoff = 4, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_none.png", () -> Arrows.mode(Arrows.ArrowMode.NONE)).addTooltip(LANG_PREFIX + id + ".none"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_pos.png", () -> Arrows.mode(Arrows.ArrowMode.POS)).addTooltip(LANG_PREFIX + id + ".pos"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_off.png", () -> Arrows.mode(Arrows.ArrowMode.OFF)).addTooltip(LANG_PREFIX + id + ".off"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_rot.png", () -> Arrows.mode(Arrows.ArrowMode.ROT)).addTooltip(LANG_PREFIX + id + ".rot"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_size.png", () -> Arrows.mode(Arrows.ArrowMode.SIZE)).addTooltip(LANG_PREFIX + id + ".size"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/arrow/mode_shape.png", () -> Arrows.mode(Arrows.ArrowMode.SHAPE)).addTooltip(LANG_PREFIX + id + ".shape"));
	}

}
