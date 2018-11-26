package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CylinderWrapper extends PolygonWrapper {
	
	public float radius = 2, length = 2, base = 1, top = 1;
	public int segments = 8, direction = ModelRendererTurbo.MR_TOP;
	public Vec3f topoff = new Vec3f(0, 0, 0);
	
	public CylinderWrapper(GroupCompound compound){
		super(compound);
	}
	
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY)
			.addCylinder(off.xCoord, off.yCoord, off.zCoord, radius, length, segments, base, top, direction, getTopOff())
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle((float)Math.toRadians(rot.xCoord), (float)Math.toRadians(rot.yCoord), (float)Math.toRadians(rot.zCoord));
	}

	private Vec3f getTopOff(){
		return topoff.xCoord == 0f && topoff.yCoord == 0f && topoff.zCoord == 0f ? null : topoff;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.CYLINDER;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cyl0": return x ? radius : y ? length : 0;
			case "cyl1": return x ? segments : y ? direction : 0;
			case "cyl2": return x ? base : y ? top : 0;
			case "cyl3": return x ? topoff.xCoord : y ? topoff.yCoord : z ? topoff.zCoord : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "cyl0":{
				if(x){ radius = value; return true; }
				if(y){ length = value; return true; }
				if(z){ return false; }
			}
			case "cyl1":{
				if(x){ segments = (int)value; return true; }
				if(y){ direction = (int)value; return true; }
				if(z){ return false; }
			}
			case "cyl2":{
				if(x){ base = value; return true; }
				if(y){ top = value; return true; }
				if(z){ return false; }
			}
			case "cyl3":{
				if(x){ topoff.xCoord = value; return true; }
				if(y){ topoff.yCoord = value; return true; }
				if(z){ topoff.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("radius", radius);
		obj.addProperty("length", length);
		obj.addProperty("segments", segments);
		obj.addProperty("direction", direction);
		obj.addProperty("basescale", base);
		obj.addProperty("topscale", top);
		if(topoff.xCoord != 0f) obj.addProperty("top_offset_x", topoff.xCoord);
		if(topoff.yCoord != 0f) obj.addProperty("top_offset_y", topoff.yCoord);
		if(topoff.zCoord != 0f) obj.addProperty("top_offset_z", topoff.zCoord);
		return obj;
	}

	@Override
	protected float[][][] newTexturePosition(){
		float tx = textureX, ty = textureY, qrad = radius / 2, rad = radius * 2, rad2 = rad + rad;
		float[][][] vecs = new float[10][][];
		vecs[0] = new float[][]{
			new float[]{ tx, ty },
			new float[]{ tx + rad, ty + rad }
		};
		vecs[1] = new float[][]{
			new float[]{ tx + rad, ty},
			new float[]{ tx + rad2, ty + rad }
		};
		for(int i = 0; i < 8; i++){
			vecs[2 + i] = new float[][]{
				new float[]{ tx + (qrad * i), ty + rad },
				new float[]{ tx + (qrad * (i + 1)), ty + rad + length }
			};
		}
		return vecs;
	}
	
}
