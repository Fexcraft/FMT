package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.settings.Settings.TRIANGULATION_L;
import static net.fexcraft.app.fmt.utils.ShaderManager.getUniform;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.TexturedVertex;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class MRTRenderer extends ModelRendererTurbo.Renderer {
	
	private static Matrix4f matrix = new Matrix4f();
	public static final float[] EMPTY = { 0, 0, 0, 0 };
	private static final float[] LINECOLOR = { 0, 0, 0, 1};
	public static final float[] SELCOLOR = { 1, 1, 0, 1 };//TODO setting
	private static final Vector3f GIF_AXIS = new Vector3f(0, 1, 0);
	public static DrawMode MODE = DrawMode.TEXTURED;

	@Override
	public void render(ModelRendererTurbo mrt, float scale){
        if(!mrt.showModel){ return; }
        boolean lines = MODE.lines();
        int index = lines ? 1 : 0;
        GlCache cache = mrt.glObject() == null ? mrt.glObject(new GlCache()) : mrt.glObject();
        if(cache.glObj[0].glid == null || mrt.forcedRecompile){
            compileModel(cache, cache.glObj[0], mrt, scale, false);
            compileModel(cache, cache.glObj[1], mrt, scale, true);
            cache.linecolor =  mrt.linesColor != null ? mrt.linesColor.toFloatArray() : LINECOLOR;
            if(cache.polycolor == null) cache.polycolor = EMPTY;
            mrt.forcedRecompile = false;
        }
		matrix = new Matrix4f().identity();
		if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
		matrix.translate(new Vector3f(mrt.rotationPointX * scale, mrt.rotationPointY * scale, mrt.rotationPointZ * scale));
		if(mrt.rotationAngleY != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleY), axis_y);
		if(mrt.rotationAngleZ != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleZ), axis_z);
		if(mrt.rotationAngleX != 0f) matrix.rotate((float)Math.toRadians(mrt.rotationAngleX), axis_x);
		glUniformMatrix4fv(getUniform("model"), false, matrix.get(new float[16]));
		glUniform4fv(getUniform("line_color"), MODE == DrawMode.LINES ? cache.linecolor : MODE == DrawMode.SELLINES ? SELCOLOR : EMPTY);
		glUniform4fv(getUniform("poly_color"), MODE.singleColor() ? cache.polycolor : EMPTY);
		glUniform1f(getUniform("textured"), MODE == DrawMode.TEXTURED ? 1 : 0);
		//
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, cache.glObj[index].glid);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        if(!lines){
    		glEnableVertexAttribArray(1);
    		glBindBuffer(GL_ARRAY_BUFFER, cache.glObj[index].colorss);
    		glVertexAttribPointer(1, 4, GL_FLOAT, false, 4 * 4, 0);
    		glEnableVertexAttribArray(2);
    		glBindBuffer(GL_ARRAY_BUFFER, cache.glObj[index].uvss);
    		glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, 0);
    		glEnableVertexAttribArray(3);
    		glBindBuffer(GL_ARRAY_BUFFER, cache.glObj[index].normss);
    		glVertexAttribPointer(3, 3, GL_FLOAT, false, 3 * 4, 0);
    		glEnableVertexAttribArray(4);
    		glBindBuffer(GL_ARRAY_BUFFER, cache.glObj[index].lightss);
    		glVertexAttribPointer(4, 1, GL_FLOAT, false, 1 * 4, 0);
        }
		glDrawArrays(lines ? GL_LINES : GL_TRIANGLES, 0, cache.glObj[index].size);
		//
        if(mrt.childModels != null){
            for(ModelRendererTurbo child : mrt.childModels) child.render(scale);
        }
	}
	
	public static enum DrawMode {
		
		TEXTURED, UNTEXTURED, RGBCOLOR, POLYGON_PICKER, FACE_PICKER, SELLINES, LINES;
		
		public boolean lines(){
			return this == LINES || this == SELLINES;
		}

		boolean singleColor(){
			return this == POLYGON_PICKER || this == RGBCOLOR;
		}

		public static DrawMode textured(boolean bool){
			return bool ? TEXTURED : UNTEXTURED;
		}
		
	}
	
	public static class GlCache {
		
		public GlObj[] glObj = { new GlObj(), new GlObj() };
		public float[] linecolor;
		public float[] polycolor;
		public Polygon polygon;
		
	}

	private static class GlObj {
		
	    public int uvss;
	    public int normss;
	    public int colorss;
	    public int lightss;
	    public float[] verts;
	    public float[] uvs;
	    public float[] norms;
	    public float[] colors;
	    public float[] lights;
	    public int size;
	    public Integer glid;
		
	}
    
    public static final Vector3f axis_x = new Vector3f(1, 0, 0);
    public static final Vector3f axis_y = new Vector3f(0, 1, 0);
    public static final Vector3f axis_z = new Vector3f(0, 0, 1);

    private static final int[] order2 = { 0, 1, 0, 3, 2, 1, 2, 3  };
    private static final int[] order1 = { 0, 1, 2, 3, 0, 2 };
    private static final int[] order0 = { 0, 1, 2 };

	private void compileModel(GlCache cache, GlObj obj, ModelRendererTurbo mrt, float scale, boolean lines){
    	for(TexturedPolygon polygon : mrt.getFaces()){
    		obj.size += polygon.getVertices().length == 4 ? lines ? 8 : 6 : 3;
    	}
    	obj. verts = new float[obj.size * 3];
    	if(!lines){
        	obj.   uvs = new float[obj.size * 2];
        	obj. norms = new float[obj.size * 3];
        	obj.colors = new float[obj.size * 4];
        	obj.lights = new float[obj.size];
    	}
		//
    	float[] colarr;
		int ver = 0, uv = 0, nor = 0, lig = 0, col = 0;
    	for(int i = 0; i < mrt.getFaces().size(); i++){
    		TexturedPolygon poly = mrt.getFaces().get(i);
    		int[] order = poly.getVertices().length == 4 ? lines && !TRIANGULATION_L.value ? order2 : order1 : order0;
        	Vec3f vec0 = new Vec3f(poly.getVertices()[1].vector.sub(poly.getVertices()[0].vector));
	        Vec3f vec1 = new Vec3f(poly.getVertices()[1].vector.sub(poly.getVertices()[2].vector));
	        Vec3f vec2 = vec1.cross(vec0).normalize();
    		for(int o = 0; o < order.length; o++){
    			TexturedVertex vert = poly.getVertices()[order[o]];
    			obj.verts[ver++] = vert.vector.x * scale;
    			obj.verts[ver++] = vert.vector.y * scale;
    			obj.verts[ver++] = vert.vector.z * scale;
    			if(lines) continue;
    			obj.uvs[uv++] = vert.textureX;
    			obj.uvs[uv++] = vert.textureY;
    			obj.norms[nor++] = vec2.x;
    			obj.norms[nor++] = vec2.y;
    			obj.norms[nor++] = vec2.z;
    			colarr = cache.polygon == null ? EMPTY : cache.polygon.getFaceColor(i);
    			obj.colors[col++] = colarr[0];
    			obj.colors[col++] = colarr[1];
    			obj.colors[col++] = colarr[2];
    			obj.colors[col++] = colarr[3];
    			obj.lights[lig++] = 1;
    		}
        }
    	if(obj.glid != null){
    		glDeleteBuffers(obj.glid);
    		if(!lines){
        		glDeleteBuffers(obj.uvss);
        		glDeleteBuffers(obj.normss);
        		glDeleteBuffers(obj.colorss);
        		glDeleteBuffers(obj.lightss);
    		}
    	}
		//
		glBindBuffer(GL_ARRAY_BUFFER, obj.glid = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, obj.verts, GL_STATIC_DRAW);
		if(lines) return;
		glBindBuffer(GL_ARRAY_BUFFER, obj.uvss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, obj.uvs, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, obj.normss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, obj.norms, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, obj.colorss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, obj.colors, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, obj.lightss = glGenBuffers());
		glBufferData(GL_ARRAY_BUFFER, obj.lights, GL_STATIC_DRAW);
	}

	public static void mode(DrawMode mode){
		MODE = mode;
	}
	
}
