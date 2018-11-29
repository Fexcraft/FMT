package net.fexcraft.app.fmt.wrappers;

import java.io.File;
import com.google.gson.JsonObject;

import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ObjPreviewWrapper extends PolygonWrapper {
	
	public File source;
	
	public ObjPreviewWrapper(GroupCompound compound, File file){
		super(compound); this.source = file;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new ObjPreviewWrapper(compound, source);
	}
	
	protected ModelRendererTurbo newMRT(){
		try{
			return new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY)
				.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
				.setRotationAngle((float)Math.toRadians(rot.xCoord), (float)Math.toRadians(rot.yCoord), (float)Math.toRadians(rot.zCoord));
		}
		catch(Exception e){
			e.printStackTrace();
			return new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY)
				.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
				.addSphere(-8, -8, -8, 16, 16, 16, 1, 1);
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("location", source.getPath());
		return obj;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.OBJ;
	}

	@Override
	protected float[][][] newTexturePosition(){
		return new float[0][][];
	}
	
}
