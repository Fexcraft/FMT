package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class VoxelWrapper extends PolygonWrapper {
	
	public boolean[][][] content;
	public int divider;
	
	public VoxelWrapper(GroupCompound compound, int divider, boolean def){
		super(compound); this.divider = divider;
		content = new boolean[divider][][];
		for(int i = 0; i < divider; i++){
			content[i] = new boolean[divider][];
			for(int j = 0; j < divider; j++){
				content[i][j] = new boolean[divider];
				for(int k = 0; k < divider; k++){
					content[i][j][k] = def;//Static.random.nextBoolean();;
				}
			}
		 }
	}

	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
			.addVoxelShape(divider, content).setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord).setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.VOXEL;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "voxel": return x ? divider : y ? 0 : z ? 0 : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "size":{
				if(x){ divider = (int)value; return true; }
				//if(y){ size.yCoord = value; return true; }
				//if(z){ size.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("segments", divider);
		return obj;
	}

	@Override
	protected float[][][] newTexturePosition(){
		/*float tx = 0 , ty = 0, w = size.xCoord, h = size.yCoord, d = size.zCoord;
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
		return vecs;*/  return new float[0][][];
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		VoxelWrapper wrapper = new VoxelWrapper(compound, divider, false);
		for(int i = 0; i < divider; i++){
			for(int j = 0; j < divider; j++){
				for(int k = 0; k < divider; k++){
					wrapper.content[i][j][k] = content[i][j][k];
				}
			}
		} return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == ShapeType.VOXEL ? this.clone() : null;
	}
	
}
