package net.fexcraft.lib.fmr.polygons;

import com.google.gson.JsonObject;

import net.fexcraft.lib.fmr.FexcraftModelRenderer;
import net.fexcraft.lib.fmr.ModelCompound;
import net.fexcraft.lib.fmr.PolygonShape;
import net.fexcraft.lib.fmr.Shape;
import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

public class Cylinder extends PolygonShape {
	
	protected float radius, length, basescale = 1f, topscale = 1f;
	protected int segments = 8, direction;

	public Cylinder(){ super(Shape.CYLINDER); }
	
	public Cylinder(boolean flip, boolean mirror){
		super(Shape.CYLINDER, flip, mirror);
	}
	
	public Cylinder(boolean flip, boolean mirror, float radius, float length, int segments, float basescale, float topscale, int direction){
		super(Shape.CYLINDER, flip, mirror);
	}

	@Override
	protected void populateJsonObject(JsonObject obj){
		obj.addProperty("radius", radius);
		obj.addProperty("length", length);
		if(basescale != 1f) obj.addProperty("basescale", basescale);
		if(topscale != 1f) obj.addProperty("topscale", topscale);
		obj.addProperty("segments", segments);
		obj.addProperty("direction", direction);
	}

	@Override /** Based on TMT */
	protected PolygonShape compileShape(){
		boolean dirTop = (direction == ModelCompound.DIR_TOP || direction == ModelCompound.DIR_BOTTOM);
		boolean dirSide = (direction == ModelCompound.DIR_RIGHT || direction == ModelCompound.DIR_LEFT);
		boolean dirFront = (direction == ModelCompound.DIR_FRONT || direction == ModelCompound.DIR_BACK);
		boolean dirMirror = (direction == ModelCompound.DIR_LEFT || direction == ModelCompound.DIR_BOTTOM || direction == ModelCompound.DIR_BACK);
		boolean coneBase = (basescale == 0);
		boolean coneTop = (topscale == 0);
		if(coneBase && coneTop){ basescale = 1F; coneBase = false; }
		TexturedVertex[] verts = new TexturedVertex[segments * (coneBase || coneTop ? 1 : 2) + 2];
		TexturedPolygon[] poly = new TexturedPolygon[segments * (coneBase || coneTop ? 2 : 3)];
		float xLength = (dirSide ? length : 0);
		float yLength = (dirTop ? length : 0);
		float zLength = (dirFront ? length : 0);
		float xStart = (dirMirror ? offsetX + xLength : offsetX);
		float yStart = (dirMirror ? offsetY + yLength : offsetY);
		float zStart = (dirMirror ? offsetZ + zLength : offsetZ);
		float xEnd = (!dirMirror ? offsetX + xLength : offsetX);
		float yEnd = (!dirMirror ? offsetY + yLength : offsetY);
		float zEnd = (!dirMirror ? offsetZ + zLength : offsetZ);
		verts[0] = new TexturedVertex(xStart, yStart, zStart, 0, 0);
		verts[verts.length - 1] = new TexturedVertex(xEnd, yEnd, zEnd, 0, 0);
		float xCur = xStart;
		float yCur = yStart;
		float zCur = zStart;
		float sCur = (coneBase ? topscale : basescale);
		for(int repeat = 0; repeat < (coneBase || coneTop ? 1 : 2); repeat++){
			for(int index = 0; index < segments; index++){
				float xSize = (float)((mirror ^ dirMirror ? -1 : 1) * Math.sin((FexcraftModelRenderer.PI / segments) * index * 2F + FexcraftModelRenderer.PI) * radius * sCur);
				float zSize = (float)(-Math.cos((FexcraftModelRenderer.PI / segments) * index * 2F + FexcraftModelRenderer.PI) * radius * sCur);
				float xPlace = xCur + (!dirSide ? xSize : 0);
				float yPlace = yCur + (!dirTop ? zSize : 0);
				float zPlace = zCur + (dirSide ? xSize : (dirTop ? zSize : 0));
				verts[1 + index + repeat * segments] = new TexturedVertex(xPlace, yPlace, zPlace, 0, 0 );
			}
			xCur = xEnd;
			yCur = yEnd;
			zCur = zEnd;
			sCur = topscale;
		}
		float uScale = 1.0F / model.textureSizeX;
		float vScale = 1.0F / model.textureSizeY;
		float uOffset = uScale / 20.0F;
		float vOffset = vScale / 20.0F;
		float uCircle = (int)Math.floor(radius * 2F) * uScale;
		float vCircle = (int)Math.floor(radius * 2F) * vScale;
		float uWidth = (uCircle * 2F - uOffset * 2F) / segments;
		float vHeight = (int)Math.floor(length) * vScale - uOffset * 2f;
		float uStart = texoffx * uScale;
		float vStart = texoffy * vScale;	
		TexturedVertex[] vert;
		for(int index = 0; index < segments; index++){
			int index2 = (index + 1) % segments;
			float uSize = (float)Math.sin((FexcraftModelRenderer.PI / segments) * index * 2F + (!dirTop ? 0 : FexcraftModelRenderer.PI)) * (0.5F * uCircle - 2F * uOffset);
			float vSize = (float)Math.cos((FexcraftModelRenderer.PI / segments) * index * 2F + (!dirTop ? 0 : FexcraftModelRenderer.PI)) * (0.5F * vCircle - 2F * vOffset);
			float uSize1 = (float)Math.sin((FexcraftModelRenderer.PI / segments) * index2 * 2F + (!dirTop ? 0 : FexcraftModelRenderer.PI)) * (0.5F * uCircle - 2F * uOffset);
			float vSize1 = (float)Math.cos((FexcraftModelRenderer.PI / segments) * index2 * 2F + (!dirTop ? 0 : FexcraftModelRenderer.PI)) * (0.5F * vCircle - 2F * vOffset);
			vert = new TexturedVertex[3];	
			vert[0] = verts[0].setTexturePosition(uStart + 0.5F * uCircle, vStart + 0.5F * vCircle);
			vert[1] = verts[1 + index2].setTexturePosition(uStart + 0.5F * uCircle + uSize1, vStart + 0.5F * vCircle + vSize1);
			vert[2] = verts[1 + index].setTexturePosition(uStart + 0.5F * uCircle + uSize, vStart + 0.5F * vCircle + vSize);
			poly[index] = new TexturedPolygon(vert);
			if(!(mirror ^ flip)){ poly[index].flipFace(); }
			if(!coneBase && !coneTop){
				vert = new TexturedVertex[4];
				vert[0] = verts[1 + index].setTexturePosition(uStart + uOffset + uWidth * index, vStart + vOffset + vCircle);
				vert[1] = verts[1 + index2].setTexturePosition(uStart + uOffset + uWidth * (index + 1), vStart + vOffset + vCircle);
				vert[2] = verts[1 + segments + index2].setTexturePosition(uStart + uOffset + uWidth * (index + 1), vStart + vOffset + vCircle + vHeight);
				vert[3] = verts[1 + segments + index].setTexturePosition(uStart + uOffset + uWidth * index, vStart + vOffset + vCircle + vHeight);
				poly[index + segments] = new TexturedPolygon(vert);
				if(!(mirror ^ flip)){
					poly[index + segments].flipFace();
				}
			}
			vert = new TexturedVertex[3];
			vert[0] = verts[verts.length - 1].setTexturePosition(uStart + 1.5F * uCircle, vStart + 0.5F * vCircle);
			vert[1] = verts[verts.length - 2 - index].setTexturePosition(uStart + 1.5F * uCircle + uSize1, vStart + 0.5F * vCircle + vSize1);
			vert[2] = verts[verts.length - (1 + segments) + ((segments - index) % segments)].setTexturePosition(uStart + 1.5F * uCircle + uSize, vStart + 0.5F * vCircle + vSize);
			poly[poly.length - segments + index]  = new TexturedPolygon(vert);
			if(!(mirror ^ flip)){
				poly[poly.length - segments + index].flipFace();
			}
		}
		this.vertices = verts; this.faces = poly; compiled = true;
		return this;
	}
	
	public Cylinder setRadius(float rad){
		this.radius = rad; return this;
	}
	
	public Cylinder setLength(float len){
		this.length = len; return this;
	}
	
	public Cylinder setSegments(int seg){
		this.segments = seg; return this;
	}
	
	public Cylinder setScale(float basescale, float topscale){
		this.basescale = basescale; this.topscale = topscale; return this;
	}
	
	public Cylinder setDirection(int dir){
		this.direction = dir; return this;
	}

}
