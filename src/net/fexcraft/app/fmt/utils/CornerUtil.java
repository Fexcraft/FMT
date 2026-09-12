package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.ui.editor.EditorRoot;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;

import static net.fexcraft.lib.frl.gen.Generator.Values.*;

public class CornerUtil {

	public static Polyhedron ROT_MARKER_NORMAL;
	public static Polyhedron ROT_MARKER_SMALL;
	public static RGB[] CORNER_COLOURS = new RGB[]{
		new RGB(255, 255, 0),//yellow
		new RGB(255, 0, 0),//red
		new RGB(0, 127, 255),//cyan
		new RGB(255, 0, 127),//magenta
		new RGB(0, 255, 0),//green
		new RGB(0, 0, 255),//blue
		new RGB(0, 127, 0),//dark green
		new RGB(127, 0, 255)//purple
	};
	static{
		compile();
	}

	public static void compile(){
		if(ROT_MARKER_NORMAL != null){
			ROT_MARKER_NORMAL.delete();
			ROT_MARKER_SMALL.delete();
		}
		ROT_MARKER_NORMAL = new Generator(Generator.Type.CUBOID)
			.set(OFF_X, -.25f).set(OFF_Y, -.25f).set(OFF_Z, -.25f)
			.set(WIDTH, .5f).set(HEIGHT, .5f).set(DEPTH, .5f)
			.set(SCALE, EditorRoot.MARKER_SCALE).make();
		ROT_MARKER_SMALL = new Generator(Generator.Type.CUBOID)
			.set(OFF_X, -.2f).set(OFF_Y, -.2f).set(OFF_Z, -.2f)
			.set(WIDTH, .4f).set(HEIGHT, .4f).set(DEPTH, .4f)
			.set(SCALE, EditorRoot.MARKER_SCALE * 0.5f).make();
		ROT_MARKER_NORMAL.glObj(GLObject.class).polycolor = RGB.GREEN.toFloatArray();
		ROT_MARKER_SMALL.glObj(GLObject.class).polycolor = RGB.WHITE.toFloatArray();
	}

}
