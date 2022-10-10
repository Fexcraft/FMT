package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.polygon.uv.CylFace;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.gen.AxisDir;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class Cylinder extends Polygon {
	
	public float radius = 2, radius2, length = 1, base = 1, top = 1;
	public int segments = 8, seglimit, direction = ModelRendererTurbo.MR_TOP;
	public Vector3f topoff = new Vector3f(), toprot = new Vector3f();
	public boolean[] bools = new boolean[6];
	public boolean radial;
	public float seg_width, seg_height;
	
	public Cylinder(Model model){
		super(model);
		pos.y = -1;
	}

	protected Cylinder(Model model, JsonMap obj, int format){
		super(model, obj);
		radius = obj.get("radius", radius);
		radius2 = obj.get("radius2", radius2);
		length = obj.get("length", length);
		segments = obj.get("segments", segments);
		seglimit = obj.get("seglimit", seglimit);
		direction = obj.get("direction", direction);
		if(format < 4 && (direction == 2 || direction == 3)){
			direction = direction == 2 ? 3 : 2;
		}
		base = obj.get("basescale", base);
		top = obj.get("topscale", top);
		topoff = getVector(obj, "top_offset_%s", 0f);
		toprot = getVector(obj, "top_rotation_%s", 0f);
		if(obj.has("faces_off")){
			JsonArray array = obj.getArray("faces_off");
			for(int i = 0; i < bools.length; i++){
				if(i >= array.size()) break;
				bools[i] = array.get(i).value();
			}
		}
		radial = obj.get("radialtex", radial);
		seg_width = obj.get("seg_width", seg_width);
		seg_height = obj.get("seg_height", seg_height);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("radius", radius);
		map.add("radius2", radius2);
		map.add("length", length);
		map.add("segments", segments);
		map.add("seglimit", seglimit);
		map.add("direction", direction);
		map.add("basescale", base);
		map.add("topscale", top);
		setVector(map, "top_offset_%s", topoff);
		setVector(map, "top_rotation_%s", toprot);
		boolean anyoff = false;
		for(boolean bool : bools) if(bool) anyoff = true;
		if(anyoff){
			JsonArray array = new JsonArray();
			for(boolean bool : bools) array.add(bool);
			map.add("faces_off", array);
		}
		map.add("radialtex", radial);
		map.add("seg_width", seg_width);
		map.add("seg_height", seg_height);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.CYLINDER;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		Generator<GLObject> gen = new Generator<GLObject>(glm, glm.glObj.grouptex ? group().texSizeX : model().texSizeX, glm.glObj.grouptex ? group().texSizeY : model().texSizeY)
			.set("type", Generator.Type.CYLINDER)
			.set("x", off.x)
			.set("y", off.y)
			.set("z", off.z)
			.set("radius", radius)
			.set("radius2", radius2)
			.set("length", length)
			.set("axis_dir", AxisDir.values()[direction])
			.set("segments", segments)
			.set("seg_limit", seglimit)
			.set("base_scale", base)
			.set("top_scale", top);
		if(topoff.x != 0f || topoff.y != 0f || topoff.z != 0f){
			gen.set("top_offset", new Vec3f(topoff.x, topoff.y, topoff.z));
		}
		if(toprot.x != 0f || toprot.y != 0f || toprot.z != 0f){
			AxisRotator axe = AxisRotator.newDefInstance();
			axe.setAngles(toprot.x, toprot.y, toprot.z);
			gen.set("top_rot", axe);
		}
		if(radial){
			gen.set("radial", true);
			gen.set("seg_width", seg_width);
			gen.set("seg_height", seg_height);
		}
		for(int i = 0; i < bools.length; i++) if(bools[i]) gen.removePolygon(i);
		if(cuv.any()){
			ArrayList<Integer> list = new ArrayList<>();
			ArrayList<float[]> uv = new ArrayList<>();
			for(int i = 0; i < 6; i++){
				if(cuv.get(CylFace.values()[i]).detached()) list.add(i);
				uv.add(cuv.get(CylFace.values()[i]).value());
			}
			gen.set("detached_uv", list);
			gen.set("uv", uv);
		}
		return gen;
	}

	@Override
	public RGB getFaceColor(int idx){
		int segs = seglimit < segments && seglimit > 0 ? seglimit : segments;
		if(idx < segs){
			return blu0;
		}
		if(idx < segs * 2){
			return blu1;
		}
		if(idx < segs * 3){
			return red1;
		}
		if(idx < segs * 4){
			return red0;
		}
		return idx % 2 == 1 ? gre1 : gre0;
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case RADIUS: return radius;
			case RADIUS2: return radius2;
			case LENGTH: return length;
			case SEGMENTS: return segments;
			case SEG_LIMIT: return seglimit;
			case DIRECTION: return direction;
			case BASE_SCALE: return base;
			case TOP_SCALE: return top;
			case TOP_OFF: return getVectorValue(topoff, polyval.axe());
			case TOP_ROT: return getVectorValue(toprot, polyval.axe());
			case RADIAL: return getBooleanAsIntValue(radial);
			case SEG_WIDTH: return seg_width;
			case SEG_HEIGHT: return seg_height;
			case SIDES: return getIndexValue(bools, polyval.axe().ordinal());
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case RADIUS: radius = value; break;
			case RADIUS2: radius2 = value; break;
			case LENGTH: length = value; break;
			case SEGMENTS: segments = (int)value; break;
			case SEG_LIMIT: seglimit = (int)value; break;
			case DIRECTION: direction = (int)value; break;
			case BASE_SCALE: base = value; break;
			case TOP_SCALE: top = value; break;
			case TOP_OFF: setVectorValue(topoff, polyval.axe(), value); break;
			case TOP_ROT: setVectorValue(toprot, polyval.axe(), value); break;
			case RADIAL: radial = parseBooleanValue(value); break;
			case SEG_WIDTH: seg_width = value; break;
			case SEG_HEIGHT: seg_height = value; break;
			case SIDES: setIndexValue(bools, polyval.axe().ordinal(), value); break;
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	public boolean usesTopRotation(){
		return toprot.x != 0f || toprot.y != 0f || toprot.z != 0f;
	}

	private Vec3f getTopOff(){
		return topoff.x == 0f && topoff.y == 0f && topoff.z == 0f ? null : new Vec3f(topoff.x, topoff.y, topoff.z);
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Cylinder == false) return poly;
		Cylinder cyl = (Cylinder)poly;
		cyl.radius = radius;
		cyl.radius2 = radius2;
		cyl.length = length;
		cyl.segments = segments;
		cyl.seglimit = seglimit;
		cyl.direction = direction;
		cyl.base = base;
		cyl.top = top;
		cyl.topoff.set(topoff);
		cyl.toprot.set(toprot);
		cyl.radial = radial;
		cyl.seg_width = seg_width;
		cyl.seg_height = seg_height;
		for(int i = 0; i < bools.length; i++) cyl.bools[i] = bools[i];
		return poly;
	}

	@Override
	public Face[] getUVFaces(){
		return CylFace.values();
	}

	@Override
	public float[][][] newUV(boolean include_offsets, boolean exclude_detached){
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
		if(!bools[0] && !detached(0, exclude_detached)){
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
				vecs[0] = gets(CylFace.CYL_BASE, vecs[0]);
			}
		}
		if(!bools[1] && !detached(1, exclude_detached)){
			if(radial){
				vecs[1] = new float[][]{
					new float[]{ 0, height },
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
				vecs[1] = gets(CylFace.CYL_TOP, vecs[1]);
			}
		}
		if(!bools[2] && !detached(2, exclude_detached)){
			vecs[2] = new float[][]{
				new float[]{ 0, height },
				new float[]{ dia2, height + the }
			};
			if(include_offsets && !cuv.get(CylFace.CYL_OUTER).automatic()){
				vecs[2] = gets(CylFace.CYL_OUTER, vecs[2]);
			}
		}
		if(radius2 != 0f){
			if(!bools[3] && !detached(3, exclude_detached)){
				float hei = detached(2) ? 0 : the;
				vecs[3] = new float[][]{
					new float[]{ 0, height + hei },
					new float[]{ dia2, height + hei + the }
				};
				if(include_offsets && !cuv.get(CylFace.CYL_INNER).automatic()){
					vecs[3] = gets(CylFace.CYL_INNER, vecs[3]);
				}
			}
			if(seglimit > 0 && seglimit < segments){
				float seg = radius - radius2;
				if(seg < 1) seg = 1;
				else if(seg % 1 != 0){
					seg = (int)seg + (seg % 1 > 0.5f ? 1 : 0);
				}
				float beg = detached(2) && detached(3) ? 0 : dia2;
				if(!bools[4] && !detached(4, exclude_detached)){
					vecs[4] = new float[][]{
						new float[]{ beg, height },
						new float[]{ beg + seg, height + the }
					};
					if(include_offsets && !cuv.get(CylFace.SEG_SIDE_0).automatic()){
						vecs[4] = gets(CylFace.SEG_SIDE_0, vecs[4]);
					}
				}
				if(!bools[5] && !detached(5, exclude_detached)){
					float shi = detached(2) || detached(3) ? seg : 0;
					float hai = detached(2) || detached(3) ? 0 : the;
					vecs[5] = new float[][]{
						new float[]{ beg + shi, height + hai },
						new float[]{ beg + shi + seg, height + hai + the }
					};
					if(include_offsets && !cuv.get(CylFace.SEG_SIDE_1).automatic()){
						vecs[5] = gets(CylFace.SEG_SIDE_1, vecs[5]);
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

	private boolean detached(int index, boolean exclude_detached){
		return exclude_detached && cuv.get(CylFace.values()[index]).detached();
	}

	private boolean detached(int i){
		return bools[i] || cuv.get(CylFace.values()[i]).detached();
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
			default: return null;
		}
		return res;
	}

	@Override
	public boolean isActive(Face face){
		if(face instanceof CylFace == false) return false;
		switch((CylFace)face){
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

}
