package net.fexcraft.app.fmt.polygon.uv;

import java.util.TreeMap;

import net.fexcraft.app.fmt.polygon.Polygon;

public class UVMap extends TreeMap<String, UVCoords> {
	
	private Polygon poly;

	public UVMap(Polygon polygon){
		poly = polygon;
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
		entrySet().forEach(entry -> poly.cuv.get(entry.getKey()).copy(entry.getValue()));
	}

}
