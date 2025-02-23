package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.PolyVal.CORNER_0;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_1;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_2;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_3;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.*;
import net.fexcraft.app.fmt.texture.Texture;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.frl.gen.Generator;

public class RectCurve extends CurvePolygon {

	public static PolyVal[] CORNERS = { CORNER_0, CORNER_1, CORNER_2, CORNER_3 };
	public static ArrayList<RGB> cols = new ArrayList<>();
	public static ArrayList<VarFace> vars = new ArrayList<>();
	static{
		cols.add(blu1);
		cols.add(blu0);
		vars.add(new VarFace(0, blu1.packed));
		vars.add(new VarFace(1, blu0.packed));
	}
	public boolean side_top;
	public boolean side_bot;

	public RectCurve(Model model){
		super(model);
	}

	public RectCurve(Model model, JsonMap obj){
		super(model, obj);
	}

	@Override
	public Shape getShape(){
		return Shape.RECT_CURVE;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		if(glp.sub == null) glp.sub = new ArrayList<>();
		Curve cu = act_curve();
		if(glp.sub.size() != cu.points.size()){
			while(glp.sub.size() > cu.points.size()){
				PolyRenderer.RENDERER.delete(glp.sub.remove(glp.sub.size() - 1));
			}
			while(glp.sub.size() < cu.points.size()){
				Polyhedron<GLObject> poly = new Polyhedron<>();
				poly.setGlObj(new GLObject());
				glp.sub.add(poly);
			}
		}
		Axis3DL axe = new Axis3DL();
		for(int i = 0; i < cu.points.size(); i++){
			Polyhedron<GLObject> poly = glp.sub.get(i);
			poly.glObj.polycolor = cu.points.get(i).color.toFloatArray();
			Vector3f vec = i == 0 ? pos : new Vector3f(cu.points.get(i).vector).add(pos);
			poly.pos(vec.x, vec.y, vec.z);
			poly.rot(rot.x, rot.y, rot.z);
			Marker.getMarkerGenerator(poly, 1).make();
		}
		Vec3f vpos = new Vec3f(pos.x, pos.y, pos.z);
		if(showline){
			Vec3f las = cu.path.start.sub(vpos);
			float by = cu.path.length / cu.points.size() * 0.25f;
			for(int i = 0; i < cu.points.size() * 4; i++){
				Vec3f vec = cu.path.getVectorPosition(by * i + by, false).sub(vpos);
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
				gll.glObj.polycolor = cu.points.get(0).color.toFloatArray();
				gll.rot(rot.x, rot.y, rot.z);
				gll.pos(pos.x, pos.y, pos.z);
				las = vec;
			}
		}
		//
		axe.setAngles(0, 0, 0);
		CurvePlane seg0 = cu.planes.get(0);
		CurvePlane seg_;
		Vec3f tr, tl, br, bl, ntr, ntl, nbr, nbl;
		float dif = cu.litloc ? cu.path.length / (cu.planes.size() - 1) : 1f / (cu.planes.size() - 1);
		Vec3f coff = cu.path.getVectorPosition(0, false).sub(vpos);
		axe.set(coff, cu.path.getVectorPosition(dif, false).sub(vpos));
		axe.add(seg0.rot, 0, 0);
		float loc;
		float tw = 1f / (group().texgroup == null ? FMT.MODEL.texSizeX : group().texSizeX);
		float th = 1f / (group().texgroup == null ? FMT.MODEL.texSizeY : group().texSizeY);
		float rx = tw * textureX;
		float ry = tw * textureY;
		float tx = rx;
		float ty = 0;
		float sx = 0;
		float sy = 0;
		float sx0 = 0;
		float sx1 = 0;
		tr = coff.add(axe.get(off.x, off.y, off.z));
		tl = coff.add(axe.get(off.x, off.y, off.z + seg0.size.z));
		bl = coff.add(axe.get(off.x, off.y + seg0.size.y, off.z + seg0.size.z));
		br = coff.add(axe.get(off.x, off.y + seg0.size.y, off.z));
		if(!side_top){
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tr, rx + seg0.size.z * tw, ry),
				new Vertex(tl, rx,  ry),
				new Vertex(bl, rx, ry + seg0.size.y * th),
				new Vertex(br, rx + seg0.size.z * tw, ry + seg0.size.y * th)
			}));
			tx += seg0.size.z * tw;
		}
		for(int i = 1; i < cu.planes.size(); i++){
			seg0 = cu.planes.get(i);
			seg_ = cu.planes.get(i - 1);
			loc = cu.litloc ? seg0.location : cu.path.length * seg0.location;
			coff = cu.path.getVectorPosition(loc, false).sub(vpos);
			axe.set(cu.path.getVectorPosition(loc - dif, false).sub(vpos), coff);
			axe.add(seg0.rot, 0, 0);
			ty = ry;
			sx = Math.max(seg0.size.z, seg_.size.z);
			sy = Math.max(seg0.size.y, seg_.size.y);
			sx0 = 0;//seg_.size.z < seg0.size.z ? (seg0.size.z - seg_.size.z) * .5f * tw : 0;
			sx1 = 0;//seg0.size.z < sx ? (sx - seg0.size.z) * .5f * tw : 0;
			ntr = coff.add(axe.get(seg0.offset.x, seg0.offset.y, seg0.offset.z));
			ntl = coff.add(axe.get(seg0.offset.x, seg0.offset.y, seg0.offset.z + seg0.size.z));
			nbl = coff.add(axe.get(seg0.offset.x, seg0.offset.y + seg0.size.y, seg0.offset.z + seg0.size.z));
			nbr = coff.add(axe.get(seg0.offset.x, seg0.offset.y + seg0.size.y, seg0.offset.z));
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(ntr, tx + sx * tw, sx1 + ty),
				new Vertex(tr, tx, sx0 + ty),
				new Vertex(br, tx, -sx0 + ty + sy * th),
				new Vertex(nbr, tx + sx * tw, -sx1 + ty + sy * th)
			}));
			ty += sy * th;
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(ntl, -sx1 + tx + sx * tw, ty),
				new Vertex(nbl, sx1 + tx, ty),
				new Vertex(bl, sx0 + tx, ty + sy * th),
				new Vertex(tl, -sx0 + tx + sx * tw, ty + sy * th)
			}));
			ty += sy * th;
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tr, tx + sx * tw, sx0 + ty),
				new Vertex(ntr, tx, sx1 + ty),
				new Vertex(ntl, tx, -sx1 + ty + sy * th),
				new Vertex(tl, tx + sx * tw, -sx0 + ty + sy * th)
			}));
			ty += sy * th;
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(nbl, -sx1 + tx + sx * tw, ty),
				new Vertex(nbr, sx1 + tx, ty),
				new Vertex(br, sx0 + tx, ty + sy * th),
				new Vertex(bl, -sx0 + tx + sx * tw, ty + sy * th)
			}));
			tx += sx * tw;
			tr = ntr;
			tl = ntl;
			br = nbr;
			bl = nbl;
		}
		if(!side_bot){
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
				new Vertex(tl, tx + sx * tw, ry),
				new Vertex(tr, tx,  ry),
				new Vertex(br, tx, ry + sy * th),
				new Vertex(bl, tx + sx * tw, ry + sy * th)
			}));
		}
		while(glm.polygons.size() > cols.size()){
			int o = (cols.size() - 2) / 4;
			vars.add(new VarFace(cols.size(), gre1.packed + o));
			vars.add(new VarFace(cols.size() + 1, gre0.packed + o + 1));
			vars.add(new VarFace(cols.size() + 2, red1.packed + o + 2));
			vars.add(new VarFace(cols.size() + 3, red0.packed + o + 3));
			cols.add(new RGB(gre1.packed + o));
			cols.add(new RGB(gre0.packed + o + 1));
			cols.add(new RGB(red1.packed + o + 2));
			cols.add(new RGB(red0.packed + o + 3));
		}
		faces = new Face[glm.polygons.size()];
		for(int i = 0; i < faces.length; i++){
			faces[i] = vars.get(i);
			if(!cuv.containsKey(faces[i].id())) cuv.put(faces[i].id(), new UVCoords(this, faces[i], null));
		}
		return new Generator<>(glm);
	}

	@Override
	public float getValue(PolygonValue polyval){
		Curve cu = act_curve();
		switch(polyval.val()){
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) return side_top ? 1 : 0;
				if(idx == 3) return side_bot ? 1 : 0;
				return getIndexValue(cu.planes.get(cu.active_segment).sides, idx > 1 ? idx - 2 : idx);
			}
			default: return super.getValue(polyval);
		}
	}

	@Override
	public void setValue(PolygonValue polyval, float value){
		Curve cu = act_curve();
		switch(polyval.val()){
			case SIDES:{
				int idx = polyval.axe().ordinal();
				if(idx == 2) side_top = value > 0.5f;
				else if(idx == 2) side_bot = value > 0.5f;
				else setIndexValue(cu.planes.get(cu.active_segment).sides, idx > 1 ? idx - 2 : idx, value);
				break;
			}
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		return super.copyInternal(poly);
	}

	@Override
	public RGB getFaceColor(int idx){
		if(idx == 0) return cols.get(0);
		if(idx == glm.polygons.size() - 1) return cols.get(1);
		return cols.get(idx + 1);
	}

	@Override
	public Face getFaceByColor(int color){
		for(VarFace var : vars){
			if(var.color == color) return var;
		}
		return NoFace.NONE;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		float[][][] uvs = new float[glm.polygons.size()][][];
		for(int i = 0; i < glm.polygons.size(); i++){
			net.fexcraft.lib.frl.Polygon poly = glm.polygons.get(i);
			uvs[i > 0 ? i == glm.polygons.size() - 1 ? 1 : i + 1 : 0] = new float[][]{
				new float[]{ poly.vertices[1].u, poly.vertices[1].v },
				new float[]{ poly.vertices[3].u, poly.vertices[3].v },
			};
		}
		return uvs;
	}

}
