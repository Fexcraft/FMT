package net.fexcraft.lib.fmr.polygons;

import com.google.gson.JsonObject;

import net.fexcraft.lib.fmr.PolygonShape;
import net.fexcraft.lib.fmr.Shape;
import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

/**
 * @author Ferdinand Calo' (FEX___96)
**/
public class Cuboid extends PolygonShape {
	
	protected float width;
	protected float height;
	protected float depth;

	public Cuboid(Shape type){
		super(type = type == null ? Shape.BOX : type);
		if(!type.isCuboid()){
			//Static.exception(new Exception(String.format("Invalid usage of CuboidShape! Wrong PolygonType: %s", type.name())), true);
		}
	}
	
	public Cuboid(Shape type, boolean flip, boolean mirror){
		super(type = type == null ? Shape.BOX : type, flip, mirror);
		if(!type.isCuboid()){
			//Static.exception(new Exception(String.format("Invalid usage of CuboidShape! Wrong PolygonType: %s", type.name())), true);
		}
	}
	
	public Cuboid(){ this(Shape.BOX); }
	
	public Cuboid(boolean flip, boolean mirror){ this(Shape.BOX, flip, mirror); }
	
	public Cuboid(Shape type, boolean flip, boolean mirror, float width, float height, float depth, float posx, float posy, float posz, float offx, float offy, float offz){
		super(type = type == null ? Shape.BOX : type, flip, mirror);
		this.setSize(width, height, depth).setPosition(posx, posy, posz).setOffset(offx, offy, offx);
		if(!type.isCuboid()){
			//Static.exception(new Exception(String.format("Invalid usage of CuboidShape! Wrong PolygonType: %s", type.name())), true);
		}
	}
	
	public Cuboid setSize(float x, float y, float z){
		this.width = x; this.height = y; this.depth = z;
		compiled = false; return this;
	}

	@Override
	protected void populateJsonObject(JsonObject obj){
		obj.addProperty("width", width);
		obj.addProperty("height", height);
		obj.addProperty("depth", depth);
	}

	@Override
	protected PolygonShape compileShape(){
    	if(width == 0) width = 0.01F; if(height == 0) height = 0.01F; if(depth == 0) depth = 0.01F;
        float scaleX = width * scale, scaleY = height * scale, scaleZ = depth * scale;
        float x = offsetX, y = offsetY, z = offsetZ;
        float x1 = x + scaleX, y1 = y + scaleY, z1 = z + scaleZ;
        x -= scaleX - width; y -= scaleY - height; z -= scaleZ - depth;
        if(mirror){ float xt = x1; x1 = x; x = xt; }
        //
        float[] v0 = {  x,  y,  z }; float[] v1 = { x1,  y,  z }; float[] v2 = { x1, y1,  z }; float[] v3 = {  x, y1,  z };
        float[] v4 = {  x,  y, z1 }; float[] v5 = { x1,  y, z1 }; float[] v6 = { x1, y1, z1 }; float[] v7 = {  x, y1, z1 };
        convertToPolygons(v0, v1, v2, v3, v4, v5, v6, v7, width, height, depth);
		return this;
	}
	
	protected void convertToPolygons(float[] v0, float[] v1, float[] v2, float[] v3, float[] v4, float[] v5, float[] v6, float[] v7, float w, float h, float d){
    	TexturedVertex[] verts = new TexturedVertex[8]; TexturedPolygon[] poly = new TexturedPolygon[6];
        TexturedVertex tv0 = new TexturedVertex(v0[0], v0[1], v0[2], 0.0F, 0.0F);
        TexturedVertex tv1 = new TexturedVertex(v1[0], v1[1], v1[2], 0.0F, 8.0F);
        TexturedVertex tv2 = new TexturedVertex(v2[0], v2[1], v2[2], 8.0F, 8.0F);
        TexturedVertex tv3 = new TexturedVertex(v3[0], v3[1], v3[2], 8.0F, 0.0F);
        TexturedVertex tv4 = new TexturedVertex(v4[0], v4[1], v4[2], 0.0F, 0.0F);
        TexturedVertex tv5 = new TexturedVertex(v5[0], v5[1], v5[2], 0.0F, 8.0F);
        TexturedVertex tv6 = new TexturedVertex(v6[0], v6[1], v6[2], 8.0F, 8.0F);
        TexturedVertex tv7 = new TexturedVertex(v7[0], v7[1], v7[2], 8.0F, 0.0F);
        verts[0] = tv0; verts[1] = tv1; verts[2] = tv2; verts[3] = tv3;
        verts[4] = tv4; verts[5] = tv5; verts[6] = tv6; verts[7] = tv7;
        poly[0] = toPolygon(new TexturedVertex[] { tv5, tv1, tv2, tv6 }, texoffx + d + w, texoffy + d, texoffx + d + w + d, texoffy + d + h);
        poly[1] = toPolygon(new TexturedVertex[] { tv0, tv4, tv7, tv3 }, texoffx + 0, texoffy + d, texoffx + d, texoffy + d + h);
        poly[2] = toPolygon(new TexturedVertex[] { tv5, tv4, tv0, tv1 }, texoffx + d, texoffy + 0, texoffx + d + w, texoffy + d);
        poly[3] = toPolygon(new TexturedVertex[] { tv2, tv3, tv7, tv6 }, texoffx + d + w, texoffy + 0, texoffx + d + w + w, texoffy + d);
        poly[4] = toPolygon(new TexturedVertex[] { tv1, tv0, tv3, tv2 }, texoffx + d, texoffy + d, texoffx + d + w, texoffy + d + h);
        poly[5] = toPolygon(new TexturedVertex[] { tv4, tv5, tv6, tv7 }, texoffx + d + w + d, texoffy + d, texoffx + d + w + d + w, texoffy + d + h);
        if(mirror || flip){ for(int l = 0; l < poly.length; l++){ poly[l].flipFace(); } }
        this.vertices = verts; this.faces = poly; this.compiled = true;
	}

}
