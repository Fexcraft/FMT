package net.fexcraft.app.fmt.demo;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Marker;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ModelMark {
	
	private Polyhedron<GLObject>[] poly = new Polyhedron[6];
	private static float shrink = 1.875f * 0.5f;
	
	public void fill(Marker mark){
		if(poly[0] == null){
			for(int i = 0; i < poly.length; i++){
				poly[i] = new Polyhedron<>();
				poly[i].setGlObj(new GLObject());
			}
		}
		for(int i = 0; i < poly.length; i++){
			poly[i].recompile = true;
			poly[i].clear();
			poly[i].rotY = 0;
			poly[i].pos(0, 0, 0);
		}
		float s = mark.biped_scale * shrink;
		poly[0].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(16, 16).addBox(-4,   -12,    -2, 8, 12, 4), false, s);
		poly[1].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset( 0,  0).addBox(-4,     0,    -4, 8,  8, 8).setRotationPoint(0, -20 * s, 0).setRotationAngle(0, 0, 0), false, s);
		poly[2].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(32, 48).addBox( 4, -0.4f, -0.9f, 4, 12, 4).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0), false, s);
		poly[3].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(40, 16).addBox(-8, -0.4f, -0.9f, 4, 12, 4).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0), false, s);
		poly[4].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset( 0, 16).addBox(-4,     0,    -4, 4, 12, 4).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, 14.5f, 0), false, s);
		poly[5].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(16, 48).addBox( 0,     0,    -4, 4, 12, 4).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, -14.5f, 0), false, s);
		for(Polyhedron<GLObject> hed : poly){
			hed.posX += mark.pos.x;
			hed.posY += mark.pos.y;
			hed.posY += mark.pos.z;
			hed.rotY += mark.angle;
		}
	}

	public void render(){
		for(Polyhedron<GLObject> hed : poly) hed.render();
	}

}
