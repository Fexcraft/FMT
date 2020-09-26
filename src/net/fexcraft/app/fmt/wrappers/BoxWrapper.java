package net.fexcraft.app.fmt.wrappers;

import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.BoxBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class BoxWrapper extends PolygonWrapper {
	
	public Vec3f size = new Vec3f(1, 1, 1);
	public boolean[] sides = new boolean[6];
	
	public BoxWrapper(GroupCompound compound){
		super(compound);
	}

	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = initMRT()
 			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.xCoord, off.yCoord, off.zCoord).setSize(size.xCoord, size.yCoord, size.zCoord).removePolygons(sides);
		if(cuv.anyCustom()){
			for(UVCoords coord : cuv.values()){
				if(!isFaceActive(coord.face())) continue;//disabled
				builder.setPolygonUV(coord.side().index(), coord.value());
				if(coord.absolute()) builder.setDetachedUV(coord.side().index());
			}
		}
		return builder.build();
	}
	
	protected ModelRendererTurbo initMRT(){
		return new ModelRendererTurbo(null, textureX(), textureY(), compound.tx(getTurboList()), compound.ty(getTurboList())){
			@Override
			public RGB getColor(int i){
				return this.textured ? null : super.getColor(getUnShiftedIndex(i));
			}
		};
	}
	
	@Override
	public int getUnShiftedIndex(int shifted){
		for(Face face : BoxFace.values()){
			int index = getShiftedIndex(face);
			if(index == shifted){
				return face.index();
			}
		}
		return 0;
	}

	private int getShiftedIndex(Face face){
		if(sides[0] && face == BoxFace.FRONT) return -1;
		int index = 0;
		for(int i = 0; i < 6; i++){
			if(face.index() == i) return sides[i] ? -1 : index;
			if(!sides[i]) index++;
		}
		return -1;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.BOX;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "size": return x ? size.xCoord : y ? size.yCoord : z ? size.zCoord : 0;
			case "side0": return x ? (sides[0] ? 1 : 0) : y ? (sides[1] ? 1 : 0) : z ? (sides[2] ? 1 : 0) : 0;
			case "side1": return x ? (sides[3] ? 1 : 0) : y ? (sides[4] ? 1 : 0) : z ? (sides[5] ? 1 : 0) : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "size":{
				if(x){ size.xCoord = value; return true; }
				if(y){ size.yCoord = value; return true; }
				if(z){ size.zCoord = value; return true; }
			}
			case "side0":{
				if(x){ sides[0] = value == 1; return true; }
				if(y){ sides[1] = value == 1; return true; }
				if(z){ sides[2] = value == 1; return true; }
			}
			case "side1":{
				if(x){ sides[3] = value == 1; return true; }
				if(y){ sides[4] = value == 1; return true; }
				if(z){ sides[5] = value == 1; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("width", size.xCoord);
		obj.addProperty("height", size.yCoord);
		obj.addProperty("depth", size.zCoord);
		boolean anysides = false;
		for(boolean bool : sides) if(bool) anysides = true;
		if(anysides){
			JsonArray array = new JsonArray();
			for(boolean bool : sides) array.add(bool);
			obj.add("sides_off", array);
		}
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		float w = size.xCoord, h = size.yCoord, d = size.zCoord;
        if(w % 1 != 0) w = w < 1 ? 1 : (int)w + (w % 1 > 0.5f ? 1 : 0);
        if(h % 1 != 0) h = h < 1 ? 1 : (int)h + (h % 1 > 0.5f ? 1 : 0);
        if(d % 1 != 0) d = d < 1 ? 1 : (int)d + (d % 1 > 0.5f ? 1 : 0);
		float[][][] vecs = new float[6][][];
		//
    	float yp = detached(2) && detached(3) ? 0 : d;
    	float x0 = detached(1) ? 0 : d;
    	float x1 = detached(2) ? 0 : w;
    	float x2 = detached(4) ? 0 : w;
    	float x3 = detached(0) ? 0 : d;
		//
		if(!sides[0] && !absolute(0, exclude_detached)){
			vecs[0] = new float[][]{
				new float[]{ x0 + x2, yp },
				new float[]{ x0 + x2 + d, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.FRONT).automatic()){
				vecs[0] = getCoords(BoxFace.FRONT, vecs[0]);
			}
		}
		if(!sides[1] && !absolute(1, exclude_detached)){
			vecs[1] = new float[][]{
				new float[]{ 0, yp },
				new float[]{ d, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.BACK).automatic()){
				vecs[1] = getCoords(BoxFace.BACK, vecs[1]);
			}
		}
		if(!sides[2] && !absolute(2, exclude_detached)){
			vecs[2] = new float[][]{
				new float[]{ x0, 0 },
				new float[]{ x0 + w, d }
			};
			if(include_offsets && !cuv.get(BoxFace.TOP).automatic()){
				vecs[2] = getCoords(BoxFace.TOP, vecs[2]);
			}
		}
		if(!sides[3] && !absolute(3, exclude_detached)){
			vecs[3] = new float[][]{
				new float[]{ x0 + x1, 0 },
				new float[]{ x0 + x1 + w, d }
			};
			if(include_offsets && !cuv.get(BoxFace.DOWN).automatic()){
				vecs[3] = getCoords(BoxFace.DOWN, vecs[3]);
			}
		}
		if(!sides[4] && !absolute(4, exclude_detached)){
			vecs[4] = new float[][]{
				new float[]{ x0, yp },
				new float[]{ x0 + w, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.RIGHT).automatic()){
				vecs[4] = getCoords(BoxFace.RIGHT, vecs[4]);
			}
		}
		if(!sides[5] && !absolute(5, exclude_detached)){
			vecs[5] = new float[][]{
				new float[]{ x0 + x2 + x3, yp },
				new float[]{ x0 + x2 + x3 + w, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.LEFT).automatic()){
				vecs[5] = getCoords(BoxFace.LEFT, vecs[5]);
			}
		}
		return vecs;
	}

	private boolean absolute(int index, boolean exclude_detached){
		return exclude_detached && cuv.get(BoxFace.values()[index]).absolute();
	}

	private boolean detached(int i){
		return sides[i] || cuv.get(BoxFace.values()[i]).absolute();
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
			case ABSOLUTE_ENDS:
			case OFFSET_ENDS:{
				float minx, miny, maxx, maxy;
				minx = maxx = arr[0];
				miny = maxy = arr[1];
				if(minx > arr[2]) minx = arr[2];
				if(maxx < arr[2]) maxx = arr[2];
				if(miny > arr[3]) miny = arr[3];
				if(maxy < arr[3]) maxy = arr[3];
				res = new float[][]{
					new float[]{ minx, miny },
					new float[]{ maxx, maxy }
				};
				break;
			}
			case ABSOLUTE_FULL:
			case OFFSET_FULL:{
				float minx, miny, maxx, maxy;
				minx = maxx = arr[0];
				miny = maxy = arr[1];
				for(int i = 0; i < 4; i++){
					if(arr[i * 2] < minx) minx = arr[i * 2];
					if(arr[i * 2 + 1] < miny) miny = arr[i * 2 + 1];
					if(arr[i * 2] > maxx) maxx = arr[i * 2];
					if(arr[i * 2 + 1] > maxy) maxy = arr[i * 2 + 1];
				}
				res = new float[][]{
					new float[]{ minx, miny },
					new float[]{ maxx, maxy }
				};
				break;
			}
			default: return null;
		}
		return res;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		BoxWrapper wrapper = new BoxWrapper(compound);
		wrapper.size = new Vec3f(size);
		wrapper.sides = Arrays.copyOf(sides, 6);
		wrapper.cuv.copyFrom(wrapper, cuv);
		return wrapper;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(type == ShapeType.BOX) return this.clone();
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		BoxWrapper wrapper = null;
		switch(type){
			case QUAD: wrapper = new QuadWrapper(compound); break;
			case SHAPEQUAD: wrapper = new ShapeQuadWrapper(compound); break;
			case SHAPEBOX: wrapper = new ShapeboxWrapper(compound); break;
			default: return null;
		}
		wrapper.size = new Vec3f(size);
		wrapper.sides = Arrays.copyOf(sides, 6);
		wrapper.cuv.copyFrom(wrapper, cuv);
		return copyTo(wrapper, true);
	}

	public boolean anySidesOff(){
		boolean result = false;
		for(boolean bool : sides) if(bool) result = true;
		return result;
	}

	@Override
	public Face[] getTexturableFaces(){
		return BoxFace.values();
	}

	@Override
	public boolean isFaceActive(String str){
		for(Face face : getTexturableFaces()){
			if(face.id().equals(str)) return !sides[face.index()];
		}
		return false;
	}

	@Override
	public boolean isFaceActive(Face other){
		for(Face face : getTexturableFaces()){
			if(face == other) return !sides[face.index()];
		}
		return false;
	}
	
}
