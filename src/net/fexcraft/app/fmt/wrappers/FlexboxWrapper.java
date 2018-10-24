package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import org.lwjgl.opengl.GL11;

public class FlexboxWrapper extends BoxWrapper {

	public float[] scales;
	public int mr_side=0;

	public FlexboxWrapper(GroupCompound compound){
		super(compound);
	}
	
	@Override
	public void recompile(){
		if(turbo != null && turbo.displaylist() != null){ GL11.glDeleteLists(turbo.displaylist(), 1); turbo = null; }
		turbo = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		turbo.addFlexBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 1f, scales[0],scales[1],scales[2],scales[3], mr_side);
		turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		turbo.rotateAngleX = rot.xCoord; turbo.rotateAngleY = rot.yCoord; turbo.rotateAngleZ = rot.zCoord;
		//
		if(lines != null && lines.displaylist() != null){ GL11.glDeleteLists(lines.displaylist(), 0); lines = null; }
		lines = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		lines.addFlexBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0, scales[0], scales[1], scales[2], scales[3], mr_side); lines.lines = true;
		lines.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		lines.rotateAngleX = rot.xCoord; lines.rotateAngleY = rot.yCoord; lines.rotateAngleZ = rot.zCoord;
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
	
}
