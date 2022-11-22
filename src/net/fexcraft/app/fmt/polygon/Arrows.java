package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.lib.frl.Polyhedron;

public class Arrows {
	
	public static Polyhedron<GLObject> UP = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> DOWN = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> LEFT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> RIGHT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> FRONT = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static Polyhedron<GLObject> BACK = new Polyhedron<GLObject>().setGlObj(new GLObject());
	static {
		//
	}
	public static ArrowMode MODE = ArrowMode.POS;
	
	public static void render(){
		PolyRenderer.mode(DrawMode.RGBCOLOR);
		//
	}
	
	public static enum ArrowMode {
		
		NONE, POS, OFF, ROT, SIZE, SHAPE;
		
		public boolean active(){
			return this != NONE;
		}
	}

}
