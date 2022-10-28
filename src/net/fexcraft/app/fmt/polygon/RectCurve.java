package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.PolyVal.CORNER_0;
import static net.fexcraft.app.fmt.attributes.PolyVal.CORNER_1;
import static net.fexcraft.app.fmt.attributes.PolyVal.CORNER_2;
import static net.fexcraft.app.fmt.attributes.PolyVal.CORNER_3;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonObject;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.frl.gen.Path;

public class RectCurve extends Polygon {
	
	public static PolyVal[] CORNERS = { CORNER_0, CORNER_1, CORNER_2, CORNER_3 };
	public ArrayList<RectSegment> segments = new ArrayList<>();
	public ArrayList<Point> points = new ArrayList<>();
	public int active_point = 0, active_segment = 0;
	public boolean side_top, side_bot;
	public Path path;
	
	public static class RectSegment {

		public Vector3f size = new Vector3f(1);
		public boolean[] sides = new boolean[4];
		public Vector3f cor0, cor1, cor2, cor3, rot;
		public float location;
		
		public RectSegment(){
			cor0 = new Vector3f();
			cor1 = new Vector3f();
			cor2 = new Vector3f();
			cor3 = new Vector3f();
			rot = new Vector3f();
		}
		
		public RectSegment(JsonMap map){
			size.x = map.get("width", 1f);
			size.y = map.get("height", 1f);
			size.z = map.get("depth", 1f);
			if(map.has("sides_off")){
				JsonArray array = map.getArray("sides_off");
				for(int i = 0; i < sides.length; i++){
					if(i >= array.size()) break;
					sides[i] = array.get(i).value();
				}
			}
			cor0 = getVector(map, "%s0", 0);
			cor1 = getVector(map, "%s1", 0);
			cor2 = getVector(map, "%s2", 0);
			cor3 = getVector(map, "%s3", 0);
			rot = getVector(map, "rot%s", 0);
		}

		public Vector3f[] corners(){
			return new Vector3f[]{ cor0, cor1, cor2, cor3 };
		}

		public JsonObject<?> save(){
			JsonMap map = new JsonMap();
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
			setVector(map, "%s0", cor0);
			setVector(map, "%s1", cor1);
			setVector(map, "%s2", cor2);
			setVector(map, "%s3", cor3);
			setVector(map, "rot%s", rot);
			return map;
		}
		
	}
	
	public static class Point {
		public Vector3f vector;
		public RGB color = RGB.WHITE.copy();
		
		public Point(){
			vector = new Vector3f();
		}
		
		public Point(float x, float y, float z){
			vector = new Vector3f(x, y, z);
		}
		
		public Point(Vector3f vec, int rgb){
			vector = vec;
			color.packed = rgb;
		}

		public Point(Point point){
			this(new Vector3f(point.vector), point.color.packed);
		}

		public Vec3f toVec3f(){
			return new Vec3f(vector.x, vector.y, vector.z);
		}
		
	}
	
	public RectCurve(Model model){
		super(model);
		points.add(new Point(pos.x, pos.y, pos.z));
		points.add(new Point(pos.x + 1, pos.y, pos.z));
		compath();
		segments.add(new RectSegment());
		segments.add(new RectSegment());
	}

	public RectCurve(Model model, JsonMap obj){
		super(model, obj);
		JsonArray points = obj.getArray("points");
		points.value.forEach(elm -> {
			JsonArray array = elm.asArray();
			this.points.add(new Point(getVector(array, 0), array.get(3).integer_value()));
		});
		JsonArray segs = obj.getArray("segments");
		segs.value.forEach(elm -> {
			segments.add(new RectSegment(elm.asMap()));
		});
		compath();
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		JsonArray points = new JsonArray();
		this.points.forEach(point -> {
			JsonArray array = new JsonArray();
			array.add(point.vector.x);
			array.add(point.vector.y);
			array.add(point.vector.z);
			array.add(point.color.packed);
			points.add(array);
		});
		map.add("points", points);
		JsonArray segs = new JsonArray();
		segments.forEach(segment -> {
			segs.add(segment.save());
		});
		map.add("segments", segs);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.RECT_CURVE;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		if(glm.sub == null){
			glm.sub = new ArrayList<>();
			Polyhedron<GLObject> poly = new Polyhedron<>();
			poly.setGlObj(new GLObject());
			glm.sub.add(poly);
			poly = new Polyhedron<>();
			poly.setGlObj(new GLObject());
			poly.sub = new ArrayList<>();
			glm.sub.add(poly);
		}
		if(glm.sub.get(1).sub.size() != points.size()){
			while(glm.sub.get(1).sub.size() > points.size()){
				PolyRenderer.RENDERER.delete(glm.sub.get(1).sub.remove(glm.sub.get(1).sub.size() - 1));
			}
			while(glm.sub.get(1).sub.size() < points.size()){
				Polyhedron<GLObject> poly = new Polyhedron<>();
				poly.setGlObj(new GLObject());
				glm.sub.get(1).sub.add(poly);
			}
		}
		for(int i = 0; i < points.size(); i++){
			Polyhedron<GLObject> poly = glm.sub.get(1).sub.get(i);
			poly.glObj.polycolor = points.get(i).color.toFloatArray();
			Vector3f vec = i == 0 ? pos : points.get(i).vector;
			poly.pos(vec.x, vec.y, vec.z);
			Marker.getMarkerGenerator(poly, 1).make();
		}
		Vec3f las = path.start;
		float by = path.length / points.size() * 0.25f;
		for(int i = 0; i < points.size() * 4; i++){
			Vec3f vec = path.getVectorPosition(by * i + by, false);
			var poly = new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(las.sub(pos.x, pos.y, pos.z).add(0, 0.05f, 0)),
				new Vertex(vec.sub(pos.x, pos.y, pos.z).add(0, 0.05f, 0)),
				new Vertex(vec.sub(pos.x, pos.y, pos.z).add(0, -.05f, 0)),
				new Vertex(las.sub(pos.x, pos.y, pos.z).add(0, -.05f, 0))
			});
			glm.sub.get(0).polygons.add(poly);
			poly = new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(vec.sub(pos.x, pos.y, pos.z).add(0, 0.05f, 0)),
				new Vertex(las.sub(pos.x, pos.y, pos.z).add(0, 0.05f, 0)),
				new Vertex(las.sub(pos.x, pos.y, pos.z).add(0, -.05f, 0)),
				new Vertex(vec.sub(pos.x, pos.y, pos.z).add(0, -.05f, 0))
			});
			glm.sub.get(0).polygons.add(poly);
			glm.sub.get(0).glObj.polycolor = points.get(0).color.toFloatArray();
			glm.sub.get(0).pos(pos.x, pos.y, pos.z);
			las = vec;
		}
		return new Generator<>(glm);
	}
	
	@Override
	public void recompile(){
		if(glm.sub != null){
			glm.sub.get(1).sub.forEach(sub -> { sub.recompile = true; sub.clear(); });
			glm.sub.get(0).recompile = true;
			glm.sub.get(0).clear();
		}
		super.recompile();
	}
	
	@Override
	public void render(){
		PolyRenderer.mode(DrawMode.RGBCOLOR);
		glm.render();
	}

	private void compath(){
		Vec3f[] arr = new Vec3f[points.size()];
		int idx = 0;
		for(Point point : points) arr[idx++] = point.toVec3f();
		path = new Path(arr);
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case POS: {
				if(active_point > 0) return getVectorValue(points.get(active_point).vector, polyval.axe());
				else return super.getValue(polyval);
			}
			case ROT: {
				if(active_segment > 0) return getVectorValue(segments.get(active_segment).rot, polyval.axe());
				else return super.getValue(polyval);
			}
			case SIZE: return getVectorValue(segments.get(active_segment).size, polyval.axe());
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) return side_top ? 1 : 0;
				if(idx == 3) return side_bot ? 1 : 0;
				return getIndexValue(segments.get(active_segment).sides, idx > 1 ? idx - 2 : idx);
			}
			case CORNER_0: return getVectorValue(segments.get(active_segment).cor0, polyval.axe());
			case CORNER_1: return getVectorValue(segments.get(active_segment).cor1, polyval.axe());
			case CORNER_2: return getVectorValue(segments.get(active_segment).cor2, polyval.axe());
			case CORNER_3: return getVectorValue(segments.get(active_segment).cor3, polyval.axe());
			case COLOR: return points.get(active_point).color.packed;
			case CUR_ACTIVE_POINT: return active_point;
			case CUR_ACTIVE_SEGMENT: return active_segment;
			case CUR_POINTS: return points.size();
			case CUR_SEGMENTS: return segments.size();
			case CUR_LENGTH: return path.length;
			case SEG_ROT: return getVectorValue(segments.get(active_segment).rot, polyval.axe());
			case SEG_LOC: return segments.get(active_segment).location;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case POS:{
				if(active_point == 0) super.setValue(polyval, value);
				setVectorValue(points.get(active_point).vector, polyval.axe(), value);
				compath();
				break;
			}
			case ROT:{
				if(active_segment == 0) super.setValue(polyval, value);
				setVectorValue(segments.get(active_segment).rot, polyval.axe(), value);
				break;
			}
			case SIZE: setVectorValue(segments.get(active_segment).size, polyval.axe(), value); break;
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) side_top = value > 0.5f;
				else if(idx == 2) side_bot = value > 0.5f;
				else setIndexValue(segments.get(active_segment).sides, idx > 1 ? idx - 2 : idx, value);
				break;
			}
			case CORNER_0: setVectorValue(segments.get(active_segment).cor0, polyval.axe(), value); break;
			case CORNER_1: setVectorValue(segments.get(active_segment).cor1, polyval.axe(), value); break;
			case CORNER_2: setVectorValue(segments.get(active_segment).cor2, polyval.axe(), value); break;
			case CORNER_3: setVectorValue(segments.get(active_segment).cor3, polyval.axe(), value); break;
			case COLOR: points.get(active_point).color.packed = (int)value; break;
			case CUR_ACTIVE_POINT:{
				if(value < 0) value = 0;
				if(value >= points.size()) value = points.size() - 1;
				active_point = (int)value;
				break;
			}
			case CUR_ACTIVE_SEGMENT:{
				if(value < 0) value = 0;
				if(value >= segments.size()) value = segments.size() - 1;
				active_segment = (int)value;
				break;
			}
			case CUR_POINTS:{
				int val = (int)value;
				if(value < 2) value = 2;
				if(value > points.size()) value = points.size() - 1;
				if(val == points.size()) break;
				if(val < points.size()){
					while(points.size() > val) points.remove(points.size() - 1);
					if(active_point >= points.size()) active_point = points.size() - 1;
				}
				if(val > points.size()) while(points.size() < val) points.add(new Point(points.get(points.size() - 1)));
				compath();
				break;
			}
			case CUR_SEGMENTS:{
				int val = (int)value;
				if(value < 2) value = 2;
				if(value > segments.size()) value = segments.size() - 1;
				if(val == segments.size()) break;
				if(val < segments.size()){
					while(segments.size() > val) segments.remove(segments.size() - 1);
					if(active_segment >= segments.size()) active_segment = segments.size() - 1;
				}
				if(val > segments.size()) while(segments.size() < val) segments.add(new RectSegment());
				break;
			}
			case SEG_ROT: setVectorValue(segments.get(active_segment).rot, polyval.axe(), value); break;
			case SEG_LOC: segments.get(active_segment).location = value; break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		//TODO 
		return poly;
	}

	@Override
	public Face[] getUVFaces(){
		return BoxFace.values();
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
		if(color == c_gre0) return BoxFace.LEFT;
		if(color == c_gre1) return BoxFace.RIGHT;
		return NoFace.NONE;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

}
