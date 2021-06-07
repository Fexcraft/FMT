package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;

public class CylinderComponentBasic extends EditorComponent {
	
	public CylinderComponentBasic(){
		super("polygon.cylinder.basic", 180, false, true);
		String id = "polygon.cylinder";
		this.add(new Label(translate(LANG_PREFIX + id + ".radius_length"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.RADIUS, ValAxe.N)));
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0.5f, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.LENGTH, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".seg_dir"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(3, 360, false, new PolygonValue(PolyVal.SEGMENTS, ValAxe.N)));
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, 5, false, new PolygonValue(PolyVal.DIRECTION, ValAxe.N)));
		this.add(new Label(translate(LANG_PREFIX + id + ".base_top_scale"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.BASE_SCALE, ValAxe.N)));
		this.add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TOP_OFF, ValAxe.N)));
	}

}
