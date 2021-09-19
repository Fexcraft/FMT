package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class QuadWrapper extends BoxWrapper {
	
	public QuadWrapper(GroupCompound compound){
		super(compound);
	}

	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX(), textureY(), compound.tx(getTurboList()), compound.ty(getTurboList()))
			.addQuad(off.x, off.y, off.z, size.x, size.y)
			.setRotationPoint(pos.x, pos.y, pos.z)
			.setRotationAngle(rot.x, rot.y, rot.z);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.QUAD;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "size": return x ? size.x : y ? size.y : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "size":{
				if(x){ size.x = value; return true; }
				if(y){ size.y = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("width", size.x);
		obj.addProperty("height", size.y);
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		float tx = 0 /*textureX*/, ty = 0 /*textureY*/, w = size.x, h = size.y;
		float[][][] vecs = new float[2][][];
		vecs[0] = new float[][]{
			new float[]{ tx, ty },
			new float[]{ tx + w, ty + h }
		};
		vecs[1] = new float[][]{
			new float[]{ tx + w, ty },
			new float[]{ tx + w + w, ty + h }
		};
		return vecs;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		QuadWrapper wrapper = new QuadWrapper(compound);
		wrapper.size = new Vec3f(size); return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(type == ShapeType.BOX) return this.clone();
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		BoxWrapper wrapper = null;
		switch(type){
			case BOX: wrapper = new BoxWrapper(compound); break;
			case SHAPEQUAD: wrapper = new ShapeQuadWrapper(compound); break;
			case SHAPEBOX: wrapper = new ShapeboxWrapper(compound); break;
			default: return null;
		} wrapper.size = new Vec3f(size.x, size.y, 1); return copyTo(wrapper, true);
	}
	
}
