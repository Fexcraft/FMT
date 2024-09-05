package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.PosCopyIcon;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Panel;
import com.spinyowl.legui.component.SelectBox;

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

	public PolygonAttributes(boolean box){
		super(genid, box ? 330 : 230, false, true);
		if(box) addBoxSize();
		//
		add(new Label(translate(LANG_PREFIX + genid + ".pos"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, PolyVal.POS));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.POS, ValAxe.Z)));
		add(new Label(translate(LANG_PREFIX + genid + ".off"), L5, row(1), LWI, HEIGHT));
		add(new PosCopyIcon(LPI, row(0) + 4, PolyVal.OFF));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.OFF, ValAxe.Z)));
		add(new Label(translate(LANG_PREFIX + genid + ".rot"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(-180, 180, true, new PolygonValue(PolyVal.ROT, ValAxe.Z)));
		add(new Label(translate(LANG_PREFIX + genid + ".tex"), L5, row(1), LW, HEIGHT));
		add(TX = new NumberField(this, F30, row(1), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.X)));
		add(TY = new NumberField(this, F31, row(0), F3S, HEIGHT).setup(-1, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.TEX, ValAxe.Y)));
		add(new RunButton(LANG_PREFIX + genid + ".tex_reset", F32, row(0), F3S, HEIGHT, () -> resetUV()));
		//
		if(box) addBoxExtra();
	}

	private void addBoxSize(){
		add(new Label(translate(LANG_PREFIX + genid + ".box_size"), L5, row(1), LW, HEIGHT));
		add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.X)));
		add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Y)));
		add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(0, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Z)));
	}

	private void addBoxExtra(){
		add(new Label(translate(LANG_PREFIX + genid + ".box_face_vis"), L5, row(1), LW, HEIGHT));
		add(new BoolButton(this, F30, row(1), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X)));
		add(new BoolButton(this, F61, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y)));
		add(new BoolButton(this, F62, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z)));
		add(new BoolButton(this, F63, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X2)));
		add(new BoolButton(this, F64, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y2)));
		add(new BoolButton(this, F65, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z2)));
	}

	private void resetUV(){
		FMT.MODEL.updateValue(TX.polyval(), TX.apply(-1), 0);
		FMT.MODEL.updateValue(TY.polyval(), TY.apply(-1), 0);
	}

}
