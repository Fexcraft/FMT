package net.fexcraft.lib.fmr;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fexcraft.lib.fmr.polygons.Imported;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * Although this in combination with PolygonShape may seem similar to a "ModelRendererTurbo" object, the aim in<br>
 * general usage is slightly different. MRT objects usually get stored stored in an array which then does get<br>
 * looped through and rendered, the aim of this class though is to store all data multiple MRT's would in one<br>
 * object which does require only one call.<br>
 * Theoretically anyway.
 * <hr>
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelCompound {
	
	public int textureSizeX, textureSizeY;
	public float rotAngleX = 0, rotAngleY = 0, rotAngleZ = 0, rotPointX = 0, rotPointY = 0, rotPointZ = 0;
    private PolygonShape[] polygons;
	public boolean visible = true, lines;
	private String[] animations;
	private Integer dislist;
	//
    public static final int DIR_FRONT = 0, DIR_BACK = 1, DIR_LEFT = 2, DIR_RIGHT = 3, DIR_TOP = 4, DIR_BOTTOM = 5;
	
	/** Generic Empty **/
	public ModelCompound(){ reset(); }
	
	/** Importer **/
	public ModelCompound(ModelRendererTurbo[] array){
		reset(); insertAll(array);
	}

	public void render(){ render(FexcraftModelRenderer.MODELSCALE); }
    
    public void render(float scale){
        if(!visible){ return; }
        if(dislist == null){
            compileDisplayList(scale);
        }
        if(rotAngleX != 0.0F || rotAngleY != 0.0F || rotAngleZ != 0.0F){
            GL11.glPushMatrix();
            GL11.glTranslatef(rotPointX * scale, rotPointY * scale, rotPointZ * scale);
            if(rotAngleY != 0.0F){
                GL11.glRotatef(rotAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
            }
            if(rotAngleZ != 0.0F){
                GL11.glRotatef(rotAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
            }
            if(rotAngleX != 0.0F){
                GL11.glRotatef(rotAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
            }
    		GL11.glCallList(dislist);
            GL11.glPopMatrix();
        }
        else if(rotPointX != 0.0F || rotPointY != 0.0F || rotPointZ != 0.0F){
            GL11.glTranslatef(rotPointX * scale, rotPointY * scale, rotPointZ * scale);
    		GL11.glCallList(dislist);
            GL11.glTranslatef(-rotPointX * scale, -rotPointY * scale, -rotPointZ * scale);
        }
        else{
    		GL11.glCallList(dislist);
        }
    }
    
	private void compileDisplayList(float scale){
        dislist = GL11.glGenLists(1);
        GL11.glNewList(dislist, 4864 /*GL_COMPILE*/);
        for(int i = 0; i < polygons.length; i++){
            if(polygons[i].rotateAngleX != 0.0F || polygons[i].rotateAngleY != 0.0F || polygons[i].rotateAngleZ != 0.0F){
                GL11.glPushMatrix();
                GL11.glTranslatef(polygons[i].rotationPointX * scale, polygons[i].rotationPointY * scale, polygons[i].rotationPointY * scale);
                if(polygons[i].rotateAngleY != 0.0F){
                    GL11.glRotatef(polygons[i].rotateAngleY * 57.29578F, 0.0F, 1.0F, 0.0F);
                }
                if(polygons[i].rotateAngleZ != 0.0F){
                    GL11.glRotatef(polygons[i].rotateAngleZ * 57.29578F, 0.0F, 0.0F, 1.0F);
                }
                if(polygons[i].rotateAngleX != 0.0F){
                    GL11.glRotatef(polygons[i].rotateAngleX * 57.29578F, 1.0F, 0.0F, 0.0F);
                }
                for(int j = 0; j < polygons[i].faces.length; j++){
                    polygons[i].faces[j].draw(Tessellator.INSTANCE, scale, lines, null);
                }
                GL11.glPopMatrix();
            }
            else if(polygons[i].rotationPointX != 0.0F || polygons[i].rotationPointY != 0.0F || polygons[i].rotationPointZ != 0.0F){
                GL11.glTranslatef(polygons[i].rotationPointX * scale, polygons[i].rotationPointY * scale, polygons[i].rotationPointZ * scale);
                for(int j = 0; j < polygons[i].faces.length; j++){
                    polygons[i].faces[j].draw(Tessellator.INSTANCE, scale, lines, null);
                }
                GL11.glTranslatef(-polygons[i].rotationPointX * scale, -polygons[i].rotationPointY * scale, -polygons[i].rotationPointZ * scale);
            }
            else{
                for(int j = 0; j < polygons[i].faces.length; j++){
                    polygons[i].faces[j].draw(Tessellator.INSTANCE, scale, lines, null);
                }
            }
        }
        GL11.glEndList();
	}
	
    public void reset(){ polygons = new PolygonShape[]{}; }
	
    private void insertAll(ModelRendererTurbo[] array){
    	for(ModelRendererTurbo turbo : array){ insert(turbo); }
	}

	private void insert(ModelRendererTurbo turbo){
		insert(new Imported(Shape.IMPORTED, turbo.flip, turbo.mirror).importTMT(turbo));
	}
	
	public void insertAll(PolygonShape[] shapes){
		for(PolygonShape shape : shapes){ insert(shape); }
	}
	
	public void insertAll(Iterable<PolygonShape> shapes){
		for(PolygonShape shape : shapes){ insert(shape); }
	}
	
	public void insert(PolygonShape shape){
		polygons = Arrays.copyOf(polygons, polygons.length + 1);
		polygons[polygons.length - 1] = shape.setModel(this).compile();
	}
	
	public void queueRecompile(){
		GL11.glDeleteLists(dislist, 1);
		dislist = null; for(PolygonShape shape : polygons) shape.queueRecompile();
	}
	
	public JsonObject toJsonObject(){
		JsonObject obj = new JsonObject();
		if(animations != null){
			JsonArray array = new JsonArray();
			for(String str : animations) array.add(new JsonPrimitive(str));
			obj.add("animations", array);
		}
		obj.addProperty("texture_size_x", textureSizeX);
		obj.addProperty("texture_size_y", textureSizeY);
		obj.addProperty("rot_x", rotAngleX); obj.addProperty("rot_y", rotAngleY); obj.addProperty("rot_z", rotAngleZ);
		obj.addProperty("pos_x", rotPointX); obj.addProperty("pos_y", rotPointY); obj.addProperty("pos_z", rotPointZ);
		JsonArray array = new JsonArray();
		for(PolygonShape poly : polygons) array.add(poly.toJsonObject());
		obj.add("shapes", array);
		return obj;
	}

	public void translate(float x, float y, float z){
		this.rotPointX += x; this.rotPointY += y; this.rotPointZ += z;
	}

	public void rotate(float x, float y, float z){
		this.rotAngleX += x; this.rotAngleY += y; this.rotAngleZ += z;
	}

	public boolean shouldRender(){
		return /*visible && dislist != null &&*/ polygons.length > 0;
	}

	public ModelCompound setPosition(float x, float y, float z){
		this.rotPointX = x; this.rotPointY = y; this.rotPointZ = z; return this;
	}

	public ModelCompound setRotation(float x, float y, float z){
		this.rotAngleX = x; this.rotAngleY = y; this.rotAngleZ = z; return this;
	}

}
