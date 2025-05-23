package net.fexcraft.app.fmt.ui.editors;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.components.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonSelected;
import net.fexcraft.app.fmt.update.UpdateEvent.VertexSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolygonEditor extends Editor {

	public static EditorComponent BOXON, BOXOFF, SHAPEBOX, CYLINDER, CURVE, MARKER, VERTOFF;

	public PolygonEditor(){
		super("polygon_editor", "Polygon Editor", false);
		addComponent(new PolygonSorting());
		addComponent(VERTOFF = new VertoffComponent());
		addComponent(BOXON = new PolygonAttributes(true));
		addComponent(BOXOFF = new PolygonAttributes(false));
		addComponent(SHAPEBOX = new ShapeboxComponent());
		addComponent(CYLINDER = new CylinderComponentFull());
		addComponent(CURVE = new CurveComponent());
		addComponent(MARKER = new MarkerComponent());
		BOXOFF.minimize(true);
		VERTOFF.minimize(true);
		UpdateCompound com = new UpdateCompound();
		if(Settings.AUTO_SHOW_COMPONENTS.value){
			com.add(VertexSelected.class, con -> {
				VERTOFF.minimize(con.selected() <= 0);
			});
			com.add(PolygonSelected.class, con -> {
				BOXON.minimize(true);
				BOXOFF.minimize(true);
				SHAPEBOX.minimize(true);
				CYLINDER.minimize(true);
				CURVE.minimize(true);
				MARKER.minimize(true);
				ArrayList<Polygon> polys = FMT.MODEL.selected();
				boolean curv = true;
				for(Polygon poly : polys){
					if(!poly.getShape().isCurve()) curv = false;
					if(!curv){
						if(poly.getShape().isRectagular()){
							BOXON.minimize(false);
						}
						else{
							BOXOFF.minimize(false);
						}
					}
					if(poly.getShape().isShapebox()){
						SHAPEBOX.minimize(false);
					}
					if(poly.getShape().isCylinder()){
						CYLINDER.minimize(false);
					}
					if(poly.getShape().isCurve()){
						CURVE.minimize(false);
					}
					if(poly.getShape().isMarker() || poly.getShape().isBoundingBox()){
						MARKER.minimize(false);
					}
				}
				if(!curv){
					if(!BOXON.minimized() && !BOXOFF.minimized()) BOXOFF.minimize(true);
					if(BOXON.minimized() && BOXOFF.minimized()) BOXOFF.minimize(false);
				}
			});
			UpdateHandler.register(com);
		}
	}

	public static boolean shrink(EditorComponent com){
		return com == BOXON || com == BOXOFF || com == SHAPEBOX || com == CYLINDER || com == CURVE || com == MARKER || com == VERTOFF;
	}

}
