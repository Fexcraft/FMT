package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.*;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.frl.gen.Generator.Values;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Sphere extends Polygon {

	public float radius = 2;
	public int segments = 8, seglimit;
	public int circles = 8, cirlimit;
	public boolean[] bools = new boolean[3];
	public float seg_off, cir_off;

	public Sphere(Model model){
		super(model);
		if(!FMT.MODEL.orient.rect()) pos.y = -1;
	}

	protected Sphere(Model model, JsonMap obj){
		super(model, obj);
		radius = obj.get("radius", radius);
		segments = obj.get("segments", segments);
		seglimit = obj.get("seglimit", seglimit);
		circles = obj.get("circles", circles);
		cirlimit = obj.get("cirlimit", cirlimit);
		if(obj.has("faces_off")){
			JsonArray array = obj.getArray("faces_off");
			for(int i = 0; i < bools.length; i++){
				if(i >= array.size()) break;
				bools[i] = array.get(i).value();
			}
		}
		seg_off = obj.get("seg_off", seg_off);
		cir_off = obj.get("cir_off", cir_off);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("radius", radius);
		map.add("segments", segments);
		map.add("seglimit", seglimit);
		map.add("circles", circles);
		map.add("cirlimit", cirlimit);
		boolean anyoff = false;
		for(boolean bool : bools) if(bool) anyoff = true;
		if(anyoff){
			JsonArray array = new JsonArray();
			for(boolean bool : bools) array.add(bool);
			map.add("faces_off", array);
		}
		map.add("seg_off", seg_off);
		map.add("cir_off", cir_off);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.SPHERE;
	}

	@Override
	protected void generate(){
		Generator gen = new Generator(glm,
			glm.glObj(GLObject.class).grouptex ? group().texgroup.width : model().texgroup.width,
			glm.glObj(GLObject.class).grouptex ? group().texgroup.height : model().texgroup.height)
			.set(Values.TYPE, Generator.Type.SPHERE)
			.set(Values.OFF_X, off.x)
			.set(Values.OFF_Y, off.y)
			.set(Values.OFF_Z, off.z)
			.set(Values.RADIUS1, radius)
			.set(Values.SEGMENTS, segments)
			.set(Values.SEG_LIMIT, seglimit)
			.set(Values.CIRCLES, circles)
			.set(Values.CIR_LIMIT, cirlimit)
			.set(Values.SEG_OFFSET, seg_off)
			.set(Values.CIR_OFFSET, cir_off);
		for(int i = 0; i < bools.length; i++) if(bools[i]) gen.removePolygon(i);
		if(cuv.any()){
			ArrayList<Integer> list = new ArrayList<>();
			ArrayList<float[]> uv = new ArrayList<>();
			for(int i = 0; i < 6; i++){
				if(cuv.get(CylFace.values()[i]).detached()) list.add(i);
				uv.add(cuv.get(CylFace.values()[i]).value());
			}
			gen.set(Values.DETACHED_UV, list);
			gen.set(Values.UV, uv);
		}
		gen.set(Values.ORDERED, true);
		gen.make();
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

	@Override
	public Face getFaceByColor(int color){
		if(color == c_blu1) return CylFace.CYL_TOP;
		if(color == c_blu0) return CylFace.CYL_BASE;
		if(color == c_red1) return CylFace.CYL_OUTER;
		if(color == c_red0) return CylFace.CYL_INNER;
		if(color == c_gre0) return CylFace.SEG_SIDE_0;
		if(color == c_gre1) return CylFace.SEG_SIDE_1;
		return NoFace.NONE;
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case RADIUS_O: return radius;
			case SEGMENTS: return segments;
			case SEG_LIMIT: return seglimit;
			case SEG_OFF: return seg_off;
			case CIRCLES: return circles;
			case CIR_LIMIT: return cirlimit;
			case CIR_OFF: return cir_off;
			case SIDES: return getIndexValue(bools, polyval.axe().ordinal());
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case RADIUS_O:{
				radius = value;
				break;
			}
			case SEGMENTS: segments = (int)value; break;
			case SEG_LIMIT: seglimit = (int)value; break;
			case SEG_OFF: seg_off = value; break;
			case CIRCLES: circles = (int)value; break;
			case CIR_LIMIT: cirlimit = (int)value; break;
			case CIR_OFF: cir_off = value; break;
			case SIDES: setIndexValue(bools, polyval.axe().ordinal(), value); break;
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Sphere == false) return poly;
		Sphere cyl = (Sphere)poly;
		cyl.radius = radius;
		cyl.segments = segments;
		cyl.seglimit = seglimit;
		cyl.seg_off = seg_off;
		cyl.circles = circles;
		cyl.cirlimit = cirlimit;
		cyl.cir_off = cir_off;
		for(int i = 0; i < bools.length; i++) cyl.bools[i] = bools[i];
		return poly;
	}

	@Override
	public Face[] getUVFaces(){
		return CylFace.values();
	}

	@Override
	public float[][][] newUV(boolean include_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	@Override
	public boolean isActive(Face face){
		if(face instanceof SphFace == false) return false;
		switch((SphFace)face){
			case SPH_TOP:
				return !bools[0];
			case SPH_BOT:
				return !bools[1];
			case SPH_OUTER:
				return !bools[2];
			default:
				return false;
		}
	}

}
