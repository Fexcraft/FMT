package net.fexcraft.lib.tmt;

import java.util.ArrayList;

import net.fexcraft.app.fmt.utils.Vec3f;
import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

public class Shape2D {
	
	public ArrayList<Coord2D> coords;
	
	public Shape2D(){
		coords = new ArrayList<Coord2D>();
	}
	
	public Shape2D(Coord2D[] coordArray){
		coords = new ArrayList<Coord2D>();
		for(Coord2D coord : coordArray){
			coords.add(coord);
		}
	}
	
	public Shape2D(ArrayList<Coord2D> coordList){
		coords = coordList;
	}
	
	public Coord2D[] getCoordArray(){
		return (Coord2D[])coords.toArray();
	}
	
	public Shape3D extrude(float x, float y, float z, float rotX, float rotY, float rotZ, float depth, int u, int v, float textureWidth, float textureHeight, int shapeTextureWidth, int shapeTextureHeight, int sideTextureWidth, int sideTextureHeight, float[] faceLengths){
		TexturedVertex[] verts = new TexturedVertex[coords.size() * 2];
		TexturedVertex[] vertsTop = new TexturedVertex[coords.size()];
		TexturedVertex[] vertsBottom = new TexturedVertex[coords.size()];
		TexturedPolygon[] poly = new TexturedPolygon[coords.size() + 2];
		Vec3f extrudeVector = new Vec3f(0, 0, depth);

		setVectorRotations(extrudeVector, rotX, rotY, rotZ);
		
		if(faceLengths != null && faceLengths.length < coords.size()){
			faceLengths = null;
		}
		float totalLength = 0;
		for(int idx = 0; idx < coords.size(); idx++){
			Coord2D curCoord = coords.get(idx);
			Coord2D nextCoord = coords.get((idx + 1) % coords.size());
			float texU1 = ((float)(curCoord.uCoord + u) / (float)textureWidth);
			float texU2 = ((float)(shapeTextureWidth * 2 - curCoord.uCoord + u) / (float)textureWidth);
			float texV = ((float)(curCoord.vCoord + v) / (float)textureHeight);
			Vec3f vecCoord = new Vec3f(curCoord.xCoord, curCoord.yCoord, 0);
			setVectorRotations(vecCoord, rotX, rotY, rotZ);
			verts[idx] = new TexturedVertex(
				x + vecCoord.xCoord, y + vecCoord.yCoord, z + vecCoord.zCoord, texU1, texV);
			verts[idx + coords.size()] = new TexturedVertex(
				x + vecCoord.xCoord - extrudeVector.xCoord, y + vecCoord.yCoord - extrudeVector.yCoord,
				z + vecCoord.zCoord - extrudeVector.zCoord, texU2, texV);
			vertsTop[idx] = new TexturedVertex(verts[idx]);
			vertsBottom[coords.size() - idx - 1] = new TexturedVertex(verts[idx + coords.size()]);
			if(faceLengths != null){
				totalLength+= faceLengths[idx];
			}
			else{
				totalLength+= Math.sqrt(Math.pow(curCoord.xCoord - nextCoord.xCoord, 2) + Math.pow(curCoord.yCoord - nextCoord.yCoord, 2));
			}
		}
		poly[coords.size()] = new TexturedPolygon(vertsTop);
		poly[coords.size() + 1] = new TexturedPolygon(vertsBottom);
		float currentLengthPosition = totalLength;
		
		for(int idx = 0; idx < coords.size(); idx++){
			Coord2D curCoord = coords.get(idx);
			Coord2D nextCoord = coords.get((idx + 1) % coords.size());
			float currentLength = (float)Math.sqrt(Math.pow(curCoord.xCoord - nextCoord.xCoord, 2) + Math.pow(curCoord.yCoord - nextCoord.yCoord, 2));
			if(faceLengths != null){
				currentLength = faceLengths[faceLengths.length - idx - 1];
			}
			float ratioPosition = currentLengthPosition / totalLength;
			float ratioLength = (currentLengthPosition - currentLength) / totalLength;
			float texU1 = ((float)(ratioLength * (float)sideTextureWidth + u) / (float)textureWidth);
			float texU2 = ((float)(ratioPosition * (float)sideTextureWidth + u) / (float)textureWidth);
			float texV1 = (((float)v + (float)shapeTextureHeight) / (float)textureHeight);
			float texV2 = (((float)v + (float)shapeTextureHeight + (float)sideTextureHeight) / (float)textureHeight);
			TexturedVertex[] polySide = new TexturedVertex[4];
			polySide[0] = new TexturedVertex(verts[idx], texU2, texV1);
			polySide[1] = new TexturedVertex(verts[coords.size() + idx], texU2, texV2);
			polySide[2] = new TexturedVertex(verts[coords.size() + ((idx + 1) % coords.size())], texU1, texV2);
			polySide[3] = new TexturedVertex(verts[(idx + 1) % coords.size()], texU1, texV1);
			poly[idx] = new TexturedPolygon(polySide);
			currentLengthPosition -= currentLength;
		}
		return new Shape3D(verts, poly);
	}
	
	protected Vec3f setVectorRotations(Vec3f extrudeVector, float xRot, float yRot, float zRot){
		float x = xRot;
		float y = yRot;
		float z = zRot;
        float xC = (float)Math.cos(x);
        float xS = (float)Math.sin(x);
        float yC = (float)Math.cos(y);
        float yS = (float)Math.sin(y);
        float zC = (float)Math.cos(z);
        float zS = (float)Math.sin(z);
        
        double xVec = extrudeVector.xCoord;
        double yVec = extrudeVector.yCoord;
        double zVec = extrudeVector.zCoord;
        
        // rotation around x
		double xy = xC*yVec - xS*zVec;
		double xz = xC*zVec + xS*yVec;
		// rotation around y
		double yz = yC*xz - yS*xVec;
		double yx = yC*xVec + yS*xz;
		// rotation around z
		double zx = zC*yx - zS*xy;
		double zy = zC*xy + zS*yx;
		
		xVec = zx;
		yVec = zy;
		zVec = yz;
		
        return new Vec3f(xVec, yVec, zVec);
	}
	
}
