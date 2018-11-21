package net.fexcraft.app.fmt.wrappers;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class PolygonWrapper {
	
	public Vec3f pos = new Vec3f(), off = new Vec3f(), rot = new Vec3f();
	public int textureX, textureY;
	protected ModelRendererTurbo turbo, lines, sellines;
	protected final GroupCompound compound;
	private static boolean widelines;
	public boolean visible = true;
	private TurboList turbolist;
	public boolean mirror, flip;
	public boolean selected;
	public String name;
	
	public PolygonWrapper(GroupCompound compound){
		this.compound = compound;
	}
	
	public void recompile(){
		this.clearMRT(turbo, lines, sellines); this.setupMRT();
	}

	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render();
		/*if(selected && turbo != null){
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslatef( turbo.rotationPointX * 0.0625F,  turbo.rotationPointY * 0.0625F,  turbo.rotationPointZ * 0.0625F);
			TextureManager.bindTexture("white"); yellow.glColorApply(); sphere.render(); RGB.glColorReset();
			GL11.glTranslatef(-turbo.rotationPointX * 0.0625F, -turbo.rotationPointY * 0.0625F, -turbo.rotationPointZ * 0.0625F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}*/
	}
	
	public void renderLines(boolean rotXb, boolean rotYb, boolean rotZb){
		//if(Settings.lines()) (selected ? sellines : lines).render();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if(Settings.lines()){
			if(selected || turbolist.selected){
				if(!widelines){ GL11.glLineWidth(4f); widelines = true; }
				sellines.render();
			}
			else{
				if(widelines){ GL11.glLineWidth(1f); widelines = false; }
				lines.render();
			}
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	public abstract ShapeType getType();

	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "tex": return x ? textureX : y ? textureY : 0;
			case "pos": return x ? pos.xCoord : y ? pos.yCoord : z ? pos.zCoord : 0;
			case "off": return x ? off.xCoord : y ? off.yCoord : z ? off.zCoord : 0;
			case "rot": return x ? rot.xCoord : y ? rot.yCoord : z ? rot.zCoord : 0;
		}
		return 0;
	}

	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		switch(id){
			case "tex":{
				if(x){ textureX = (int)value; return true; }
				if(y){ textureY = (int)value; return true; }
				if(z) return false;
			}
			case "pos":{
				if(x){ pos.xCoord = value; return true; }
				if(y){ pos.yCoord = value; return true; }
				if(z){ pos.zCoord = value; return true; }
			}
			case "off":{
				if(x){ off.xCoord = value; return true; }
				if(y){ off.yCoord = value; return true; }
				if(z){ off.zCoord = value; return true; }
			}
			case "rot":{
				if(x){ rot.xCoord = value; return true; }
				if(y){ rot.yCoord = value; return true; }
				if(z){ rot.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	public String name(){
		return name == null ? "unnamed " + this.getType().name().toLowerCase() : name;
	}

	public JsonObject toJson(boolean export){
		JsonObject obj = new JsonObject();
		obj.addProperty("texture_x", textureX);
		obj.addProperty("texture_y", textureY);
		obj.addProperty("type", this.getType().name().toLowerCase());
		if(name != null) obj.addProperty("name", name);
		if(pos.xCoord != 0) obj.addProperty("pos_x", pos.xCoord);
		if(pos.yCoord != 0) obj.addProperty("pos_y", pos.yCoord);
		if(pos.zCoord != 0) obj.addProperty("pos_z", pos.zCoord);
		//
		if(off.xCoord != 0) obj.addProperty("off_x", off.xCoord);
		if(off.yCoord != 0) obj.addProperty("off_y", off.yCoord);
		if(off.zCoord != 0) obj.addProperty("off_z", off.zCoord);
		//
		if(rot.xCoord != 0) obj.addProperty("rot_x", export ? Math.toRadians(rot.xCoord) : rot.xCoord);
		if(rot.yCoord != 0) obj.addProperty("rot_y", export ? Math.toRadians(rot.yCoord) : rot.yCoord);
		if(rot.zCoord != 0) obj.addProperty("rot_z", export ? Math.toRadians(rot.zCoord) : rot.zCoord);
		if(mirror != false) obj.addProperty("mirror", true);
		if(flip != false) obj.addProperty("flip", true);
		//temporary data
		if(!export){
			obj.addProperty("visible", visible);
		}
		return populateJson(obj, export);
	}

	protected abstract JsonObject populateJson(JsonObject obj, boolean export);
	
	protected void clearMRT(ModelRendererTurbo... mrts){
		if(mrts == null) mrts = new ModelRendererTurbo[]{ turbo, lines, sellines };
		for(ModelRendererTurbo mrt : mrts){
			if(mrt != null && mrt.displaylist() != null){
				GL11.glDeleteLists(mrt.displaylist(), 1); mrt = null;
			}
		}
	}
	
	protected void setupMRT(){
		turbo = newMRT().setTextured(compound.texture != null);
		lines = newMRT().setLines(true);
		sellines = newMRT().setLines(Settings.selectedColor);
	}
	
	protected abstract ModelRendererTurbo newMRT();

	public boolean apply(String id, float value, boolean x, boolean y, boolean z){
		boolean bool = false;
		switch(id){
			case "size":{
				if(this.getType().isCuboid()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
			case "pos": case "off": {
				bool = this.setFloat(id, x, y, z, value); break;
			}
			case "rot":{
				bool = this.setFloat(id, x, y, z, value); break; //(float)Math.toRadians(value)); break;
			}
			case "cor0": case "cor1": case "cor2": case "cor3": case "cor4": case "cor5": case "cor6": case "cor7":{
				if(this.getType().isShapebox()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
			case "cyl0": case "cyl1": case "cyl2": case "cyl3":{
				if(this.getType().isCylinder()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
		}
		this.recompile();
		return bool;
	}

	public PolygonWrapper setList(TurboList trlist){
		this.turbolist = trlist; return this;
	}

	public TurboList getList(){
		return turbolist;
	}
	
	public ModelRendererTurbo getTurboObject(int i){
		if(i < 0 || i > 2) i = 0; return i == 0 ? turbo : i == 1 ? lines : sellines;
	}
	
}
