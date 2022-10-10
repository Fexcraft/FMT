package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.polygon.uv.CylFace;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.AxisRotator;
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
	public float[] getFaceColor(int idx){
		int segs = seglimit < segments && seglimit > 0 ? seglimit : segments;
		if(idx < segs){
			return blu0.toFloatArray();
		}
		if(idx < segs * 2){
			return blu1.toFloatArray();
		}
		if(idx < segs * 3){
			return red1.toFloatArray();
		}
		if(idx < segs * 4){
			return red0.toFloatArray();
		}
		return (idx % 2 == 1 ? gre1 : gre0).toFloatArray();
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

}
