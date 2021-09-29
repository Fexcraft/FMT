package net.fexcraft.app.fmt.wrappers;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ShapeQuadWrapper extends QuadWrapper {
	
	public Vec3f cor0 = new Vec3f(), cor1 = new Vec3f(), cor2 = new Vec3f(), cor3 = new Vec3f();
	
	public ShapeQuadWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		ShapeQuadWrapper wrapper = new ShapeQuadWrapper(compound);
		wrapper.cor0 = new Vec3f(cor0); wrapper.cor1 = new Vec3f(cor1); wrapper.cor2 = new Vec3f(cor2); wrapper.cor3 = new Vec3f(cor3);
		wrapper.size = new Vec3f(size); //wrapper.bool = new boolean[]{ bool[0], bool[1], bool[2], bool[3], bool[4], bool[5] };
		return wrapper;
	}
	
	@SuppressWarnings("deprecation")
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
			.addShapeQuad(off.x, off.y, off.z, size.x, size.y, 0, cor0.x, cor0.y, cor0.z,
				cor1.x, cor1.y, cor1.z, cor2.x, cor2.y, cor2.z, cor3.x, cor3.y, cor3.z)
			.setRotationPoint(pos.x, pos.y, pos.z)
			.setRotationAngle(rot.x, rot.y, rot.z);
		//for(int i = 0; i < bool.length; i++){ turbo.getFaces()[i].setOppositeTriangles(bool[i]); }
		//turbo.triline = true; return turbo;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.SHAPEQUAD;
	}
	
	private static ModelRendererTurbo[] cornermarkers = new ModelRendererTurbo[4];
	private static RGB[] cornercolors = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 0, 255), new RGB(0, 255, 0)
	};
	public static RGB[] cornercolors2 = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 127, 255), new RGB(255, 0, 127)
	};
	static{
		for(int i = 0; i < 4; i++){
			cornermarkers[i] = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f).setTextured(false).setColor(cornercolors[i]);
		}
	}
	
	@Override
	public void renderLines(){
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		if((selected || this.getTurboList().selected) && Settings.polygonMarker()){
			if(this.compound.getLastSelected() != this){
				rotmarker.setRotationPoint(lines.rotationPointX, lines.rotationPointY, lines.rotationPointZ);
				rotmarker.render();
			}
			else{
				GL11.glPushMatrix();
				if(rot.x != 0f) GL11.glRotatef(rot.x, 1, 0, 0);
				if(rot.y != 0f) GL11.glRotatef(rot.y, 0, 1, 0);
				if(rot.z != 0f) GL11.glRotatef(rot.z, 0, 0, 1);
				Vec3f vector = null;
				for(int i = 0; i < cornermarkers.length; i++){
					vector = turbo.getFaces().get(0).getVertices()[i].vector;
					cornermarkers[i].setPosition(vector.x + pos.x, vector.y + pos.y, vector.z + pos.z);
					cornermarkers[i].render();
				}
				GL11.glPopMatrix();
			}
		}
		if(Settings.lines()){
			if(selected || this.getTurboList().selected){
				if(!widelines){ GL11.glLineWidth(4f); widelines = true; }
				if(sellines != null) sellines.render();
			}
			else{
				if(widelines){ GL11.glLineWidth(1f); widelines = false; }
				if(lines != null) lines.render();
			}
		}
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cor0": return x ? cor0.x : y ? cor0.y : z ? cor0.z : 0;
			case "cor1": return x ? cor1.x : y ? cor1.y : z ? cor1.z : 0;
			case "cor2": return x ? cor2.x : y ? cor2.y : z ? cor2.z : 0;
			case "cor3": return x ? cor3.x : y ? cor3.y : z ? cor3.z : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		//if(!id.startsWith("cor")) return false;
		//int iID = Integer.parseInt(id.replace("cor", ""));
		switch(id){
			case "cor0":{
				if(x){ cor0.x = value; return true; }
				if(y){ cor0.y = value; return true; }
				if(z){ cor0.z = value; return true; }
			}
			case "cor1":{
				if(x){ cor1.x = value; return true; }
				if(y){ cor1.y = value; return true; }
				if(z){ cor1.z = value; return true; }
			}
			case "cor2":{
				if(x){ cor2.x = value; return true; }
				if(y){ cor2.y = value; return true; }
				if(z){ cor2.z = value; return true; }
			}
			case "cor3":{
				if(x){ cor3.x = value; return true; }
				if(y){ cor3.y = value; return true; }
				if(z){ cor3.z = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		if(cor0.x != 0) obj.addProperty("x0", cor0.x);
		if(cor0.y != 0) obj.addProperty("y0", cor0.y);
		if(cor0.z != 0) obj.addProperty("z0", cor0.z);
		//
		if(cor1.x != 0) obj.addProperty("x1", cor1.x);
		if(cor1.y != 0) obj.addProperty("y1", cor1.y);
		if(cor1.z != 0) obj.addProperty("z1", cor1.z);
		//
		if(cor2.x != 0) obj.addProperty("x2", cor2.x);
		if(cor2.y != 0) obj.addProperty("y2", cor2.y);
		if(cor2.z != 0) obj.addProperty("z2", cor2.z);
		//
		if(cor3.x != 0) obj.addProperty("x3", cor3.x);
		if(cor3.y != 0) obj.addProperty("y3", cor3.y);
		if(cor3.z != 0) obj.addProperty("z3", cor3.z);
		//
		/*if(!export){
			JsonArray array = new JsonArray();
			for(boolean bool : bool) array.add(bool);
			obj.add("face_triangle_flip", array);
		}*/
		return obj;
	}


	public ShapeQuadWrapper sets(Vec3f xyz0, Vec3f xyz1, Vec3f xyz2, Vec3f xyz3){
		cor0 = xyz0; cor1 = xyz1; cor2 = xyz2; cor3 = xyz3; return this;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		if(type == ShapeType.QUAD){ QuadWrapper box = new QuadWrapper(compound); box.size = new Vec3f(size); return copyTo(box, true); }
		if(type == ShapeType.BOX){ BoxWrapper box = new BoxWrapper(compound); box.size = new Vec3f(size); return copyTo(box, true); }
		/*if(type == ShapeType.SHAPEBOX) return this.clone();
		AdvFaceWrapper wrapper = null;
		switch(type){
			case TEXRECT_A: wrapper = new TexrectWrapperA(compound); break;
			case TEXRECT_B: wrapper = new TexrectWrapperB(compound); break;
			default: return null;
		}
		wrapper.size = new Vec3f(size); wrapper.sets(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);*///TODO
		return null;//copyTo(wrapper, true);
	}
	
}
