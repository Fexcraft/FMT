package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class MarkerWrapper extends PolygonWrapper {
	
	public int color;
	
	public MarkerWrapper(GroupCompound compound){
		super(compound); color = RGB.GREEN.packed;
	}
	
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.textureX, compound.textureY)
			.addSphere(off.xCoord, off.yCoord, off.zCoord, 0.5f, 8, 8, 0, 0)
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord)
			.setTextured(false).setColor(new RGB(color));
	}
	
	@Override
	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render();
	}
	
	@Override
	public void renderLines(boolean rotX, boolean rotY, boolean rotZ){ return; }

	@Override
	public void renderPicking(boolean rotX, boolean rotY, boolean rotZ){ return; }

	@Override
	public ShapeType getType(){
		return ShapeType.MARKER;
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
		if(!export){
			obj.addProperty("marker", true);
			obj.addProperty("color", color);
		} return obj;
	}

	@Override
	protected float[][][] newTexturePosition(){
		return new float[0][][];
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new MarkerWrapper(compound);
	}
	
}
