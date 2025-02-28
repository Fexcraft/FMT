package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.lib.frl.Vertex;

import java.util.Map;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Vertoff {

	public VOKey key;
	public float[] color;
	public Polygon polygon;
	public Vector3F off = new Vector3F();
	public float u, v;

	public Vertoff(VOKey vkey){
		key = vkey;
	}

	public void apply(Polygon poly, Vertex vertex){
		if(vertex == null) return;
		polygon = poly;
		vertex.vector = vertex.vector.add(off.x, off.y, off.z);
		float su = polygon.glm.glObj.grouptex ? polygon.group().texSizeX : polygon.model().texSizeX;
		float sv = polygon.glm.glObj.grouptex ? polygon.group().texSizeY : polygon.model().texSizeY;
		vertex.u += u / su;
		vertex.v += v / sv;
	}

	public static Vertoff getPicked(int pick){
		Vertex vert = null;
		for(Map.Entry<Vertex, Integer> entry : Polygon.vertcolors.entrySet()){
			if(entry.getValue() != pick) continue;
			vert = entry.getKey();
			break;
		}
		if(vert == null) return null;
		for(Group group : FMT.MODEL.allgroups()){
			for(Polygon poly : group){
				for(Vertoff off : poly.vertices.values()){
					//TODO if(off.vertex == vert) return off;
				}
			}
		}
		return null;
	}

	public static record VOKey(VOType type, int vertix, int secondary){

		@Override
		public boolean equals(Object o){
			if(o instanceof VOKey == false) return false;
			VOKey vo = (VOKey)o;
			return type == vo.type && vertix == vo.vertix && secondary == vo.secondary;
		}

	}

	public static enum VOType {

		BOX_CORNER,
		CYL_INNER,
		CYL_OUTER,
		CURVE,
		;

	}

}
