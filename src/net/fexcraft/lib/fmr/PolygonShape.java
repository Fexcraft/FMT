package net.fexcraft.lib.fmr;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.lib.fmr.polygons.Cuboid;
import net.fexcraft.lib.fmr.polygons.Cylinder;
import net.fexcraft.lib.fmr.polygons.Imported;
import net.fexcraft.lib.fmr.polygons.Shapebox;
import net.fexcraft.lib.fmr.polygons.Sphere;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
**/
public abstract class PolygonShape {
	
	public String name;
	public final Shape type;
	protected boolean compiled;
	public boolean flip, mirror;
	protected ModelCompound model;
    protected TexturedPolygon faces[];
    protected TexturedVertex vertices[];
    public float rotateAngleX, rotateAngleY, rotateAngleZ, rotationPointX, rotationPointY, rotationPointZ, scale = 1f;
	protected float offsetX, offsetY, offsetZ;
    public float texoffx, texoffy;
	
	public PolygonShape(Shape type){ this.type = type; }
	
	public PolygonShape(Shape type, boolean flip, boolean mirror){
		this.flip = flip; this.mirror = mirror; this.type = type;
	}
	
	/** */
	public static PolygonShape fromJsonObject(JsonObject obj, Object... objs){
		if(!obj.has("type")) return null;
		Shape type = Shape.fromString(obj.get("type").getAsString());
		String name = obj.has("name") ? obj.get("name").getAsString() : null;
		float scale = JsonUtil.getIfExists(obj, "scale", 1f).floatValue();
		boolean flip = JsonUtil.getIfExists(obj, "flip", false);
		boolean mirror = JsonUtil.getIfExists(obj, "mirror", false);
		float[] offset = new float[]{
			JsonUtil.getIfExists(obj, "offset_x", 1f).floatValue(),
			JsonUtil.getIfExists(obj, "offset_y", 1f).floatValue(),
			JsonUtil.getIfExists(obj, "offset_z", 1f).floatValue()
		};
		float[] pos = new float[]{
			JsonUtil.getIfExists(obj, "pos_x", 1f).floatValue(),
			JsonUtil.getIfExists(obj, "pos_y", 1f).floatValue(),
			JsonUtil.getIfExists(obj, "pos_z", 1f).floatValue()
		};
		float texx = JsonUtil.getIfExists(obj, "texture_x", 0).floatValue();
		float texy = JsonUtil.getIfExists(obj, "texture_y", 0).floatValue();
		switch(type){
			case BOX:{
				float width = JsonUtil.getIfExists(obj, "width", 0).floatValue();
				float height = JsonUtil.getIfExists(obj, "height", 0).floatValue();
				float depth = JsonUtil.getIfExists(obj, "depth", 0).floatValue();
				return new Cuboid(type, flip, mirror)
					.setSize(width, height, depth).setName(name).setScale(scale)
					.setOffset(offset[0], offset[1], offset[2]).setTexture(texx, texy)
					.setPosition(pos[0], pos[1], pos[2]);
			}
			case SHAPEBOX:{
				float width = JsonUtil.getIfExists(obj, "width", 0).floatValue();
				float height = JsonUtil.getIfExists(obj, "height", 0).floatValue();
				float depth = JsonUtil.getIfExists(obj, "depth", 0).floatValue();
				float[][] corners = new float[8][];
				for(int i = 0; i < corners.length; i++){
					corners[i] = new float[3];
					corners[i][0] = JsonUtil.getIfExists(obj, "x" + i, 0f).floatValue();
					corners[i][1] = JsonUtil.getIfExists(obj, "y" + i, 0f).floatValue();
					corners[i][2] = JsonUtil.getIfExists(obj, "z" + i, 0f).floatValue();
				}
				return new Shapebox(flip, mirror).setSize(width, height, depth)
					.setCorner(0, corners[0][0], corners[0][1], corners[0][2])
					.setCorner(1, corners[1][0], corners[1][1], corners[1][2])
					.setCorner(2, corners[2][0], corners[2][1], corners[2][2])
					.setCorner(3, corners[3][0], corners[3][1], corners[3][2])
					.setCorner(4, corners[4][0], corners[4][1], corners[4][2])
					.setCorner(5, corners[5][0], corners[5][1], corners[5][2])
					.setCorner(6, corners[6][0], corners[6][1], corners[6][2])
					.setCorner(7, corners[7][0], corners[7][1], corners[7][2])
					.setName(name).setScale(scale).setOffset(offset[0], offset[1], offset[2])
					.setTexture(texx, texy).setPosition(pos[0], pos[1], pos[2]);
			}
			case CYLINDER:{
				float radius = JsonUtil.getIfExists(obj, "radius", 0).floatValue();
				float length = JsonUtil.getIfExists(obj, "length", 0).floatValue();
				int segments = JsonUtil.getIfExists(obj, "segments", 0).intValue();
				float base = JsonUtil.getIfExists(obj, "base_scale", 1f).floatValue();
				float top = JsonUtil.getIfExists(obj, "top_scale", 1f).floatValue();
				int direction = JsonUtil.getIfExists(obj, "direction", 0).intValue();
				return new Cylinder(flip, mirror).setRadius(radius).setLength(length).setSegments(segments)
					.setScale(base, top).setDirection(direction).setOffset(offset[0], offset[1], offset[2])
					.setTexture(texx, texy).setPosition(pos[0], pos[1], pos[2]).setName(name);
			}
			case SPHERE:{
				float radius = JsonUtil.getIfExists(obj, "radius", 0).floatValue();
				int rings = JsonUtil.getIfExists(obj, "rings", 4).intValue();
				int segments = JsonUtil.getIfExists(obj, "segments", 0).intValue();
				return new Sphere(flip, mirror).setRadius(radius).setRings(rings).setSegments(segments)
					.setOffset(offset[0], offset[1], offset[2]).setTexture(texx, texy)
					.setPosition(pos[0], pos[1], pos[2]).setName(name);
			}
			case IMPORTED:{
				if(objs != null && objs[0] != null){
					if(objs[0] instanceof ModelRendererTurbo){
						ModelRendererTurbo turbo = (ModelRendererTurbo)objs[0];
						return new Imported(Shape.IMPORTED, turbo.flip, turbo.mirror).importTMT(turbo);
					}
				}
				return new Imported(Shape.IMPORTED).importFMRJSON(obj).setName(name).setScale(scale)
					.setOffset(offset[0], offset[1], offset[2]).setPosition(pos[0], pos[1], pos[2]);
			}
			case POLYGON: return null; //TODO
			case WAVEFRONT_OBJ:{
				if(objs != null && objs[0] != null){
					if(objs[0] instanceof String){
						return new Imported(type, flip, mirror).importOBJ((String)objs[0]);
					}
					/*else if(objs[0] instanceof ResourceLocation){
						return new Imported(type, flip, mirror).importOBJ((ResourceLocation)objs[0]);
					}*/
					else return null;
				}
				return null;
			}
			case UNDEFINED:
			default: return null;
		}
	}

	public JsonObject toJsonObject(){
		JsonObject obj = new JsonObject();
		if(name != null) obj.addProperty("name", name);
		obj.addProperty("type", type.name().toLowerCase());
		if(flip) obj.addProperty("flip", flip);
		if(mirror) obj.addProperty("mirror", mirror);
		if(scale != 1f) obj.addProperty("scale", scale);
		if(offsetX != 0f) obj.addProperty("offset_x", offsetX);
		if(offsetY != 0f) obj.addProperty("offset_y", offsetY);
		if(offsetZ != 0f) obj.addProperty("offset_z", offsetZ);
		if(rotationPointX != 0f) obj.addProperty("pos_x", rotationPointX);
		if(rotationPointY != 0f) obj.addProperty("pos_y", rotationPointY);
		if(rotationPointZ != 0f) obj.addProperty("pos_z", rotationPointZ);
		if(rotateAngleX != 0f) obj.addProperty("rot_x", rotateAngleX);
		if(rotateAngleY != 0f) obj.addProperty("rot_y", rotateAngleY);
		if(rotateAngleZ != 0f) obj.addProperty("rot_z", rotateAngleZ);
		this.populateJsonObject(obj);
		return obj;
	}

	/** Fill the JSON with polygon-type specific data. **/
	protected abstract void populateJsonObject(JsonObject obj);

	public PolygonShape compile(){
		return compiled ? this : compileShape();
	}

	protected abstract PolygonShape compileShape();
	
	public PolygonShape setName(String name){
		this.name = name; return this;
	}
	
	public PolygonShape setScale(float scale){
		this.scale = scale; return this;
	}
	
	public PolygonShape setOffset(float x, float y, float z){
		offsetX = x; offsetY = y; offsetZ = z; return this;
	}
	
	public PolygonShape setTexture(float x, float y){
		this.texoffx = x; this.texoffy = y; return this;
	}
	
	public PolygonShape setPosition(float x, float y, float z){
		this.rotationPointX = x; this.rotationPointY = y; this.rotationPointZ = z; return this;
	}
	
	public PolygonShape setRotation(float x, float y, float z){
		this.rotateAngleX = x; this.rotateAngleY = y; this.rotateAngleZ = z; return this;
	}
	
    protected TexturedPolygon toPolygon(TexturedVertex[] verts, float f, float g, float h, float j){
    	if(verts.length < 3){ return null; }
    	float uOffs = 1.0F / (model.textureSizeX * 10.0F);
    	float vOffs = 1.0F / (model.textureSizeY * 10.0F);
    	if(verts.length < 4){
    		float xMin = -1, yMin = -1;
    		float xMax =  0, yMax =  0;
    		for(int i = 0; i < verts.length; i++){
    			float xPos = verts[i].textureX; float yPos = verts[i].textureY;
    			xMax = Math.max(xMax, xPos);
    			xMin = (xMin < -1 ? xPos : Math.min(xMin, xPos));
    			yMax = Math.max(yMax, yPos);
    			yMin = (yMin < -1 ? yPos : Math.min(yMin, yPos));
    		}
    		float uMin = f / model.textureSizeX + uOffs;
    		float vMin = g / model.textureSizeY + vOffs;
    		float uSize = (h - f) / model.textureSizeX - uOffs * 2;
    		float vSize = (j - g) / model.textureSizeY - vOffs * 2;
    		float xSize = xMax - xMin;
    		float ySize = yMax - yMin;
    		for(int i = 0; i < verts.length; i++){
    			float xPos = verts[i].textureX, yPos = verts[i].textureY;
    			xPos = (xPos - xMin) / xSize; yPos = (yPos - yMin) / ySize;
    			verts[i] = verts[i].setTexturePosition(uMin + (xPos * uSize), vMin + (yPos * vSize));
    		}
    	}
    	else{
	    	verts[0] = verts[0].setTexturePosition(h / model.textureSizeX - uOffs, g / model.textureSizeY + vOffs);
	    	verts[1] = verts[1].setTexturePosition(f / model.textureSizeX + uOffs, g / model.textureSizeY + vOffs);
	    	verts[2] = verts[2].setTexturePosition(f / model.textureSizeX + uOffs, j / model.textureSizeY - vOffs);
	    	verts[3] = verts[3].setTexturePosition(h / model.textureSizeX - uOffs, j / model.textureSizeY - vOffs);
    	}
    	return new TexturedPolygon(verts);
    }
    
    public PolygonShape setFlipped(boolean bool){
    	this.flip = bool; return this;
    }
    
    public PolygonShape setMirrored(boolean bool){
    	this.mirror = bool; return this;
    }
    
    public void queueRecompile(){ compiled = false; }
    
    protected PolygonShape setModel(ModelCompound compound){
    	this.model = compound; return this;
    }

}
