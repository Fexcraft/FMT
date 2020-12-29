package net.fexcraft.app.fmt.utils;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.TexturedVertex;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class MRTRenderer extends ModelRendererTurbo.Renderer {

	@Override
	public void render(ModelRendererTurbo mrt, float scale){
        if(!mrt.showModel){ return; }
        if(mrt.glId() == null || mrt.forcedRecompile){
            compileModel(mrt, scale);
        }
		matrix = new Matrix4f().identity();
		matrix.translate(new Vector3f(mrt.rotationPointX * scale, mrt.rotationPointY * scale, mrt.rotationPointZ * scale));
		if(mrt.rotationAngleY != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleY), axis_y);
		if(mrt.rotationAngleZ != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleZ), axis_z);
		if(mrt.rotationAngleX != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleX), axis_x);
		int model = glGetUniformLocation(ShaderManager.GENERAL.program(), "model");
		glUniformMatrix4fv(model, false, matrix.get(new float[16]));
		//
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, mrt.glId());
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ARRAY_BUFFER, uvss);
		glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, 0);
		glEnableVertexAttribArray(3);
		glBindBuffer(GL_ARRAY_BUFFER, normss);
		glVertexAttribPointer(3, 3, GL_FLOAT, false, 3 * 4, 0);
		glEnableVertexAttribArray(4);
		glBindBuffer(GL_ARRAY_BUFFER, lightss);
		glVertexAttribPointer(4, 1, GL_FLOAT, false, 1 * 4, 0);
		glDrawArrays(GL_TRIANGLES, 0, size);
		//
        if(mrt.childModels != null){
            for(ModelRendererTurbo child : mrt.childModels) child.render(scale);
        }
	}

    public int uvss;
    public int normss;
    public int lightss;
    public float[] verts;
    public float[] uvs;
    public float[] norms;
    public float[] lights;
    public Matrix4f matrix;
    public int size;
    
    public static final Vector3f axis_x = new Vector3f(1, 0, 0);
    public static final Vector3f axis_y = new Vector3f(0, 1, 0);
    public static final Vector3f axis_z = new Vector3f(0, 0, 1);
    
    private static final int[] order1 = { 0, 1, 2, 3, 0, 2 };
    private static final int[] order0 = { 0, 1, 2 };

	private void compileModel(ModelRendererTurbo mrt, float scale){
    	for(TexturedPolygon polygon : mrt.getFaces()){
    		size += polygon.getVertices().length > 3 ? 6 : 3;
    	}
		verts = new float[size * 3];
		  uvs = new float[size * 2];
		norms = new float[size * 3];
		lights = new float[size];
		//
		int ver = 0, uv = 0, nor = 0, lig = 0;
    	for(int i = 0; i < mrt.getFaces().size(); i++){
    		TexturedPolygon poly = mrt.getFaces().get(i);
    		int[] order = poly.getVertices().length > 3 ? order1 : order0;
        	Vec3f vec0 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[0].vector));
	        Vec3f vec1 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[2].vector));
	        Vec3f vec2 = vec1.cross(vec0).normalize();
    		for(int o = 0; o < order.length; o++){
    			TexturedVertex vert = poly.getVertices()[order[o]];
    			verts[ver++] = vert.vector.xCoord * scale;
    			verts[ver++] = vert.vector.yCoord * scale;
    			verts[ver++] = vert.vector.zCoord * scale;
    			uvs[uv++] = vert.textureX;
    			uvs[uv++] = vert.textureY;
    			norms[nor++] = vec2.xCoord;
    			norms[nor++] = vec2.yCoord;
    			norms[nor++] = vec2.zCoord;
        		lights[lig++] = 1;//poly.level;
    		}
        }
		//
		glBindBuffer(GL_ARRAY_BUFFER, mrt.glId(glGenBuffers()));
		glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, uvss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, uvs, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, normss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, norms, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, lightss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, lights, GL_STATIC_DRAW);
	}
	
}
