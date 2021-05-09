package net.fexcraft.app.fmt.utils;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.utils.MRTRenderer.DrawMode;
import net.fexcraft.app.fmt.utils.MRTRenderer.GlCache;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CornerUtil {

	protected static final ModelRendererTurbo ROT_MARKER_NORMAL = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f);
	protected static final ModelRendererTurbo ROT_MARKER_SMALL = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.2f, -.2f, -.2f, .4f, .4f, .4f);
	private static ModelRendererTurbo[] CORNER_MARKER = new ModelRendererTurbo[8];
	public static RGB[] CORNER_COLOURS = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 127, 255), new RGB(255, 0, 127),
		new RGB(0, 255, 0), new RGB(0, 0, 255), new RGB(0, 127, 0), new RGB(127, 0, 255)
	};
	private static Axis3DL axe = new Axis3DL();
	static{
		for(int i = 0; i < 8; i++){
			CORNER_MARKER[i] = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f);
			CORNER_MARKER[i].glObject(new GlCache()).polycolor = CORNER_COLOURS[i].toFloatArray();
		}
		ROT_MARKER_NORMAL.glObject(new GlCache()).polycolor = RGB.GREEN.toFloatArray();
		ROT_MARKER_SMALL.glObject(new GlCache()).polycolor = RGB.WHITE.toFloatArray();
	}

	public static void renderCorners(){
		Shapebox box = (Shapebox)FMT.MODEL.last_selected();
		MRTRenderer.mode(DrawMode.RGBCOLOR);
		for(int i = 0; i < CORNER_MARKER.length; i++){
			ROT_MARKER_SMALL.setRotationPoint(box.pos.x, box.pos.y, box.pos.z);
			ROT_MARKER_SMALL.render();
			axe.setAngles(-box.rot.y, -box.rot.z, -box.rot.x);
			Vector3f vector = null;
			for(int j = 0; j < CORNER_MARKER.length; j++){
				vector = axe.getRelativeVector(corneroffset(box, j).add(box.off));
				CORNER_MARKER[j].setPosition(vector.x + box.pos.x, vector.y + box.pos.y, vector.z + box.pos.z);
				CORNER_MARKER[j].setRotationAngle(box.rot.x, box.rot.y, box.rot.z);
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
