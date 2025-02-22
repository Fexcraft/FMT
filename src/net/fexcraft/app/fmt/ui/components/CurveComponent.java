package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import com.spinyowl.legui.component.Label;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.RectCurve;
import net.fexcraft.app.fmt.ui.PosCopyIcon;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.update.UpdateEvent;

import java.util.ArrayList;

public class CurveComponent extends EditorComponent {

	protected static final String genid = "polygon.curve";
	private static PolygonValue PLANELOC = new PolygonValue(PolyVal.PLANE_LOC, ValAxe.N);
	private static PolygonValue PLANELIT = new PolygonValue(PolyVal.PLANE_LOC_LIT, ValAxe.N);
	
	public CurveComponent(){
		super(genid, 580, false, true);
		add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Z)));
		add(new Label(translate(LANG_PREFIX + id + ".length"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0, 360, false, new PolygonValue(PolyVal.CUR_LENGTH, ValAxe.N)));
		add(new BoolButton(this, F21, row(0), F2S, HEIGHT, new PolygonValue(PolyVal.RADIAL, ValAxe.N)));
		add(new Label(translate(LANG_PREFIX + id + ".active"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_AMOUNT, ValAxe.N)).index());
		add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE, ValAxe.N)).index());
		//points
		row += 20;
		add(new Label(translate(LANG_PREFIX + id + ".points"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_POINTS, ValAxe.N)).index());
		add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_POINT, ValAxe.N)).index());
		//point pos
		add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, PolyVal.POS));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Z)));
		//point color
		add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		add(new ColorField(this, F20, row(1), LW, HEIGHT, new PolygonValue(PolyVal.COLOR, ValAxe.N)));
		//segments
		row += 20;
		add(new Label(translate(LANG_PREFIX + id + ".planes"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(2, 50, false, new PolygonValue(PolyVal.CUR_PLANES, ValAxe.N)).index());
		add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, false, new PolygonValue(PolyVal.CUR_ACTIVE_PLANES, ValAxe.N)).index());
		//seg size
		add(new Label(translate(LANG_PREFIX + genid + ".plane_size"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F20, row(1), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Y)));
		add(new NumberField(this, F21, row(0), F2S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Z)));
		//seg pos
		add(new Label(translate(LANG_PREFIX + id + ".plane_loc"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F40, row(1), F4S, HEIGHT).setup(-360, 360, true, new PolygonValue(PolyVal.PLANE_ROT, ValAxe.Y)));
		add(new NumberField(this, F41, row(0), F4S, HEIGHT).setup(0, Integer.MAX_VALUE, true, PLANELOC));
		add(new BoolButton(this, F42, row(0), F4S, HEIGHT, PLANELIT));
		add(new RunButton("R-Auto", F43, row(0), F4S, HEIGHT, () -> {
			ArrayList<Polygon> sel = FMT.MODEL.selected();
			for(Polygon poly : sel){
				if(!(poly instanceof RectCurve curv)) continue;
				int size = curv.planes.size() - 1;
				float loc = curv.dirloc ? curv.path.length / size : 1f / size;
				for(int i = 0; i < curv.planes.size(); i++){
					curv.planes.get(i).location = loc * i;//(i + 1);
				}
				curv.compath();
				curv.recompile();
				update(new UpdateEvent.PolygonValueEvent(poly, PLANELOC, true));
				update(new UpdateEvent.PolygonValueEvent(poly, PLANELIT, true));
			}
		}).addTooltip("editor.component.polygon.curve.plane_loc_reset"));
		//seg offset
		add(new Label(translate(LANG_PREFIX + genid + ".off"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, PolyVal.OFF));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Z)));
	}

}
