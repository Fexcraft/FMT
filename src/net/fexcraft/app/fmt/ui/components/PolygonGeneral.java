package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fieds.NumberField;

public class PolygonGeneral extends EditorComponent {

	public PolygonGeneral(){
		super("polygon.general", 200, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".pos"), L5, HEIGHT + R0, LW, HEIGHT));
		this.add(new NumberField(F30, HEIGHT + R1, 90, HEIGHT));
		this.add(new NumberField(F31, HEIGHT + R1, 90, HEIGHT));
		this.add(new NumberField(F32, HEIGHT + R1, 90, HEIGHT));
		this.add(new Label(translate(LANG_PREFIX + id + ".off"), L5, HEIGHT + R2, LW, HEIGHT));
		this.add(new NumberField(F30, HEIGHT + R3, 90, HEIGHT));
		this.add(new NumberField(F31, HEIGHT + R3, 90, HEIGHT));
		this.add(new NumberField(F32, HEIGHT + R3, 90, HEIGHT));
		this.add(new Label(translate(LANG_PREFIX + id + ".rot"), L5, HEIGHT + R4, LW, HEIGHT));
		this.add(new NumberField(F30, HEIGHT + R5, 90, HEIGHT));
		this.add(new NumberField(F31, HEIGHT + R5, 90, HEIGHT));
		this.add(new NumberField(F32, HEIGHT + R5, 90, HEIGHT));
	}

}
