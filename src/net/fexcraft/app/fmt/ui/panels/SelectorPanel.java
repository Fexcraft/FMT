package net.fexcraft.app.fmt.ui.panels;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Selector;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SelectorPanel extends EditorPanel {

	public SelectorPanel(){
		super("selector", "selmode", "editor.component.selector");
		setPos(6);
		ex_x = 240;
		ex_y = 30;
		//
		int yoff = 1, xoff = 35, size = 28;
		add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/painter/polygon.png", () -> Selector.set(Picker.PickType.POLYGON)).addTooltip(lang_prefix + ".polygon"));
		add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/painter/face.png", () -> Selector.set(Picker.PickType.FACE)).addTooltip(lang_prefix + ".face"));
		add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/painter/pixel.png", () -> Selector.set(Picker.PickType.VERTEX)).addTooltip(lang_prefix + ".vertex"));
		add(new Icon(3, size, 2, xoff += 10, yoff, "./resources/textures/icons/component/visible.png", () -> Selector.SHOW_VERTICES = !Selector.SHOW_VERTICES).addTooltip(lang_prefix + ".visibility"));
		add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/component/remove.png", () -> FMT.MODEL.clearSelectedVerts()).addTooltip(lang_prefix + ".clear"));
		add(new Icon(5, size, 2, xoff += 10, yoff, "./resources/textures/icons/component/move_right.png", () -> Selector.move()).addTooltip(lang_prefix + ".move"));
	}

}
