package net.fexcraft.app.fmt.demo;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Marker;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;

import java.util.ArrayList;
import java.util.Arrays;

import static net.fexcraft.lib.frl.gen.Generator.Values.*;

public class ModelMark {
	
	private Polyhedron[] poly = new Polyhedron[6];
	private Polyhedron mmark = new Polyhedron();
	private static float shrink = 1.875f * 0.5f;
	
	public void fill(Marker mark){
		if(poly[0] == null){
			for(int i = 0; i < poly.length; i++){
				poly[i] = new Polyhedron();
			}
		}
		for(int i = 0; i < poly.length; i++){
			poly[i].recompile = true;
			poly[i].clear();
			poly[i].pos(0, 0, 0);
		}
		float s = mark.biped_scale * shrink;
		//
		poly[0].ruv(0, 17).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, -2f).set(OFF_Y, -2f).set(OFF_Z, -12f)
			.set(WIDTH, 4f).set(HEIGHT, 4f).set(DEPTH, 12f)
			.set(SCALE, s).make()
			.pos(2, 2, 0).rot(-15, -15, 0);
		poly[1].ruv(33, 22).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, -4f).set(OFF_Y, 0f).set(OFF_Z, -2f)
			.set(WIDTH, 8f).set(HEIGHT, 12f).set(DEPTH, 4f)
			.set(SCALE, s).make()
			.pos(0, 2, 0).rot(0, 0, 0);
		poly[2].ruv(21, 5).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, -2f).set(OFF_Y, -2f).set(OFF_Z, -12f)
			.set(WIDTH, 4f).set(HEIGHT, 4f).set(DEPTH, 12f)
			.set(SCALE, s).make()
			.pos(-2, 2, 0).rot(-15, 15, 0);
		poly[3].ruv(0, 0).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, -4f).set(OFF_Y, 0f).set(OFF_Z, -4f)
			.set(WIDTH, 8f).set(HEIGHT, 8f).set(DEPTH, 8f)
			.set(SCALE, s).make()
			.pos(0, 14, 0).rot(0, 0, 0);
		poly[4].ruv(0, 34).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, -4f).set(OFF_Y, -2f).set(OFF_Z, -2f)
			.set(WIDTH, 4f).set(HEIGHT, 12f).set(DEPTH, 4f)
			.set(SCALE, s).make()
			.pos(-4, 12, 0).rot(-130, 0, 0);
		poly[5].ruv(42, 0).newGen().set(TYPE, Generator.Type.CUBOID).set(TEXTURE_WIDTH, 64f).set(TEXTURE_HEIGHT, 64f)
			.set(OFF_X, 0f).set(OFF_Y, -2f).set(OFF_Z, -2f)
			.set(WIDTH, 4f).set(HEIGHT, 12f).set(DEPTH, 4f)
			.set(SCALE, s).make()
			.pos(4, 12, 0).rot(-130, 0, 0);
		//
		mmark.sub = new ArrayList<>();
		mmark.sub.addAll(Arrays.asList(poly));
		mmark.posX = mark.pos.x;
		mmark.posY = mark.pos.y;
		mmark.posZ = mark.pos.z;
		mmark.rotY = mark.angle;
		mmark.rotZ = FMT.MODEL.orient.rect() ? 0 : 180;
	}

	public void render(){
		mmark.render();
	}

}
