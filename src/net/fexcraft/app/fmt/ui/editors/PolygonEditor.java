package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.BoxComponent;
import net.fexcraft.app.fmt.ui.components.CurveComponent;
import net.fexcraft.app.fmt.ui.components.CylinderComponentFull;
import net.fexcraft.app.fmt.ui.components.MarkerComponent;
import net.fexcraft.app.fmt.ui.components.MultiplierComponent;
import net.fexcraft.app.fmt.ui.components.PolygonGeneral;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.ui.components.ShapeboxComponent;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditor extends Editor {

	public PolygonEditor(){
		super("polygon_editor", "Polygon Editor", false);
		if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new PolygonGeneral());
		addComponent(new BoxComponent());
		addComponent(new ShapeboxComponent());
		addComponent(new CylinderComponentFull());
		addComponent(new CurveComponent());
		addComponent(new MarkerComponent());
	}

}
