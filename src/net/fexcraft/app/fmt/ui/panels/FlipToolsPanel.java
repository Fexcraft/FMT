package net.fexcraft.app.fmt.ui.panels;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.ui.ToolbarMenu;
import net.fexcraft.app.fmt.ui.editors.EditorPanel;
import net.fexcraft.app.fmt.ui.fields.NumberField;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FlipToolsPanel extends EditorPanel {

	private NumberField field;

	public FlipToolsPanel(){
		super("fliptools", "fliptools", "editor.component.fliptools");
		setPosition(Editor.WIDTH, I_SIZE * 4);
		ex_x = 310;
		ex_y = 30;
		//
		int yoff = 1, xoff = 35, size = 28;
		this.add(new Icon(0, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_lr.png", () -> FMT.MODEL.flipShapeboxes(null, 0)).addTooltip(lang_prefix + ".flip_lr"));
		this.add(new Icon(1, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_ud.png", () -> FMT.MODEL.flipShapeboxes(null, 1)).addTooltip(lang_prefix + ".flip_ud"));
		this.add(new Icon(2, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_fb.png", () -> FMT.MODEL.flipShapeboxes(null, 2)).addTooltip(lang_prefix + ".flip_fb"));
		this.add(new Icon(3, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posx.png", () -> FMT.MODEL.flipBoxPosition(null, 0)).addTooltip(lang_prefix + ".flip_posx"));
		this.add(new Icon(4, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posy.png", () -> FMT.MODEL.flipBoxPosition(null, 1)).addTooltip(lang_prefix + ".flip_posy"));
		this.add(new Icon(5, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posz.png", () -> FMT.MODEL.flipBoxPosition(null, 2)).addTooltip(lang_prefix + ".flip_posz"));
		this.add(new Icon(6, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posx_fb.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 2);
			FMT.MODEL.flipBoxPosition(null, 0);
		}).addTooltip(lang_prefix + ".flip_posx_fb"));
		this.add(new Icon(7, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posy_ud.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 1);
			FMT.MODEL.flipBoxPosition(null, 1);
		}).addTooltip(lang_prefix + ".flip_posy_ud"));
		this.add(new Icon(8, size, 2, xoff, yoff, "./resources/textures/icons/polygon/flip_posz_lr.png", () -> {
			FMT.MODEL.flipShapeboxes(null, 0);
			FMT.MODEL.flipBoxPosition(null, 2);
		}).addTooltip(lang_prefix + ".flip_posz_lr"));
	}

}
