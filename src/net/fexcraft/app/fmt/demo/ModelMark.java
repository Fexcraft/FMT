package net.fexcraft.app.fmt.demo;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Marker;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

import java.util.ArrayList;
import java.util.Arrays;

public class ModelMark {
	
	private Polyhedron<GLObject>[] poly = new Polyhedron[6];
	private Polyhedron<GLObject> mmark = new Polyhedron();
	private static float shrink = 1.875f * 0.5f;
	
	public void fill(Marker mark){
		if(poly[0] == null){
			for(int i = 0; i < poly.length; i++){
				poly[i] = new Polyhedron<>();
			}
		}
		for(int i = 0; i < poly.length; i++){
			poly[i].recompile = true;
			poly[i].clear();
			poly[i].pos(0, 0, 0);
		}
		float s = mark.biped_scale * shrink;
		if(FMT.MODEL.orient.rect()){
			poly[0].importMRT(new ModelRendererTurbo(null, 0, 17, 64, 64).addBox(-2, -2, -12, 4, 4, 12).setRotationPoint(2, 2, 0).setRotationAngle(-15, -15, 0), false, s);
			poly[1].importMRT(new ModelRendererTurbo(null, 33, 22, 64, 64).addBox(-4, 0, -2, 8, 12, 4).setRotationPoint(0, 2, 0).setRotationAngle(0, 0, 0), false, s);
			poly[2].importMRT(new ModelRendererTurbo(null, 21, 5, 64, 64).addBox(-2, -2, -12, 4, 4, 12).setRotationPoint(-2, 2, 0).setRotationAngle(-15, 15, 0), false, s);
			poly[3].importMRT(new ModelRendererTurbo(null, 0, 0, 64, 64).addBox(-4, 0, -4, 8, 8, 8).setRotationPoint(0, 14, 0).setRotationAngle(0, 0, 0), false, s);
			poly[4].importMRT(new ModelRendererTurbo(null, 0, 34, 64, 64).addBox(-4, -2, -2, 4, 12, 4).setRotationPoint(-4, 12, 0).setRotationAngle(-130, 0, 0), false, s);
			poly[5].importMRT(new ModelRendererTurbo(null, 42, 0, 64, 64).addBox(0, -2, -2, 4, 12, 4).setRotationPoint(4, 12, 0).setRotationAngle(-130, 0, 0), false, s);
		}
		else{
			poly[0].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(16, 16).addBox(-4,   -12,    -2, 8, 12, 4), false, s);
			poly[1].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset( 0,  0).addBox(-4,     0,    -4, 8,  8, 8).setRotationPoint(0, -20 * s, 0).setRotationAngle(0, 0, 0), false, s);
			poly[2].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(32, 48).addBox( 4, -0.4f, -0.9f, 4, 12, 4).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0), false, s);
			poly[3].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(40, 16).addBox(-8, -0.4f, -0.9f, 4, 12, 4).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0), false, s);
			poly[4].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset( 0, 16).addBox(-4,     0,    -4, 4, 12, 4).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, 14.5f, 0), false, s);
			poly[5].importMRT(new ModelRendererTurbo(null, 4, 4, 64, 64).setTextureOffset(16, 48).addBox( 0,     0,    -4, 4, 12, 4).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, -14.5f, 0), false, s);
		}
		mmark.sub = new ArrayList<>();
		mmark.sub.addAll(Arrays.asList(poly));
		mmark.posX = mark.pos.x;
		mmark.posY = mark.pos.y;
		mmark.posZ = mark.pos.z;
		mmark.rotY = mark.angle;
	}

	public void render(){
		mmark.render();
	}

}
