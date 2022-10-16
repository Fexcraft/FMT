package net.fexcraft.app.fmt.polygon;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.gen.Generator;

public class Box extends Polygon {
	
	public Vector3f size = new Vector3f(1);
	public boolean[] sides = new boolean[6];
	
	public Box(Model model){
		super(model);
		pos.y = -1;
	}

	protected Box(Model model, JsonMap obj){
		super(model, obj);
		size.x = obj.get("width", 1f);
		size.y = obj.get("height", 1f);
		size.z = obj.get("depth", 1f);
		if(obj.has("sides_off")){
			JsonArray array = obj.getArray("sides_off");
			for(int i = 0; i < sides.length; i++){
				if(i >= array.size()) break;
				sides[i] = array.get(i).value();
			}
		}
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("width", size.x);
		map.add("height", size.y);
		map.add("depth", size.z);
		boolean anysides = false;
		for(boolean bool : sides) if(bool) anysides = true;
		if(anysides){
			JsonArray array = new JsonArray();
			for(boolean bool : sides) array.add(bool);
			map.add("sides_off", array);
		}
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.BOX;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		Generator<GLObject> gen = new Generator<GLObject>(glm, glm.glObj.grouptex ? group().texSizeX : model().texSizeX, glm.glObj.grouptex ? group().texSizeY : model().texSizeY)
			.set("type", Generator.Type.CUBOID)
			.set("x", off.x)
			.set("y", off.y)
			.set("z", off.z)
			.set("width", size.x)
			.set("height", size.y)
			.set("depth", size.z);
		for(int i = 0; i < sides.length; i++) if(sides[i]) gen.removePolygon(i);
		if(cuv.any()){
			ArrayList<Integer> list = new ArrayList<>();
			ArrayList<float[]> uv = new ArrayList<>();
			for(int i = 0; i < 6; i++){
				if(cuv.get(BoxFace.values()[i]).detached()) list.add(i);
				uv.add(cuv.get(BoxFace.values()[i]).value());
			}
			gen.set("detached_uv", list);
			gen.set("uv", uv);
		}
		return gen;
	}

	@Override
	public RGB getFaceColor(int idx){
		switch(idx){
			case 0: return blu0;
			case 1: return blu1;
			case 2: return red1;
			case 3: return red0;
			case 4: return gre1;
			case 5: return gre0;
		}
		return RGB.GREEN;
	}

	@Override
	public Face getFaceByColor(int color){
		if(color == c_blu0) return BoxFace.FRONT;
		if(color == c_blu1) return BoxFace.BACK;
		if(color == c_red1) return BoxFace.TOP;
		if(color == c_red0) return BoxFace.DOWN;
		if(color == c_gre1) return BoxFace.LEFT;
		if(color == c_gre0) return BoxFace.RIGHT;
		return NoFace.NONE;
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case SIZE: return getVectorValue(size, polyval.axe());
			case SIDES: return getIndexValue(sides, polyval.axe().ordinal());
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case SIZE: setVectorValue(size, polyval.axe(), value); break;
			case SIDES: setIndexValue(sides, polyval.axe().ordinal(), value); break;
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Box == false) return poly;
		Box box = (Box)poly;
		box.size.set(size);
		for(int i = 0; i < sides.length; i++) box.sides[i] = sides[i];
		return poly;
	}

	@Override
	public Face[] getUVFaces(){
		return BoxFace.values();
	}

	@Override
	public float[][][] newUV(boolean include_offsets, boolean exclude_detached) {
		float w = size.x, h = size.y, d = size.z;
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
		if(!sides[0] && !detached(0, exclude_detached)){
			vecs[0] = new float[][]{
				new float[]{ x0 + x2, yp },
				new float[]{ x0 + x2 + d, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.FRONT).automatic()){
				vecs[0] = gets(BoxFace.FRONT, vecs[0]);
			}
		}
		if(!sides[1] && !detached(1, exclude_detached)){
			vecs[1] = new float[][]{
				new float[]{ 0, yp },
				new float[]{ d, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.BACK).automatic()){
				vecs[1] = gets(BoxFace.BACK, vecs[1]);
			}
		}
		if(!sides[2] && !detached(2, exclude_detached)){
			vecs[2] = new float[][]{
				new float[]{ x0, 0 },
				new float[]{ x0 + w, d }
			};
			if(include_offsets && !cuv.get(BoxFace.TOP).automatic()){
				vecs[2] = gets(BoxFace.TOP, vecs[2]);
			}
		}
		if(!sides[3] && !detached(3, exclude_detached)){
			vecs[3] = new float[][]{
				new float[]{ x0 + x1, 0 },
				new float[]{ x0 + x1 + w, d }
			};
			if(include_offsets && !cuv.get(BoxFace.DOWN).automatic()){
				vecs[3] = gets(BoxFace.DOWN, vecs[3]);
			}
		}
		if(!sides[4] && !detached(4, exclude_detached)){
			vecs[4] = new float[][]{
				new float[]{ x0, yp },
				new float[]{ x0 + w, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.RIGHT).automatic()){
				vecs[4] = gets(BoxFace.RIGHT, vecs[4]);
			}
		}
		if(!sides[5] && !detached(5, exclude_detached)){
			vecs[5] = new float[][]{
				new float[]{ x0 + x2 + x3, yp },
				new float[]{ x0 + x2 + x3 + w, yp + h }
			};
			if(include_offsets && !cuv.get(BoxFace.LEFT).automatic()){
				vecs[5] = gets(BoxFace.LEFT, vecs[5]);
			}
		}
		return vecs;
	}

	private boolean detached(int index, boolean exclude_detached){
		return exclude_detached && cuv.get(BoxFace.values()[index]).detached();
	}

	private boolean detached(int i){
		return sides[i] || cuv.get(BoxFace.values()[i]).detached();
	}

	private float[][] gets(Face face, float[][] def){
		UVCoords coords = cuv.get(face);
		float[] arr = coords.value();
		float[][] res = null;
		switch(coords.type()){
			case DETACHED:
			case OFFSET:{
				def[1][0] -= def[0][0];
				def[1][1] -= def[0][1];
				def[0][0] = def[0][1] = 0;
				res = new float[][]{
					new float[]{ def[0][0] + arr[0], def[0][1] + arr[1] },
					new float[]{ def[1][0] + arr[0], def[1][1] + arr[1] }
				};
				break;
			}
			case DETACHED_ENDS:
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
			case DETACHED_FULL:
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
	public boolean isActive(Face face){
		for(Face fuv : BoxFace.values()){
			if(fuv == face) return !sides[face.index()];
		}
		return false;
	}

	public boolean anySidesOff(){
		for(boolean side : sides) if(side) return true;
		return false;
	}

}
