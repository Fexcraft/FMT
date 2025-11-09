package net.fexcraft.app.fmt.ui.panels;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.fields.NumberField;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class EditorsPanel extends EditorPanel {

	private NumberField field;

	public EditorsPanel(){
		super("editors", "editors", "editor.component.editors");
		setPos(0);
		ex_x = 250;
		ex_y = 30;
		//
		int yoff = 1, xoff = 35, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/editor/polygon.png", () -> Editor.POLYGON_EDITOR.show()).addTooltip(lang_prefix + ".polygon"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/editor/pivot.png", () -> Editor.PIVOT_EDITOR.show()).addTooltip(lang_prefix + ".pivot"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/editor/model.png", () -> Editor.MODEL_EDITOR.show()).addTooltip(lang_prefix + ".model"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/editor/texture.png", () -> Editor.TEXTURE_EDITOR.show()).addTooltip(lang_prefix + ".texture"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/editor/preview.png", () -> Editor.PREVIEW_EDITOR.show()).addTooltip(lang_prefix + ".preview"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/editor/animation.png", () -> Editor.ANIM_EDITOR.show()).addTooltip(lang_prefix + ".animation"));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/editor/variable.png", () -> Editor.VAR_EDITOR.show()).addTooltip(lang_prefix + ".variable"));
	}

}
