package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.wrappers.face.CylFace;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.CylinderBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CylinderWrapper extends PolygonWrapper {
	
	public float radius = 2, radius2, length = 2, base = 1, top = 1;
	public int segments = 8, seglimit, direction = ModelRendererTurbo.MR_TOP;
	public Vec3f topoff = new Vec3f(0, 0, 0), toprot = new Vec3f(0, 0, 0);
	public boolean[] bools = new boolean[6];
	//
	public boolean radial;
	public float seg_width, seg_height;
	
	public CylinderWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		CylinderWrapper wrapper = new CylinderWrapper(compound);
		wrapper.radius = radius; wrapper.radius2 = radius2;
		wrapper.length = length; wrapper.base = base; wrapper.top = top;
		wrapper.segments = segments; wrapper.direction = direction;
		wrapper.seglimit = seglimit; wrapper.topoff = new Vec3f(topoff);
		wrapper.bools = new boolean[]{ bools[0], bools[1], bools[2], bools[2] };
		wrapper.radial = radial; wrapper.seg_width = seg_width; wrapper.seg_height = seg_height;
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = new ModelRendererTurbo(null, textureX(), textureY(), compound.tx(getTurboList()), compound.ty(getTurboList()));
		if(radius != 0f || radial || usesTopRotation() || cuv.anyCustom()){
			CylinderBuilder builder = turbo.newCylinderBuilder().setPosition(off.xCoord, off.yCoord, off.zCoord)
				.setRadius(radius, radius2).setLength(length).setSegments(segments, seglimit).setScale(base, top)
				.setDirection(direction).setTopOffset(topoff).removePolygons(bools);
			if(cuv.anyCustom()){
				for(UVCoords coord : cuv.values()){
					if(!isFaceActive(coord.face())) continue;//disabled
					builder.setPolygonUV(coord.side().index(), coord.value());
					if(coord.absolute()) builder.setDetachedUV(coord.side().index());
				}
			}
			if(radial) builder.setRadialTexture(seg_width, seg_height);
			else builder.setTopRotation(toprot);
			builder.build();
		}
		/*else if(radius2 != 0){
			turbo.addHollowCylinder(off.xCoord, off.yCoord, off.zCoord, radius, radius2, length, segments, seglimit, base, top, direction, getTopOff(), bools);
		}*/
		else{
			turbo.addCylinder(off.xCoord, off.yCoord, off.zCoord, radius, length, segments, base, top, direction, getTopOff());
		}
		return turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord).setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	private Vec3f getTopOff(){
		return topoff.xCoord == 0f && topoff.yCoord == 0f && topoff.zCoord == 0f ? null : topoff;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.CYLINDER;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cyl0": return x ? radius : y ? length : z ? radius2 : 0;
			case "cyl1": return x ? segments : y ? direction : z ? seglimit : 0;
			case "cyl2": return x ? base : y ? top : 0;
			case "cyl3": return x ? topoff.xCoord : y ? topoff.yCoord : z ? topoff.zCoord : 0;
			case "cyl4": return x ? (bools[0] ? 1 : 0) : y ? (bools[1] ? 1 : 0) : 0;
			case "cyl5": return x ? (bools[2] ? 1 : 0) : y ? (bools[3] ? 1 : 0) : 0;
			case "cyl6": return x ? (radial ? 1 : 0) : y ? seg_width : z ? seg_height : 0;
			case "cyl7": return x ? toprot.xCoord : y ? toprot.yCoord : z ? toprot.zCoord : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "cyl0":{
				if(x){
					radius = value; seg_width = radius / 2;
					GeneralEditor.cyl6_y.apply(seg_width);
					return true;
				}
				if(y){
					length = value; seg_height = radius - radius2;
					GeneralEditor.cyl6_z.apply(seg_height);
					return true;
				}
				if(z){
					radius2 = value; seg_height = radius - radius2;
					GeneralEditor.cyl6_z.apply(seg_height);
					return true;
				}
			}
			case "cyl1":{
				if(x){ segments = (int)value; return true; }
				if(y){ direction = (int)value; return true; }
				if(z){ seglimit = (int)value; if(seglimit > segments) seglimit = segments; return true; }
			}
			case "cyl2":{
				if(x){ base = value; return true; }
				if(y){ top = value; return true; }
				if(z){ return false; }//topangle = value; if(topangle < -360) topangle = -360; if(topangle > 360) topangle = 360; return false; }
			}
			case "cyl3":{
				if(x){ topoff.xCoord = value; return true; }
				if(y){ topoff.yCoord = value; return true; }
				if(z){ topoff.zCoord = value; return true; }
			}
			case "cyl4":{
				if(x){ bools[0] = value == 1; return true; }
				if(y){ bools[1] = value == 1; return true; }
				if(z){ bools[4] = value == 1; return true; }
			}
			case "cyl5":{
				if(x){ bools[2] = value == 1; return true; }
				if(y){ bools[3] = value == 1; return true; }
				if(z){ bools[5] = value == 1; return true; }
			}
			case "cyl6":{
				if(x){ radial = value == 1; return true; }
				if(y){ seg_width = (int)value; return true; }
				if(z){ seg_height = (int)value; return true; }
			}
			case "cyl7":{
				if(x){ toprot.xCoord = value; return true; }
				if(y){ toprot.yCoord = value; return true; }
				if(z){ toprot.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("radius", radius);
		if(radius2 != 0f){
			obj.addProperty("radius2", radius2);
		}
		obj.addProperty("length", length);
		obj.addProperty("segments", segments);
		if(seglimit != 0){
			obj.addProperty("seglimit", seglimit);
		}
		obj.addProperty("direction", direction);
		obj.addProperty("basescale", base);
		obj.addProperty("topscale", top);
		if(topoff.xCoord != 0f) obj.addProperty("top_offset_x", topoff.xCoord);
		if(topoff.yCoord != 0f) obj.addProperty("top_offset_y", topoff.yCoord);
		if(topoff.zCoord != 0f) obj.addProperty("top_offset_z", topoff.zCoord);
		if(toprot.xCoord != 0f) obj.addProperty("top_rotation_x", toprot.xCoord);
		if(toprot.yCoord != 0f) obj.addProperty("top_rotation_y", toprot.yCoord);
		if(toprot.zCoord != 0f) obj.addProperty("top_rotation_z", toprot.zCoord);
		boolean bool = false; for(boolean bl : bools) if(bl) bool = true;
		if(bool){
			JsonArray array = new JsonArray();
			for(boolean bl : bools) array.add(bl);
			obj.add("faces_off", array);
		}
		//if(topangle != 0f) obj.addProperty("top_angle", topangle);
		if(radial){
			obj.addProperty("radialtex", radial);
			obj.addProperty("seg_width", seg_width);
			obj.addProperty("seg_height", seg_height);
		}
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		float dia = (int)Math.floor(radius * 2F);
		float the = (int)Math.floor(length);
		if(radius < 1){
			int rad = radius < 0.5 ? 1 : 2;
			if(dia < rad) dia = rad;
		}
		if(length < 1) the = 1;
		else if(length % 1 != 0){
			the = (int)length + (length % 1 > 0.5f ? 1 : 0);
		}
		float dia2 = dia + dia;
		float[][][] vecs = new float[6][][];
		float height = radial ? detached(0) ? 0 : seg_height : dia;
		if(!bools[0] && !absolute(0, exclude_detached)){
			if(radial){
				vecs[0] = new float[][]{
					new float[]{ 0, 0 },
					new float[]{ (seg_width * segments), seg_height }
				};
			}
			else{
				vecs[0] = new float[][]{
					new float[]{ 0, 0 },
					new float[]{ dia, dia }
				};
			}
			if(include_offsets && !cuv.get(CylFace.CYL_BASE).automatic()){
				vecs[0] = getCoords(CylFace.CYL_BASE, vecs[0]);
			}
		}
		if(!bools[1] && !absolute(1, exclude_detached)){
			if(radial){
				vecs[1] = new float[][]{
					new float[]{ 0, seg_height },
					new float[]{ (seg_width * segments), seg_height }
				};
				height += seg_height;
			}
			else{
				boolean det = detached(0);
				vecs[1] = new float[][]{
					new float[]{ det ? 0 : dia, 0 },
					new float[]{ (det ? 0 : dia) + dia, 0 + dia }
				};
			}
			if(include_offsets && !cuv.get(CylFace.CYL_TOP).automatic()){
				vecs[1] = getCoords(CylFace.CYL_TOP, vecs[1]);
			}
		}
		if(!bools[2] && !absolute(2, exclude_detached)){
			vecs[2] = new float[][]{
				new float[]{ 0, height },
				new float[]{ dia2, 0 + height + the }
			};
			if(include_offsets && !cuv.get(CylFace.CYL_OUTER).automatic()){
				vecs[2] = getCoords(CylFace.CYL_OUTER, vecs[2]);
			}
		}
		if(radius2 != 0f){
			if(!bools[3] && !absolute(3, exclude_detached)){
				float hei = (detached(2) ? 0 : the);
				vecs[3] = new float[][]{
					new float[]{ 0, height + hei },
					new float[]{ 0 + dia2, 0 + height + hei + the }
				};
				if(include_offsets && !cuv.get(CylFace.CYL_INNER).automatic()){
					vecs[3] = getCoords(CylFace.CYL_INNER, vecs[3]);
				}
			}
			if(seglimit > 0 && seglimit < segments){
				float seg = radius - radius2;
				if(seg < 1) seg = 1;
				else if(seg % 1 != 0){
					seg = (int)seg + (seg % 1 > 0.5f ? 1 : 0);
				}
				float beg = detached(2) && detached(3) ? 0 : dia2;
				if(!bools[4] && !absolute(4, exclude_detached)){
					vecs[4] = new float[][]{
						new float[]{ beg, height },
						new float[]{ beg + seg, height + the }
					};
					if(include_offsets && !cuv.get(CylFace.SEG_SIDE_0).automatic()){
						vecs[4] = getCoords(CylFace.SEG_SIDE_0, vecs[4]);
					}
				}
				if(!bools[5] && !absolute(5, exclude_detached)){
					float shi = detached(2) || detached(3) ? seg : 0;
					float hai = detached(2) || detached(3) ? 0 : the;
					vecs[5] = new float[][]{
						new float[]{ beg + shi, height + hai },
						new float[]{ beg + shi + seg, height + hai + the }
					};
					if(include_offsets && !cuv.get(CylFace.SEG_SIDE_1).automatic()){
						vecs[5] = getCoords(CylFace.SEG_SIDE_1, vecs[5]);
					}
				}
			}
			else{
				vecs[4] = new float[][]{ { 0, 0 }, { 0, 0 } };
				vecs[5] = new float[][]{ { 0, 0 }, { 0, 0 } };
			}
		}
		else{
			vecs[3] = new float[][]{ { 0, 0 }, { 0, 0 } };
			vecs[4] = new float[][]{ { 0, 0 }, { 0, 0 } };
			vecs[5] = new float[][]{ { 0, 0 }, { 0, 0 } };
		}
		return vecs;
	}

	private boolean absolute(int index, boolean exclude_detached){
		return exclude_detached && cuv.get(CylFace.values()[index]).absolute();
	}

	private boolean detached(int i){
		return bools[i] || cuv.get(CylFace.values()[i]).absolute();
	}
	
	private float[][] getCoords(Face face, float[][] def){
		UVCoords coords = cuv.get(face);
		float[] arr = coords.value();
		float[][] res = null;
		switch(coords.type()){
			case ABSOLUTE:
			case OFFSET_ONLY:{
				def[1][0] -= def[0][0];
				def[1][1] -= def[0][1];
				def[0][0] = def[0][1] = 0;
				res = new float[][]{
					new float[]{ def[0][0] + arr[0], def[0][1] + arr[1] },
					new float[]{ def[1][0] + arr[0], def[1][1] + arr[1] }
				};
				break;
			}
			default: return null;
		}
		return res;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == ShapeType.CYLINDER ? this.clone() : null;
	}

	public boolean usesTopRotation(){
		return toprot.xCoord != 0f || toprot.yCoord != 0f || toprot.zCoord != 0f;
	}

	@Override
	public Face[] getTexturableFaces(){
		return CylFace.values();
	}

	@Override
	public boolean isFaceActive(String str){
		return isFaceActive(Face.byId(str, true));
	}

	@Override
	public boolean isFaceActive(Face other){
		if(other instanceof CylFace == false) return false;
		switch((CylFace)other){
			case CYL_BASE:
				return !bools[0];
			case CYL_TOP:
				return !bools[1];
			case CYL_OUTER:
				return !bools[2];
			case CYL_INNER:
				return radius2 != 0f && !bools[3];
			case SEG_SIDE_0:
			case SEG_SIDE_1:
				return radius2 != 0f && (seglimit > 0 && seglimit < segments);
			default:
				return false;
		}
	}

	public boolean anySidesOff(){
		boolean result = false;
		for(boolean bool : bools) if(bool) result = true;
		return result;
	}

	/*public Vec3f getTopOffForDir(float val){
		Vec3f vec = new Vec3f();
		switch(direction){
			case 0: vec.zCoord -= val; break;
			case 1: vec.zCoord += val; break;
			case 2: vec.xCoord += val; break;
			case 3: vec.xCoord -= val; break;
			case 4: vec.yCoord -= val; break;
			case 5: vec.yCoord += val; break;
		}
		return vec;
	}*/
	
}
