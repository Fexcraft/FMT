package net.fexcraft.app.fmt.wrappers;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class BoxWrapper extends PolygonWrapper {
	
	public Vec3f size = new Vec3f(1, 1, 1);
	
	public BoxWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	public void recompile(){
		if(turbo != null && turbo.displaylist() != null){ GL11.glDeleteLists(turbo.displaylist(), 1); turbo = null; }
		turbo = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		turbo.addBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord);
		turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		turbo.rotateAngleX = rot.xCoord; turbo.rotateAngleY = rot.yCoord; turbo.rotateAngleZ = rot.zCoord;
		//
		if(lines != null && lines.displaylist() != null){  GL11.glDeleteLists(lines.displaylist(), 1); lines = null; }
		lines = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY); lines.lines = true;
		lines.addBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord);
		lines.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		lines.rotateAngleX = rot.xCoord; lines.rotateAngleY = rot.yCoord; lines.rotateAngleZ = rot.zCoord;
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
	
}
