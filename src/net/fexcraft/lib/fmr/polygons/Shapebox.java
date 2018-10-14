package net.fexcraft.lib.fmr.polygons;

import com.google.gson.JsonObject;

import net.fexcraft.lib.fmr.PolygonShape;
import net.fexcraft.lib.fmr.Shape;

/**
 * @author Ferdinand Calo' (FEX___96)
**/
public class Shapebox extends Cuboid {
	
	private float[][] corners;

	public Shapebox(){ super(Shape.SHAPEBOX); }
	
	public Shapebox(boolean flip, boolean mirror){ super(Shape.SHAPEBOX, flip, mirror); }
	
	public Shapebox(Shape type, boolean flip, boolean mirror, float width, float height, float depth, float posx, float posy, float posz, float offx, float offy, float offz){
		super(Shape.SHAPEBOX, flip, mirror, width, height, depth, posx, posy, posz, offx, offy, offz);
	}
	
	public Shapebox setCorners(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float x5, float y5, float z5, float x6, float y6, float z6, float x7, float y7, float z7){
		if(corners == null) setupCorners();
		corners[0] = new float[]{ x0, y0, z0 }; corners[1] = new float[]{ x1, y1, z1 };
		corners[2] = new float[]{ x2, y2, z2 }; corners[3] = new float[]{ x3, y3, z3 };
		corners[4] = new float[]{ x4, y4, z4 }; corners[5] = new float[]{ x5, y5, z5 };
		corners[6] = new float[]{ x6, y6, z6 }; corners[7] = new float[]{ x7, y7, z7 };
		return this;
	}
	
	public Shapebox setCorner(int corner, float x0, float y0, float z0){
		if(corners == null) setupCorners();
		corners[corner] = new float[]{ x0, y0, z0 };
		return this;
	}
	
	private void setupCorners(){
		corners = new float[8][];
		for(int i = 0; i < corners.length; i++){
			corners[i] = new float[]{ 0f, 0f, 0f };
		}
	}

	@Override
	public Shapebox setSize(float x, float y, float z){
		this.width = x; this.height = y; this.depth = z;
		compiled = false; return this;
	}

	@Override
	protected void populateJsonObject(JsonObject obj){
		super.populateJsonObject(obj);
		if(corners == null) return;
		for(int i = 0; i < corners.length; i++){
			if(corners[i][0] != 0f) obj.addProperty("x" + i, corners[i][0]);
			if(corners[i][1] != 0f) obj.addProperty("y" + i, corners[i][1]);
			if(corners[i][2] != 0f) obj.addProperty("z" + i, corners[i][2]);
		}
	}

	@Override
	protected PolygonShape compileShape(){
    	if(width == 0) width = 0.01F; if(height == 0) height = 0.01F; if(depth == 0) depth = 0.01F;
        float x = offsetX, y = offsetY, z = offsetZ;
    	float f4 = x + width, f5 = y + height, f6 = z + depth;
		x -= scale; y -= scale; z -= scale;
		f4 += scale; f5 += scale; f6 += scale;
		if(mirror){ float f7 = f4; f4 = x; x = f7; }
		float[] v0 = {x  - corners[0][0], y  - corners[0][1], z  - corners[0][2]};
		float[] v1 = {f4 + corners[1][0], y  - corners[1][1], z  - corners[1][2]};
		float[] v2 = {f4 + corners[5][0], f5 + corners[5][1], z  - corners[5][2]};
		float[] v3 = {x  - corners[4][0], f5 + corners[4][1], z  - corners[4][2]};
		float[] v4 = {x  - corners[3][0], y  - corners[3][1], f6 + corners[3][2]};
		float[] v5 = {f4 + corners[2][0], y  - corners[2][1], f6 + corners[2][2]};
		float[] v6 = {f4 + corners[6][0], f5 + corners[6][1], f6 + corners[6][2]};
		float[] v7 = {x  - corners[7][0], f5 + corners[7][1], f6 + corners[7][2]};
        convertToPolygons(v0, v1, v2, v3, v4, v5, v6, v7, width, height, depth);
		return this;
	}

}
