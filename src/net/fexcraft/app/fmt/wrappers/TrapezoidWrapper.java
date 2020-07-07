package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class TrapezoidWrapper extends BoxWrapper {

	public float scale;
	public int mr_side = 0;

	public TrapezoidWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		TrapezoidWrapper wrapper = new TrapezoidWrapper(compound);
		wrapper.size = new Vec3f(size); wrapper.scale = scale; wrapper.mr_side = mr_side;
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		return super.newMRT().clear().addTrapezoid(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0, scale, mr_side);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.TRAPEZOID;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "scale": {return scale;}
			case "side": {return mr_side;}
			default: {return super.getFloat(id, x, y, z);}
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		//if(!id.startsWith("cor")) return false;
		//int iID = Integer.parseInt(id.replace("cor", ""));
		switch(id){
			case "scale":{ scale=value; return true; }
			case "side":{ mr_side=(int)value; return true; }
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		if(scale != 0) obj.addProperty("scale", scale);
		if(mr_side != 0) obj.addProperty("side", mr_side);
		return obj;
	}


	public TrapezoidWrapper setCoords(float var1, String side){
		scale = var1; mr_side = getSide(side); return this;
	}

	private static int getSide(String s){
	    switch(s){
            case "MR_LEFT": {return ModelRendererTurbo.MR_LEFT;}
            case "MR_RIGHT": {return ModelRendererTurbo.MR_RIGHT;}
            case "MR_FRONT": {return ModelRendererTurbo.MR_FRONT;}
            case "MR_BACK": {return ModelRendererTurbo.MR_BACK;}
            case "MR_BOTTOM": {return ModelRendererTurbo.MR_BOTTOM;}
            default: case "MR_TOP": {return ModelRendererTurbo.MR_TOP;}
        }
    }

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == this.getType() ? this.clone() : null;
	}
	
}
