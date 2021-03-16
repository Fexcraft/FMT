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
			.addShapeQuad(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, 0, cor0.xCoord, cor0.yCoord, cor0.zCoord,
				cor1.xCoord, cor1.yCoord, cor1.zCoord, cor2.xCoord, cor2.yCoord, cor2.zCoord, cor3.xCoord, cor3.yCoord, cor3.zCoord)
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
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
	public void renderLines(boolean rotXb, boolean rotYb, boolean rotZb){
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		if((selected || this.getTurboList().selected) && Settings.polygonMarker()){
			if(this.compound.getLastSelected() != this){
				rotmarker.setRotationPoint(lines.rotationPointX, lines.rotationPointY, lines.rotationPointZ);
				rotmarker.render();
			}
			else{
				GL11.glPushMatrix();
				if(rot.xCoord != 0f) GL11.glRotatef(rot.xCoord, 1, 0, 0);
				if(rot.yCoord != 0f) GL11.glRotatef(rot.yCoord, 0, 1, 0);
				if(rot.zCoord != 0f) GL11.glRotatef(rot.zCoord, 0, 0, 1);
				Vec3f vector = null;
				for(int i = 0; i < cornermarkers.length; i++){
					vector = turbo.getFaces().get(0).getVertices()[i].vector;
					cornermarkers[i].setPosition(vector.xCoord + pos.xCoord, vector.yCoord + pos.yCoord, vector.zCoord + pos.zCoord);
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
			case "cor0": return x ? cor0.xCoord : y ? cor0.yCoord : z ? cor0.zCoord : 0;
			case "cor1": return x ? cor1.xCoord : y ? cor1.yCoord : z ? cor1.zCoord : 0;
			case "cor2": return x ? cor2.xCoord : y ? cor2.yCoord : z ? cor2.zCoord : 0;
			case "cor3": return x ? cor3.xCoord : y ? cor3.yCoord : z ? cor3.zCoord : 0;
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
				if(x){ cor0.xCoord = value; return true; }
				if(y){ cor0.yCoord = value; return true; }
				if(z){ cor0.zCoord = value; return true; }
			}
			case "cor1":{
				if(x){ cor1.xCoord = value; return true; }
				if(y){ cor1.yCoord = value; return true; }
				if(z){ cor1.zCoord = value; return true; }
			}
			case "cor2":{
				if(x){ cor2.xCoord = value; return true; }
				if(y){ cor2.yCoord = value; return true; }
				if(z){ cor2.zCoord = value; return true; }
			}
			case "cor3":{
				if(x){ cor3.xCoord = value; return true; }
				if(y){ cor3.yCoord = value; return true; }
				if(z){ cor3.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		if(cor0.xCoord != 0) obj.addProperty("x0", cor0.xCoord);
		if(cor0.yCoord != 0) obj.addProperty("y0", cor0.yCoord);
		if(cor0.zCoord != 0) obj.addProperty("z0", cor0.zCoord);
		//
		if(cor1.xCoord != 0) obj.addProperty("x1", cor1.xCoord);
		if(cor1.yCoord != 0) obj.addProperty("y1", cor1.yCoord);
		if(cor1.zCoord != 0) obj.addProperty("z1", cor1.zCoord);
		//
		if(cor2.xCoord != 0) obj.addProperty("x2", cor2.xCoord);
		if(cor2.yCoord != 0) obj.addProperty("y2", cor2.yCoord);
		if(cor2.zCoord != 0) obj.addProperty("z2", cor2.zCoord);
		//
		if(cor3.xCoord != 0) obj.addProperty("x3", cor3.xCoord);
		if(cor3.yCoord != 0) obj.addProperty("y3", cor3.yCoord);
		if(cor3.zCoord != 0) obj.addProperty("z3", cor3.zCoord);
		//
		/*if(!export){
			JsonArray array = new JsonArray();
			for(boolean bool : bool) array.add(bool);
			obj.add("face_triangle_flip", array);
		}*/
		return obj;
	}


	public ShapeQuadWrapper setCoords(Vec3f xyz0, Vec3f xyz1, Vec3f xyz2, Vec3f xyz3){
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
		wrapper.size = new Vec3f(size); wrapper.setCoords(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);*///TODO
		return null;//copyTo(wrapper, true);
	}
	
}
