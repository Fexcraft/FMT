package net.fexcraft.app.fmt.polygon;

import java.util.ArrayList;

import net.fexcraft.app.fmt.demo.ModelMark;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.frl.GLO;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.frl.gen.Generator_Cuboid;
import net.fexcraft.lib.frl.gen.Generator_Cylinder;
import net.fexcraft.lib.frl.gen.ValueMap;
import net.fexcraft.lib.script.elm.FltElm;
import org.lwjgl.opengl.GL;

public class ObjView extends Polygon {

	private ArrayList<TexturedPolygon> polis;
	private RGB rgb = RGB.random();
	public float scale = 1;

	public ObjView(Model model){
		super(model);
	}

	public ObjView(Model model, JsonMap obj){
		super(model, obj);
		scale = obj.get("scale", scale);
	}

	public ObjView(Model model, ArrayList<TexturedPolygon> value){
		super(model);
		polis = value;
	}

	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		if(!export){
			map.add("scale", scale);
		}
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.OBJECT;
	}

	@Override
	protected void generate(){
		glm.texU = glm.texV = 1;
		for(TexturedPolygon poli : polis){
			net.fexcraft.lib.frl.Polygon gon = new net.fexcraft.lib.frl.Polygon(poli.getVertices().length);
			for(int i = 0; i < gon.vertices.length; i++){
				gon.vertices[i] = new Vertex(poli.getVertices()[i].vector);
				gon.vertices[i].u = poli.getVertices()[i].textureX;
				gon.vertices[i].v = poli.getVertices()[i].textureY;
			}
			glm.polygons.add(gon);
		}
	}

	@Override
	public RGB getFaceColor(int i){
		return rgb;
	}

	@Override
	public Face getFaceByColor(int i){
		return NoFace.NONE;
	}
	
	@Override
	public void render(FltElm alpha){
		//DrawMode mode = PolyRenderer.mode();
		//PolyRenderer.mode(DrawMode.RGBCOLOR);
		glm.render();
		//PolyRenderer.mode(mode);
	}
	
	@Override
	public void recompile(){
		super.recompile();
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case COLOR: return rgb.packed;
			case SCALE: return scale;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case COLOR: rgb.packed = (int)value; break;
			case SCALE: scale = value; break;
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof ObjView == false) return poly;
		ObjView marker = (ObjView)poly;
		marker.rgb.packed = rgb.packed;
		marker.scale = scale;
		return poly;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

}
