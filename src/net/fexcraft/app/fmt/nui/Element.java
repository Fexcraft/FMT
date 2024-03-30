package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Element {

	public static int elmIdx = 7;
	public int colorIdx = 0;
	public Polyhedron<GLObject> hedron;
	public List<Element> elements;
	public Element root;
	public String texture;
	public boolean visible;
	public boolean hovered;
	public boolean rounded;
	public boolean hoverable;
	public boolean border;
	public RGB linecolor = RGB.WHITE;
	public int z;
	public float w;
	public float h;

	public Element(){
		hedron = new Polyhedron<>();
		hedron.setGlObj(new GLObject());
		visible = true;
	}

	public Element recompile(){
		hedron.recompile = true;
		hedron.clear();
		if(hedron.glObj.pickercolor == null) hedron.glObj.pickercolor = new RGB(colorIdx == 0 ? colorIdx = elmIdx++ : colorIdx).toFloatArray();
		hedron.glObj.textured = texture != null;
		if(rounded){
			hedron.polygons.add(new Polygon(new Vertex[]{
				new Vertex(5, 0, z),
				new Vertex(0, 5, z),
				new Vertex(0, h - 5, z),
				new Vertex(5, h, z),
				new Vertex(w - 5, h, z),
				new Vertex(w, h - 5, z),
				new Vertex(w, 5, z),
				new Vertex(w - 5, 0, z)
			}));
		}
		else{
			hedron.polygons.add(new Polygon(new Vertex[]{
				new Vertex(w, 0, z).uv(1, 0),
				new Vertex(0, 0, z).uv(0, 0),
				new Vertex(0, h, z).uv(0, 1),
				new Vertex(w, h, z).uv(1, 1)
			}));
		}
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
		TextureManager.load(newtex, true);
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
		linecolor = color;
		hedron.glObj.linecolor = color.toFloatArray();
		border = true;
		return this;
	}

	public Element rounded(boolean bool){
		rounded = bool;
		return this;
	}

	public Element hoverable(boolean bool){
		hoverable = bool;
		return this;
	}

	public Element zidx(int idx){
		z = idx;
		return this;
	}

	public void render(Picker.PickTask picker){
		if(!visible) return;
		if(picker == null){
			if(hedron.glObj.linecolor != null){
				PolyRenderer.mode(DrawMode.LINES);
				hedron.render();
				PolyRenderer.mode(DrawMode.UI);
			}
			if(texture != null) TextureManager.bind(texture);
		}
		if(picker == Picker.PickTask.HOVER && !hoverable) return;
		hedron.render();
		if(elements != null) for(Element elm : elements) elm.render(picker);
		hovered(false);
	}

	public void add(Element elm){
		if(elements == null) elements = new ArrayList<>();
		elm.x(elm.x() + x());
		elm.y(elm.y() + y());
		elements.add(elm.zidx(z + 1).recompile());
	}

	public void hovered(boolean bool){
		hovered = bool;
		hedron.glObj.linecolor = bool ? PolyRenderer.SELCOLOR : border ? linecolor.toFloatArray() : null;
	}

	public void click(){
		Logging.log(this);
	}

}
