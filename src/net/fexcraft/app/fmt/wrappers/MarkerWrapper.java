package net.fexcraft.app.fmt.wrappers;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.demo.ModelSteve;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.NullFace;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class MarkerWrapper extends PolygonWrapper {
	
	public int color, angle = -90;
	public boolean biped, detached;
	public float scale = 1;
	
	public MarkerWrapper(GroupCompound compound){
		super(compound);
		color = RGB.GREEN.packed;
	}
	
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
			.addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f) .setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord).setTextured(false).setColor(new RGB(color));
	}
	
	protected MarkerWrapper setup(int color, boolean biped, int angle, float scale){
		this.color = color; this.biped = biped; this.angle = angle; this.scale = scale; return this;
	}
	
	@Override
	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null){
			if(detached && !rotX){
				this.compound.detached.add(this);
				return;
			}
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			turbo.render();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			if(biped){
				RGB.glColorReset();
				GL11.glPushMatrix();
				GL11.glScalef(scale, scale, scale);
				GL11.glTranslatef(Static.sixteenth * pos.xCoord, Static.sixteenth * pos.yCoord, Static.sixteenth * pos.zCoord);
				if(!Settings.oldrot()) GL11.glRotatef(180, 1, 0, 0);
				TextureManager.bindTexture("steve"); ModelSteve.render(angle);
				GL11.glPopMatrix();
			}
		}
	}
	
	@Override
	public void renderLines(boolean rotX, boolean rotY, boolean rotZ){
		if(detached && !rotX) return;
		if(biped && Settings.lines() && (selected || getTurboList().selected)){
			if(!widelines){ GL11.glLineWidth(4f); widelines = true; }
			RGB.glColorReset();
			GL11.glPushMatrix();
			GL11.glScalef(scale, scale, scale);
			GL11.glTranslatef(Static.sixteenth * pos.xCoord, Static.sixteenth * pos.yCoord, Static.sixteenth * pos.zCoord);
			if(!Settings.oldrot()) GL11.glRotatef(180, 1, 0, 0);
			ModelSteve.renderLines(angle);
			GL11.glPopMatrix();
		}
		return;
	}

	@Override
	public void renderPicking(boolean rotX, boolean rotY, boolean rotZ){
		super.renderPicking(rotX, rotY, rotZ);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.MARKER;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "marker_color": return color;
			case "marker_biped": return biped ? 1 : 0;
			case "marker_angle": return angle;
			case "marker_scale": return scale;
			case "marker_detached": return detached ? 1 : 0;
		}
		return super.getFloat(id, x, y, z);
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "marker_color":{
				if(x){ color = (int)value; return true; }
			}
			case "marker_biped":{
				if(x){ biped = (int)value == 1 ? true : false; return true; }
			}
			case "marker_angle":{
				if(x){ angle = (int)value; return true; }
			}
			case "marker_scale":{
				if(x){ scale = value; return true; }
			}
			case "marker_detached":{
				if(x){ detached = (int)value == 1 ? true : false; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		if(!export){
			obj.addProperty("marker", true);
			obj.addProperty("color", color);
			obj.addProperty("biped", biped);
			obj.addProperty("biped_angle", angle);
			obj.addProperty("biped_scale", scale);
			obj.addProperty("detached", detached);
		} return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new MarkerWrapper(compound).setup(color, biped, angle, scale);
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
