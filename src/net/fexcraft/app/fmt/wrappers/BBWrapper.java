package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * Bounding Box Wrapper
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class BBWrapper extends BoxWrapper {
	
	public BBWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		BBWrapper wrapper = new BBWrapper(compound);
		wrapper.size = new Vec3f(size);
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, 0, 0, 8, 8)
			.addBox(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0, 1f, sides)
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.BB;
	}
	
	@Override
	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null){
			String tex = TextureManager.getBoundTexture();
			TextureManager.bindTexture("transparent");
			turbo.render();
			TextureManager.bindTexture(tex);
		}
	}

	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		return super.getFloat(id, x, y, z);
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		return super.setFloat(id, x, y, z, value);
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		//
		return obj;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		if(type == ShapeType.BB) return this.clone();
		BoxWrapper wrapper = null;
		switch(type){
			case BOX: wrapper = new BoxWrapper(compound);
			case QUAD: wrapper = new QuadWrapper(compound);
			case SHAPEBOX: wrapper = new ShapeboxWrapper(compound); break;
			default: return null;
		}
		wrapper.size = new Vec3f(size);
		return copyTo(wrapper, true);
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		return new float[0][][];
	}
	
}
