package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.settings.Settings.TRIANGULATION_L;
import static net.fexcraft.app.fmt.utils.ShaderManager.getUniform;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
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
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Logging;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.polygon.GLObject.GPUData;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.ImageHandler;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.ColoredVertex;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;

public class PolyRenderer extends net.fexcraft.lib.frl.Renderer<GLObject> {

    private static Pivot PIVOT = null;
    private static Matrix4f matrix = new Matrix4f();
	private static DrawMode MODE = DrawMode.TEXTURED;
	public static final float[] LINECOLOR = { 0, 0, 0, 1}, EMPTY = { 0, 0, 0, 0 }, SELCOLOR = { 1, 1, 0, 1 };
	private static final Vector3f GIF_AXIS = new Vector3f(0, 1, 0);
	private boolean lines;
	private GLObject glo;

	@Override
	public void render(Polyhedron<GLObject> poly){
		if(!poly.visible) return;
		int index = (lines = MODE.lines()) ? 1 : 0;
		glo = poly.glObj;
		if(poly.recompile || glo.gpu[0].glid == null){
			compile(poly, glo, glo.gpu[0], false);
			compile(poly, glo, glo.gpu[1], true);
			glo.linecolor = LINECOLOR;
			if(glo.polycolor == null) glo.polycolor = EMPTY;
			if(glo.pickercolor == null) glo.pickercolor = EMPTY;
			poly.recompile = false;
		}
		if(PIVOT == null){
			matrix = new Matrix4f().identity();
			if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
		}
		else{
			matrix = PIVOT.matrix.get(new Matrix4f());
		}
		matrix.translate(new Vector3f(poly.posX, poly.posY, poly.posZ));
		if(poly.rotY != 0f) matrix.rotate((float)Math.toRadians(poly.rotY), axis_y);
		if(poly.rotZ != 0f) matrix.rotate((float)Math.toRadians(poly.rotZ), axis_z);
		if(poly.rotX != 0f) matrix.rotate((float)Math.toRadians(poly.rotX), axis_x);
		//
		glUniformMatrix4fv(getUniform("model"), false, matrix.get(new float[16]));
		glUniform4fv(getUniform("line_color"), MODE == DrawMode.LINES ? glo.linecolor : MODE == DrawMode.SELLINES ? SELCOLOR : EMPTY);
		glUniform4fv(getUniform("poly_color"), MODE.picker() ? glo.pickercolor : MODE.color() ? glo.polycolor : EMPTY);
		glUniform1f(getUniform("textured"), MODE.textured() ? 1 : 0);
		//
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, glo.gpu[index].glid);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        if(!lines){
    		glEnableVertexAttribArray(1);
    		glBindBuffer(GL_ARRAY_BUFFER, glo.gpu[index].colorss);
    		glVertexAttribPointer(1, 4, GL_FLOAT, false, 4 * 4, 0);
    		glEnableVertexAttribArray(2);
    		glBindBuffer(GL_ARRAY_BUFFER, glo.gpu[index].uvss);
    		glVertexAttribPointer(2, 2, GL_FLOAT, false, 2 * 4, 0);
    		glEnableVertexAttribArray(3);
    		glBindBuffer(GL_ARRAY_BUFFER, glo.gpu[index].normss);
    		glVertexAttribPointer(3, 3, GL_FLOAT, false, 3 * 4, 0);
    		glEnableVertexAttribArray(4);
    		glBindBuffer(GL_ARRAY_BUFFER, glo.gpu[index].lightss);
    		glVertexAttribPointer(4, 1, GL_FLOAT, false, 1 * 4, 0);
        }
		glDrawArrays(lines ? GL_LINES : GL_TRIANGLES, 0, glo.gpu[index].size);
		//
        if(poly.sub != null){
            for(Polyhedron<GLObject> sub : poly.sub) sub.render();
        }
	}

	public static void setPivot(Pivot npivot){
		PIVOT = npivot;
		if(PIVOT == null) return;
		Matrix4f matrix = PIVOT.matrix = new Matrix4f().identity();
		if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
		for(Pivot pivot : PIVOT.roots){
			matrix.translate(pivot.pos);
			if(pivot.rot.y != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.y), axis_y);
			if(pivot.rot.z != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.z), axis_z);
			if(pivot.rot.x != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.x), axis_x);
		}
		matrix.translate(PIVOT.pos);
		if(PIVOT.rot.y != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.y), axis_y);
		if(PIVOT.rot.z != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.z), axis_z);
		if(PIVOT.rot.x != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.x), axis_x);
	}
	
	public static void updateLightState(){
		glUniform1f(getUniform("lighting"), Settings.LIGHTING_ON.value && MODE.lighting() ? 1 : 0);
		glUniform3fv(getUniform("lightcolor"), Settings.LIGHT_COLOR.value.toFloatArray());
		glUniform3fv(getUniform("lightpos"), new float[]{ Settings.LIGHT_POSX.value, Settings.LIGHT_POSY.value, Settings.LIGHT_POSZ.value });
		glUniform1f(getUniform("ambient"), Settings.LIGHT_AMBIENT.value);
		glUniform1f(getUniform("diffuse"), Settings.LIGHT_DIFFUSE.value);
	}
    
    public static final Vector3f axis_x = new Vector3f(1, 0, 0);
    public static final Vector3f axis_y = new Vector3f(0, 1, 0);
    public static final Vector3f axis_z = new Vector3f(0, 0, 1);

    private static final int[] order2 = { 0, 1, 0, 3, 2, 1, 2, 3  };
    private static final int[] order1 = { 0, 1, 2, 3, 0, 2 };
    private static final int[] order0 = { 0, 1, 2 };

	private void compile(Polyhedron<GLObject> poli, GLObject glo, GPUData obj, boolean lines){
    	for(net.fexcraft.lib.frl.Polygon polygon : poli.polygons){
    		obj.size += polygon.vertices.length == 4 ? lines ? 8 : 6 : 3;
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
    	for(int i = 0; i < poli.polygons.size(); i++){
    		Polygon poly = poli.polygons.get(i);
    		int[] order = poly.vertices.length == 4 ? lines && !TRIANGULATION_L.value ? order2 : order1 : order0;
        	Vec3f vec0 = new Vec3f(poly.vertices[1].vector.sub(poly.vertices[0].vector));
	        Vec3f vec1 = new Vec3f(poly.vertices[1].vector.sub(poly.vertices[2].vector));
	        Vec3f vec2 = vec1.cross(vec0).normalize();
    		for(int o = 0; o < order.length; o++){
    			Vertex vert = poly.vertices[order[o]];
    			obj.verts[ver++] = vert.vector.x;
    			obj.verts[ver++] = vert.vector.y;
    			obj.verts[ver++] = vert.vector.z;
    			if(lines) continue;
    			obj.uvs[uv++] = vert.u;
    			obj.uvs[uv++] = vert.v;
    			obj.norms[nor++] = vec2.x;
    			obj.norms[nor++] = vec2.y;
    			obj.norms[nor++] = vec2.z;
    			if(glo.polygon == null){
    				if(vert instanceof ColoredVertex){
            			obj.colors[col++] = vert.color().x;
            			obj.colors[col++] = vert.color().y;
            			obj.colors[col++] = vert.color().z;
            			obj.colors[col++] = 1f;
    				}
    				else{
            			obj.colors[col++] = EMPTY[0];
            			obj.colors[col++] = EMPTY[1];
            			obj.colors[col++] = EMPTY[2];
            			obj.colors[col++] = EMPTY[3];
    				}
    			}
    			else{
        			colarr = glo.polygon.getFaceColor(i).toFloatArray();
        			obj.colors[col++] = colarr[0];
        			obj.colors[col++] = colarr[1];
        			obj.colors[col++] = colarr[2];
        			obj.colors[col++] = colarr[3];
    			}
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

	@Override
	public void delete(Polyhedron<GLObject> poly){
		for(int i = 0; i < 2; i++){
    		if(poly.glObj.gpu[i].glid != null) glDeleteBuffers(poly.glObj.gpu[i].glid);
    		if(i == 0){
        		glDeleteBuffers(poly.glObj.gpu[i].uvss);
        		glDeleteBuffers(poly.glObj.gpu[i].normss);
        		glDeleteBuffers(poly.glObj.gpu[i].colorss);
        		glDeleteBuffers(poly.glObj.gpu[i].lightss);
    		}
		}
	}
	
	public static enum DrawMode {
		
		TEXTURED, UNTEXTURED, RGBCOLOR, PICKER, PICKER_FACE, SELLINES, LINES;
		
		public boolean lines(){
			return this == LINES || this == SELLINES;
		}

		boolean picker(){
			return this == PICKER;
		}

		boolean face_picker(){
			return this == PICKER_FACE;
		}

		boolean color(){
			return this == RGBCOLOR;
		}

		public static DrawMode textured(boolean bool){
			return bool ? TEXTURED : UNTEXTURED;
		}

		public boolean lighting(){
			return this != PICKER && this != PICKER_FACE && this != LINES;
		}

		public boolean textured(){
			return this == TEXTURED && this != PICKER_FACE;
		}
		
	}

	public static void mode(DrawMode mode){
		MODE = mode;
	}

	public static DrawMode mode(){
		return MODE;
	}

}
