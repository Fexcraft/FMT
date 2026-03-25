package net.fexcraft.app.fmt.ui;

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
import java.util.function.Predicate;

import static net.fexcraft.app.fmt.ui.FMTInterface.TOOLBAR_HEIGHT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Element {

	public static Element SELECTED;
	public static Element HOVERED;
	//public static int elmIdx = 7;
	//public int colorIdx = 0;
	public Polyhedron<GLObject> hedron;
	public Consumer<ClickInfo> onclick;
	public Consumer<ScrollInfo> onscroll;
	public List<Element> elements;
	public Element root;
	public Element hint;
	public String texture;
	public boolean visible;
	public boolean hovered;
	public boolean hoverable;
	public boolean selectable;
	private RGB border;
	public RGB col_def = RGB.WHITE.copy();
	public RGB col_hov = new RGB(0xdede00);
	public RGB col_sel = new RGB(0x43f0ae);
	public Text text;
	private ElmShape shape = ElmShape.RECTANGLE;
	public float[] pickpos = new float[4];
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
		hedron.polygons.clear();
		//if(hedron.glObj.pickercolor == null) hedron.glObj.pickercolor = new RGB(colorIdx == 0 ? colorIdx = elmIdx++ : colorIdx).toFloatArray();
		hedron.glObj.textured = texture != null;
		pickpos[0] = hedron.posX = gx();
		pickpos[1] = hedron.posY = gy();
		pickpos[2] = pickpos[0] + w;
		pickpos[3] = pickpos[1] + h;
		switch(shape){
			case RECTANGLE -> {
				hedron.polygons.add(new Polygon(new Vertex[]{
					new Vertex(w, 0, z).uv(1, 0),
					new Vertex(0, 0, z).uv(0, 0),
					new Vertex(0, h, z).uv(0, 1),
					new Vertex(w, h, z).uv(1, 1)
				}));
			}
			case RECT_ROUNDED -> {
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
			case CUSTOM -> {
				genShape();
			}
			case NONE -> {}
		}
		if(text != null) text.recompile();
		return this;
	}

	protected void genShape(){}

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
		pickpos[0] = hedron.posX = gx();
		pickpos[2] = pickpos[0] + w;
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.xa(0);
	}

	public void y(float ny){
		y = ny;
		pickpos[1] = hedron.posY = gy();
		pickpos[3] = pickpos[1] + h;
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.ya(0);
	}

	public void xa(float nx){
		x += nx;
		pickpos[0] = hedron.posX = gx();
		pickpos[2] = pickpos[0] + w;
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.xa(0);
	}

	public void ya(float ny){
		y += ny;
		pickpos[1] = hedron.posY = gy();
		pickpos[3] = pickpos[1] + h;
		if(text != null) postext();
		if(elements != null) for(Element elm : elements) elm.ya(0);
	}

	public void delete(){
		hedron.delete();
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

	public Element color(int color){
		col_def.packed = color;
		hedron.glObj.polycolor = col_def.toFloatArray();
		return this;
	}

	public Element color(RGB color){
		col_def = color;
		hedron.glObj.polycolor = col_def.toFloatArray();
		return this;
	}

	public Element border(RGB color){
		return border(color == null ? -1 : color.packed);
	}

	public Element border(int color){
		if(color < 0) border = null;
		else if(border == null){
			border = new RGB();
			border.packed = color;
		}
		if(border != null){
			hedron.glObj.linecolor = border.toFloatArray();
		}
		return this;
	}

	public Element shape(ElmShape nshape){
		shape = nshape;
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
		hint.translate(hinttext);
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

	public Element translate(String ntext, Object... format){
		return text(Translator.format(ntext, format));
	}

	public void postext(){
		text.hedron.posX = hedron.posX;
		text.hedron.posY = hedron.posY;
		text.hedron.posX += text.centered ? (w - text.w) * 0.5 : 5;
		text.hedron.posY += (h - text.h) * 0.5;
	}

	public Element text_scale(float s){
		if(text == null) return this;
		text.scale = s;
		return this;
	}

	public Element text_centered(boolean bool){
		if(text != null) text.centered(bool);
		return this;
	}

	public Element text_color(int col){
		if(text != null) text.color(col);
		return this;
	}

	public Element text_autoscale(){
		if(text != null) text.autoscale = true;
		return this;
	}

	public void render(Picker.PickTask picker){
		if(!visible) return;
		if(pickpos[0] > FMT.SCALED_WIDTH || pickpos[1] > FMT.SCALED_HEIGHT || pickpos[2] < 0 || pickpos[3] < 0) return;
		if(picker == null){
			if(border != null){
				PolyRenderer.mode(DrawMode.UI_LINES);
				hedron.render();
				PolyRenderer.mode(DrawMode.UI);
			}
			if(texture != null) TextureManager.bind(texture);
		}
		/*if(picker != Picker.PickTask.HOVER || hoverable)*/ hedron.render();
		if(text != null && picker == null) text.render();
		if(elements != null) for(Element elm : elements) elm.render(picker);
		if(hint != null && picker == null && hovered){
			hint.pos(GGR.mousePosX() + 10, GGR.mousePosY() + (GGR.mousePosY() > FMT.HEIGHT - TOOLBAR_HEIGHT ? -30 : 0)).render(picker);
		}
	}

	public void update0(){
		update();
		if(elements != null) for(Element elm : elements) elm.update0();
		hovered(false);
	}

	public void update(){}

	public void resize(){
		onResize();
		if(elements == null) return;
		for(Element elm : elements) elm.resize();
	}

	public void onResize(){}

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
		if(hoverable){
			if(border != null){
				hedron.glObj.linecolor = bool ? PolyRenderer.SELCOLOR : border.toFloatArray();
			}
			update_polycolor();
		}
	}

	protected void update_polycolor(){
		hedron.glObj.polycolor = (selected() ? col_sel : hovered ? col_hov : col_def).toFloatArray();
	}

	public boolean hoveredx(){
		if(hovered) return true;
		if(elements != null) for(Element elm : elements) if(elm.hoveredx()) return true;
		return false;
	}

	public void click(int x, int y){
		if(!selected() && !root.selected()) select(null);
		if(onclick != null) onclick.accept(new ClickInfo(x, y, (int)(x - gx()), (int)(y - gy())));
		else if(selectable) select(this);
	}

	public void click(ClickInfo info){
		if(!selected() && !root.selected()) select(null);
		if(onclick != null) onclick.accept(info);
		else if(selectable) select(this);
	}

	public void scroll(double x, double y){
		if(onscroll != null) onscroll.accept(new ScrollInfo((int)x, (int)y, (int)(x - gx()), (int)(y - gy())));
	}

	public void scroll(ScrollInfo info){
		if(onscroll != null) onscroll.accept(info);
	}

	public boolean selected(){
		return this == SELECTED;
	}

	public static void select(Element elm){
		if(SELECTED != null){
			SELECTED.onDeselect(elm);
			SELECTED.update_polycolor();
		}
		SELECTED = elm == SELECTED ? null : elm;
		if(SELECTED == null) return;
		SELECTED.onSelect();
		SELECTED.update_polycolor();
	}

	protected void onSelect(){

	}

	protected void onDeselect(Element current){

	}

	public boolean isField(){
		return this instanceof Field;
	}

	public static boolean isSelectedAField(){
		return SELECTED != null && SELECTED.isField();
	}

	public Element hide(){
		visible = false;
		return this;
	}

	public Element show(){
		visible = true;
		return this;
	}

	protected Element zi(){
		z++;
		return this;
	}

	protected void clearElements(boolean del){
		if(elements != null){
			for(Element element : elements){
				element.clearElements(false);
				element.delete();
			}
			elements.clear();
		}
		if(del) delete();
	}

	public Element lastElement(){
		if(elements == null || elements.isEmpty()) return null;
		return elements.get(elements.size() - 1);
	}

	public void toggleVisibility(){
		if(visible) hide();
		else show();
	}

	public Element getElmAt(double x, double y){
		if(!visible) return null;
		Element ret;
		if(elements != null){
			for(Element elm : elements){
				ret = elm.getElmAt(x, y);
				if(ret != null) return ret;
			}
		}
		if(x >= pickpos[0] && x <= pickpos[2] && y >= pickpos[1] && y <= pickpos[3]) return this;
		return null;
	}

	public void remElmIf(Predicate<Element> pre){
		if(elements == null) return;
		elements.removeIf(elm -> {
			if(pre.test(elm)){
				elm.delete();
				return true;
			}
			return false;
		});
	}

	public static record ClickInfo(int cx, int cy, int lx, int ly){}

	public static record ScrollInfo(int sx, int sy, int lx, int ly){}

	public static enum ElmShape{

		RECTANGLE,
		RECT_ROUNDED,
		CUSTOM,
		NONE

	}

}
