package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
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
		//
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
	}
	public static ArrowMode MODE = ArrowMode.POS;
	
	public static void render(){
		PolyRenderer.mode(DrawMode.RGBCOLOR);
		UP.render();
		DOWN.render();
		LEFT.render();
		RIGHT.render();
		FRONT.render();
		BACK.render();
	}
	
	public static enum ArrowMode {
		
		NONE, POS, OFF, SIZE, SHAPE;
		
		public boolean active(){
			return this != NONE;
		}
	}

}
