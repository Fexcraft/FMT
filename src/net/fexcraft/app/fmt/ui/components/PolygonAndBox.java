package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField;

public class PolygonAndBox extends PolygonGeneral {
	
	private SelectBox<String> box = new SelectBox<>(), type = new SelectBox<>();
	private static final String NOGROUPS = "< no groups >";
	private static final String NOPOLYSEL = "< no polygon selected >";
	protected static final String genid = "polygon.general";
	private TextField name;
	private NumberField TX, TY;
	
	public PolygonAndBox(){
		this(480);
	}

	public PolygonAndBox(int height){
		super(null, height);
		addSortingFields();
		this.add(new Label(translate(LANG_PREFIX + "polygon.box" + ".size"), L5, row(1), LW, HEIGHT));
		this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.X)));
		this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Y)));
		this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(PolyVal.SIZE, ValAxe.Z)));
		addGeneralFields();
		this.add(new Label(translate(LANG_PREFIX + "polygon.box" + ".face_vis"), L5, row(1), LW, HEIGHT));
		this.add(new BoolButton(this, F30, row(1), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X)));
		this.add(new BoolButton(this, F61, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y)));
		this.add(new BoolButton(this, F62, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z)));
		this.add(new BoolButton(this, F63, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.X2)));
		this.add(new BoolButton(this, F64, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Y2)));
		this.add(new BoolButton(this, F65, row(0), F6S, HEIGHT, new PolygonValue(PolyVal.SIDES, ValAxe.Z2)));
	}

}
