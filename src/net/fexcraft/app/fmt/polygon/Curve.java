package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.gen.Path;
import org.joml.Vector3f;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Curve {

	public ArrayList<CurvePlane> planes = new ArrayList<>();
	public ArrayList<CurvePoint> points = new ArrayList<>();
	public int active_point = 0;
	public int active_segment = 0;
	public boolean litloc;
	public CurvePolygon polygon;
	public Path path;

	public Curve(CurvePolygon poly){
		polygon = poly;
	}

	public void parse(JsonMap map){
		JsonArray points = map.getArray("points");
		points.value.forEach(elm -> {
			JsonArray array = elm.asArray();
			this.points.add(new CurvePoint(getVector(array, 0), array.get(3).integer_value()));
		});
		JsonArray segs = null;
		if(map.has("planes")) segs = map.getArray("planes");
		if(segs != null) segs.value.forEach(elm -> planes.add(new CurvePlane(elm.asMap())));
		litloc = map.getBoolean("litloc", litloc);
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
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
		return map;
	}

	public void compilePath(){
		Vec3f[] arr = new Vec3f[points.size()];
		int idx = 0;
		for(CurvePoint point : points) arr[idx++] = point.toVec3f(polygon.pos);
		path = new Path(arr);
	}

	public Curve compilePathRet(){
		compilePath();
		return this;
	}

	public Curve copy(CurvePolygon poly){
		Curve cu = new Curve(poly);
		cu.litloc = litloc;
		cu.points.clear();
		for(CurvePoint point : points){
			cu.points.add(new CurvePoint(new Vector3f(point.vector), point.color.packed));
		}
		cu.planes.clear();
		for(CurvePlane plane : planes){
			cu.planes.add(new CurvePlane(plane.save()));
		}
		return cu;
	}

}
