package net.fexcraft.app.fmt.wrappers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ObjPreviewWrapper extends PolygonWrapper {
	
	public File source;
	
	public ObjPreviewWrapper(GroupCompound compound, File file){
		super(compound); this.source = file;
	}

	@Override
	public void recompile(){
		if(turbo != null && turbo.displaylist() != null){ GL11.glDeleteLists(turbo.displaylist(), 1); turbo = null; }
		turbo = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY);
		try{ turbo.addObj(source.getName(), new FileInputStream(source)); }
		catch(FileNotFoundException e1){ e1.printStackTrace(); }
		turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		turbo.rotateAngleX = rot.xCoord; turbo.rotateAngleY = rot.yCoord; turbo.rotateAngleZ = rot.zCoord;
		turbo.textured = compound.texture != null;
		//
		if(lines != null && lines.displaylist() != null){  GL11.glDeleteLists(lines.displaylist(), 1); lines = null; }
		lines = new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY); lines.lines = true;
		try{ lines.addObj(source.getName(), new FileInputStream(source)); }
		catch(FileNotFoundException e){ e.printStackTrace(); }
		lines.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord);
		lines.rotateAngleX = rot.xCoord; lines.rotateAngleY = rot.yCoord; lines.rotateAngleZ = rot.zCoord;
		lines.textured = compound.texture != null;
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
	
}
