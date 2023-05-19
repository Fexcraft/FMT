package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class CylinderComponentFull extends EditorComponent {
	
	public CylinderComponentFull(){
		super("polygon.cylinder.all", 380, false, true);
		String id = "polygon.cylinder";
		this.add(new Label(translate(LANG_PREFIX + id + ".radius"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F40, row(1), F4S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS, ValAxe.X)));
		this.add(new NumberField(this, F41, row(0), F4S, HEIGHT).setup(0f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS, ValAxe.Y)));
		this.add(new NumberField(this, F42, row(0), F4S, HEIGHT).setup(0f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS2, ValAxe.X)));
		this.add(new NumberField(this, F43, row(0), F4S, HEIGHT).setup(0f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS2, ValAxe.Y)));
		this.add(new Label(translate(LANG_PREFIX + id + ".length"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.LENGTH, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".seg_limit_rot"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F40, row(1), F4S, HEIGHT).setup(3, 360, false, new PolygonValue(PolyVal.SEGMENTS, ValAxe.N)).index());
		this.add(new NumberField(this, F41, row(0), F4S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.SEG_LIMIT, ValAxe.N)).index());
		this.add(new NumberField(this, F42, row(0), F4S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.SEG_OFF, ValAxe.X)));
		this.add(new NumberField(this, F43, row(0), F4S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.SEG_OFF, ValAxe.Y)));
		this.add(new Label(translate(LANG_PREFIX + id + ".base_top_dir"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.BASE_SCALE, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_SCALE, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0, 5, false, new PolygonValue(PolyVal.DIRECTION, ValAxe.N)).index());
		this.add(new Label(translate(LANG_PREFIX + id + ".top_off"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_OFF, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_OFF, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_OFF, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + id + ".top_rot"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_ROT, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_ROT, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_ROT, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + id + ".face_vis"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F60, row(1), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X)));
		this.add(new BoolButton(this, F61, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y)));
		this.add(new BoolButton(this, F62, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z)));
		this.add(new BoolButton(this, F63, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X2)));
		this.add(new BoolButton(this, F64, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y2)));
		this.add(new BoolButton(this, F65, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z2)));
		this.add(new Label(translate(LANG_PREFIX + id + ".radial"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F30, row(1), F3S, HEIGHT, new PolygonValue(PolyVal.RADIAL, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.SEG_WIDTH, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.SEG_HEIGHT, ValAxe.N)));
	}

}
