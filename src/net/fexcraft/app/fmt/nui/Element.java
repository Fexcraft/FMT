package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.*;
import net.fexcraft.lib.frl.gen.Generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Element {

	public static int colorIdx = 0;
	public static int elmIdx = 7;
	public Polyhedron<GLObject> hedron;
	public List<Element> elements;
	public Element root;
	public String texture;
	public float w;
	public float h;

	public Element(){
		hedron = new Polyhedron<>();
		hedron.setGlObj(new GLObject());
	}

	public Element recompile(){
		hedron.recompile = true;
		hedron.clear();
		if(hedron.glObj.pickercolor == null) hedron.glObj.pickercolor = new RGB(colorIdx == 0 ? colorIdx = elmIdx++ : colorIdx).toFloatArray();
		hedron.glObj.textured = texture != null;
		hedron.polygons.add(new Polygon(new Vertex[]{
			new Vertex(w, 0, 0),
			new Vertex(0, 0, 0),
			new Vertex(0, h, 0),
			new Vertex(w, h, 0)
		}));
		return this;
	}

	public float x(){
		return hedron.posX;
	}

	public float y(){
		return hedron.posY;
	}

	public void x(float nx){
		hedron.posX = nx;
	}

	public void y(float ny){
		hedron.posY = ny;
	}

	public void delete(){
		Renderer.RENDERER.delete(hedron);
	}

	public  Element pos(int x, int y){
		hedron.posX = x;
		hedron.posY = y;
		return this;
	}

	public  Element uv(int x, int y){
		hedron.texU = x;
		hedron.texV = y;
		return this;
	}

	public  Element texture(String newtex){
		texture = newtex;
		return this;
	}

	public  Element size(int x, int y){
		w = x;
		h = y;
		return this;
	}

	public Element color(RGB color){
		hedron.glObj.polycolor = color.toFloatArray();
		return this;
	}

	public Element linecolor(RGB color){
		hedron.glObj.linecolor = color.toFloatArray();
		return this;
	}

	public void render(){
		hedron.render();
		if(elements != null) for(Element elm : elements) elm.render();
	}

}
