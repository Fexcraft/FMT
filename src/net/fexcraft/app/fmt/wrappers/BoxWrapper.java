package net.fexcraft.app.fmt.wrappers;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.BoxBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class BoxWrapper extends PolygonWrapper {
	
	public Vec3f size = new Vec3f(1, 1, 1);
	public boolean[] sides = new boolean[6];
	//
	public static String[] faces = { "front", "back", "top", "down", "right", "left" };
	
	public BoxWrapper(GroupCompound compound){
		super(compound);
	}

	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.xCoord, off.yCoord, off.zCoord).setSize(size.xCoord, size.yCoord, size.zCoord).removePolygon(sides);
		if(!uvtypes.isEmpty()){
			for(Map.Entry<String, float[]> entry : uvcoords.entrySet()){
				int index = getTexturableFaceIndex(entry.getKey());
				builder.setPolygonUV(index, entry.getValue());
			}
		}
		return builder.build();
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
	public float[][][] newTexturePosition(boolean include_offsets){
		float tx = 0 /*textureX*/, ty = 0 /*textureY*/, w = size.xCoord, h = size.yCoord, d = size.zCoord;
		int sideson = 0, sideid = 0;
		for(boolean bool : sides) if(!bool) sideson++;
		float[][][] vecs = new float[sideson][][];
		//
    	float yp = sides[2] && sides[3] ? 0 : d;
    	float x0 = sides[1] ? 0 : d;
    	float x1 = sides[2] ? 0 : w;
    	float x2 = sides[4] ? 0 : w;
    	float x3 = sides[0] ? 0 : d;
		//
		if(!sides[0]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx + x0 + x2, ty + yp },
				new float[]{ tx + x0 + x2 + d, ty + yp + h }
			};
			if(include_offsets && getFaceUVType(faces[0]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[0], vecs[sideid - 1]);
			}
		}
		if(!sides[1]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx, ty + yp },
				new float[]{ tx + d, ty + yp + h }
			};
			if(include_offsets && getFaceUVType(faces[1]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[1], vecs[sideid - 1]);
			}
		}
		if(!sides[2]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx + x0, ty },
				new float[]{ tx + x0 + w, ty + d }
			};
			if(include_offsets && getFaceUVType(faces[2]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[2], vecs[sideid - 1]);
			}
		}
		if(!sides[3]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx + x0 + x1, ty + 0 },
				new float[]{ tx + x0 + x1 + w, ty + d }
			};
			if(include_offsets && getFaceUVType(faces[3]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[3], vecs[sideid - 1]);
			}
		}
		if(!sides[4]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx + x0, ty + yp },
				new float[]{ tx + x0 + w, ty + yp + h }
			};
			if(include_offsets && getFaceUVType(faces[4]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[4], vecs[sideid - 1]);
			}
		}
		if(!sides[5]){
			vecs[sideid++] = new float[][]{
				new float[]{ tx + x0 + x2 + x3, ty + yp },
				new float[]{ tx + x0 + x2 + x3 + w, ty + yp + h }
			};
			if(include_offsets && getFaceUVType(faces[5]) != FaceUVType.AUTOMATIC){
				vecs[sideid - 1] = getCoords(faces[5], vecs[sideid - 1]);
			}
		}
		return vecs;
	}

	private float[][] getCoords(String string, float[][] def){
		FaceUVType type = getFaceUVType(string);
		float[] arr = getFaceUVCoords(string);
		float[][] res = null;
		switch(type){
			case OFFSET_ONLY:{
				res = new float[][]{
					new float[]{ def[0][0] + arr[0], def[0][1] + arr[1] },
					new float[]{ def[1][0] + arr[0], def[1][1] + arr[1] }
				};
				break;
			}
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
		wrapper.size = new Vec3f(size); return wrapper;
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
			case TEXRECT_A: wrapper = new TexrectWrapperA(compound); break;
			case TEXRECT_B: wrapper = new TexrectWrapperB(compound); break;
			default: return null;
		} wrapper.size = new Vec3f(size); return copyTo(wrapper, true);
	}

	public boolean anySidesOff(){
		boolean result = false;
		for(boolean bool : sides) if(bool) result = true;
		return result;
	}

	@Override
	public String[] getTexturableFaceIDs(){
		return faces;
	}
	
	@Override
	public Integer getTexturableFaceIndex(String str){
		int index = 0;
		for(int i = 0; i < faces.length; i++){
			if(faces[i].equals(str)) return index;
			if(!sides[i]) index++;
		}
		return null;
	}
	
}
