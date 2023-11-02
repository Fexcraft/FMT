package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonAttributes extends EditorComponent {
	
	private SelectBox<String> box = new SelectBox<>(), type = new SelectBox<>();
	private static final String NOGROUPS = "< no groups >";
	private static final String NOPOLYSEL = "< no polygon selected >";
	protected static final String genid = "polygon.general";
	private TextField name;
	private NumberField TX, TY;
	
	public PolygonAttributes(){
		this(330);
	}

	public PolygonAttributes(int height){
		super(genid, height, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".box_size"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Z)));
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".off"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Z)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex"), L5, row(1), LW, HEIGHT));
		this.add(TX = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.X)));
		this.add(TY = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.Y)));
		this.add(new RunButton(LANG_PREFIX + genid + ".tex_reset", F32, row(0), F3S, HEIGHT, () -> resetUV()));
		//
		this.add(new Label(translate(LANG_PREFIX + genid + ".box_face_vis"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F30, row(1), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X)));
		this.add(new BoolButton(this, F61, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y)));
		this.add(new BoolButton(this, F62, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z)));
		this.add(new BoolButton(this, F63, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X2)));
		this.add(new BoolButton(this, F64, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y2)));
		this.add(new BoolButton(this, F65, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z2)));
	}

	private void resetUV(){
		FMT.MODEL.updateValue(TX.polyval(), TX.apply(-1), 0);
		FMT.MODEL.updateValue(TY.polyval(), TY.apply(-1), 0);
	}

}
