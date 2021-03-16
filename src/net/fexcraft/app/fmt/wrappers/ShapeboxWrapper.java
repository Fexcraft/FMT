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
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.xCoord, off.yCoord, off.zCoord).setSize(size.xCoord, size.yCoord, size.zCoord).removePolygons(sides);
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
	public void renderLines(boolean rotXb, boolean rotYb, boolean rotZb){
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
				axe.setAngles(-rot.yCoord, -rot.zCoord, -rot.xCoord);
				Vec3f vector = null;
				for(int i = 0; i < cornermarkers.length; i++){
					vector = axe.getRelativeVector(corneroffset(i).add(off));
					cornermarkers[i].setPosition(vector.xCoord + pos.xCoord, vector.yCoord + pos.yCoord, vector.zCoord + pos.zCoord);
					cornermarkers[i].setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
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
			case 0: return new Vec3f(-cor0.xCoord, -cor0.yCoord, -cor0.zCoord);
			case 1: return new Vec3f(cor1.xCoord + size.xCoord, -cor1.yCoord, -cor1.zCoord);
			case 2: return new Vec3f(cor2.xCoord + size.xCoord, -cor2.yCoord, cor2.zCoord + size.zCoord);
			case 3: return new Vec3f(-cor3.xCoord, -cor3.yCoord, cor3.zCoord + size.zCoord);
			case 4: return new Vec3f(-cor4.xCoord, cor4.yCoord + size.yCoord, -cor4.zCoord);
			case 5: return new Vec3f(cor5.xCoord + size.xCoord, cor5.yCoord + size.yCoord, -cor5.zCoord);
			case 6: return new Vec3f(cor6.xCoord + size.xCoord, cor6.yCoord + size.yCoord, cor6.zCoord + size.zCoord);
			case 7: return new Vec3f(-cor7.xCoord, cor7.yCoord + size.yCoord, cor7.zCoord + size.zCoord);
			default: return null;
		}
	}

	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cor0": return x ? cor0.xCoord : y ? cor0.yCoord : z ? cor0.zCoord : 0;
			case "cor1": return x ? cor1.xCoord : y ? cor1.yCoord : z ? cor1.zCoord : 0;
			case "cor2": return x ? cor2.xCoord : y ? cor2.yCoord : z ? cor2.zCoord : 0;
			case "cor3": return x ? cor3.xCoord : y ? cor3.yCoord : z ? cor3.zCoord : 0;
			case "cor4": return x ? cor4.xCoord : y ? cor4.yCoord : z ? cor4.zCoord : 0;
			case "cor5": return x ? cor5.xCoord : y ? cor5.yCoord : z ? cor5.zCoord : 0;
			case "cor6": return x ? cor6.xCoord : y ? cor6.yCoord : z ? cor6.zCoord : 0;
			case "cor7": return x ? cor7.xCoord : y ? cor7.yCoord : z ? cor7.zCoord : 0;
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
			case "cor4":{
				if(x){ cor4.xCoord = value; return true; }
				if(y){ cor4.yCoord = value; return true; }
				if(z){ cor4.zCoord = value; return true; }
			}
			case "cor5":{
				if(x){ cor5.xCoord = value; return true; }
				if(y){ cor5.yCoord = value; return true; }
				if(z){ cor5.zCoord = value; return true; }
			}
			case "cor6":{
				if(x){ cor6.xCoord = value; return true; }
				if(y){ cor6.yCoord = value; return true; }
				if(z){ cor6.zCoord = value; return true; }
			}
			case "cor7":{
				if(x){ cor7.xCoord = value; return true; }
				if(y){ cor7.yCoord = value; return true; }
				if(z){ cor7.zCoord = value; return true; }
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
		if(cor4.xCoord != 0) obj.addProperty("x4", cor4.xCoord);
		if(cor4.yCoord != 0) obj.addProperty("y4", cor4.yCoord);
		if(cor4.zCoord != 0) obj.addProperty("z4", cor4.zCoord);
		//
		if(cor5.xCoord != 0) obj.addProperty("x5", cor5.xCoord);
		if(cor5.yCoord != 0) obj.addProperty("y5", cor5.yCoord);
		if(cor5.zCoord != 0) obj.addProperty("z5", cor5.zCoord);
		//
		if(cor6.xCoord != 0) obj.addProperty("x6", cor6.xCoord);
		if(cor6.yCoord != 0) obj.addProperty("y6", cor6.yCoord);
		if(cor6.zCoord != 0) obj.addProperty("z6", cor6.zCoord);
		//
		if(cor7.xCoord != 0) obj.addProperty("x7", cor7.xCoord);
		if(cor7.yCoord != 0) obj.addProperty("y7", cor7.yCoord);
		if(cor7.zCoord != 0) obj.addProperty("z7", cor7.zCoord);
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
		wrapper.setCoords(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);
		wrapper.sides = Arrays.copyOf(sides, 6);
		wrapper.cuv.copyFrom(wrapper, cuv);
		return copyTo(wrapper, true);*/
		return null;
	}
	
}
