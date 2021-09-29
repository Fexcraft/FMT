package net.fexcraft.app.fmt.wrappers;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.BoxBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ShapeboxWrapper extends BoxWrapper {
	
	public Vec3f cor0 = new Vec3f(), cor1 = new Vec3f(), cor2 = new Vec3f(), cor3 = new Vec3f(),
				 cor4 = new Vec3f(), cor5 = new Vec3f(), cor6 = new Vec3f(), cor7 = new Vec3f();
	//public boolean bool[] = new boolean[]{ false, false, false, false, false, false };
	private Axis3DL axe = new Axis3DL();
	
	public ShapeboxWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		ShapeboxWrapper wrapper = new ShapeboxWrapper(compound);
		wrapper.cor0 = new Vec3f(cor0); wrapper.cor1 = new Vec3f(cor1); wrapper.cor2 = new Vec3f(cor2); wrapper.cor3 = new Vec3f(cor3);
		wrapper.cor4 = new Vec3f(cor4); wrapper.cor5 = new Vec3f(cor5); wrapper.cor6 = new Vec3f(cor6); wrapper.cor7 = new Vec3f(cor7);
		wrapper.size = new Vec3f(size); //wrapper.bool = new boolean[]{ bool[0], bool[1], bool[2], bool[3], bool[4], bool[5] };
		wrapper.sides = Arrays.copyOf(sides, 6);
		wrapper.cuv.copyFrom(wrapper, cuv);
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = initMRT()
			.setRotationPoint(pos.x, pos.y, pos.z)
			.setRotationAngle(rot.x, rot.y, rot.z);
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.x, off.y, off.z).setSize(size.x, size.y, size.z).removePolygons(sides);
		builder.setCorners(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);
		if(cuv.anyCustom()){
			for(UVCoords coord : cuv.values()){
				if(!isFaceActive(coord.face())) continue;//disabled
				builder.setPolygonUV(coord.side().index(), coord.value());
				if(coord.absolute()) builder.setDetachedUV(coord.side().index());
			}
		}
		return builder.build();
	}

	@Override
	public ShapeType getType(){
		return ShapeType.SHAPEBOX;
	}
	
	private static ModelRendererTurbo[] cornermarkers = new ModelRendererTurbo[8];
	/*private static RGB[] cornercolors = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 0, 255), new RGB(0, 255, 0),
		new RGB(255, 0, 127), new RGB(0, 127, 255), new RGB(0, 127, 0), new RGB(127, 0, 255)
	};*/
	public static RGB[] cornercolors2 = new RGB[]{
		new RGB(255, 255, 0), new RGB(255, 0, 0), new RGB(0, 127, 255), new RGB(255, 0, 127),
		new RGB(0, 255, 0), new RGB(0, 0, 255), new RGB(0, 127, 0), new RGB(127, 0, 255)
	};
	static{
		for(int i = 0; i < 8; i++){
			cornermarkers[i] = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f).setTextured(false).setColor(cornercolors2[i]);
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
				rotmarker2.setRotationPoint(lines.rotationPointX, lines.rotationPointY, lines.rotationPointZ);
				rotmarker2.render();
				GL11.glPushMatrix();
				axe.setAngles(-rot.y, -rot.z, -rot.x);
				Vec3f vector = null;
				for(int i = 0; i < cornermarkers.length; i++){
					vector = axe.getRelativeVector(corneroffset(i).add(off));
					cornermarkers[i].setPosition(vector.x + pos.x, vector.y + pos.y, vector.z + pos.z);
					cornermarkers[i].setRotationAngle(rot.x, rot.y, rot.z);
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

	private Vec3f corneroffset(int index){
		switch(index){
			case 0: return new Vec3f(-cor0.x, -cor0.y, -cor0.z);
			case 1: return new Vec3f(cor1.x + size.x, -cor1.y, -cor1.z);
			case 2: return new Vec3f(cor2.x + size.x, -cor2.y, cor2.z + size.z);
			case 3: return new Vec3f(-cor3.x, -cor3.y, cor3.z + size.z);
			case 4: return new Vec3f(-cor4.x, cor4.y + size.y, -cor4.z);
			case 5: return new Vec3f(cor5.x + size.x, cor5.y + size.y, -cor5.z);
			case 6: return new Vec3f(cor6.x + size.x, cor6.y + size.y, cor6.z + size.z);
			case 7: return new Vec3f(-cor7.x, cor7.y + size.y, cor7.z + size.z);
			default: return null;
		}
	}

	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cor0": return x ? cor0.x : y ? cor0.y : z ? cor0.z : 0;
			case "cor1": return x ? cor1.x : y ? cor1.y : z ? cor1.z : 0;
			case "cor2": return x ? cor2.x : y ? cor2.y : z ? cor2.z : 0;
			case "cor3": return x ? cor3.x : y ? cor3.y : z ? cor3.z : 0;
			case "cor4": return x ? cor4.x : y ? cor4.y : z ? cor4.z : 0;
			case "cor5": return x ? cor5.x : y ? cor5.y : z ? cor5.z : 0;
			case "cor6": return x ? cor6.x : y ? cor6.y : z ? cor6.z : 0;
			case "cor7": return x ? cor7.x : y ? cor7.y : z ? cor7.z : 0;
			//case "face0": return x ? bool[0] ? 1 : 0 : y ? bool[1] ? 1 : 0 : z ? bool[2] ? 1 : 0 : 0;
			//case "face1": return x ? bool[3] ? 1 : 0 : y ? bool[4] ? 1 : 0 : z ? bool[5] ? 1 : 0 : 0;
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
			case "cor4":{
				if(x){ cor4.x = value; return true; }
				if(y){ cor4.y = value; return true; }
				if(z){ cor4.z = value; return true; }
			}
			case "cor5":{
				if(x){ cor5.x = value; return true; }
				if(y){ cor5.y = value; return true; }
				if(z){ cor5.z = value; return true; }
			}
			case "cor6":{
				if(x){ cor6.x = value; return true; }
				if(y){ cor6.y = value; return true; }
				if(z){ cor6.z = value; return true; }
			}
			case "cor7":{
				if(x){ cor7.x = value; return true; }
				if(y){ cor7.y = value; return true; }
				if(z){ cor7.z = value; return true; }
			}
			/*case "face0":{
				if(x){ bool[0] = (int)value == 1; return true; }
				if(y){ bool[1] = (int)value == 1; return true; }
				if(z){ bool[2] = (int)value == 1; return true; }
			}
			case "face1":{
				if(x){ bool[3] = (int)value == 1; return true; }
				if(y){ bool[4] = (int)value == 1; return true; }
				if(z){ bool[5] = (int)value == 1; return true; }
			}*/
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
		if(cor4.x != 0) obj.addProperty("x4", cor4.x);
		if(cor4.y != 0) obj.addProperty("y4", cor4.y);
		if(cor4.z != 0) obj.addProperty("z4", cor4.z);
		//
		if(cor5.x != 0) obj.addProperty("x5", cor5.x);
		if(cor5.y != 0) obj.addProperty("y5", cor5.y);
		if(cor5.z != 0) obj.addProperty("z5", cor5.z);
		//
		if(cor6.x != 0) obj.addProperty("x6", cor6.x);
		if(cor6.y != 0) obj.addProperty("y6", cor6.y);
		if(cor6.z != 0) obj.addProperty("z6", cor6.z);
		//
		if(cor7.x != 0) obj.addProperty("x7", cor7.x);
		if(cor7.y != 0) obj.addProperty("y7", cor7.y);
		if(cor7.z != 0) obj.addProperty("z7", cor7.z);
		//
		/*if(!export){
			JsonArray array = new JsonArray();
			for(boolean bool : bool) array.add(bool);
			obj.add("face_triangle_flip", array);
		}*/
		return obj;
	}


	public ShapeboxWrapper setCoords(Vec3f xyz0, Vec3f xyz1, Vec3f xyz2, Vec3f xyz3, Vec3f xyz4, Vec3f xyz5, Vec3f xyz6, Vec3f xyz7){
		cor0 = xyz0; cor1 = xyz1; cor2 = xyz2; cor3 = xyz3; cor4 = xyz4; cor5 = xyz5; cor6 = xyz6; cor7 = xyz7; return this;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		if(type == ShapeType.QUAD){ QuadWrapper box = new QuadWrapper(compound); box.size = new Vec3f(size); return copyTo(box, true); }
		if(type == ShapeType.BOX){
			BoxWrapper box = new BoxWrapper(compound);
			box.size = new Vec3f(size);
			box.sides = Arrays.copyOf(sides, 6);
			box.cuv.copyFrom(box, cuv);
			return copyTo(box, true);
		}
		if(type == ShapeType.SHAPEBOX) return this.clone();
		/*ShapeboxWrapper wrapper = null;
		switch(type){
			case TEXRECT_A: wrapper = new TexrectWrapperA(compound); break;
			case TEXRECT_B: wrapper = new TexrectWrapperB(compound); break;
			default: return null;
		}
		wrapper.size = new Vec3f(size);
		wrapper.sets(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);
		wrapper.sides = Arrays.copyOf(sides, 6);
		wrapper.cuv.copyFrom(wrapper, cuv);
		return copyTo(wrapper, true);*/
		return null;
	}
	
	public Vec3f[] cornerArray(){
		return new Vec3f[]{ cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7 };
	}
	
}
