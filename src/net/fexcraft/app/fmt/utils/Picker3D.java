package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.V3F;
import net.fexcraft.lib.frl.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.polygon.PolyRenderer.axis_x;
import static net.fexcraft.app.fmt.polygon.PolyRenderer.axis_y;
import static net.fexcraft.app.fmt.polygon.PolyRenderer.axis_z;
import static net.fexcraft.app.fmt.texture.TexturePainter.getCurrentColor;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Picker3D {

	private static Pivot PIVOT = null;
	private static Matrix4f matrix0 = new Matrix4f();
	private static ArrayList<Triangle> triangles = new ArrayList<>();
	private static final float E = Math.ulp(1f);
	private static V3F pos = new V3F(), dir;

	public static void pick(){
		/*if(triangles.isEmpty())*/ genTriangles();
		pos.set(FMT.CAM.pos.x, FMT.CAM.pos.y, FMT.CAM.pos.z);
		var ray = new Vector4f((float)GGR.cpos_x * 2 / FMT.WIDTH - 1f, 1f - (float)GGR.cpos_y * 2 / FMT.HEIGHT, -1, 1);
		Matrix4f mat = GGR.projection.invert(new Matrix4f());
		ray.mul(mat);
		ray.z = -1f;
		ray.w = 0f;
		GGR.view.invert(mat);
		ray.mul(mat);
		dir = /*pos.add(*/new V3F(ray.x, ray.y, ray.z).norm().scale(1000)/*)*/;
		Result res, rsu = new Result(null, Float.MAX_VALUE, null);
		for(Triangle tri : triangles){
			res = intersects(tri);
			if(res == null) continue;
			if(res.dis < rsu.dis) rsu = res;
		}
		if(rsu.triangle != null){
			//FMT.MODEL.select(rsu.triangle().poly);
			Group group = rsu.triangle.poly.group();
			TextureGroup tex = group.texgroup == null ? group.model.texgroup : group.texgroup;
			if(tex == null) return;
			V3F v0 = rsu.triangle.v0.sub(rsu.vec);
			V3F v1 = rsu.triangle.v1.sub(rsu.vec);
			V3F v2 = rsu.triangle.v2.sub(rsu.vec);
			float m = rsu.triangle.v0.sub(rsu.triangle.v1).cross(rsu.triangle.v0.sub(rsu.triangle.v2)).length();
			float x = v1.cross(v2).length() / m;
			float y = v2.cross(v0).length() / m;
			float z = v0.cross(v1).length() / m;
			float u = rsu.triangle.x0.u * x + rsu.triangle.x1.u * y + rsu.triangle.x2.u * z;
			float v = rsu.triangle.x0.v * x + rsu.triangle.x1.v * y + rsu.triangle.x2.v * z;
			tex.texture.set((int)(u * tex.width), (int)(v * tex.height), getCurrentColor());
			tex.texture.rebind();
		}
	}

	/** https://en.wikipedia.org/wiki/M%C3%B6ller%E2%80%93Trumbore_intersection_algorithm */
	private static Result intersects(Triangle tri){
		V3F e0 = tri.v1.sub(tri.v0);
		V3F e1 = tri.v2.sub(tri.v0);
		V3F h = dir.cross(e1);
		double a = e0.dot(h);
		if(a > -E && a < E) return null;
		double f = 1f / a;
		V3F s = pos.sub(tri.v0);
		double u = f * (s.dot(h));
		if(u < 0f || u > 1f) return null;
		V3F q = s.cross(e0);
		double v = f * dir.dot(q);
		if(v < 0f || u + v > 1f) return null;
		double t = f * e1.dot(q);
		if(t > E){
			return new Result(dir.scale((float)t).add(pos), t, tri);
		}
		return null;
	}

	private static void genTriangles(){
		triangles.clear();
		for(Group group : FMT.MODEL.allgroups()){
			setPivot(FMT.MODEL.getP(group.pivot));
			if(!group.visible) continue;
			for(Polygon poly : group){
				PIVOT.matrix.get(matrix0);
				matrix0.translate(poly.glm.posX, poly.glm.posY, poly.glm.posZ);
				if(poly.glm.rotY != 0f) matrix0.rotate((float)Math.toRadians(poly.glm.rotY), axis_y);
				if(poly.glm.rotX != 0f) matrix0.rotate((float)Math.toRadians(poly.glm.rotX), axis_x);
				if(poly.glm.rotZ != 0f) matrix0.rotate((float)Math.toRadians(poly.glm.rotZ), axis_z);
				for(net.fexcraft.lib.frl.Polygon gon : poly.glm.polygons){
					if(gon.vertices.length == 3){
						triangles.add(new Triangle(poly, gon.vertices));
					}
					else{
						triangles.add(new Triangle(poly, gon.vertices, false));
						triangles.add(new Triangle(poly, gon.vertices, true));
					}
				}
			}
		}
	}

	public static class Triangle {

		public Polygon poly;
		public Vertex x0, x1, x2;
		public V3F v0, v1, v2;

		public Triangle(Polygon poly, Vertex x0, Vertex x1, Vertex x2){
			this.poly = poly;
			this.v0 = (this.x0 = x0).vector.copy();
			this.v1 = (this.x1 = x1).vector.copy();
			this.v2 = (this.x2 = x2).vector.copy();
		}

		public Triangle(Polygon poly, Vertex[] verts){
			this(poly, verts[0], verts[1], verts[2]);
			transform();
		}

		public Triangle(Polygon poly, Vertex[] verts, boolean qi){
			this(poly, verts[qi ? 3 : 0], verts[qi ? 0 : 1], verts[2]);
			transform();
		}

		private void transform(){
			var v = new Vector4f();
			v.set(v0.x, v0.y, v0.z, 1f).mul(matrix0);
			v0.set(v.x, v.y, v.z);
			v.set(v1.x, v1.y, v1.z, 1f).mul(matrix0);
			v1.set(v.x, v.y, v.z);
			v.set(v2.x, v2.y, v2.z, 1f).mul(matrix0);
			v2.set(v.x, v.y, v.z);
		}

		@Override
		public String toString(){
			return poly.group().id + ":" + poly.name() + "|" + v0 + " " + v1 + " " + v2;
		}

	}

	public static record Result(V3F vec, double dis, Triangle triangle){

		@Override
		public String toString(){
			return triangle + " / " + vec + " / " + dis;
		}

	}

	public static void setPivot(Pivot npivot){
		PIVOT = npivot;
		Matrix4f matrix = PIVOT.matrix.identity();
		if(PIVOT.root_rot){
			for(Pivot pivot : PIVOT.roots){
				matrix.translate(pivot.pos);
				if(pivot.rot.y != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.y), axis_y);
				if(pivot.rot.x != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.x), axis_x);
				if(pivot.rot.z != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.z), axis_z);
			}
			matrix.translate(PIVOT.pos);
		}
		else{
			V3D trs = PIVOT.getVec(V3D.NULL);
			matrix.translate((float)trs.x, (float)trs.y, (float)trs.z);
		}
		if(PIVOT.rot.y != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.y), axis_y);
		if(PIVOT.rot.x != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.x), axis_x);
		if(PIVOT.rot.z != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.z), axis_z);
	}

}
