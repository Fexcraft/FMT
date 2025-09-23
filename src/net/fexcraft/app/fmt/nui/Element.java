package net.fexcraft.app.fmt.nui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Picker;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Renderer;
import net.fexcraft.lib.frl.Vertex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.nui.FMTInterface.TOOLBAR_HEIGHT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Element {

	public static int elmIdx = 7;
	public int colorIdx = 0;
	public Polyhedron<GLObject> hedron;
	public Consumer<ClickInfo> onclick;
	public Consumer<ScrollInfo> onscroll;
	public List<Element> elements;
	public Element root;
	public Element hint;
	public String texture;
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

	public void init(Object... args){

	}

	public Element recompile(){
		hedron.recompile = true;
		hedron.clear();
		if(hedron.glObj.pickercolor == null) hedron.glObj.pickercolor = new RGB(colorIdx == 0 ? colorIdx = elmIdx++ : colorIdx).toFloatArray();
		hedron.glObj.textured = texture != null;
		hedron.posX = gx();
		hedron.posY = gy();
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
		if(elements != null) for(Element elm : elements) elm.xa(0);
	}

	public void y(float ny){
		y = ny;
		hedron.posY = gy();
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.ya(0);
	}

	public void xa(float nx){
		x += nx;
		hedron.posX = gx();
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.xa(0);
	}

	public void ya(float ny){
		y += ny;
		hedron.posY = gy();
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.ya(0);
	}

	public void delete(){
		Renderer.RENDERER.delete(hedron);
	}

	public Element pos(float x, float y){
		x(x);
		y(y);
		return this;
	}

	public Element posa(float x, float y){
		xa(x);
		ya(y);
		return this;
	}

	public Element uv(float x, float y){
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

	public Element onclick(Consumer<ClickInfo> cons){
		onclick = cons;
		return this;
	}

	public Element onscroll(Consumer<ScrollInfo> cons){
		onscroll = cons;
		return this;
	}

	public Element hint(String hinttext){
		if(hint == null){
			hint = new Hint();
			hint.init(hinttext);
			hoverable = true;
		}
		hint.text(hinttext);
		return this;
	}

	public Element text(Object ntext){
		if(text == null) text = new Text(this);
		text.text(ntext);
		return this;
	}

	public Element translate(String ntext){
		return text(Translator.translate(ntext));
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
		if(text != null && picker == null) text.render();
		if(elements != null) for(Element elm : elements) elm.render(picker);
		if(hint != null && picker == null && hovered){
			hint.pos(GGR.mousePosX() + 10, GGR.mousePosY() + (GGR.mousePosY() > FMT.HEIGHT - TOOLBAR_HEIGHT ? -30 : 0)).render(picker);
		}
	}

	public void update(){
		if(elements != null) for(Element elm : elements) elm.update();
		hovered(false);
	}

	public void onResize(){

	}

	public void add(Element elm){
		add(elm, new Object[0]);
	}

	public void add(Element elm, Object... args){
		if(elements == null) elements = new ArrayList<>();
		elements.add(elm.root(this));
		elm.init(args);
		elm.recompile();
	}

	public Element root(Element elm){
		root = elm;
		z += elm.z + 1;
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

	public void click(int x, int y){
		if(onclick != null) onclick.accept(new ClickInfo(x, y, (int)(x - gx()), (int)(y - gy())));
	}

	public void click(ClickInfo info){
		if(onclick != null) onclick.accept(info);
	}

	public void scroll(double x, double y){
		if(onscroll != null) onscroll.accept(new ScrollInfo((int)x, (int)y, (int)(x - gx()), (int)(y - gy())));
	}

	public void scroll(ScrollInfo info){
		if(onscroll != null) onscroll.accept(info);
	}

	protected Element hide(){
		visible = false;
		return this;
	}

	protected Element zi(){
		z++;
		return this;
	}

	public static record ClickInfo(int cx, int cy, int lx, int ly){}

	public static record ScrollInfo(int sx, int sy, int lx, int ly){}

}
