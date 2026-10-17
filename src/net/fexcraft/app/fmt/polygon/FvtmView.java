package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;

public class FvtmView extends Polygon {

	private RGB rgb = RGB.random();

	public FvtmView(Model model){
		super(model);
	}

	public FvtmView(Model model, JsonMap obj){
		super(model, obj);
	}

	public FvtmView(Model model, Polyhedron hed){
		super(model);
		glm = hed;
	}

	@Override
	public Shape getShape(){
		return Shape.VIEW;
	}

	@Override
	protected void generate(){
		//
	}

	@Override
	public int getFaceColor(int i){
		return i;
	}

	@Override
	public Face getFaceByColor(int i){
		return NoFace.NONE;
	}
	
	@Override
	public void render(float alpha){
		PolyRenderer.SCALE0 = FMT.MODEL.scl.x;
		super.render(alpha);
		PolyRenderer.SCALE0 = 1f;
	}
	
	@Override
	public void recompile(){
		//super.recompile();
		GLObject gl = glm.glObj();
		gl.polycolor = RGB.random().toFloatArray();
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case COLOR: return rgb.packed;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case COLOR: rgb.packed = (int)value; break;
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof FvtmView == false) return poly;
		FvtmView marker = (FvtmView)poly;
		marker.rgb.packed = rgb.packed;
		return poly;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	public boolean paintTex(Texture tex, Integer face){
		if(face == null){
			for(int i = 0; i < glm.polygons.size(); i++) paintTex(tex, i);
			return true;
		}
		//
		return true;
	}

}
