package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.script.elm.FltElm;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public abstract class CurvePolygon extends Polygon {

	public Polyhedron<GLObject> gll = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public Polyhedron<GLObject> glp = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public ArrayList<Curve> curves = new ArrayList<>();
	public boolean showline = false;
	public int active;
	public Face[] faces;

	public CurvePolygon(Model model){
		super(model);
		addDefCurve(pos);
	}

	public CurvePolygon(Model model, JsonMap obj){
		super(model, obj);
		parseCurves(obj);
		for(Curve cu : curves) cu.compilePath();
		showline = obj.getBoolean("line", showline);
	}

	protected void addDefCurve(Vector3f pos){
		curves.add(new Curve(this));
		Curve cu = last_curve();
		cu.points.add(new CurvePoint(pos.x, pos.y, pos.z));
		cu.points.add(new CurvePoint(pos.x + 1, pos.y, pos.z));
		cu.compilePath();
		cu.planes.add(new CurvePlane(0));
		cu.planes.add(new CurvePlane(1));
	}

	protected void parseCurves(JsonMap obj){
		if(obj.has("curves")){
			for(JsonValue<?> jsn : obj.getArray("curves").value){
				curves.add(new Curve(this));
				curves.get(curves.size() - 1).parse(jsn.asMap());
			}
		}
		else{
			curves.add(new Curve(this));
			curves.get(0).parse(obj);
		}
	}

	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		JsonArray curvs = new JsonArray();
		for(Curve curve : curves) curvs.add(curve.save());
		map.add("curves", curvs);
		return map;
	}

	public Curve act_curve(){
		return curves.get(active);
	}

	public Curve last_curve(){
		return curves.get(curves.size() - 1);
	}

	@Override
	public float getValue(PolyVal.PolygonValue polyval){
		Curve cu = act_curve();
		switch(polyval.val()){
			case POS: {
				if(active > 0 || cu.active_point > 0) return getVectorValue(cu.points.get(cu.active_point).vector, polyval.axe());
				else return super.getValue(polyval);
			}
			case OFF: {
				if(active > 0 || cu.active_segment > 0) return getVectorValue(cu.planes.get(cu.active_segment).offset, polyval.axe());
				else return super.getValue(polyval);
			}
			case SIZE: return getVectorValue(cu.planes.get(cu.active_segment).size, polyval.axe());
			case CORNER_0: return getVectorValue(cu.planes.get(cu.active_segment).cor0, polyval.axe());
			case CORNER_1: return getVectorValue(cu.planes.get(cu.active_segment).cor1, polyval.axe());
			case CORNER_2: return getVectorValue(cu.planes.get(cu.active_segment).cor2, polyval.axe());
			case CORNER_3: return getVectorValue(cu.planes.get(cu.active_segment).cor3, polyval.axe());
			case COLOR: return cu.points.get(cu.active_point).color.packed;
			case CUR_ACTIVE_POINT: return cu.active_point;
			case CUR_ACTIVE_PLANES: return cu.active_segment;
			case CUR_POINTS: return cu.points.size();
			case CUR_PLANES: return cu.planes.size();
			case CUR_LENGTH: return cu.path.length;
			case CUR_AMOUNT: return curves.size();
			case CUR_ACTIVE: return active;
			case PLANE_ROT: return cu.planes.get(cu.active_segment).rot;
			case PLANE_LOC: return cu.planes.get(cu.active_segment).location;
			case PLANE_LOC_LIT: return cu.litloc ? 1 : 0;
			case RADIAL: return showline ? 1 : 0;
			default: return super.getValue(polyval);
		}
	}

	@Override
	public void setValue(PolyVal.PolygonValue polyval, float value){
		Curve cu = act_curve();
		switch(polyval.val()){
			case POS:{
				if(cu.active_point == 0 && active == 0) super.setValue(polyval, value);
				else setVectorValue(cu.points.get(cu.active_point).vector, polyval.axe(), value);
				compileAllPaths();
				break;
			}
			case OFF:{
				if(active == 0 && cu.active_segment == 0) super.setValue(polyval, value);
				setVectorValue(cu.planes.get(cu.active_segment).offset, polyval.axe(), value);
				break;
			}
			case SIZE: setVectorValue(cu.planes.get(cu.active_segment).size, polyval.axe(), value); break;
			case CORNER_0: setVectorValue(cu.planes.get(cu.active_segment).cor0, polyval.axe(), value); break;
			case CORNER_1: setVectorValue(cu.planes.get(cu.active_segment).cor1, polyval.axe(), value); break;
			case CORNER_2: setVectorValue(cu.planes.get(cu.active_segment).cor2, polyval.axe(), value); break;
			case CORNER_3: setVectorValue(cu.planes.get(cu.active_segment).cor3, polyval.axe(), value); break;
			case COLOR: cu.points.get(cu.active_point).color.packed = (int)value; break;
			case CUR_ACTIVE_POINT:{
				if(value < 0) value = 0;
				if(value >= cu.points.size()) value = cu.points.size() - 1;
				cu.active_point = (int)value;
				break;
			}
			case CUR_ACTIVE_PLANES:{
				if(value < 0) value = 0;
				if(value >= cu.planes.size()) value = cu.planes.size() - 1;
				cu.active_segment = (int)value;
				break;
			}
			case CUR_POINTS:{
				int val = (int)value;
				if(val < 2) val = 2;
				if(val < cu.points.size()){
					while(cu.points.size() > val) cu.points.remove(cu.points.size() - 1);
					if(cu.active_point >= cu.points.size()) cu.active_point = cu.points.size() - 1;
				}
				if(val > cu.points.size()) while(cu.points.size() < val) cu.points.add(new CurvePoint(cu.points.get(cu.points.size() - 1)));
				compileAllPaths();
				break;
			}
			case CUR_PLANES:{
				int val = (int)value;
				if(val < 2) val = 2;
				if(val < cu.planes.size()){
					while(cu.planes.size() > val) cu.planes.remove(cu.planes.size() - 1);
					if(cu.active_segment >= cu.planes.size()) cu.active_segment = cu.planes.size() - 1;
				}
				if(val > cu.planes.size()) while(cu.planes.size() < val) cu.planes.add(new CurvePlane(cu.planes.get(cu.planes.size() - 1), cu.litloc));
				break;
			}
			case PLANE_ROT: cu.planes.get(cu.active_segment).rot = value; break;
			case PLANE_LOC: cu.planes.get(cu.active_segment).location = value; break;
			case PLANE_LOC_LIT: cu.litloc = value > 0; break;
			case RADIAL: showline = value > 0; break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	private void compileAllPaths(){
		for(Curve cu : curves) cu.compilePath();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		CurvePolygon curv = (CurvePolygon)poly;
		curv.curves.clear();
		for(Curve cu : curves){
			curv.curves.add(cu.copy(curv));
		}
		curv.showline = showline;
		curv.compileAllPaths();
		return poly;
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
			PolyRenderer.DrawMode mode = PolyRenderer.mode();
			PolyRenderer.mode(PolyRenderer.DrawMode.RGBCOLOR);
			if(selected || group().selected) glp.render();
			if(showline) gll.render();
			PolyRenderer.mode(mode);
		}
		glm.render();
	}

	@Override
	public Face[] getUVFaces(){
		return faces;
	}

	@Override
	public boolean isValidUVFace(String str){
		return str.startsWith("var-");
	}

	@Override
	protected float paintScale(Texture tex, boolean x){
		return x ? tex.getWidth() : tex.getHeight();
	}

}
