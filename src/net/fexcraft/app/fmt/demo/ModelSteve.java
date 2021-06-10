package net.fexcraft.app.fmt.demo;

import net.fexcraft.app.fmt.polygon.Marker;
import net.fexcraft.app.fmt.utils.MRTRenderer.GlCache;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.TexturedVertex;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ModelSteve {
	
	private ModelRendererTurbo[] marker = new ModelRendererTurbo[6];
	private static float shrink = 1.875f * 0.5f;
	
	public void fill(Marker mark){
		if(marker[0] == null){
			for(int i = 0; i < marker.length; i++) marker[i] = new ModelRendererTurbo(this, 4, 4, 64, 64);
		}
		for(int i = 0; i < marker.length; i++){
			marker[i].forcedRecompile = true;
			marker[i].clear();
			marker[i].rotationAngleY = 0;
			marker[i].setRotationPoint(0, 0, 0);
		}
		float s = mark.biped_scale * shrink;
		marker[0].addBox(-4,   -12,    -2, 8, 12, 4).setTextureOffset(16, 16);
		marker[1].addBox(-4,     0,    -4, 8,  8, 8).setTextureOffset( 0,  0).setRotationPoint(0, -20 * s, 0).setRotationAngle(0, 0, 0);
		marker[2].addBox( 4, -0.4f, -0.9f, 4, 12, 4).setTextureOffset(32, 48).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0);
		marker[3].addBox(-8, -0.4f, -0.9f, 4, 12, 4).setTextureOffset(40, 16).setRotationPoint(0, -12 * s, 0).setRotationAngle(-35, 0, 0);
		marker[4].addBox(-4,     0,    -4, 4, 12, 4).setTextureOffset( 0, 16).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, 14.5f, 0);
		marker[5].addBox( 0,     0,    -4, 4, 12, 4).setTextureOffset(16, 48).setRotationPoint(0, 2 * s, 0).setRotationAngle(-83, -14.5f, 0);
		for(ModelRendererTurbo turbo : marker){
			if(turbo.glObject() == null) turbo.glObject(new GlCache());
			turbo.setTextured(true);
			for(TexturedPolygon poly : turbo.getFaces()){
				for(TexturedVertex vert : poly.getVertices()){
					vert.vector = vert.vector.scale(s);
				}
			}
			turbo.rotationPointX += mark.pos.x;
			turbo.rotationPointY += mark.pos.y;
			turbo.rotationPointZ += mark.pos.z;
			turbo.rotationAngleY += mark.angle;
		}
	}

	public void render(){
		for(ModelRendererTurbo turbo : marker) turbo.render();
	}

}
