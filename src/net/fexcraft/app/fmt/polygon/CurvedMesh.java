package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.VarFace;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.frl.gen.Generator;
import org.joml.Vector3f;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.polygon.Vertoff.VOType.CURVE;
import static net.fexcraft.app.fmt.update.PolyVal.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CurvedMesh extends CurvePolygon {

	public static ArrayList<RGB> cols = new ArrayList<>();
	public static ArrayList<VarFace> vars = new ArrayList<>();
	public boolean flip;

	public CurvedMesh(Model model){
		super(model);
		addDefCurve(new Vector3f());
	}

	public CurvedMesh(Model model, JsonMap obj){
		super(model, obj);
		flip = obj.getBoolean("flip", false);
	}

	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("flip", flip);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.MESH_CURVE;
	}

	@Override
	protected void generate(){
		ArrayList<Polyhedron<GLObject>> subs = glp.sub;
		Axis3DL axe0 = new Axis3DL();
		Axis3DL axe1 = new Axis3DL();
		if(subs == null) subs = new ArrayList<>();
		for(Polyhedron<GLObject> sub : subs) PolyRenderer.RENDERER.delete(sub);
		subs.clear();
		vertoffs.clear();
		for(int c = 0; c < curves.size(); c++){
			Curve cu = curves.get(c);
			for(int i = 0; i < cu.points.size(); i++){
				Polyhedron<GLObject> poly = new Polyhedron<>();
				poly.setGlObj(new GLObject());
				poly.glObj.polycolor = cu.points.get(i).color.toFloatArray();
				Vector3f vec = c == 0 && i == 0 ? pos : new Vector3f(cu.points.get(i).vector).add(pos);
				poly.pos(vec.x, vec.y, vec.z);
				poly.rot(rot.x, rot.y, rot.z);
				Marker.getMarkerGenerator(poly, mscale).make();
				subs.add(poly);
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
					Vector3f pov = new Vector3f(pos);
					if(c > 0) pov.add(cu.points.get(0).vector);
					//gll.pos(pov.x, pov.y, pov.z);
					las = vec;
				}
			}
			if(c >= curves.size() - 1) break;
			Curve cn = curves.get(c + 1);
			//
			int voi = 0;
			axe0.setAngles(0, 0, 0);
			axe1.setAngles(0, 0, 0);
			CurvePlane seg0 = cu.planes.get(0);
			Vec3f vr, vl, nr, nl;
			float dif0 = 1f / (cu.planes.size() - 1);
			float dif1 = 1f / (cn.planes.size() - 1);
			Vec3f coff = cu.path.getVectorPosition(0, false).sub(vpos);
			Vec3f noff = cn.path.getVectorPosition(0, false).sub(vpos);
			axe0.set(coff, cu.path.getVectorPosition(dif0, false).sub(vpos));
			axe0.add(seg0.rot, 0, 0);
			axe1.set(noff, cn.path.getVectorPosition(dif1, false).sub(vpos));
			axe1.add(seg0.rot, 0, 0);
			float loc0;
			float loc1;
			vr = coff.add(axe0.get(off.x, off.y, off.z));
			vl = noff.add(axe1.get(off.x, off.y, off.z));
			getVO(CURVE, voi++, c).apply(this, vr);
			getVO(CURVE, voi++, c).apply(this, vl);
			for(int i = 1; i < cu.planes.size(); i++){
				seg0 = cu.planes.get(i);
				loc0 = cu.path.length * seg0.location;
				loc1 = cn.path.length * seg0.location;
				coff = cu.path.getVectorPosition(loc0, false).sub(vpos);
				noff = cn.path.getVectorPosition(loc1, false).sub(vpos);
				axe0.set(cu.path.getVectorPosition(loc0 - dif0, false).sub(vpos), coff);
				axe0.add(seg0.rot, 0, 0);
				axe1.set(cn.path.getVectorPosition(loc1 - dif1, false).sub(vpos), noff);
				axe1.add(seg0.rot, 0, 0);
				nr = coff.add(axe0.get(seg0.offset.x, seg0.offset.y, seg0.offset.z));
				nl = noff.add(axe1.get(seg0.offset.x, seg0.offset.y, seg0.offset.z));
				getVO(CURVE, voi++, c).apply(this, nr);
				getVO(CURVE, voi++, c).apply(this, nl);
				if(flip){
					glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
						new Vertex(vl, 0, 0),
						new Vertex(vr, 0, 0),
						new Vertex(nr, 0, 0),
						new Vertex(nl, 0, 0)
					}));
				}
				else{
					glm.polygons.add(new net.fexcraft.lib.frl.Polygon(new Vertex[]{
						new Vertex(nl, 0, 0),
						new Vertex(nr, 0, 0),
						new Vertex(vr, 0, 0),
						new Vertex(vl, 0, 0)
					}));
				}
				vr = nr;
				vl = nl;
			}
			while(glm.polygons.size() > cols.size()){
				vars.add(new VarFace(cols.size(), gre1.packed + cols.size()));
				cols.add(new RGB(gre1.packed + cols.size()));
			}
			faces = new Face[glm.polygons.size()];
			for(int i = 0; i < faces.length; i++){
				faces[i] = vars.get(i);
				if(!cuv.containsKey(faces[i].id())) cuv.put(faces[i].id(), new UVCoords(this, faces[i], null));
			}
		}
		glp.sub = subs;
	}

	@Override
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case CUR_AMOUNT: return curves.size();
			case CUR_ACTIVE: return active;
			case DIRECTION: return flip ? 1 : 0;
			default: return super.getValue(polyval);
		}
	}

	@Override
	public void setValue(PolygonValue polyval, float value){
		int val = (int)value;
		switch(polyval.val()){
			case CUR_ACTIVE:{
				active = val;
				if(active >= curves.size()) active = curves.size() - 1;
				if(active < 0) active = 0;
				break;
			}
			case CUR_AMOUNT:{
				while(val > curves.size()) curves.add(curves.get(curves.size() - 1).copy(this).compilePathRet());
				while(val < curves.size() && curves.size() > 1) curves.remove(curves.size() - 1);
				if(active >= curves.size()) active = curves.size() - 1;
				break;
			}
			case DIRECTION:{
				flip = val > 0;
				break;
			}
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		((CurvedMesh)poly).flip = flip;
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
