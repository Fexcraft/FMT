package net.fexcraft.app.fmt.polygon;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Vertex;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VertexOffset {

	public Vertex vertex;
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

}
