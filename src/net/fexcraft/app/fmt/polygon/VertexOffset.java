package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Vertex;

import java.util.Map;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VertexOffset {

	public Vertex vertex;
	public float[] color;
	public Vector3F off = new Vector3F();
	public Vec3f org;
	public float ou, ov;
	public float u, v;

	public VertexOffset(Vertex vert){
		set(vert);
	}

	public void set(Vertex vert){
		vertex = vert;
		org = vert.vector;
		ou = vert.u;
		ov = vert.v;
	}

	public void apply(Polygon polygon){
		vertex.vector = org.add(off.x, off.y, off.z);
		float su = polygon.glm.glObj.grouptex ? polygon.group().texSizeX : polygon.model().texSizeX;
		float sv = polygon.glm.glObj.grouptex ? polygon.group().texSizeY : polygon.model().texSizeY;
		vertex.u = ou + u / su;
		vertex.v = ov + v / sv;
	}

	public static VertexOffset getPicked(int pick){
		Vertex vert = null;
		for(Map.Entry<Vertex, Integer> entry : Polygon.vertcolors.entrySet()){
			if(entry.getValue() != pick) continue;
			vert = entry.getKey();
			break;
		}
		if(vert == null) return null;
		for(Group group : FMT.MODEL.allgroups()){
			for(Polygon poly : group){
				for(VertexOffset off : poly.vertices){
					if(off.vertex == vert) return off;
				}
			}
		}
		return null;
	}

}
