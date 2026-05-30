package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.ui.tree.ObjPolyCom;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PolyObject extends Polygon {

	public static HashMap<Integer, Vertoff.VOKey> KEYS = new HashMap<>();
	public ArrayList<Vertoff.VOKey> vectors = new ArrayList<>();
	public ArrayList<ObjFace> faces = new ArrayList<>();
	public int selvec;
	public int selfac;

	public PolyObject(Model model){
		super(model);
		setVectors(4);
		vertoffs.get(vectors.get(1)).off.y = 1;
		vertoffs.get(vectors.get(2)).off.y = 1;
		vertoffs.get(vectors.get(2)).off.x = 1;
		vertoffs.get(vectors.get(3)).off.x = 1;
		faces.add(new ObjFace(false));
		faces.get(0).vecs[0] = 0;
		faces.get(0).vecs[1] = 1;
		faces.get(0).vecs[2] = 2;
		faces.get(0).vecs[3] = 3;
		faces.get(0).tria = false;
	}

	public PolyObject(Model model, JsonMap map){
		super(model, map);
		setVectors(map.getInteger("vectors", 0));
		faces.clear();
		if(map.has("faces")){
			for(JsonValue<?> val : map.getArray("faces").value){
				faces.add(new ObjFace(val.asMap()));
			}
		}
	}

	private Vertoff.VOKey getVOKey(int i){
		return KEYS.computeIfAbsent(i, idx -> new Vertoff.VOKey(Vertoff.VOType.OBJECT, idx, 0));
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("vectors", vectors.size());
		JsonArray polys = new JsonArray();
		for(ObjFace face : faces){
			polys.add(face.save());
		}
		map.add("faces", polys);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.OBJECT;
	}

	@Override
	protected void generate(){
		for(Vertoff.VOKey vok : vectors){
			vertoffs.get(vok).apply(this);
		}
		for(ObjFace face : faces){
			Vertex[] verts = new Vertex[face.vecs.length];
			for(int i = 0; i < face.vecs.length; i++){
				Vertoff vec = vertoffs.get(getVOKey(face.vecs[i]));
				verts[i] = new Vertex(vec.off.x, vec.off.y, vec.off.z).uv(face.uv[i * 2], face.uv[i * 2 + 1]);
			}
			glm.polygons.add(new net.fexcraft.lib.frl.Polygon(verts));
		}
	}

	@Override
	public RGB getFaceColor(int idx){
		return gre1;
	}

	@Override
	public Face getFaceByColor(int i){
		return NoFace.NONE;
	}
	
	@Override
	public void render(float alpha){
		super.render(alpha);
	}
	
	@Override
	public void recompile(){
		super.recompile();
	}

	@Override
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case VERTICES: return vectors.size();
			case OBJ_VERT_ACTIVE: return selvec;
			case OBJ_VERT_OFFSET: return getVectorValue(vertoffs.get(vectors.get(selvec)).off, polyval.axe());
			case OBJ_FACES: return faces.size();
			case OBJ_FACE_ACTIVE: return selfac;
			case OBJ_FACE_VERTEX:{
				if(polyval.axe() == PolyVal.ValAxe.N){
					ObjFace face = faces.get(selfac);
					return face.tria ? 0 : face.vecs[3];
				}
				return faces.get(selfac).vecs[polyval.axe().ordinal()];
			}
			case OBJ_FACE_TRIANGLE: return getBooleanAsIntValue(faces.get(selfac).tria);
			default: return super.getValue(polyval);
		}
	}

	@Override
	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case VERTICES:{
				setVectors((int)value);
				return;
			}
			case OBJ_VERT_ACTIVE:{
				if(value < 0 || value >= vectors.size()) return;
				selvec = (int)value;
				break;
			}
			case OBJ_VERT_OFFSET:{
				setVectorValue(vertoffs.get(vectors.get(selvec)).off, polyval.axe(), value);
				break;
			}
			case OBJ_FACES:{
				if(value < 2) return;
				while(faces.size() > value) removeFace(faces.size() - 1);
				while(faces.size() < value) addFace();
				break;
			}
			case OBJ_FACE_ACTIVE:{
				if(value < 0 || value >= faces.size()) return;
				selfac = (int)value;
				break;
			}
			case OBJ_FACE_VERTEX:{
				if(polyval.axe() == PolyVal.ValAxe.N){
					ObjFace face = faces.get(selfac);
					if(!face.tria) face.vecs[3] = (int)value;
				}
				else faces.get(selfac).vecs[polyval.axe().ordinal()] = (int)value;
				break;
			}
			case OBJ_FACE_TRIANGLE:{
				boolean bool = value > 0.5f;
				if(bool != faces.get(selfac).tria) toggleTriangleQuad(selfac);
				break;
			}
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof PolyObject == false) return poly;
		PolyObject obj = (PolyObject)poly;
		obj.setVectors(vectors.size());
		for(Map.Entry<Vertoff.VOKey, Vertoff> entry : obj.vertoffs.entrySet()){
			entry.getValue().copy(vertoffs.get(entry.getKey()));
		}
		obj.faces.clear();
		for(ObjFace face : faces){
			obj.faces.add(face.copy());
		}
		return poly;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	public void setVectors(int size){
		if(size < 3) size = 3;
		while(vectors.size() > size) vectors.remove(vectors.size() - 1);
		while(vectors.size() < size){
			Vertoff.VOKey vok = getVOKey(vectors.size());
			vectors.add(vok);
			vertoffs.computeIfAbsent(vok, v -> vectors.size() == 1 ? new Vertoff() : new Vertoff(vertoffs.get(vectors.get(vectors.size() - 2))));
		}
	}

	public void addFace(){
		faces.add(faces.isEmpty() ? new ObjFace(true) : faces.get(faces.size() - 1).copy());
		recompile();
		update(new UpdateEvent.PolygonValueEvent(this, ObjPolyCom.OBJ_FACES, true));
	}

	public void removeFace(int idx){
		if(idx < 0 || idx >= faces.size() || faces.size() < 2) return;
		faces.remove(idx);
		FMT.MODEL.updateValue(ObjPolyCom.OBJ_FACE_ACT, null, idx == 0 ? 0 : idx - 1, true);
	}

	public void addVertex(){
		setVectors(vectors.size() + 1);
		recompile();
		update(new UpdateEvent.PolygonValueEvent(this, ObjPolyCom.VERTICES, true));
	}

	public void removeVertex(int idx){
		if(idx < 0 || idx >= vectors.size() || vectors.size() < 4) return;
		Vertoff vo, vp;
		for(int i = idx; i < vectors.size() - 1; i++){
			vo = vertoffs.get(vectors.get(i));
			vp = vertoffs.get(vectors.get(i + 1));
			vo.copy(vp);
		}
		Vertoff.VOKey vok = vectors.remove(vectors.size() - 1);
		vertoffs.remove(vok);
		for(ObjFace face : faces){
			for(int i = 0; i < face.vecs.length; i++){
				if(face.vecs[i] >= idx) face.vecs[i] = face.vecs[i] == 0 ? 0 : face.vecs[i] - 1;
			}
		}
		FMT.MODEL.updateValue(ObjPolyCom.VERT_ACT, null, idx == 0 ? 0 : idx - 1, true);
	}

	public void toggleTriangleQuad(int idx){
		if(idx < 0 || idx >= faces.size()) return;
		ObjFace face = faces.get(idx);
		if(face.tria){
			face.tria = false;
			face.vecs = new int[]{ face.vecs[0], face.vecs[1], face.vecs[2], face.vecs[2] };
			face.uv = new float[]{ face.uv[0], face.uv[1], face.uv[2], face.uv[3], face.uv[4], face.uv[5], face.uv[4], face.uv[5]};
		}
		else{
			face.tria = true;
			face.vecs = new int[]{ face.vecs[0], face.vecs[1], face.vecs[2] };
			face.uv = new float[]{ face.uv[0], face.uv[1], face.uv[2], face.uv[3], face.uv[4], face.uv[5] };
		}
	}

	public void flipFace(){
		faces.get(selfac).flip();
		recompile();
	}

	public static class ObjFace {

		public boolean tria;
		public int[] vecs;
		public float[] uv;

		public ObjFace(boolean t){
			tria = t;
			vecs = new int[t ? 3 : 4];
			uv = new float[t ? 6 : 8];
		}

		public ObjFace(JsonMap map){
			vecs = map.getArray("vec").toIntegerArray();
			uv = map.getArray("uv").toFloatArray();
			tria = vecs.length == 3;
		}

		public JsonMap save(){
			JsonMap map = new JsonMap();
			JsonArray arr = new JsonArray.Flat();
			for(int vec : vecs) arr.add(vec);
			map.add("vec", arr);
			arr = new JsonArray.Flat();
			for(float v : uv) arr.add(v);
			map.add("uv", arr);
			return map;
		}

		public ObjFace copy(){
			ObjFace face = new ObjFace(tria);
			face.vecs = Arrays.copyOf(vecs, vecs.length);
			face.uv = Arrays.copyOf(uv, uv.length);
			return face;
		}

		public void flip(){
			if(tria){
				int v = vecs[0];
				vecs[0] = vecs[2];
				vecs[2] = v;
			}
			else{
				int v = vecs[0];
				vecs[0] = vecs[1];
				vecs[1] = v;
				v = vecs[2];
				vecs[2] = vecs[3];
				vecs[3] = v;
			}
		}

	}

}
