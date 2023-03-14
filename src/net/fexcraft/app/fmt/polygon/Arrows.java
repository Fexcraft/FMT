package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.AxisDir;
import net.fexcraft.lib.frl.gen.Generator;

public class Arrows {
	
	public static Polyhedron<GLObject> UP = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> DOWN = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> LEFT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> RIGHT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> FRONT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> BACK = new Polyhedron<GLObject>().setGlObj(new GLObject());
	static {
		float width = 0.1f, iwidth = 0.01f, hwidth = 0.25f, length = 2f, hlength = 1f;
		int segs = 16;
		new Generator<>(DOWN)
			.set("type", Generator.Type.CYLINDER)
			.set("y", 0f)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[4])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(DOWN)
			.set("type", Generator.Type.CYLINDER)
			.set("y", length)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[4])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		DOWN.glObj.polycolor = RGB.GREEN.toFloatArray();
		DOWN.glObj.pickercolor = new RGB(1).toFloatArray();
		//
		new Generator<>(UP)
			.set("type", Generator.Type.CYLINDER)
			.set("y", -length)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[5])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(UP)
			.set("type", Generator.Type.CYLINDER)
			.set("y", -length - hlength)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[5])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		UP.glObj.polycolor = RGB.GREEN.toFloatArray();
		UP.glObj.pickercolor = new RGB(2).toFloatArray();
		//
		new Generator<>(LEFT)
			.set("type", Generator.Type.CYLINDER)
			.set("z", 0f)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[0])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(LEFT)
			.set("type", Generator.Type.CYLINDER)
			.set("z", length)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[0])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		LEFT.glObj.polycolor = RGB.BLUE.toFloatArray();
		LEFT.glObj.pickercolor = new RGB(3).toFloatArray();
		//
		new Generator<>(RIGHT)
			.set("type", Generator.Type.CYLINDER)
			.set("z", -length)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[1])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(RIGHT)
			.set("type", Generator.Type.CYLINDER)
			.set("z", -length - hlength)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[1])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		RIGHT.glObj.polycolor = RGB.BLUE.toFloatArray();
		RIGHT.glObj.pickercolor = new RGB(4).toFloatArray();
		//
		new Generator<>(FRONT)
			.set("type", Generator.Type.CYLINDER)
			.set("x", 0f)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[2])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(FRONT)
			.set("type", Generator.Type.CYLINDER)
			.set("x", length)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[2])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		FRONT.glObj.polycolor = RGB.RED.toFloatArray();
		FRONT.glObj.pickercolor = new RGB(5).toFloatArray();
		//
		new Generator<>(BACK)
			.set("type", Generator.Type.CYLINDER)
			.set("x", -length)
			.set("radius", width)
			.set("radius2", iwidth)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[3])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 1f)
			.make();
		new Generator<>(BACK)
			.set("type", Generator.Type.CYLINDER)
			.set("x", -length - hlength)
			.set("radius", hwidth)
			.set("radius2", iwidth)
			.set("length", hlength)
			.set("axis_dir", AxisDir.values()[3])
			.set("segments", segs)
			.set("base_scale", 1f)
			.set("top_scale", 0f)
			.make();
		BACK.glObj.polycolor = RGB.RED.toFloatArray();
		BACK.glObj.pickercolor = new RGB(6).toFloatArray();
	}
	public static ArrowMode MODE = ArrowMode.NONE;
	public static int SEL;
	public static boolean DIR = false;
	
	public static void render(DrawMode mode){
		Polygon poly = FMT.MODEL.first_selected();
		if(poly == null) return;
		PolyRenderer.mode(mode);
		boolean rotated = poly.rot.x != 0 || poly.rot.y != 0 || poly.rot.z != 0;
		if(poly.getShape().isBox()){
			UP.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
			RIGHT.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
			BACK.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
			if(!rotated){
				Box box = (Box)poly;
				DOWN.pos(poly.pos.x, poly.pos.y + box.size.y, poly.pos.z).render();
				LEFT.pos(poly.pos.x, poly.pos.y, poly.pos.z + box.size.z).render();
				FRONT.pos(poly.pos.x + box.size.x, poly.pos.y, poly.pos.z).render();
			}
		}
		if(poly.getShape().isShapebox()){
			Shapebox box = (Shapebox)poly;
			UP.pos(poly.pos.x - box.cor0.x, poly.pos.y - box.cor0.y, poly.pos.z - box.cor0.z).render();
			RIGHT.pos(poly.pos.x - box.cor0.x, poly.pos.y - box.cor0.y, poly.pos.z - box.cor0.z).render();
			BACK.pos(poly.pos.x - box.cor0.x, poly.pos.y - box.cor0.y, poly.pos.z - box.cor0.z).render();
			//
		}
		else if(poly.getShape().isCylinder()){
			UP.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
			RIGHT.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
			BACK.pos(poly.pos.x, poly.pos.y, poly.pos.z).render();
		}
	}
	
	public static enum ArrowMode {
		
		NONE, POS, OFF, ROT, SIZE, SHAPE;
		
		public boolean active(){
			return this != NONE;
		}
	}

	public static void mode(ArrowMode mode){
		MODE = mode;
	}
	
	public static final PolygonValue POS_X = new PolygonValue(PolyVal.POS, ValAxe.X);
	public static final PolygonValue POS_Y = new PolygonValue(PolyVal.POS, ValAxe.Y);
	public static final PolygonValue POS_Z = new PolygonValue(PolyVal.POS, ValAxe.Z);
	public static final PolygonValue OFF_X = new PolygonValue(PolyVal.OFF, ValAxe.X);
	public static final PolygonValue OFF_Y = new PolygonValue(PolyVal.OFF, ValAxe.Y);
	public static final PolygonValue OFF_Z = new PolygonValue(PolyVal.OFF, ValAxe.Z);
	public static final PolygonValue ROT_X = new PolygonValue(PolyVal.ROT, ValAxe.X);
	public static final PolygonValue ROT_Y = new PolygonValue(PolyVal.ROT, ValAxe.Y);
	public static final PolygonValue ROT_Z = new PolygonValue(PolyVal.ROT, ValAxe.Z);
	public static final PolygonValue SIZE_X = new PolygonValue(PolyVal.SIZE, ValAxe.X);
	public static final PolygonValue SIZE_Y = new PolygonValue(PolyVal.SIZE, ValAxe.Y);
	public static final PolygonValue SIZE_Z = new PolygonValue(PolyVal.SIZE, ValAxe.Z);

	public static void process(float distance){
		Polygon poly = FMT.MODEL.first_selected();
		if(poly == null) return;
		float dis = (int)distance * Editor.RATE;
		Logging.bar(dis + " " + NumberField.getFormat().format(distance));
		if(DIR) dis = -dis;
		switch(MODE){
			case POS:
				if(SEL < 3) FMT.MODEL.updateValue(POS_Y, null, dis);
				else if(SEL < 5) FMT.MODEL.updateValue(POS_Z, null, dis);
				else if(SEL < 7) FMT.MODEL.updateValue(POS_X, null, dis);
				break;
			case OFF:
				if(SEL < 3) FMT.MODEL.updateValue(OFF_Y, null, dis);
				else if(SEL < 5) FMT.MODEL.updateValue(OFF_Z, null, dis);
				else if(SEL < 7) FMT.MODEL.updateValue(OFF_X, null, dis);
				break;
			case ROT:
				if(SEL < 3) FMT.MODEL.updateValue(ROT_Y, null, dis);
				else if(SEL < 5) FMT.MODEL.updateValue(ROT_Z, null, dis);
				else if(SEL < 7) FMT.MODEL.updateValue(ROT_X, null, dis);
				break;
			case SIZE:
				if(SEL < 3) FMT.MODEL.updateValue(SIZE_Y, null, dis);
				else if(SEL < 5) FMT.MODEL.updateValue(SIZE_Z, null, dis);
				else if(SEL < 7) FMT.MODEL.updateValue(SIZE_X, null, dis);
				break;
			case SHAPE:
				break;
			case NONE:
			default: return;
			
		}
	}

}
