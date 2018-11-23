package net.fexcraft.app.fmt.utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class RayCoastAway {

	private static final ArrayList<ModelRendererTurbo> arr = new ArrayList<ModelRendererTurbo>();
	private static final ModelRendererTurbo model = new ModelRendererTurbo(null, 0, 0, 16, 16);
	static { model.addSphere(0, 0, 0, 1, 8, 8, 0, 0); }
	private static final ArrayList<Vec3f[]> polis = new ArrayList<Vec3f[]>();

	public static void doTest(){
		if(!Static.dev()) return;
		Vec3f posbc = new Vec3f(FMTB.ggr.pos);
		posbc.yCoord = -posbc.yCoord; posbc.zCoord = -posbc.zCoord;
		//
		//see http://www.java-gaming.org/topics/ray-casting-tutorial/31900/view.html
		int width = FMTB.get().getDisplayMode().getWidth(), height = FMTB.get().getDisplayMode().getHeight();
		float[] matModelView = new float[16], matProjView = new float[16]; int[] view = new int[16];
        float mouseX = width / 2, mouseY = height / 2;
        Vec3f start = new Vec3f(), end = new Vec3f();
        FloatBuffer modelBuffer = compileBuffer(matModelView);
        FloatBuffer projBuffer = compileBuffer(matProjView);
        FloatBuffer startBuffer = compileBuffer(new float[]{start.xCoord, start.yCoord, start.zCoord, 1});
        FloatBuffer endBuffer = compileBuffer(new float[]{end.xCoord, end.yCoord, end.zCoord, 1});
        IntBuffer viewBuffer = compileBuffer(view);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelBuffer);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projBuffer);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewBuffer);
        GLU.gluUnProject(mouseX, mouseY, 0.0f, modelBuffer, projBuffer, viewBuffer, startBuffer);
        GLU.gluUnProject(mouseX, mouseY, 1.0f, modelBuffer, projBuffer, viewBuffer, endBuffer);
        start = new Vec3f(startBuffer.get(0), startBuffer.get(1), startBuffer.get(2));
        end = new Vec3f(endBuffer.get(0), endBuffer.get(1), endBuffer.get(2));
        //see end;
        
        model.setRotationPoint((start.xCoord * 16), (start.yCoord * 16), (start.zCoord * 16)); arr.clear();
        while(start.distanceTo(end) > 0.1 && start.distanceTo(posbc) < 20){ start = start.distance(end, 0.1f);
        	ModelRendererTurbo model = new ModelRendererTurbo(null, 0, 0, 16, 16).addSphere(0, 0, 0, 1, 8, 8, 0, 0);
        	arr.add(model.setRotationPoint(start.xCoord * 16, start.yCoord * 16, start.zCoord * 16));
        }
        //
        polis.clear();
        FMTB.MODEL.getCompound().values().forEach(val -> {
        	val.forEach(pol -> {
        		if(pol.getTurboObject(0) != null){
        			ModelRendererTurbo tmt = pol.getTurboObject(0);
        			for(TexturedPolygon poly : tmt.getFaces()){
        				Vec3f[] vecs = new Vec3f[poly.getVertices().length];
        				for(int i = 0; i < vecs.length; i++){
        					vecs[i] = new Vec3f(poly.getVertices()[i].vector);
        					vecs[i].xCoord += tmt.rotationPointX;
        					vecs[i].yCoord += tmt.rotationPointY;
        					vecs[i].zCoord += tmt.rotationPointZ;
        				}
        				polis.add(vecs);// Print.console(vecs.length + " vecs || "  + str(poly.getVertices()[0].vector));
        			}
        		}
        	});
        });
        //polis.forEach(pol -> { for(Vec3f vec : pol){ Print.console(str(vec)); } Print.console("--- --- --- ---"); });
        for(Vec3f[] vecs : polis){
        	if(collided(vecs)){
        		for(Vec3f vec : vecs){ Print.console(str(vec)); }
        	}
        }
	}
	
	private static boolean collided(Vec3f[] vecs){
		// TODO Auto-generated method stub
		return false;
	}

	private static String str(Vec3f vec){
		return vec.xCoord + " " + vec.yCoord + " " + vec.zCoord;
	}
	
	private static FloatBuffer compileBuffer(float[] floats){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(floats.length);
        buffer.put(floats);  buffer.flip(); return buffer;
    }

	private static IntBuffer compileBuffer(int[] ints){
        IntBuffer buffer = BufferUtils.createIntBuffer(ints.length);
        buffer.put(ints); buffer.flip(); return buffer;
    }

	public static void render(){
		if(arr == null) model.render(); else for(ModelRendererTurbo turbo : arr) if(turbo != null) turbo.render();
	}

}