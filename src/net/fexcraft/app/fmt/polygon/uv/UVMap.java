package net.fexcraft.app.fmt.polygon.uv;

import java.util.TreeMap;

import net.fexcraft.app.fmt.polygon.Polygon;

public class UVMap extends TreeMap<String, UVCoords> {
	
	private Polygon poly;

	public UVMap(Polygon polygon){
		poly = polygon;
		if(polygon.getUVFaces() == null){
			put(NoFace.NONE.id(), new UVCoords(polygon, NoFace.NONE, null));
			return;
		}
		for(Face face : polygon.getUVFaces()){
			put(face.id(), new UVCoords(polygon, face, null));
		}
	}

	public boolean any(){
		for(UVCoords val : values()){
			if(!val.automatic()) return true;
		}
		return false;
	}
	
	public UVCoords get(Face side){
		return get(side.id());
	}

	public void copyTo(Polygon poly){
		entrySet().forEach(entry -> {
			UVCoords coords = poly.cuv.get(entry.getKey());
			if(coords != null) coords.copy(entry.getValue());
		});
	}

	public boolean anyDetached(){
		for(UVCoords coord : values()){
			if(coord.detached()) return true;
		}
		return false;
	}
	
	public boolean allDetached(){
		for(Face face : poly.getUVFaces()){
			if(!poly.isActive(face)) continue;
			if(!get(face).detached()) return false;
		}
		return true;
	}

}
