package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.ui.Editor;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CornerUtil {

	public static Polyhedron<GLObject> ROT_MARKER_NORMAL;
	public static Polyhedron<GLObject> ROT_MARKER_SMALL;
	private static Polyhedron<GLObject>[] CORNER_MARKER = new Polyhedron[8];
	public static RGB[] CORNER_COLOURS = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 127, 255), new RGB(255, 0, 127),
		new RGB(0, 255, 0), new RGB(0, 0, 255), new RGB(0, 127, 0), new RGB(127, 0, 255)
	};
	private static Axis3DL axe = new Axis3DL();
	static{
		compile();
	}

	public static void compile(){
		if(ROT_MARKER_NORMAL != null){
			PolyRenderer.RENDERER.delete(ROT_MARKER_NORMAL);
			PolyRenderer.RENDERER.delete(ROT_MARKER_SMALL);
			for(Polyhedron<GLObject> poly : CORNER_MARKER){
				PolyRenderer.RENDERER.delete(poly);
			}
		}
		ROT_MARKER_NORMAL = new Polyhedron<GLObject>().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f), false, Editor.MARKER_SCALE);
		ROT_MARKER_SMALL = new Polyhedron<GLObject>().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.2f, -.2f, -.2f, .4f, .4f, .4f), false, Editor.MARKER_SCALE * 0.5f);
		for(int i = 0; i < 8; i++){
			CORNER_MARKER[i] = new Polyhedron<GLObject>().importMRT(new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f), false, Editor.MARKER_SCALE);
			CORNER_MARKER[i].setGlObj(new GLObject()).glObj.polycolor = CORNER_COLOURS[i].toFloatArray();
		}
		ROT_MARKER_NORMAL.setGlObj(new GLObject()).glObj.polycolor = RGB.GREEN.toFloatArray();
		ROT_MARKER_SMALL.setGlObj(new GLObject()).glObj.polycolor = RGB.WHITE.toFloatArray();
	}

	public static void renderCorners(){
		Shapebox box = (Shapebox)FMT.MODEL.last_selected();
		PolyRenderer.mode(DrawMode.RGBCOLOR);
		for(int i = 0; i < CORNER_MARKER.length; i++){
			ROT_MARKER_SMALL.pos(box.pos.x, box.pos.y, box.pos.z);
			ROT_MARKER_SMALL.render();
			axe.setAngles(-box.rot.y, -box.rot.z, -box.rot.x);
			Vector3f vector = null;
			for(int j = 0; j < CORNER_MARKER.length; j++){
				vector = axe.getRelativeVector(corneroffset(box, j).add(box.off));
				CORNER_MARKER[j].pos(vector.x + box.pos.x, vector.y + box.pos.y, vector.z + box.pos.z);
				CORNER_MARKER[j].rot(box.rot.x, box.rot.y, box.rot.z);
				CORNER_MARKER[j].render();
			}
		}
	}

	private static Vector3f corneroffset(Shapebox box, int index){
		switch(index){
			case 0: return new Vector3f(-box.cor0.x, -box.cor0.y, -box.cor0.z);
			case 1: return new Vector3f( box.cor1.x + box.size.x, -box.cor1.y, -box.cor1.z);
			case 2: return new Vector3f( box.cor2.x + box.size.x, -box.cor2.y,  box.cor2.z + box.size.z);
			case 3: return new Vector3f(-box.cor3.x, -box.cor3.y,  box.cor3.z + box.size.z);
			case 4: return new Vector3f(-box.cor4.x,  box.cor4.y + box.size.y, -box.cor4.z);
			case 5: return new Vector3f( box.cor5.x + box.size.x,  box.cor5.y + box.size.y, -box.cor5.z);
			case 6: return new Vector3f( box.cor6.x + box.size.x,  box.cor6.y + box.size.y,  box.cor6.z + box.size.z);
			case 7: return new Vector3f(-box.cor7.x,  box.cor7.y + box.size.y,  box.cor7.z + box.size.z);
			default: return null;
		}
	}

}
