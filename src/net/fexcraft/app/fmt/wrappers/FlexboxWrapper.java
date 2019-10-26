package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class FlexboxWrapper extends BoxWrapper {

	public float[] scales;
	public int mr_side = 0;

	public FlexboxWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		FlexboxWrapper wrapper = new FlexboxWrapper(compound);
		wrapper.size = new Vec3f(size);
		wrapper.scales = new float[scales.length];
		for(int i = 0; i < wrapper.scales.length; i++){
			wrapper.scales[i] = scales[i];
		} wrapper.mr_side = mr_side;
		return wrapper;
	}
	
	@Override
	protected ModelRendererTurbo newMRT(){
		return super.newMRT().clear().addFlexBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 1f, scales[0],scales[1],scales[2],scales[3], mr_side);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.FLEXBOX;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
            case "scale0":{return scales[0];}
            case "scale1":{return scales[1];}
            case "scale2":{return scales[2];}
            case "scale3":{return scales[3];}
            case "side":{return mr_side;}
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		//if(!id.startsWith("cor")) return false;
		//int iID = Integer.parseInt(id.replace("cor", ""));
		switch(id){
            case "scale0":{scales[0] = value; return true;}
            case "scale1":{scales[1] = value; return true;}
            case "scale2":{scales[2] = value; return true;}
            case "scale3":{scales[3] = value; return true;}
            case "side":{mr_side = (int)value; return true;}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		if(scales[0] != 0) obj.addProperty("scale0", scales[0]);
		if(scales[1] != 0) obj.addProperty("scale1", scales[1]);
		if(scales[2] != 0) obj.addProperty("scale2", scales[2]);
		if(scales[3] != 0) obj.addProperty("scale3", scales[3]);
		if(mr_side != 0) obj.addProperty("side", mr_side);
		return obj;
	}


	public FlexboxWrapper setCoords(float var1,float var2,float var3,float var4, String side){
		scales = new float[]{var1,var2,var3,var4}; mr_side=getSide(side);
		return this;
	}

	private static int getSide(String s){
	    switch (s){
            case "MR_LEFT":{return ModelRendererTurbo.MR_LEFT;}
            case "MR_RIGHT":{return ModelRendererTurbo.MR_RIGHT;}
            case "MR_FRONT":{return ModelRendererTurbo.MR_FRONT;}
            case "MR_BACK":{return ModelRendererTurbo.MR_BACK;}
            case "MR_BOTTOM":{return ModelRendererTurbo.MR_BOTTOM;}
            default:case "MR_TOP":{return ModelRendererTurbo.MR_TOP;}
        }
    }

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == this.getType() ? this.clone() : null;
	}
	
}
