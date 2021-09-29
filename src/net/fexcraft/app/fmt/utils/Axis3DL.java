package net.fexcraft.app.fmt.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.Vec3f;

/** Taken from FVTM and adjusted/shortened. "Lite" Version. */
public class Axis3DL implements AxisRotator {
	
	private Matrix4f matrix;
	private Vec3f vec = new Vec3f();

    public Axis3DL(){ matrix = new Matrix4f(); }
    
    @Override public String toString(){ return vec.toString();  }
    
    public Vec3f getRelativeVector(float x, float y, float z){
        Matrix4f mat = new Matrix4f(); mat.m00(x); mat.m10(y); mat.m20(z);
        mat.rotate(vec.y * 3.14159265F / 180f, new Vector3f(0F, 1F, 0F), mat);
        mat.rotate(vec.z * 3.14159265F / 180f, new Vector3f(0F, 0F, 1F), mat);
        mat.rotate(vec.x * 3.14159265F / 180f, new Vector3f(1F, 0F, 0F), mat);
        return new Vec3f(mat.m00(), mat.m10(), mat.m20());
    }
    
    public Vec3f getRelativeVector(Vec3f ovec){
        Matrix4f mat = new Matrix4f();
        mat.m00(ovec.x); mat.m10(ovec.y); mat.m20(ovec.z);
        mat.rotate(vec.y * 3.14159265F / 180f, new Vector3f(0F, 1F, 0F), mat);
        mat.rotate(vec.z * 3.14159265F / 180f, new Vector3f(0F, 0F, 1F), mat);
        mat.rotate(vec.x * 3.14159265F / 180f, new Vector3f(1F, 0F, 0F), mat);
        return new Vec3f(mat.m00(), mat.m10(), mat.m20());
    }

    private final void convertMatrixToAngles(){
        vec.y = (float)(Math.atan2(matrix.m20(), matrix.m00()) * 180F / 3.14159265F);
        vec.z = (float)(Math.atan2(-matrix.m10(), Math.sqrt(matrix.m12() * matrix.m12() + matrix.m11() * matrix.m11())) * 180F / 3.14159265F);
        vec.x = (float)(Math.atan2(matrix.m12(), matrix.m11()) * 180F / 3.14159265F);
    }

    private final void convertToMatrix(boolean rad){
        matrix = new Matrix4f();
        matrix.rotate((float)(rad ? vec.y : vec.y * 3.14159265F / 180F), new Vector3f(0F, 1F, 0F));
        matrix.rotate((float)(rad ? vec.z : vec.z * 3.14159265F / 180F), new Vector3f(0F, 0F, 1F));
        matrix.rotate((float)(rad ? vec.x : vec.x * 3.14159265F / 180F), new Vector3f(1F, 0F, 0F));
        convertMatrixToAngles();
    }

    public void setAngles(float x, float y, float z){
        vec.x = x;
        vec.y = y;
        vec.z = z;
        convertToMatrix(false);
    }

}
