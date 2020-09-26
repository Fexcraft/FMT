package net.fexcraft.app.fmt.wrappers.face;

import java.util.Map;
import java.util.TreeMap;

import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class UVMap extends TreeMap<String, UVCoords> {

	public UVMap(PolygonWrapper wrapper){
		for(Face face : wrapper.getTexturableFaces()){
			put(face.id(), new UVCoords(wrapper, face, null, wrapper instanceof CylinderWrapper));
		}
	}

	public boolean anyCustom(){
		for(UVCoords val : values()){
			if(!val.automatic()) return true;
		}
		return false;
	}
	
	public UVCoords get(Face side){
		return get(side.id());
	}

	public void copyFrom(PolygonWrapper wrapper, UVMap cuv){
		for(Map.Entry<String, UVCoords> entry : cuv.entrySet()){
			this.put(entry.getKey(), entry.getValue().copy(wrapper));
		}
	}

}
