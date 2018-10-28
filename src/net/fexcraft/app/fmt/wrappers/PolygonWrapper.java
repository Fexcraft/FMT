package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class PolygonWrapper {
	
	/*private static final ModelRendererTurbo sphere = new ModelRendererTurbo(null, 256, 256);
	static {
		//sphere.insert(new Sphere().setRadius(1).setRings(4).setSegments(4));
		//sphere.insert(new Cuboid().setSize(1, 1, 1).setPosition(-0.5f, -0.5f, -0.5f).setTexture(0, 0));
		sphere.addBox(-0.5f, -0.5f, -0.5f, 1, 1, 1); sphere.textured = true;
	}
	private static final RGB yellow = new RGB(255, 255, 0);*/
	//
	public Vec3f pos = new Vec3f(), off = new Vec3f(), rot = new Vec3f();
	public int textureX, textureY;
	protected ModelRendererTurbo turbo, lines;
	protected final GroupCompound compound;
	public boolean visible = true;
	public boolean mirror, flip;
	public String name;
	
	public PolygonWrapper(GroupCompound compound){
		this.compound = compound;
	}
	
	public abstract void recompile();

	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render();
		if(lines != null && Settings.lines()) lines.render();
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
		if(rot.xCoord != 0) obj.addProperty("rot_x", rot.xCoord);
		if(rot.yCoord != 0) obj.addProperty("rot_y", rot.yCoord);
		if(rot.zCoord != 0) obj.addProperty("rot_z", rot.zCoord);
		if(mirror != false) obj.addProperty("mirror", true);
		if(flip != false) obj.addProperty("flip", true);
		//temporary data
		if(!export){
			obj.addProperty("visible", visible);
		}
		return populateJson(obj, export);
	}

	protected abstract JsonObject populateJson(JsonObject obj, boolean export);
	
}
