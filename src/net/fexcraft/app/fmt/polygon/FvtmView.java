package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;

import java.awt.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmView extends Polygon {

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
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			default: super.setValue(polyval, value); break;
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof FvtmView == false) return poly;
		//FvtmView view = (FvtmView)poly;
		return poly;
	}

	@Override
	public float[][][] newUV(boolean with_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	@Override
	public boolean paintTex(Texture tex, Integer face){
		return paintTex(glm, tex, face);
	}

	public static boolean paintTex(Polyhedron hed, Texture tex, Integer face){
		if(face == null || face < 0){
			for(int i = 0; i < hed.polygons.size(); i++) paintTex(hed, tex, i);
			return true;
		}
		net.fexcraft.lib.frl.Polygon gon = hed.polygons.get(face);
		if(gon.vertices.length == 3){
			paintTri(tex, gon.vertices[0], gon.vertices[1], gon.vertices[2]);
		}
		else if(gon.vertices.length == 4){
			paintTri(tex, gon.vertices[0], gon.vertices[1], gon.vertices[2]);
			paintTri(tex, gon.vertices[3], gon.vertices[0], gon.vertices[2]);
		}
		return true;
	}

	public static void paintTri(Texture tex, Vertex t0, Vertex t1, Vertex t2){
		byte[] c = TexturePainter.getCurrentColor();
		float u0 = t0.u * tex.getWidth();
		float u1 = t1.u * tex.getWidth();
		float u2 = t2.u * tex.getWidth();
		float v0 = t0.v * tex.getHeight();
		float v1 = t1.v * tex.getHeight();
		float v2 = t2.v * tex.getHeight();
		float r0, r1, r2;
		boolean n, p;
		int xs = (int)Math.min(u0, Math.min(u1, u2)) - 1;
		int xe = (int)Math.max(u0, Math.max(u1, u2)) + 1;
		int ys = (int)Math.min(v0, Math.min(v1, v2)) - 1;
		int ye = (int)Math.max(v0, Math.max(v1, v2)) + 1;
		for(float x = xs < 0 ? 0 : xs; x < xe && x < tex.getWidth(); x += 0.5f){
			for(float y = ys < 0 ? 0 : ys; y < ye && y < tex.getHeight(); y += 0.5f){
				r0 = s(x, y, u0, v0, u1, v1);
				r1 = s(x, y, u1, v1, u2, v2);
				r2 = s(x, y, u2, v2, u0, v0);
				n = r0 < 0 || r1 < 0 || r2 < 0;
				p = r0 > 0 || r1 > 0 || r2 > 0;
				if(n && !p){
					tex.set((int)x, (int)y, c);
				}
			}
		}
	}

	private static float s(float x, float y, float u0, float v0, float u1, float v1){
		return (x - u1) * (v0 - v1) - (u0 - u1) * (y - v1);
	}

}
