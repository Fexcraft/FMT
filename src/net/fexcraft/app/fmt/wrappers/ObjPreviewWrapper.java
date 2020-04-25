package net.fexcraft.app.fmt.wrappers;

import java.io.File;
import java.io.FileInputStream;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.utils.WavefrontObjUtil;
import net.fexcraft.lib.local_tmt.ModelRendererTurbo;

public class ObjPreviewWrapper extends PolygonWrapper {
	
	public File source;
	
	public ObjPreviewWrapper(GroupCompound compound, File file){
		super(compound); this.source = file;
	}
	
	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render(); this.selected = false;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new ObjPreviewWrapper(compound, source);
	}
	
	protected ModelRendererTurbo newMRT(){
		try{
			String str[][] = WavefrontObjUtil.findValues(new FileInputStream(source), "# FlipAxes:");
			boolean bool = str.length == 0 ? false : Boolean.parseBoolean(str[0][0]);
			return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
				.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
				.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord)
				.addObj(new FileInputStream(source), null, bool);//this.source.toString()
		}
		catch(Exception e){
			e.printStackTrace();
			return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
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
	public float[][][] newTexturePosition(){
		return new float[0][][];
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == this.getType() ? this.clone() : null;
	}
	
}
