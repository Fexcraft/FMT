package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.local_tmt.ModelRendererTurbo;

public class BoxWrapper extends PolygonWrapper {
	
	public Vec3f size = new Vec3f(1, 1, 1);
	
	public BoxWrapper(GroupCompound compound){
		super(compound);
	}

	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
			.addBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord)
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.BOX;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "size": return x ? size.xCoord : y ? size.yCoord : z ? size.zCoord : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "size":{
				if(x){ size.xCoord = value; return true; }
				if(y){ size.yCoord = value; return true; }
				if(z){ size.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("width", size.xCoord);
		obj.addProperty("height", size.yCoord);
		obj.addProperty("depth", size.zCoord);
		return obj;
	}

	@Override
	protected float[][][] newTexturePosition(){
		float tx = 0 /*textureX*/, ty = 0 /*textureY*/, w = size.xCoord, h = size.yCoord, d = size.zCoord;
		float[][][] vecs = new float[6][][];
		vecs[0] = new float[][]{
			new float[]{ tx + d + w, ty + d },
			new float[]{ tx + d + w + d, ty + d + h }
		};
		vecs[1] = new float[][]{
			new float[]{ tx, ty + d },
			new float[]{ tx + d, ty + d + h }
		};
		vecs[2] = new float[][]{
			new float[]{ tx + d, ty },
			new float[]{ tx + d + w, ty + d }
		};
		vecs[3] = new float[][]{
			new float[]{ tx + d + w, ty + 0 },
			new float[]{ tx + d + w + w, ty + d }
		};
		vecs[4] = new float[][]{
			new float[]{ tx + d, ty + d },
			new float[]{ tx + d + w, ty + d + h }
		};
		vecs[5] = new float[][]{
			new float[]{ tx + d + w + d, ty + d },
			new float[]{ tx + d + w + d + w, ty + d + h }
		};
		return vecs;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		BoxWrapper wrapper = new BoxWrapper(compound);
		wrapper.size = new Vec3f(size); return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(type == ShapeType.BOX) return this.clone();
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		BoxWrapper wrapper = null;
		switch(type){
			case QUAD: wrapper = new QuadWrapper(compound); break;
			case SHAPEQUAD: wrapper = new ShapeQuadWrapper(compound); break;
			case SHAPEBOX: wrapper = new ShapeboxWrapper(compound); break;
			case TEXRECT_A: wrapper = new TexrectWrapperA(compound); break;
			case TEXRECT_B: wrapper = new TexrectWrapperB(compound); break;
			default: return null;
		} wrapper.size = new Vec3f(size); return copyTo(wrapper, true);
	}
	
}
