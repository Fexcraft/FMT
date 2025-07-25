package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.polygon.GLObject.GPUData;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.ImageHandler;
import net.fexcraft.app.fmt.utils.ShaderManager;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.ColoredVertex;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.mod.uni.IDL;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static net.fexcraft.app.fmt.settings.Settings.TRIANGULATION_L;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

public class PolyRenderer extends net.fexcraft.lib.frl.Renderer<GLObject> {

    private static Pivot PIVOT = null;
	private static Model HELPER = null;
    private static Matrix4f matrix = new Matrix4f();
	private static DrawMode MODE = DrawMode.TEXTURED;
	public static ShaderManager.ShaderProgram program;
	public static final float[] LINECOLOR = { 0, 0, 0, 1}, EMPTY = { 0, 0, 0, 0 }, SELCOLOR = { 1, 1, 0, 1 };
	private static final Vector3f GIF_AXIS = new Vector3f(0, 1, 0);
	private boolean subpoly;
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
			if(MODE.ui()){
				if(MODE.ui_lines() && glo.linecolor == null) return;
			}
			else if(glo.linecolor == null) glo.linecolor = LINECOLOR;
			if(glo.polycolor == null) glo.polycolor = EMPTY;
			if(glo.pickercolor == null) glo.pickercolor = EMPTY;
			poly.recompile = false;
		}
		if(!subpoly){
			if(PIVOT == null && HELPER == null){
				matrix = new Matrix4f().identity();
				if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
			}
			else if(HELPER != null){
				matrix = HELPER.matrix.get(new Matrix4f()).scale(HELPER.scl.x, HELPER.scl.y, HELPER.scl.z);
			}
			else{
				matrix = PIVOT.matrix.get(new Matrix4f());
			}
		}
		matrix.translate(new Vector3f(poly.posX, poly.posY, poly.posZ));
		if(poly.rotY != 0f) matrix.rotate((float)Math.toRadians(poly.rotY), axis_y);
		if(poly.rotX != 0f) matrix.rotate((float)Math.toRadians(poly.rotX), axis_x);
		if(poly.rotZ != 0f) matrix.rotate((float)Math.toRadians(poly.rotZ), axis_z);
		//
		glUniformMatrix4fv(program.getUniform("model"), false, matrix.get(new float[16]));
		if(MODE.ui()){
			glUniform4fv(program.getUniform("line_color"), MODE.ui_lines() ? glo.linecolor : EMPTY);
			glUniform4fv(program.getUniform("poly_color"), MODE.picker() ? glo.pickercolor : !glo.textured ? glo.polycolor : EMPTY);
			glUniform1f(program.getUniform("textured"), glo.textured ? 1 : 0);
			glUniform1f(program.getUniform("tinted"), MODE.ui_text() ? 1 : 0);
		}
		else{
			glUniform4fv(program.getUniform("line_color"), MODE.lines() ? MODE == DrawMode.SELLINES ? SELCOLOR : glo.linecolor : EMPTY);
			glUniform4fv(program.getUniform("poly_color"), MODE.picker() ? glo.pickercolor : MODE.color() ? glo.polycolor : EMPTY);
			glUniform1f(program.getUniform("textured"), MODE.textured() ? 1 : 0);
		}
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
			subpoly = true;
			Matrix4f ref = matrix;
            for(Polyhedron<GLObject> sub : poly.sub){
				matrix = new Matrix4f().set(ref);
				sub.render();
			}
			subpoly = false;
        }
	}

	public static void setHelper(Model model){
		HELPER = model;
		if(HELPER == null) return;
		Matrix4f matrix = HELPER.matrix = new Matrix4f().identity();
		if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
		matrix.translate(HELPER.pos);
		if(HELPER.rot.y != 0f) matrix.rotate((float)Math.toRadians(HELPER.rot.y), axis_y);
		if(HELPER.rot.x != 0f) matrix.rotate((float)Math.toRadians(HELPER.rot.x), axis_x);
		if(HELPER.rot.z != 0f) matrix.rotate((float)Math.toRadians(HELPER.rot.z), axis_z);
	}

	public static void setPivot(Pivot npivot){
		PIVOT = npivot;
		if(PIVOT == null) return;
		Matrix4f matrix = PIVOT.matrix = new Matrix4f().identity();
		if(ImageHandler.ROT != null) matrix.rotate(ImageHandler.ROT, GIF_AXIS);
		for(Pivot pivot : PIVOT.roots){
			matrix.translate(pivot.pos);
			if(pivot.rot.y != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.y), axis_y);
			if(pivot.rot.x != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.x), axis_x);
			if(pivot.rot.z != 0f) matrix.rotate((float)Math.toRadians(pivot.rot.z), axis_z);
		}
		matrix.translate(PIVOT.pos);
		if(PIVOT.rot.y != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.y), axis_y);
		if(PIVOT.rot.x != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.x), axis_x);
		if(PIVOT.rot.z != 0f) matrix.rotate((float)Math.toRadians(PIVOT.rot.z), axis_z);
	}
	
	public static void updateLightState(){
		glUniform1f(program.getUniform("lighting"), Settings.LIGHTING_ON.value && MODE.lighting() ? 1 : 0);
		glUniform3fv(program.getUniform("lightcolor"), Settings.LIGHT_COLOR.value.toFloatArray());
		glUniform3fv(program.getUniform("lightpos"), new float[]{ Settings.LIGHT_POSX.value, Settings.LIGHT_POSY.value, Settings.LIGHT_POSZ.value });
		glUniform1f(program.getUniform("ambient"), Settings.LIGHT_AMBIENT.value);
		glUniform1f(program.getUniform("diffuse"), Settings.LIGHT_DIFFUSE.value);
	}
    
    public static final Vector3f axis_x = new Vector3f(1, 0, 0);
    public static final Vector3f axis_y = new Vector3f(0, 1, 0);
    public static final Vector3f axis_z = new Vector3f(0, 0, 1);

    private static final int[] orderql = { 0, 1, 0, 3, 2, 1, 2, 3  };
    private static final int[] orderqn = { 0, 1, 2, 3, 0, 2 };
    private static final int[] ordertn = { 0, 1, 2 };
    private static final int[] ordertl = { 0, 1, 0, 2, 1, 2 };

	private void compile(Polyhedron<GLObject> poli, GLObject glo, GPUData obj, boolean lines){
    	for(net.fexcraft.lib.frl.Polygon polygon : poli.polygons){
			if(polygon.vertices.length <= 4){
				obj.size += polygon.vertices.length == 4 ? lines ? 8 : 6 : lines ? 6 : 3;
			}
    		else{
				if(lines) obj.size += polygon.vertices.length * 2;
				else obj.size += (polygon.vertices.length - 2) * 3;
			}
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
    		int[] order = poly.vertices.length == 4 ? lines && !TRIANGULATION_L.value ? orderql : orderqn : poly.vertices.length == 3 ? lines ? ordertl : ordertn : genOrder(poly.vertices.length, lines);
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

	private int[] genOrder(int length, boolean lines){
		int[] order = new int[lines ? length * 2 : (length - 2) * 3];
		int j = 0;
		if(lines){
			for(int i = 0; i < length - 1; i++){
				order[j++] = i;
				order[j++] = i + 1;
			}
			order[j++] = length - 1;
			order[j] = 0;
		}
		else{
			for(int i = 2; i < length; i++){
				order[j++] = 0;
				order[j++] = i - 1;
				order[j++] = i;
			}
		}
		return order;
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

	@Override
	public void push(){

	}

	@Override
	public void pop(){

	}

	@Override
	public void translate(double x, double y, double z){

	}

	@Override
	public void rotate(float deg, int x, int y, int z){

	}

	@Override
	public void rotate(double deg, int x, int y, int z){

	}

	@Override
	public void scale(double x, double y, double z){

	}

	@Override
	public void bind(IDL tex){

	}

	@Override
	public void color(int rgb){

	}

	@Override
	public void light(V3D pos){

	}

	public static enum DrawMode {
		
		TEXTURED, UNTEXTURED, RGBCOLOR, PICKER, PICKER_FACE, SELLINES, LINES, UI, UI_LINES, UI_TEXT;
		
		public boolean lines(){
			return this == LINES || this == SELLINES || this == UI_LINES;
		}

		public boolean picker(){
			return this == PICKER;
		}

		public boolean face_picker(){
			return this == PICKER_FACE;
		}

		public boolean vertex_picker(){
			return this == PICKER_FACE;
		}

		public boolean ui(){
			return this == UI || this == UI_LINES || this == UI_TEXT;
		}

		public boolean ui_lines(){
			return this == UI_LINES;
		}

		public boolean ui_text(){
			return this == UI_TEXT;
		}

		public boolean color(){
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
