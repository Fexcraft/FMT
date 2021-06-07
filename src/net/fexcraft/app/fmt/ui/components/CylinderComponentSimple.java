package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class CylinderComponentSimple extends EditorComponent {
	
	public CylinderComponentSimple(){
		super("polygon.cylinder.simple", 230, false, true);
		String id = "polygon.cylinder";
		this.add(new Label(translate(LANG_PREFIX + id + ".radius_length_radius2"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.LENGTH, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS2, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".seg_dir_limit"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(3, 360, false, new PolygonValue(PolyVal.SEGMENTS, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, 5, false, new PolygonValue(PolyVal.DIRECTION, ValAxe.N)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.SEG_LIMIT, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".base_top_scale"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.BASE_SCALE, ValAxe.N)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_OFF, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".face_vis"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F60, row(1), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X)));
		this.add(new BoolButton(this, F61, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y)));
		this.add(new BoolButton(this, F62, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z)));
		this.add(new BoolButton(this, F63, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X2)));
		this.add(new BoolButton(this, F64, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y2)));
		this.add(new BoolButton(this, F65, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z2)));
	}

}
