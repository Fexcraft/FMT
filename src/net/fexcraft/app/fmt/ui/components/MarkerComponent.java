package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.ColorField;

public class MarkerComponent extends EditorComponent {
	
	public MarkerComponent(){
		super("polygon.marker", 80, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		this.add(new ColorField(this, L5, row(1), LW, HEIGHT, new PolygonValue(PolyVal.COLOR, ValAxe.N)));
	}

}
