package net.fexcraft.app.fmt.wrappers;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ShapeboxWrapper extends BoxWrapper {
	
	public Vec3f cor0 = new Vec3f(), cor1 = new Vec3f(), cor2 = new Vec3f(), cor3 = new Vec3f(),
				 cor4 = new Vec3f(), cor5 = new Vec3f(), cor6 = new Vec3f(), cor7 = new Vec3f();
	
	public ShapeboxWrapper(GroupCompound compound){
		super(compound);
	}
	
	@Override
	public void recompile(){
		if(turbo != null && turbo.displaylist() != null){ GL11.glDeleteLists(turbo.displaylist(), 1); turbo = null; }
		turbo = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		turbo.addShapeBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0,
			cor0.xCoord, cor0.yCoord, cor0.zCoord,
			cor1.xCoord, cor1.yCoord, cor1.zCoord,
			cor2.xCoord, cor2.yCoord, cor2.zCoord,
			cor3.xCoord, cor3.yCoord, cor3.zCoord,
			cor4.xCoord, cor4.yCoord, cor4.zCoord,
			cor5.xCoord, cor5.yCoord, cor5.zCoord,
			cor6.xCoord, cor6.yCoord, cor6.zCoord,
			cor7.xCoord, cor7.yCoord, cor7.zCoord);
		turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		turbo.rotateAngleX = rot.xCoord; turbo.rotateAngleY = rot.yCoord; turbo.rotateAngleZ = rot.zCoord;
		//
		if(lines != null && lines.displaylist() != null){ GL11.glDeleteLists(lines.displaylist(), 0); lines = null; }
		lines = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		lines.addShapeBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0,
			cor0.xCoord, cor0.yCoord, cor0.zCoord,
			cor1.xCoord, cor1.yCoord, cor1.zCoord,
			cor2.xCoord, cor2.yCoord, cor2.zCoord,
			cor3.xCoord, cor3.yCoord, cor3.zCoord,
			cor4.xCoord, cor4.yCoord, cor4.zCoord,
			cor5.xCoord, cor5.yCoord, cor5.zCoord,
			cor6.xCoord, cor6.yCoord, cor6.zCoord,
			cor7.xCoord, cor7.yCoord, cor7.zCoord); lines.lines = true;
		lines.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		lines.rotateAngleX = rot.xCoord; lines.rotateAngleY = rot.yCoord; lines.rotateAngleZ = rot.zCoord;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.SHAPEBOX;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cor0": return x ? cor0.xCoord : y ? cor0.yCoord : z ? cor0.zCoord : 0;
			case "cor1": return x ? cor1.xCoord : y ? cor1.yCoord : z ? cor1.zCoord : 0;
			case "cor2": return x ? cor2.xCoord : y ? cor2.yCoord : z ? cor2.zCoord : 0;
			case "cor3": return x ? cor3.xCoord : y ? cor3.yCoord : z ? cor3.zCoord : 0;
			case "cor4": return x ? cor4.xCoord : y ? cor4.yCoord : z ? cor4.zCoord : 0;
			case "cor5": return x ? cor5.xCoord : y ? cor5.yCoord : z ? cor5.zCoord : 0;
			case "cor6": return x ? cor6.xCoord : y ? cor6.yCoord : z ? cor6.zCoord : 0;
			case "cor7": return x ? cor7.xCoord : y ? cor7.yCoord : z ? cor7.zCoord : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		//if(!id.startsWith("cor")) return false;
		//int iID = Integer.parseInt(id.replace("cor", ""));
		switch(id){
			case "cor0":{
				if(x){ cor0.xCoord = value; return true; }
				if(y){ cor0.yCoord = value; return true; }
				if(z){ cor0.zCoord = value; return true; }
			}
			case "cor1":{
				if(x){ cor1.xCoord = value; return true; }
				if(y){ cor1.yCoord = value; return true; }
				if(z){ cor1.zCoord = value; return true; }
			}
			case "cor2":{
				if(x){ cor2.xCoord = value; return true; }
				if(y){ cor2.yCoord = value; return true; }
				if(z){ cor2.zCoord = value; return true; }
			}
			case "cor3":{
				if(x){ cor3.xCoord = value; return true; }
				if(y){ cor3.yCoord = value; return true; }
				if(z){ cor3.zCoord = value; return true; }
			}
			case "cor4":{
				if(x){ cor4.xCoord = value; return true; }
				if(y){ cor4.yCoord = value; return true; }
				if(z){ cor4.zCoord = value; return true; }
			}
			case "cor5":{
				if(x){ cor5.xCoord = value; return true; }
				if(y){ cor5.yCoord = value; return true; }
				if(z){ cor5.zCoord = value; return true; }
			}
			case "cor6":{
				if(x){ cor6.xCoord = value; return true; }
				if(y){ cor6.yCoord = value; return true; }
				if(z){ cor6.zCoord = value; return true; }
			}
			case "cor7":{
				if(x){ cor7.xCoord = value; return true; }
				if(y){ cor7.yCoord = value; return true; }
				if(z){ cor7.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		if(cor0.xCoord != 0) obj.addProperty("x0", cor0.xCoord);
		if(cor0.yCoord != 0) obj.addProperty("y0", cor0.yCoord);
		if(cor0.zCoord != 0) obj.addProperty("z0", cor0.zCoord);
		//
		if(cor1.xCoord != 0) obj.addProperty("x1", cor1.xCoord);
		if(cor1.yCoord != 0) obj.addProperty("y1", cor1.yCoord);
		if(cor1.zCoord != 0) obj.addProperty("z1", cor1.zCoord);
		//
		if(cor2.xCoord != 0) obj.addProperty("x2", cor2.xCoord);
		if(cor2.yCoord != 0) obj.addProperty("y2", cor2.yCoord);
		if(cor2.zCoord != 0) obj.addProperty("z2", cor2.zCoord);
		//
		if(cor3.xCoord != 0) obj.addProperty("x3", cor3.xCoord);
		if(cor3.yCoord != 0) obj.addProperty("y3", cor3.yCoord);
		if(cor3.zCoord != 0) obj.addProperty("z3", cor3.zCoord);
		//
		if(cor4.xCoord != 0) obj.addProperty("x4", cor4.xCoord);
		if(cor4.yCoord != 0) obj.addProperty("y4", cor4.yCoord);
		if(cor4.zCoord != 0) obj.addProperty("z4", cor4.zCoord);
		//
		if(cor5.xCoord != 0) obj.addProperty("x5", cor5.xCoord);
		if(cor5.yCoord != 0) obj.addProperty("y5", cor5.yCoord);
		if(cor5.zCoord != 0) obj.addProperty("z5", cor5.zCoord);
		//
		if(cor6.xCoord != 0) obj.addProperty("x6", cor6.xCoord);
		if(cor6.yCoord != 0) obj.addProperty("y6", cor6.yCoord);
		if(cor6.zCoord != 0) obj.addProperty("z6", cor6.zCoord);
		//
		if(cor7.xCoord != 0) obj.addProperty("x7", cor7.xCoord);
		if(cor7.yCoord != 0) obj.addProperty("y7", cor7.yCoord);
		if(cor7.zCoord != 0) obj.addProperty("z7", cor7.zCoord);
		return obj;
	}


	public ShapeboxWrapper setCoords(Vec3f xyz0, Vec3f xyz1, Vec3f xyz2, Vec3f xyz3, Vec3f xyz4, Vec3f xyz5, Vec3f xyz6, Vec3f xyz7){
		cor0 = xyz0; cor1 = xyz1; cor2 = xyz2; cor3 = xyz3; cor4 = xyz4; cor5 = xyz5; cor6 = xyz6; cor7 = xyz7; return this;
	}
	
}
