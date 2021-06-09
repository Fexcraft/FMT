package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class MarkerComponent extends EditorComponent {
	
	public MarkerComponent(){
		super("polygon.marker", 180, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		this.add(new ColorField(this, F20, row(1), LW, HEIGHT, new PolygonValue(PolyVal.COLOR, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".biped"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F30, row(1), F3S, HEIGHT, new PolygonValue(PolyVal.BIPED, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, false, new PolygonValue(PolyVal.BIPED_ANGLE, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-128, 128, true, new PolygonValue(PolyVal.BIPED_SCALE, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".scale"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0.001f, 256, true, new PolygonValue(PolyVal.SCALE, ValAxe.N)));
		this.add(new BoolButton(this, F21, row(0), F2S, HEIGHT, new PolygonValue(PolyVal.DETACHED, ValAxe.N)));
	}

}
