package net.fexcraft.app.fmt.wrappers.face;

import java.util.TreeMap;

import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class UVMap extends TreeMap<Face, UVCoords> {

	public UVMap(PolygonWrapper wrapper){
		for(Face face : wrapper.getTexturableFaces()){
			put(face, new UVCoords(wrapper, face, null));
		}
	}

	public boolean anyCustom(){
		for(UVCoords val : values()){
			if(!val.automatic()) return true;
		}
		return false;
	}
	
	public UVCoords get(String id){
		return get(Face.byId(id, false));
	}

}
