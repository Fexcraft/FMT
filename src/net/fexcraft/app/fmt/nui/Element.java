package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Renderer;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Element {

	public static int elmIdx = 7;
	public int colorIdx = 0;
	public Polyhedron<GLObject> hedron;
	public Runnable onclick;
	public List<Element> elements;
	public Element root;
	public String texture;
	public String tooltip;
	public boolean visible;
	public boolean hovered;
	public boolean rounded;
	public boolean hoverable;
	public boolean border;
	public RGB linecolor = RGB.WHITE;
	public Text text;
	private float x;
	private float y;
	public float z;
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
		if(text != null) text.recompile();
		return this;
	}

	public float x(){
		return x;
	}

	public float y(){
		return y;
	}

	public float gx(){
		return root == null ? x : root.gx() + x;
	}

	public float gy(){
		return root == null ? y : root.gy() + y;
	}

	public void x(float nx){
		x = nx;
		hedron.posX = gx();
		if(text != null) postext();
	}

	public void y(float ny){
		y = ny;
		hedron.posY = gy();
		if(text != null) postext();
	}

	public void xa(float nx){
		x += nx;
		hedron.posX = gx();
		if(text != null) postext();
	}

	public void ya(float ny){
		y += ny;
		hedron.posY = gy();
		if(text != null) postext();
	}

	public void delete(){
		Renderer.RENDERER.delete(hedron);
	}

	public Element pos(int x, int y){
		x(x);
		y(y);
		return this;
	}

	public Element posa(int x, int y){
		xa(x);
		ya(y);
		return this;
	}

	public Element uv(int x, int y){
		hedron.texU = x;
		hedron.texV = y;
		return this;
	}

	public Element texture(String newtex){
		TextureManager.load(newtex, true);
		texture = newtex;
		return this;
	}

	public Element size(float x, float y){
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

	public Element onclick(Runnable cons){
		onclick = cons;
		return this;
	}

	public Element tooltip(String ttip){
		tooltip = ttip;
		return this;
	}

	public Element text(String ntext){
		if(text == null) text = new Text(this);
		text.text(ntext);
		return this;
	}

	public void postext(){
		text.hedron.posX = hedron.posX;
		text.hedron.posY = hedron.posY;
		text.hedron.posX += text.centered ? (w - text.w) * 0.5 : 5;
		text.hedron.posY += (h - text.h) * 0.5;
	}

	public void render(Picker.PickTask picker){
		if(!visible) return;
		if(picker == null){
			if(hedron.glObj.linecolor != null){
				PolyRenderer.mode(DrawMode.UI_LINES);
				hedron.render();
				PolyRenderer.mode(DrawMode.UI);
			}
			if(texture != null) TextureManager.bind(texture);
		}
		if(picker != Picker.PickTask.HOVER || hoverable) hedron.render();
		if(text != null) text.render();
		if(elements != null) for(Element elm : elements) elm.render(picker);
	}

	public void update(){
		if(elements != null) for(Element elm : elements) elm.update();
		hovered(false);
	}

	public void add(Element elm){
		if(elements == null) elements = new ArrayList<>();
		elements.add(elm.root(this).recompile());
	}

	public Element root(Element elm){
		root = elm;
		z = elm.z + 1;
		return this;
	}

	public void hovered(boolean bool){
		hovered = bool;
		hedron.glObj.linecolor = bool ? PolyRenderer.SELCOLOR : border ? linecolor.toFloatArray() : null;
	}

	public boolean hoveredx(){
		if(hovered) return true;
		if(elements != null) for(Element elm : elements) if(elm.hoveredx()) return true;
		return false;
	}

	public void click(){
		if(onclick != null) onclick.run();
	}

}
