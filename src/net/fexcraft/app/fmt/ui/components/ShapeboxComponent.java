package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.ui.ColorPanel;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.utils.CornerUtil;

public class ShapeboxComponent extends EditorComponent {
	
	public ShapeboxComponent(){
		super("polygon.shapebox", 430, false, true);
		for(int i = 0; i < 8; i++){
			this.add(new ColorPanel(L5, row(1) + 2, 18, 18, CornerUtil.CORNER_COLOURS[i]));
			this.add(new Label(translate(LANG_PREFIX + id + ".corner_" + i), L5 + 24, row(0), LW, HEIGHT));
			this.add(new NumberField(this, F30, row(1), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(Shapebox.CORNERS[i], ValAxe.X)));
			this.add(new NumberField(this, F31, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(Shapebox.CORNERS[i], ValAxe.Y)));
			this.add(new NumberField(this, F32, row(0), F3S, HEIGHT).setup(Integer.MIN_VALUE, Integer.MAX_VALUE, true, new PolygonValue(Shapebox.CORNERS[i], ValAxe.Z)));
		}
	}

}
