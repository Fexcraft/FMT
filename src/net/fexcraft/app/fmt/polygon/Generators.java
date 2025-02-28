package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.polygon.Polygon.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Generators {

	public static Vec3f NULL_V3 = new Vec3f();

	public static void genBox(Box poly){
		boolean shaped = poly.getShape().isShapebox();
		boolean anyrem = false;
		boolean[] rems = new boolean[6];
		for(int i = 0; i < poly.sides.length; i++){
			if(poly.sides[i]){
				rems[i] = true;
				anyrem = true;
			}
		}
		float[] v0, v1, v2, v3, v4, v5, v6, v7;
		float x = poly.off.x;
		float y = poly.off.y;
		float z = poly.off.z;
		float w = poly.size.x;
		float h = poly.size.y;
		float d = poly.size.z;
		boolean[] deuv = new boolean[6];
		ArrayList<float[]> uv = new ArrayList<>();
		for(int i = 0; i < 6; i++){
			UVCoords face = poly.cuv.get(BoxFace.values()[i]);
			if(face.detached()) deuv[i] = true;
			uv.add(face.value());
		}
		float texw = poly.glm.glObj.grouptex ? poly.group().texSizeX : poly.model().texSizeX;
		float texh = poly.glm.glObj.grouptex ? poly.group().texSizeY : poly.model().texSizeY;
		if(shaped){
			Shapebox box = (Shapebox)poly;
			Vec3f c0 = new Vec3f(box.cor0.x, box.cor0.y, box.cor0.z);
			Vec3f c1 = new Vec3f(box.cor1.x, box.cor1.y, box.cor1.z);
			Vec3f c2 = new Vec3f(box.cor2.x, box.cor2.y, box.cor2.z);
			Vec3f c3 = new Vec3f(box.cor3.x, box.cor3.y, box.cor3.z);
			Vec3f c4 = new Vec3f(box.cor4.x, box.cor4.y, box.cor4.z);
			Vec3f c5 = new Vec3f(box.cor5.x, box.cor5.y, box.cor5.z);
			Vec3f c6 = new Vec3f(box.cor6.x, box.cor6.y, box.cor6.z);
			Vec3f c7 = new Vec3f(box.cor7.x, box.cor7.y, box.cor7.z);
			float xw = x + w, yh = y + h, zd = z + d;
			v0 = new float[]{ x  - c0.x, y  - c0.y, z  - c0.z };
			v1 = new float[]{ xw + c1.x, y  - c1.y, z  - c1.z };
			v2 = new float[]{ xw + c5.x, yh + c5.y, z  - c5.z };
			v3 = new float[]{ x  - c4.x, yh + c4.y, z  - c4.z };
			v4 = new float[]{ x  - c3.x, y  - c3.y, zd + c3.z };
			v5 = new float[]{ xw + c2.x, y  - c2.y, zd + c2.z };
			v6 = new float[]{ xw + c6.x, yh + c6.y, zd + c6.z };
			v7 = new float[]{ x  - c7.x, yh + c7.y, zd + c7.z };
		}
		else{
			float x1 = x + w + (w == 0 ? 0.01f : 0);
			float y1 = y + h + (h == 0 ? 0.01f : 0);
			float z1 = z + d + (d == 0 ? 0.01f : 0);
			//
			v0 = new float[]{  x,  y,  z };
			v1 = new float[]{ x1,  y,  z };
			v2 = new float[]{ x1, y1,  z };
			v3 = new float[]{  x, y1,  z };
			v4 = new float[]{  x,  y, z1 };
			v5 = new float[]{ x1,  y, z1 };
			v6 = new float[]{ x1, y1, z1 };
			v7 = new float[]{  x, y1, z1 };
		}
		poly.vertoffs.get(VO_0).apply(poly, v0);
		poly.vertoffs.get(VO_1).apply(poly, v1);
		poly.vertoffs.get(VO_2).apply(poly, v2);
		poly.vertoffs.get(VO_3).apply(poly, v3);
		poly.vertoffs.get(VO_4).apply(poly, v4);
		poly.vertoffs.get(VO_5).apply(poly, v5);
		poly.vertoffs.get(VO_6).apply(poly, v6);
		poly.vertoffs.get(VO_7).apply(poly, v7);
		//
		net.fexcraft.lib.frl.Polygon[] polys = new net.fexcraft.lib.frl.Polygon[6];
		Vertex tv0 = new Vertex(v0).uv(0.0F, 0.0F);
		Vertex tv1 = new Vertex(v1).uv(0.0F, 8.0F);
		Vertex tv2 = new Vertex(v2).uv(8.0F, 8.0F);
		Vertex tv3 = new Vertex(v3).uv(8.0F, 0.0F);
		Vertex tv4 = new Vertex(v4).uv(0.0F, 0.0F);
		Vertex tv5 = new Vertex(v5).uv(0.0F, 8.0F);
		Vertex tv6 = new Vertex(v6).uv(8.0F, 8.0F);
		Vertex tv7 = new Vertex(v7).uv(8.0F, 0.0F);
		float tx = poly.textureX, ty = poly.textureY;
		float w0 = w, h0 = h, d0 = d;
		if(w0 % 1 != 0) w0 = w0 < 1 ? 1 : (int)w0 + (w0 % 1 > 0.5f ? 1 : 0);
		if(h0 % 1 != 0) h0 = h0 < 1 ? 1 : (int)h0 + (h0 % 1 > 0.5f ? 1 : 0);
		if(d0 % 1 != 0) d0 = d0 < 1 ? 1 : (int)d0 + (d0 % 1 > 0.5f ? 1 : 0);
		if(!anyrem){
			polys[0] = genBoxFace(0, uv.get(0), deuv[0], texw, texh, new Vertex[] { tv5, tv1, tv2, tv6 }, tx, ty, d0 + w0, d0, d0, h0);
			polys[1] = genBoxFace(1, uv.get(1), deuv[1], texw, texh, new Vertex[] { tv0, tv4, tv7, tv3 }, tx, ty, 0, d0, d0, h0);
			polys[2] = genBoxFace(2, uv.get(2), deuv[2], texw, texh, new Vertex[] { tv5, tv4, tv0, tv1 }, tx, ty, d0, 0, w0, d0);
			polys[3] = genBoxFace(3, uv.get(3), deuv[3], texw, texh, new Vertex[] { tv2, tv3, tv7, tv6 }, tx, ty, d0 + w0, 0, w0, d0);
			polys[4] = genBoxFace(4, uv.get(4), deuv[4], texw, texh, new Vertex[] { tv1, tv0, tv3, tv2 }, tx, ty, d0, d0, w0, h0);
			polys[5] = genBoxFace(5, uv.get(5), deuv[5], texw, texh, new Vertex[] { tv4, tv5, tv6, tv7 }, tx, ty, d0 + w0 + d0, d0, w0, h0);
		}
		else{
			float yp = detached(rems, deuv, 2) && detached(rems, deuv, 3) ? 0 : d0;
			float x0 = detached(rems, deuv, 1) ? 0 : d0;
			float x1 = detached(rems, deuv, 2) ? 0 : w0;
			float x2 = detached(rems, deuv, 4) ? 0 : w0;
			float x3 = detached(rems, deuv, 0) ? 0 : d0;
			if(!rems[0]) polys[0] = genBoxFace(0, uv.get(0), deuv[0], texw, texh, new Vertex[] { tv5, tv1, tv2, tv6 }, tx, ty, x0 + x2, yp, d0, h0);
			if(!rems[1]) polys[1] = genBoxFace(1, uv.get(1), deuv[1], texw, texh, new Vertex[] { tv0, tv4, tv7, tv3 }, tx, ty, 0, yp, d0, h0);
			if(!rems[2]) polys[2] = genBoxFace(2, uv.get(2), deuv[2], texw, texh, new Vertex[] { tv5, tv4, tv0, tv1 }, tx, ty, x0, 0, w0, d0);
			if(!rems[3]) polys[3] = genBoxFace(3, uv.get(3), deuv[3], texw, texh, new Vertex[] { tv2, tv3, tv7, tv6 }, tx, ty, x0 + x1, 0, w0, d0);
			if(!rems[4]) polys[4] = genBoxFace(4, uv.get(4), deuv[4], texw, texh, new Vertex[] { tv1, tv0, tv3, tv2 }, tx, ty, x0, yp, w0, h0);
			if(!rems[5]) polys[5] = genBoxFace(5, uv.get(5), deuv[5], texw, texh, new Vertex[] { tv4, tv5, tv6, tv7 }, tx, ty, x0 + x2 + x3, yp, w0, h0);
		}
		if(anyrem){
			int polis = 0, processed = 0;
			for(int i = 0; i < polys.length; i++) if(polys[i] != null) polis++;
			net.fexcraft.lib.frl.Polygon[] polygons = new net.fexcraft.lib.frl.Polygon[polis];
			for(int i = 0; i < polys.length; i++){
				if(polys[i] != null){
					polygons[processed] = polys[i];
					processed++;
				}
			}
			polys = polygons;
		}
		//float scale = map.getValue("scale", 1f);
		//if(scale != 1f) for(net.fexcraft.lib.frl.Polygon gon : polys) for(Vertex vert : gon.vertices) vert.vector = vert.vector.scale(scale);
		for(net.fexcraft.lib.frl.Polygon gon : polys) poly.glm.polygons.add(gon);
	}

	private static boolean detached(boolean[] rems, boolean[] deuv, int i){
		return rems[i] || deuv[i];
	}


	private static net.fexcraft.lib.frl.Polygon genBoxFace(int index, float[] uv, boolean detached, float w, float h, Vertex[] vertices, float tu, float tv, float x, float y, float ex, float ey){
		float[] cuv = index < 0 || uv == null ? null : uv;
		if(index > -1 && detached){
			tu = 0;
			tv = 0;
		}
		if(cuv == null || cuv.length == 0){
			float xs = tu + x;
			float xe = tu + x + ex;
			float ys = tv + y;
			float ye = tv + y + ey;
			vertices[0] = vertices[0].nauv(xe / w, ys / h);
			vertices[1] = vertices[1].nauv(xs / w, ys / h);
			vertices[2] = vertices[2].nauv(xs / w, ye / h);
			vertices[3] = vertices[3].nauv(xe / w, ye / h);
		}
		else if(cuv.length == 2){
			float xs = tu + cuv[0];
			float xe = tu + cuv[0] + ex;
			float ys = tv + cuv[1];
			float ye = tv + cuv[1] + ey;
			vertices[0] = vertices[0].nauv(xe / w, ys / h);
			vertices[1] = vertices[1].nauv(xs / w, ys / h);
			vertices[2] = vertices[2].nauv(xs / w, ye / h);
			vertices[3] = vertices[3].nauv(xe / w, ye / h);
		}
		else if(cuv.length == 4){
			float xs = tu + cuv[0];
			float xe = tu + cuv[2];
			float ys = tv + cuv[1];
			float ye = tv + cuv[3];
			vertices[0] = vertices[0].nauv(xe / w, ys / h);
			vertices[1] = vertices[1].nauv(xs / w, ys / h);
			vertices[2] = vertices[2].nauv(xs / w, ye / h);
			vertices[3] = vertices[3].nauv(xe / w, ye / h);
		}
		else if(cuv.length == 8){
			vertices[0] = vertices[0].nauv((tu + cuv[0]) / w, (tv + cuv[1]) / h);
			vertices[1] = vertices[1].nauv((tu + cuv[2]) / w, (tv + cuv[3]) / h);
			vertices[2] = vertices[2].nauv((tu + cuv[4]) / w, (tv + cuv[5]) / h);
			vertices[3] = vertices[3].nauv((tu + cuv[6]) / w, (tv + cuv[7]) / h);
		}
		return new Polygon(vertices);
	}

}
