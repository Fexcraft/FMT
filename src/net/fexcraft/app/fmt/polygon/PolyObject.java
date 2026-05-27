package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
		vectors.add(getVOKey(0));
		vectors.add(getVOKey(1));
		vectors.add(getVOKey(2));
		vectors.add(getVOKey(3));
		for(Vertoff.VOKey vok : vectors){
			vertoffs.putIfAbsent(vok, new Vertoff());
		}
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
		vectors.clear();
		int size = map.getInteger("vectors", 0);
		for(int i = 0; i < size; i++) vectors.add(getVOKey(i));
		for(Vertoff.VOKey vok : vectors){
			vertoffs.putIfAbsent(vok, new Vertoff());
		}
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
		for(ObjFace face : faces){
			Vertex[] verts = new Vertex[face.vecs.length];
			for(int i = 0; i < face.vecs.length; i++){
				Vertoff.VOKey key = getVOKey(face.vecs[i]);
				Vertoff vec = vertoffs.get(key);
				verts[i] = new Vertex(vec.off.x, vec.off.y, vec.off.z).uv(face.uv[i * 2], face.uv[i * 2 + 1]);
				vertoffs.get(key).apply(this);
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
			case VERT_ACTIVE: return selvec;
			case OBJ_FACE_ACTIVE: return selfac;
			default: return super.getValue(polyval);
		}
	}

	@Override
	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case VERT_ACTIVE:{
				if(value < 0 || value >= vectors.size()) return;
				selvec = (int)value;
				break;
			}
			case OBJ_FACE_ACTIVE:{
				if(value < 0 || value >= faces.size()) return;
				selfac = (int)value;
				break;
			}
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof PolyObject == false) return poly;
		//
		return poly;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
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

	}

}
