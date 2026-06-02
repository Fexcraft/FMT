package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.ui.editor.EditorRoot;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CornerUtil {

	public static Polyhedron ROT_MARKER_NORMAL;
	public static Polyhedron ROT_MARKER_SMALL;
	//private static Polyhedron[] CORNER_MARKER = new Polyhedron[8];
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
			/*for(Polyhedron poly : CORNER_MARKER){
				poly.delete();
			}*/
		}
		ROT_MARKER_NORMAL = new Polyhedron().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f), false, EditorRoot.MARKER_SCALE);
		ROT_MARKER_SMALL = new Polyhedron().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.2f, -.2f, -.2f, .4f, .4f, .4f), false, EditorRoot.MARKER_SCALE * 0.5f);
		/*for(int i = 0; i < 8; i++){
			CORNER_MARKER[i] = new Polyhedron().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f), false, EditorRoot.MARKER_SCALE);
			CORNER_MARKER[i].glObj(GLObject.class).polycolor = CORNER_COLOURS[i].toFloatArray();
		}*/
		ROT_MARKER_NORMAL.glObj(GLObject.class).polycolor = RGB.GREEN.toFloatArray();
		ROT_MARKER_SMALL.glObj(GLObject.class).polycolor = RGB.WHITE.toFloatArray();
	}

}
