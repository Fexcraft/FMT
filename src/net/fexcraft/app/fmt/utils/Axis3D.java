package net.fexcraft.app.fmt.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import net.fexcraft.lib.common.math.Vec3f;

/** Taken from FVTM and adjusted/shortened. */
public class Axis3D {
	
	private Matrix4f matrix;
	private double yaw, pitch, roll;

    public Axis3D(){ matrix = new Matrix4f(); }
    
    @Override public String toString(){ return "[ " + yaw + "y, " + pitch + "p, " + roll + "r ]";  }
    
    public Vec3f getRelativeVector(Vec3f vec){
        Matrix4f mat = new Matrix4f();
        mat.m00 = (float)vec.xCoord; mat.m10 = (float)vec.yCoord; mat.m20 = (float)vec.zCoord;
        Matrix4f.rotate((float)(roll  * 3.14159265F / 180D), new Vector3f(1F, 0F, 0F), mat, mat);
        Matrix4f.rotate((float)(pitch * 3.14159265F / 180D), new Vector3f(0F, 0F, 1F), mat, mat);
        Matrix4f.rotate((float)(yaw   * 3.14159265F / 180D), new Vector3f(0F, 1F, 0F), mat, mat);
        return new Vec3f(mat.m00, mat.m10, mat.m20);
    }

    private final void convertMatrixToAngles(){
        yaw = (float) Math.atan2(matrix.m20, matrix.m00) * 180F / 3.14159265F;
        pitch = (float) Math.atan2(-matrix.m10, Math.sqrt(matrix.m12 * matrix.m12 + matrix.m11 * matrix.m11)) * 180F / 3.14159265F;
        roll = (float) Math.atan2(matrix.m12, matrix.m11) * 180F / 3.14159265F;
    }

    private final void convertToMatrix(boolean rad){
        matrix = new Matrix4f();
        matrix.rotate((float) (rad ? roll : roll * 3.14159265F / 180F), new Vector3f(1F, 0F, 0F));
        matrix.rotate((float) (rad ? pitch : pitch * 3.14159265F / 180F), new Vector3f(0F, 0F, 1F));
        matrix.rotate((float) (rad ? yaw : yaw * 3.14159265F / 180F), new Vector3f(0F, 1F, 0F));
        convertMatrixToAngles();
    }

    public void setAngles(double yaw, double pitch, double roll){
        this.yaw = yaw;
        this.pitch = pitch;
        this.roll = roll;
        convertToMatrix(false);
    }

}
