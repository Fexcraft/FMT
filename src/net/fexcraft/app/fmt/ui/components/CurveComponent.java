package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import com.spinyowl.legui.component.Label;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class CurveComponent extends EditorComponent {
	
	public CurveComponent(){
		super("polygon.curve", 230, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".points"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_POINTS, ValAxe.N)).index());
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_POINT, ValAxe.N)).index());
		this.add(new Label(translate(LANG_PREFIX + id + ".segments"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_SEGMENTS, ValAxe.N)).index());
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_SEGMENT, ValAxe.N)).index());
		this.add(new Label(translate(LANG_PREFIX + id + ".seg_loc"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.SEG_ROT, ValAxe.Y)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SEG_LOC, ValAxe.N)));
		this.add(new BoolButton(this, F32, row(0), F3S, HEIGHT, new PolygonValue(PolyVal.SEG_LOC_LIT, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".cur_length"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.CUR_LENGTH, ValAxe.N)));
		this.add(new BoolButton(this, F21, row(0), F2S, HEIGHT, new PolygonValue(PolyVal.RADIAL, ValAxe.N)));
	}

}
