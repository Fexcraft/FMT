package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.PolyVal.CORNER_0;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_1;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_2;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_3;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;
import java.util.Arrays;

import net.fexcraft.lib.script.elm.FltElm;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.frl.gen.Path;

public class RectCurve extends Polygon {

	public Polyhedron<GLObject> gll = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public Polyhedron<GLObject> glp = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public static PolyVal[] CORNERS = { CORNER_0, CORNER_1, CORNER_2, CORNER_3 };
	public ArrayList<Plane> planes = new ArrayList<>();
	public ArrayList<Point> points = new ArrayList<>();
	public int active_point = 0;
	public int active_segment = 0;
	public boolean side_top;
	public boolean side_bot;
	public boolean dirloc;
	public boolean showline = true;
	public Path path;
	
	public static class Plane {

		public Vector3f size = new Vector3f(1);
		public Vector3f offset = new Vector3f(0);
		public boolean[] sides = new boolean[4];
		public Vector3f cor0, cor1, cor2, cor3;//, rot;
		public float location, rot;
		
		public Plane(float loc){
			cor0 = new Vector3f();
			cor1 = new Vector3f();
			cor2 = new Vector3f();
			cor3 = new Vector3f();
			//rot = new Vector3f();
			location = loc;
		}
		
		public Plane(Plane seg, boolean loc){
			cor0 = new Vector3f(seg.cor0);
			cor1 = new Vector3f(seg.cor1);
			cor2 = new Vector3f(seg.cor2);
			cor3 = new Vector3f(seg.cor3);
			//rot = new Vector3f(seg.rot);
			rot = seg.rot;
			size.set(seg.size);
			offset.set(seg.offset);
			location = seg.location + (loc ? 0 : 1);
			sides = Arrays.copyOf(seg.sides, 4);
		}
		
		public Plane(JsonMap map){
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
			//rot = getVector(map, "rot%s", 0);
			rot = map.getFloat("rot", 0);
			offset = getVector(map, "off%s", 0);
			location = map.getFloat("loc", 0);
		}

		public Vector3f[] corners(){
			return new Vector3f[]{ cor0, cor1, cor2, cor3 };
		}

		public JsonMap save(){
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
			//setVector(map, "rot%s", rot);
			map.add("rot", rot);
			setVector(map, "off%s", offset);
			map.add("loc", location);
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

		public Vec3f toVec3f(Vector3f pos){
			return new Vec3f(vector.x + pos.x, vector.y + pos.y, vector.z + pos.z);
		}
		
	}
	
	public RectCurve(Model model){
		super(model);
		points.add(new Point(pos.x, pos.y, pos.z));
		points.add(new Point(pos.x + 1, pos.y, pos.z));
		compath();
		planes.add(new Plane(0));
		planes.add(new Plane(1));
	}

	public RectCurve(Model model, JsonMap obj){
		super(model, obj);
		JsonArray points = obj.getArray("points");
		points.value.forEach(elm -> {
			JsonArray array = elm.asArray();
			this.points.add(new Point(getVector(array, 0), array.get(3).integer_value()));
		});
		JsonArray segs = null;
		if(obj.has("segments")) segs = obj.getArray("segments");
		if(obj.has("planes")) segs = obj.getArray("planes");
		if(segs != null) segs.value.forEach(elm -> planes.add(new Plane(elm.asMap())));
		dirloc = obj.getBoolean("litloc", dirloc);
		showline = obj.getBoolean("line", showline);
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
		planes.forEach(segment -> {
			segs.add(segment.save());
		});
		map.add("planes", segs);
		map.add("litloc", dirloc);
		map.add("line", showline);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.RECT_CURVE;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		if(glp.sub == null) glp.sub = new ArrayList<>();
		if(glp.sub.size() != points.size()){
			while(glp.sub.size() > points.size()){
				PolyRenderer.RENDERER.delete(glp.sub.remove(glp.sub.size() - 1));
			}
			while(glp.sub.size() < points.size()){
				Polyhedron<GLObject> poly = new Polyhedron<>();
				poly.setGlObj(new GLObject());
				glp.sub.add(poly);
			}
		}
		Axis3DL axe = new Axis3DL();
		for(int i = 0; i < points.size(); i++){
			Polyhedron<GLObject> poly = glp.sub.get(i);
			poly.glObj.polycolor = points.get(i).color.toFloatArray();
			Vector3f vec = i == 0 ? pos : new Vector3f(points.get(i).vector).add(pos);
			poly.pos(vec.x, vec.y, vec.z);
			poly.rot(rot.x, rot.y, rot.z);
			Marker.getMarkerGenerator(poly, 1).make();
		}
		Vec3f vpos = new Vec3f(pos.x, pos.y, pos.z);
		if(showline){
			Vec3f las = path.start.sub(vpos);
			float by = path.length / points.size() * 0.25f;
			for(int i = 0; i < points.size() * 4; i++){
				Vec3f vec = path.getVectorPosition(by * i + by, false).sub(vpos);
				var poly = new net.fexcraft.lib.frl.Polygon(new Vertex[]{
					new Vertex(las.add(0, 0.05f, 0)),
					new Vertex(vec.add(0, 0.05f, 0)),
					new Vertex(vec.add(0, -.05f, 0)),
					new Vertex(las.add(0, -.05f, 0))
				});
				gll.polygons.add(poly);
				poly = new net.fexcraft.lib.frl.Polygon(new Vertex[]{
					new Vertex(vec.add(0, 0.05f, 0)),
					new Vertex(las.add(0, 0.05f, 0)),
					new Vertex(las.add(0, -.05f, 0)),
					new Vertex(vec.add(0, -.05f, 0))
				});
				gll.polygons.add(poly);
				gll.glObj.polycolor = points.get(0).color.toFloatArray();
				gll.rot(rot.x, rot.y, rot.z);
				gll.pos(pos.x, pos.y, pos.z);
				las = vec;
			}
		}
		//
		axe.setAngles(0, 0, 0);
		Plane seg = planes.get(0);
		Vec3f tr, tl, br, bl, ntr, ntl, nbr, nbl;
		float dif = dirloc ? path.length / (planes.size() - 1) : 1f / (planes.size() - 1);
		Vec3f coff = path.getVectorPosition(0, false).sub(vpos);
		axe.set(coff, path.getVectorPosition(dif, false).sub(vpos));
		axe.add(seg.rot, 0, 0);
		float loc;
		tr = coff.add(axe.get(off.x, off.y, off.z));
		tl = coff.add(axe.get(off.x, off.y, off.z + seg.size.z));
		bl = coff.add(axe.get(off.x, off.y + seg.size.y, off.z + seg.size.z));
		br = coff.add(axe.get(off.x, off.y + seg.size.y, off.z));
		if(!side_top){
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tr), new Vertex(tl), new Vertex(bl), new Vertex(br)
			}));
		}
		for(int i = 1; i < planes.size(); i++){
			seg = planes.get(i);
			loc = dirloc ? seg.location : path.length * seg.location;
			coff = path.getVectorPosition(loc, false).sub(vpos);
			axe.set(path.getVectorPosition(loc - dif, false).sub(vpos), coff);
			axe.add(seg.rot, 0, 0);
			ntr = coff.add(axe.get(seg.offset.x, seg.offset.y, seg.offset.z));
			ntl = coff.add(axe.get(seg.offset.x, seg.offset.y, seg.offset.z + seg.size.z));
			nbl = coff.add(axe.get(seg.offset.x, seg.offset.y + seg.size.y, seg.offset.z + seg.size.z));
			nbr = coff.add(axe.get(seg.offset.x, seg.offset.y + seg.size.y, seg.offset.z));
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(ntr), new Vertex(tr), new Vertex(br), new Vertex(nbr)
			}));
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(ntl), new Vertex(nbl), new Vertex(bl), new Vertex(tl)
			}));
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tr), new Vertex(ntr), new Vertex(ntl), new Vertex(tl)
			}));
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(nbl), new Vertex(nbr), new Vertex(br), new Vertex(bl)
			}));
			tr = ntr;
			tl = ntl;
			br = nbr;
			bl = nbl;
		}
		if(!side_bot){
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tl), new Vertex(tr), new Vertex(br), new Vertex(bl)
			}));
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
	public void render(FltElm alpha){
		if(PolyRenderer.mode().lines()){
			DrawMode mode = PolyRenderer.mode();
			PolyRenderer.mode(DrawMode.RGBCOLOR);
			glp.render();
			if(showline) gll.render();
			PolyRenderer.mode(mode);
		}
		glm.render();
	}

	public void compath(){
		Vec3f[] arr = new Vec3f[points.size()];
		int idx = 0;
		for(Point point : points) arr[idx++] = point.toVec3f(pos);
		path = new Path(arr);
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case POS: {
				if(active_point > 0) return getVectorValue(points.get(active_point).vector, polyval.axe());
				else return super.getValue(polyval);
			}
			case OFF: {
				if(active_segment > 0) return getVectorValue(planes.get(active_segment).offset, polyval.axe());
				else return super.getValue(polyval);
			}
			/*case ROT: {
				if(active_segment > 0) return getVectorValue(segments.get(active_segment).rot, polyval.axe());
				else return super.getValue(polyval);
			}*/
			case SIZE: return getVectorValue(planes.get(active_segment).size, polyval.axe());
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) return side_top ? 1 : 0;
				if(idx == 3) return side_bot ? 1 : 0;
				return getIndexValue(planes.get(active_segment).sides, idx > 1 ? idx - 2 : idx);
			}
			case CORNER_0: return getVectorValue(planes.get(active_segment).cor0, polyval.axe());
			case CORNER_1: return getVectorValue(planes.get(active_segment).cor1, polyval.axe());
			case CORNER_2: return getVectorValue(planes.get(active_segment).cor2, polyval.axe());
			case CORNER_3: return getVectorValue(planes.get(active_segment).cor3, polyval.axe());
			case COLOR: return points.get(active_point).color.packed;
			case CUR_ACTIVE_POINT: return active_point;
			case CUR_ACTIVE_SEGMENT: return active_segment;
			case CUR_POINTS: return points.size();
			case CUR_SEGMENTS: return planes.size();
			case CUR_LENGTH: return path.length;
			case PLANE_ROT: return planes.get(active_segment).rot;
			case PLANE_LOC: return planes.get(active_segment).location;
			case PLANE_LOC_LIT: return dirloc ? 1 : 0;
			case RADIAL: return showline ? 1 : 0;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case POS:{
				if(active_point == 0) super.setValue(polyval, value);
				else setVectorValue(points.get(active_point).vector, polyval.axe(), value);
				compath();
				break;
			}
			case OFF:{
				if(active_segment == 0) super.setValue(polyval, value);
				setVectorValue(planes.get(active_segment).offset, polyval.axe(), value);
				break;
			}
			/*case ROT:{
				if(active_segment == 0) super.setValue(polyval, value);
				setVectorValue(segments.get(active_segment).rot, polyval.axe(), value);
				break;
			}*/
			case SIZE: setVectorValue(planes.get(active_segment).size, polyval.axe(), value); break;
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) side_top = value > 0.5f;
				else if(idx == 2) side_bot = value > 0.5f;
				else setIndexValue(planes.get(active_segment).sides, idx > 1 ? idx - 2 : idx, value);
				break;
			}
			case CORNER_0: setVectorValue(planes.get(active_segment).cor0, polyval.axe(), value); break;
			case CORNER_1: setVectorValue(planes.get(active_segment).cor1, polyval.axe(), value); break;
			case CORNER_2: setVectorValue(planes.get(active_segment).cor2, polyval.axe(), value); break;
			case CORNER_3: setVectorValue(planes.get(active_segment).cor3, polyval.axe(), value); break;
			case COLOR: points.get(active_point).color.packed = (int)value; break;
			case CUR_ACTIVE_POINT:{
				if(value < 0) value = 0;
				if(value >= points.size()) value = points.size() - 1;
				active_point = (int)value;
				break;
			}
			case CUR_ACTIVE_SEGMENT:{
				if(value < 0) value = 0;
				if(value >= planes.size()) value = planes.size() - 1;
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
				if(value > planes.size()) value = planes.size() - 1;
				if(val == planes.size()) break;
				if(val < planes.size()){
					while(planes.size() > val) planes.remove(planes.size() - 1);
					if(active_segment >= planes.size()) active_segment = planes.size() - 1;
				}
				if(val > planes.size()) while(planes.size() < val) planes.add(new Plane(planes.get(planes.size() - 1), dirloc));
				break;
			}
			case PLANE_ROT: planes.get(active_segment).rot = value; break;
			case PLANE_LOC: planes.get(active_segment).location = value; break;
			case PLANE_LOC_LIT: dirloc = value > 0; break;
			case RADIAL: showline = value > 0; break;
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
		if(idx == 0) return blu1;
		if(idx == glm.polygons.size() - 1) return blu0;
		idx = (idx - 1) % 4;;
		switch(idx){
			case 0: return gre1;
			case 1: return gre0;
			case 2: return red1;
			case 3: return red0;
		}
		return RGB.GREEN;
	}

	@Override
	public Face getFaceByColor(int color){
		//
		return NoFace.NONE;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

}
