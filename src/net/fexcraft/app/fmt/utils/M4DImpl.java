package net.fexcraft.app.fmt.utils;

import net.fexcraft.lib.common.math.M4DW;
import net.fexcraft.lib.common.math.V3D;
import org.joml.Matrix4d;
import org.joml.Vector3d;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class M4DImpl extends M4DW {

	private Matrix4d matrix = new Matrix4d();
	private Vector3d[] axes = new Vector3d[]{
		new Vector3d(1, 0, 0),
		new Vector3d(0, 1, 0),
		new Vector3d(0, 0, 1)
	};

	@Override
	public void rotate(double am, int axe){
		matrix.rotate(am, axes[axe]);
	}

	@Override
	protected void reset(double x, double y, double z){
		matrix.identity();
		matrix.m00(x);
		matrix.m10(y);
		matrix.m20(z);
	}

	@Override
	protected V3D fill(V3D vec){
		return vec.set(matrix.m00(), matrix.m10(), matrix.m20());
	}

	@Override
	protected void norm(){
		yaw = Math.atan2(matrix.m20(), matrix.m00());
		pit = Math.atan2(-matrix.m10(), Math.sqrt(matrix.m12() * matrix.m12() + matrix.m11() * matrix.m11()));
		rol = Math.atan2(matrix.m12(), matrix.m11());
	}

}
