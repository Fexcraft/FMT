package net.fexcraft.app.fmt.polygon;

import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import org.joml.Vector3f;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class CurvePoint {

	public Vector3f vector;
	public RGB color = RGB.WHITE.copy();

	public CurvePoint(){
		vector = new Vector3f();
	}

	public CurvePoint(float x, float y, float z){
		vector = new Vector3f(x, y, z);
	}

	public CurvePoint(Vector3f vec, int rgb){
		vector = vec;
		color.packed = rgb;
	}

	public CurvePoint(CurvePoint point){
		this(new Vector3f(point.vector), point.color.packed);
	}

	public Vec3f toVec3f(Vector3f pos){
		return new Vec3f(vector.x + pos.x, vector.y + pos.y, vector.z + pos.z);
	}

}
