package net.fexcraft.lib.fmr.polygons;

import com.google.gson.JsonObject;

import net.fexcraft.lib.fmr.FexcraftModelRenderer;
import net.fexcraft.lib.fmr.PolygonShape;
import net.fexcraft.lib.fmr.Shape;
import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

public class Sphere extends PolygonShape {

	private float radius;
	private int segments, rings;

	public Sphere(){ super(Shape.SPHERE); }
	
	public Sphere(boolean flip, boolean mirror){ super(Shape.SPHERE, flip, mirror); }

	@Override
	protected void populateJsonObject(JsonObject obj){
		obj.addProperty("radius", radius);
		obj.addProperty("segments", segments);
		obj.addProperty("rings", rings);
	}

	@Override /** Based on TMT. **/
	protected PolygonShape compileShape(){
    	if(segments < 3){ segments = 3; } int rings = this.rings + 1;
    	TexturedVertex[] verts0 = new TexturedVertex[segments * (rings - 1) + 2];
    	TexturedPolygon[] poly = new TexturedPolygon[segments * rings];
    	float x = offsetX, y = offsetY, z = offsetZ;
    	verts0[0] = new TexturedVertex(x, y - radius, z, 0, 0);
    	verts0[verts0.length - 1] = new TexturedVertex(x, y + radius, z, 0, 0);
    	float uOffs = 1.0F / ( model.textureSizeX * 10.0F);
    	float vOffs = 1.0F / ( model.textureSizeY * 10.0F);
    	float texW =  this.texoffx / model.textureSizeX - 2F * uOffs;
    	float texH =  this.texoffy / model.textureSizeY - 2F * vOffs;
    	float segW = texW /  segments;
    	float segH = texH /  rings;
    	float startU =  texoffx / model.textureSizeX;
    	float startV =  texoffy / model.textureSizeY;	
    	int currentFace = 0;
    	for(int j = 1; j < rings; j++){
    		for(int i = 0; i < segments; i++){
    			float yWidth = (float)Math.cos(-FexcraftModelRenderer.PI / 2 + (FexcraftModelRenderer.PI / rings) *  j);
    			float yHeight = (float)Math.sin(-FexcraftModelRenderer.PI / 2 + (FexcraftModelRenderer.PI / rings) *  j);
    			float xSize = (float)Math.sin((FexcraftModelRenderer.PI / segments) * i * 2F + FexcraftModelRenderer.PI) * yWidth;
    			float zSize = (float)-Math.cos((FexcraftModelRenderer.PI / segments) * i * 2F + FexcraftModelRenderer.PI) * yWidth;
    			int curVert = 1 + i + segments * (j - 1);
    			verts0[curVert] = new TexturedVertex(x + xSize * radius, y + yHeight * radius, z + zSize * radius, 0, 0);
    			if(i > 0){
    				TexturedVertex[] verts;
	    			if(j == 1){
	    				verts = new TexturedVertex[4];
	    				verts[0] = verts0[curVert].setTexturePosition(startU + segW * i, startV + segH * j);
	    				verts[1] = verts0[curVert - 1].setTexturePosition(startU + segW * (i - 1), startV + segH * j);
	    				verts[2] = verts0[0].setTexturePosition(startU + segW * (i - 1), startV);
	    				verts[3] = verts0[0].setTexturePosition(startU + segW + segW * i, startV);
	    			}
	    			else{
	    				verts = new TexturedVertex[4];
	    				verts[0] = verts0[curVert].setTexturePosition(startU + segW * i, startV + segH * j);
	    				verts[1] = verts0[curVert - 1].setTexturePosition(startU + segW * (i - 1), startV + segH * j);
	    				verts[2] = verts0[curVert - 1 - segments].setTexturePosition(startU + segW * (i - 1), startV + segH * (j - 1));	    				
	    				verts[3] = verts0[curVert - segments].setTexturePosition(startU + segW * i, startV + segH * (j - 1));
	    			}
	    			poly[currentFace] = new TexturedPolygon(verts);
	    			currentFace++;
    			}
    		}
			TexturedVertex[] verts;
   			if(j == 1){
    			verts = new TexturedVertex[4];
    			verts[0] = verts0[1].setTexturePosition(startU + segW * segments, startV + segH * j);
    			verts[1] = verts0[segments].setTexturePosition(startU + segW * (segments - 1), startV + segH * j);
    			verts[2] = verts0[0].setTexturePosition(startU + segW * (segments - 1), startV);
    			verts[3] = verts0[0].setTexturePosition(startU + segW * segments, startV);
    		}
    		else{
    			verts = new TexturedVertex[4];
    			verts[0] = verts0[1 + segments * (j - 1)].setTexturePosition(startU + texW, startV + segH * j);
    			verts[1] = verts0[segments * (j - 1) + segments].setTexturePosition(startU + texW - segW, startV + segH * j);
    			verts[2] = verts0[segments * (j - 1)].setTexturePosition(startU + texW - segW, startV + segH * (j - 1));	    				
    			verts[3] = verts0[1 + segments * (j - 1) - segments].setTexturePosition(startU + texW, startV + segH * (j - 1));
			}
   			poly[currentFace] = new TexturedPolygon(verts);
   			currentFace++;
    	}
		for(int i = 0; i < segments; i++){
			TexturedVertex[] verts = new TexturedVertex[3];
			int curVert = verts0.length - (segments + 1);
			verts[0] = verts0[verts0.length - 1].setTexturePosition(startU + segW * (i + 0.5F), startV + texH);
			verts[1] = verts0[curVert + i].setTexturePosition(startU + segW * i, startV + texH - segH);
			verts[2] = verts0[curVert + ((i + 1) % segments)].setTexturePosition(startU + segW * (i + 1), startV + texH - segH);
			poly[currentFace] = new TexturedPolygon(verts);
			currentFace++;
		}
		if(!(mirror || flip)){ for(TexturedPolygon pol : poly) pol.flipFace(); }
		this.vertices = verts0; this.faces = poly; this.compiled = true;
		return this;
	}
	
	public Sphere setRadius(float rad){
		this.radius = rad; return this;
	}
	
	public Sphere setSegments(int seg){
		this.segments = seg; return this;
	}
	
	public Sphere setRings(int rin){
		this.rings = rin; return this;
	}

}
