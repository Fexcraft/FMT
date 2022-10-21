package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class CurveComponent extends EditorComponent {
	
	public CurveComponent(){
		super("polygon.curve", 180, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".points"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_POINTS, ValAxe.N)));
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_POINT, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".segments"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_SEGMENTS, ValAxe.N)));
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_SEGMENT, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".seg_loc"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.SEG_ROT, ValAxe.Y)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.SEG_LOC, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.CUR_LENGTH, ValAxe.N)));
	}

}
