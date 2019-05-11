package net.fexcraft.app.fmt.wrappers;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class PolygonWrapper {
	
	protected static final ModelRendererTurbo rotmarker = new ModelRendererTurbo(null, 0, 0, 16, 16).addSphere(0, 0, 0, 0.5f, 8, 8, 0, 0).setTextured(false).setColor(Settings.getSelectedColor());
	private static final ModelRendererTurbo something = new ModelRendererTurbo(null, 0, 0, 16, 16).setTextured(false);
	//
	public Vec3f pos = new Vec3f(), off = new Vec3f(), rot = new Vec3f();
	public float[][][] texpos = new float[0][][];
	public int textureX, textureY;
	protected ModelRendererTurbo turbo, lines, sellines, picker;
	protected final GroupCompound compound;
	protected static boolean widelines;
	public boolean visible = true;
	private TurboList turbolist;
	public boolean mirror, flip;
	public boolean selected;
	public int[] color;
	public String name;
	
	public PolygonWrapper(GroupCompound compound){
		this.compound = compound;
	}
	
	public void recompile(){
		this.clearMRT(turbo, lines, sellines, picker); this.setupMRT();
		this.texpos = this.newTexturePosition();
	}

	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render();
	}
	
	public void renderLines(boolean rotXb, boolean rotYb, boolean rotZb){
		//if(Settings.lines()) (selected ? sellines : lines).render();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		if((selected || turbolist.selected) && Settings.polygonMarker()){
			rotmarker.setRotationPoint(lines.rotationPointX, lines.rotationPointY, lines.rotationPointZ);
			rotmarker.render();
		}
		if(Settings.lines()){
			if(selected || turbolist.selected){
				if(!widelines){ GL11.glLineWidth(4f); widelines = true; }
				if(sellines != null) sellines.render();
			}
			else{
				if(widelines){ GL11.glLineWidth(1f); widelines = false; }
				if(lines != null) lines.render();
			}
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void renderPicking(boolean rotXb, boolean rotYb, boolean rotZb){
		if(visible && picker != null){
			GL11.glDisable(GL11.GL_TEXTURE_2D); picker.render(); GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
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
		if(pos.xCoord != 0f) obj.addProperty("pos_x", pos.xCoord);
		if(pos.yCoord != 0f) obj.addProperty("pos_y", pos.yCoord);
		if(pos.zCoord != 0f) obj.addProperty("pos_z", pos.zCoord);
		//
		if(off.xCoord != 0f) obj.addProperty("off_x", off.xCoord);
		if(off.yCoord != 0f) obj.addProperty("off_y", off.yCoord);
		if(off.zCoord != 0f) obj.addProperty("off_z", off.zCoord);
		//
		if(rot.xCoord != 0f) obj.addProperty("rot_x", rot.xCoord);
		if(rot.yCoord != 0f) obj.addProperty("rot_y", rot.yCoord);
		if(rot.zCoord != 0f) obj.addProperty("rot_z", rot.zCoord);
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
		if(mrts == null) mrts = new ModelRendererTurbo[]{ turbo, lines, sellines, picker };
		for(ModelRendererTurbo mrt : mrts){
			if(mrt != null && mrt.displaylist() != null){
				GL11.glDeleteLists(mrt.displaylist(), 1); mrt = null;
			}
		}
	}
	
	protected void setupMRT(){
		turbo = newMRT().setTextured(compound.texture != null);
		lines = newMRT().setLines(true);
		sellines = newMRT().setLines(Settings.getSelectedColor());
		//
		picker = new ModelRendererTurbo(null, 0, 0, 16, 16){
			@Override
			public RGB getColor(int i){
				return new RGB(genColor(this, i));
			}
		};
		picker.copyTo(sellines.getVertices(), sellines.getFaces());
		picker.setRotationPoint(sellines.rotationPointX, sellines.rotationPointY, sellines.rotationPointZ);
		picker.setRotationAngle(sellines.rotationAngleX, sellines.rotationAngleY, sellines.rotationAngleZ);
		picker.setTextured(false).setLines(false);
	}
	
	//private static int maxcol = 16777215;
	private static int lastint = 0;
	
	private RGB genColor(ModelRendererTurbo turbo, int face){
		if(color == null || face >= color.length){
			color = new int[turbo.getFaces().length];
			for(int i = 0; i < color.length; i++){
				color[i] = lastint += 1;
				if(color[i] == 2048383){ color[i] = lastint += 1; }
			}
		}
		RGB rgb = new RGB(); rgb.packed = color[face]; return rgb;
	}

	protected abstract ModelRendererTurbo newMRT();
	
	protected abstract float[][][] newTexturePosition();

	public boolean apply(String id, float value, boolean x, boolean y, boolean z){
		boolean bool = false;
		switch(id){
			case "size":{
				if(this.getType().isCuboid()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
			case "tex":{
				bool = this.setFloat(id, x, y, z, value); break;
			}
			case "pos": case "off":{
				bool = this.setFloat(id, x, y, z, value); break;
			}
			case "rot":{
				bool = this.setFloat(id, x, y, z, value); break; //(float)Math.toRadians(value)); break;
			}
			case "cor0": case "cor1": case "cor2": case "cor3": case "cor4": case "cor5": case "cor6": case "cor7": case "face0": case "face1":{
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
		if(id.startsWith("texpos") && this.getType().isTexRect()){ bool = this.setFloat(id, x, y, z, value); }
		this.recompile(); return bool;
	}

	public PolygonWrapper setList(TurboList trlist){
		this.turbolist = trlist; return this;
	}
	
	public ModelRendererTurbo getTurboObject(int i){
		if(i < 0 || i > 2) i = 0; return i == 0 ? turbo : i == 1 ? lines : sellines;
	}
	
	public boolean burnToTexture(BufferedImage image, Integer face){
		if(this.texpos == null || this.texpos.length == 0){
			Print.console("Polygon '" + turbolist.id + ":" + this.name() + "' has no texture data, skipping.");
			return false;
		}
		if(face == null){
			for(int i = 0; i < texpos.length; i++){
				float[][] ends = texpos[i]; if(ends == null || ends.length == 0) continue;
				burn(image, ends, new Color(something.getColor(i).packed).darker().darker().getRGB());
			}
		}
		else if(face == -1){
			for(int i = 0; i < texpos.length; i++){
				float[][] ends = texpos[i]; if(ends == null || ends.length == 0) continue;
				burn(image, ends, new Color(TextureEditor.CURRENTCOLOR.packed).getRGB());
			}
		}
		else{
			if(this.getType().isCylinder()){
				int segs = (int)this.getFloat("cyl1", true, false, false);//segments
				float[][] ends = null;
				if(face < segs){
					ends = texpos[0];
				}
				else if(face < (segs * 2)){
					float per = (face - segs) * 100f / segs;
					//Print.console(false, new Object[]{ segs, face - segs, per });
					int i = 0; while((per -= 12.5f) > 0f) i++;
					//Print.console(false, new Object[]{ segs, face - segs, per, i });
					ends = i < 0 || i >= 8 ? null : texpos[i + 2];
				}
				else if(face < (segs * 3)){
					ends = texpos[1];
				} else return false;
				if(ends == null || ends.length == 0) return false;
				burn(image, ends, new Color(TextureEditor.CURRENTCOLOR.packed).getRGB());
			}
			else if(this.getType().isCuboid() && !this.getType().isTexRect()){
				float[][] ends = texpos[face]; if(ends == null || ends.length == 0) return false;
				burn(image, ends, new Color(TextureEditor.CURRENTCOLOR.packed).getRGB());
			}
			else{
				Print.console("There is no known way of how to handle texture burning of '" + this.getType().name() + "'!");
			}
		}
		return true;
	}
	
	private void burn(BufferedImage img, float[][] ends, int color){
		for(float x = ends[0][0]; x < ends[1][0]; x += 0.5f){
			for(float y = ends[0][1]; y < ends[1][1]; y += 0.5f){
				int xa = (int)(x + textureX), ya = (int)(y + textureY);
				if(xa >= 0 && xa < img.getWidth() && ya >= 0 && ya < img.getHeight()){
					img.setRGB(xa, ya, color);
				} else continue;
			}
		}
	}
	
	public TurboList getTurboList(){ return turbolist; }

	public PolygonWrapper clone(){
		PolygonWrapper wrapper = this.createClone(compound);
		return copyTo(wrapper, false);
	}
	
	public PolygonWrapper copyTo(PolygonWrapper wrapper, boolean copyvisibility){
		wrapper.pos = new Vec3f(pos); wrapper.off = new Vec3f(off); wrapper.rot = new Vec3f(rot);
		wrapper.textureX = textureX; wrapper.textureY = textureY;
		wrapper.visible = copyvisibility ? visible : true;
		wrapper.name = name == null ? null : name.endsWith("cp") ? name : name + "cp";
		wrapper.mirror = mirror; wrapper.flip = flip;
		return wrapper;
	}

	protected abstract PolygonWrapper createClone(GroupCompound compound);

	public abstract PolygonWrapper convertTo(ShapeType type);
	
}
