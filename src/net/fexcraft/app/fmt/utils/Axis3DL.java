package net.fexcraft.app.fmt.utils;

import net.fexcraft.lib.common.math.V3D;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.Vec3f;

/** Taken from FVTM and adjusted/shortened. "Lite" Version. */
public class Axis3DL implements AxisRotator {
	
	private Matrix4f matrix;
	private float yaw, pitch, roll;

    public Axis3DL(){ matrix = new Matrix4f(); }
    
    @Override public String toString(){ return "[ " + yaw + "y, " + pitch + "p, " + roll + "r ]";  }
    
    public Vector3f getRelativeVector(float x, float y, float z){
        Matrix4f mat = new Matrix4f(); mat.m00(x); mat.m10(y); mat.m20(z);
        mat.rotate(roll  * 3.14159265F / 180f, new Vector3f(1F, 0F, 0F), mat);
        mat.rotate(pitch * 3.14159265F / 180f, new Vector3f(0F, 0F, 1F), mat);
        mat.rotate(yaw   * 3.14159265F / 180f, new Vector3f(0F, 1F, 0F), mat);
        return new Vector3f(mat.m00(), mat.m10(), mat.m20());
    }
    
    public Vector3f getRelativeVector(Vector3f vec){
        Matrix4f mat = new Matrix4f();
        mat.m00(vec.x); mat.m10(vec.y); mat.m20(vec.z);
        mat.rotate(roll  * 3.14159265F / 180f, new Vector3f(1F, 0F, 0F), mat);
        mat.rotate(pitch * 3.14159265F / 180f, new Vector3f(0F, 0F, 1F), mat);
        mat.rotate(yaw   * 3.14159265F / 180f, new Vector3f(0F, 1F, 0F), mat);
        return new Vector3f(mat.m00(), mat.m10(), mat.m20());
    }

	public V3D getRelativeVector(V3D vec){
		Matrix4f mat = new Matrix4f(); mat.m00((float)vec.x); mat.m10((float)vec.y); mat.m20((float)vec.z);
		mat.rotate(roll  * 3.14159265F / 180f, new Vector3f(1F, 0F, 0F), mat);
		mat.rotate(pitch * 3.14159265F / 180f, new Vector3f(0F, 0F, 1F), mat);
		mat.rotate(yaw   * 3.14159265F / 180f, new Vector3f(0F, 1F, 0F), mat);
		return new V3D(mat.m00(), mat.m10(), mat.m20());
	}

    private final void convertMatrixToAngles(){
        yaw = (float)(Math.atan2(matrix.m20(), matrix.m00()) * 180F / 3.14159265F);
        pitch = (float)(Math.atan2(-matrix.m10(), Math.sqrt(matrix.m12() * matrix.m12() + matrix.m11() * matrix.m11())) * 180F / 3.14159265F);
        roll = (float)(Math.atan2(matrix.m12(), matrix.m11()) * 180F / 3.14159265F);
    }

    private final void convertToMatrix(boolean rad){
        matrix = new Matrix4f();
        matrix.rotate((float)(rad ? roll : roll * 3.14159265F / 180F), new Vector3f(1F, 0F, 0F));
        matrix.rotate((float)(rad ? pitch : pitch * 3.14159265F / 180F), new Vector3f(0F, 0F, 1F));
        matrix.rotate((float)(rad ? yaw : yaw * 3.14159265F / 180F), new Vector3f(0F, 1F, 0F));
        convertMatrixToAngles();
    }

    @Override
    public void setAngles(float yaw, float pitch, float roll){
        this.yaw = yaw; this.pitch = pitch; this.roll = roll; convertToMatrix(false);
    }

	@Override
	public Vec3f getRelativeVector(Vec3f v){
		Vector3f vec = getRelativeVector(v.x, v.y, v.z);
		return new Vec3f(vec.x, vec.y, vec.z);
	}

	public Vec3f get(Vec3f v){
		return getRelativeVector(v);
	}

	public V3D get(V3D v){
		return getRelativeVector(v);
	}

	public Vec3f get(float x, float y, float z){
		Vector3f vec = getRelativeVector(x, y, z);
		return new Vec3f(vec.x, vec.y, vec.z);
	}

	public void set(Vec3f vec1, Vec3f vec0){
        double dx = vec0.x - vec1.x, dy = vec0.y - vec1.y, dz = vec0.z - vec1.z;
        double dxz = Math.sqrt(dx * dx + dz * dz);
        yaw = (float)Math.atan2(dz, dx);
        pitch = (float)-Math.atan2(dy, dxz);
        roll = 0;
        convertToMatrix(true);
	}

    public void add(float x, float y, float z){
        yaw += y; pitch += z; roll += x;
        convertToMatrix(false);
    }

}
