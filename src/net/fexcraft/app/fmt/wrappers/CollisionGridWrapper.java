package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CollisionGridWrapper extends PolygonWrapper {
	
	public Vec3f size = new Vec3f(1, 1, 1);
	
	public CollisionGridWrapper(GroupCompound compound){
		super(compound); this.name = "grid";
	}

	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo root = new ModelRendererTurbo(null, 0, 0, 0, 0).setTextured(false);
		float unitsize = rot.xCoord * 16; if(unitsize < 0) return root;
		for(int x = 0; x < size.xCoord; x++){
			for(int y = 0; y < size.yCoord; y++){
				for(int z = 0; z < size.zCoord; z++){
					root.addBox(pos.xCoord + (x * unitsize), pos.yCoord + (y * unitsize), pos.zCoord + (z * unitsize), unitsize, unitsize, unitsize);
				}
			}
		} return root;
	}
	
	@Override
	protected void setupMRT(){
		turbo = newMRT().setTextured(false);
		lines = newMRT().setLines(true);
		sellines = newMRT().setLines(Settings.getSelectedColor());
		picker = new ModelRendererTurbo(null, 0, 0, 16, 16);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.COLLISIONGRID;
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
		return new float[0][][];
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		CollisionGridWrapper wrapper = new CollisionGridWrapper(compound);
		wrapper.size = new Vec3f(size); return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(type == ShapeType.COLLISIONGRID) return this.clone(); return null;
	}
	
}
