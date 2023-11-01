package net.fexcraft.app.fmt.ui.editors;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.UIUtils;
import net.fexcraft.app.fmt.ui.components.*;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditor extends Editor {

	public static EditorComponent SHAPEBOX, CYLINDER, CURVE, MARKER;

	public PolygonEditor(){
		super("polygon_editor", "Polygon Editor", false);
		//if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new PolygonAndBox());
		addComponent(SHAPEBOX = new ShapeboxComponent());
		addComponent(CYLINDER = new CylinderComponentFull());
		addComponent(CURVE = new CurveComponent());
		addComponent(MARKER = new MarkerComponent());
		UpdateCompound com = new UpdateCompound();
		if(Settings.AUTO_SHOW_COMPONENTS.value){
			com.add(PolygonSelected.class, con -> {
				SHAPEBOX.minimize(true);
				CYLINDER.minimize(true);
				CURVE.minimize(true);
				MARKER.minimize(true);
				ArrayList<Polygon> polys = FMT.MODEL.selected();
				for(Polygon poly : polys){
					if(poly.getShape().isShapebox()){
						SHAPEBOX.minimize(false);
					}
					if(poly.getShape().isCylinder()){
						CYLINDER.minimize(false);
					}
					if(poly.getShape().isCurve()){
						CURVE.minimize(false);
					}
					if(poly.getShape().isMarker()){
						MARKER.minimize(false);
					}
				}
			});
			UpdateHandler.register(com);
		}
	}

}
