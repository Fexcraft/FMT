package net.fexcraft.app.fmt.wrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.NullFace;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ColorIndexedVoxelBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import net.fexcraft.lib.tmt.VoxelBuilder;

public class VoxelWrapper extends PolygonWrapper {
	
	public boolean[][][] content;
	public int segx, segy, segz;
	//
	public int[][][] icontent;
	public Map<Integer, RGB> colors;
	
	public VoxelWrapper(GroupCompound compound, int x, int y, int z, boolean def){
		super(compound);
		content = new boolean[segx = x][segy = y][segz = z];
		for(int i = 0; i < segx; i++){
			for(int j = 0; j < segy; j++){
				for(int k = 0; k < segz; k++){
					content[i][j][k] = def;//Static.random.nextBoolean();;
				}
			}
		}
	}

	public VoxelWrapper(GroupCompound compound, int x, int y, int z, boolean[][][] bools){
		super(compound);
		this.content = bools;
		this.segx = x;
		this.segy = y;
		this.segz = z;
	}

	public VoxelWrapper(GroupCompound compound, int x, int y, int z, int[][][] ints, HashMap<Integer, RGB> colours){
		super(compound);
		this.colors = colours;
		this.icontent = ints;
		this.segx = x;
		this.segy = y;
		this.segz = z;
	}

	@Override
	protected ModelRendererTurbo newMRT(){
		if(icontent != null){
			return new ModelRendererTurbo(null, textureX(), textureY(), compound.tx(getTurboList()), compound.ty(getTurboList())){
				@Override public RGB getColor(int i){ return super.getColor(i % 6); }
				@Override public String toString(){ return "VoxelShape"; }
			}.addColorIndexedVoxelShape(segx, segy, segz, icontent, colors)
				.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
				.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
		}
		return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList())){
			@Override public RGB getColor(int i){ return super.getColor(i % 6); }
			@Override public String toString(){ return "VoxelShape"; }
		}.addVoxelShape(segx, segy, segz, content).setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord).setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.VOXEL;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "voxel": return x ? segx : y ? segy : z ? segz : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "size":{
				if(x){ segx = (int)value; return true; }
				if(y){ segy = (int)value; return true; }
				if(z){ segz = (int)value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("seg_x", segx);
		obj.addProperty("seg_y", segy);
		obj.addProperty("seg_z", segz);
		if(icontent != null){
			obj.addProperty("color_indexed", true);
		}
		ArrayList<int[]> coords = icontent != null ? new ColorIndexedVoxelBuilder(null, segx, segy, segz).setVoxels(icontent).setColors(colors).buildCoords() : new VoxelBuilder(null, segx, segy, segz).setVoxels(content).buildCoords();
		JsonArray array = new JsonArray();
		for(int[] arr : coords){
			JsonArray coor = new JsonArray();
			for(int i = 0; i < arr.length; i++) coor.add(arr[i]);
			if(coor.size() >= 6) array.add(coor);
		}
		if(colors != null){
			JsonArray corray = new JsonArray();
			for(RGB rgb : colors.values()){
				corray.add(rgb.packed);
			}
			obj.add("colors", corray);
		}
		obj.add("coords", array);
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
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
		VoxelWrapper wrapper = new VoxelWrapper(compound, segx, segy, segz, false);
		for(int i = 0; i < segx; i++){
			for(int j = 0; j < segy; j++){
				for(int k = 0; k < segz; k++){
					wrapper.content[i][j][k] = content[i][j][k];
				}
			}
		} return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == ShapeType.VOXEL ? this.clone() : null;
	}

	@Override
	public Face[] getTexturableFaces(){
		return NullFace.values();
	}
	
}
