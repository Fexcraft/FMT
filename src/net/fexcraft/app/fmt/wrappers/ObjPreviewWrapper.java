package net.fexcraft.app.fmt.wrappers;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileInputStream;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.NullFace;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.WavefrontObjUtil;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ObjPreviewWrapper extends PolygonWrapper {
	
	public File source;
	public String group;
	public boolean objmode;
	public int groupidx;
	
	public ObjPreviewWrapper(GroupCompound compound, File file, String group, boolean obj, int idx){
		super(compound);
		this.source = file;
		this.group = group;
		this.objmode = obj;
		this.groupidx = idx;
	}
	
	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render(); this.selected = false;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new ObjPreviewWrapper(compound, source, group, objmode, groupidx);
	}
	
	protected ModelRendererTurbo newMRT(){
		try{
			String str[][] = WavefrontObjUtil.findValues(new FileInputStream(source), null, "# FlipAxes:");
			boolean bool = str.length == 0 ? false : Boolean.parseBoolean(str[0][0]);
			return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList())){
					@Override
					public RGB getColor(int i){
						return super.getColor(groupidx);
					}
				}
				.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
				.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord)
				.addObj(new FileInputStream(source), group, bool, objmode);//this.source.toString()
		}
		catch(Exception e){
			log(e);
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
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == this.getType() ? this.clone() : null;
	}

	@Override
	public Face[] getTexturableFaces(){
		return NullFace.values();
	}
	
}
