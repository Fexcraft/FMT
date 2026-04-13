package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_TEXT_0;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Text {

	public final Element root;
	public Polyhedron<GLObject> hedron;
	public FontRenderer.FontType font = FontRenderer.FontType.PLAIN;
	public RGB color = GENERIC_TEXT_0.value.copy();
	public boolean autoscale;
	public boolean centered;
	public boolean cut = true;
	private String text;
	public float scale = 1f;
	public float w;
	public float h;
	public float x = 5;
	public float y = 0;

	public Text(Element elm){
		hedron = new Polyhedron<>();
		hedron.setGlObj(new GLObject());
		root = elm;
	}

	public void recompile(){
		hedron.recompile = true;
		hedron.polygons.clear();
		//if(hedron.glObj.pickercolor == null) hedron.glObj.pickercolor = root.hedron.glObj.pickercolor;
		hedron.glObj.textured = true;
		if(autoscale){
			w = FontRenderer.getWidth(text, font);
			scale = (root.w - 10) < w ? (root.w - 10) / w : 1;
			w *= scale;
			h = FontRenderer.getHeight(text, font) * scale;
		}
		else{
			w = FontRenderer.getWidth(text, font) * scale;
			h = FontRenderer.getHeight(text, font) * scale;
		}
		root.postext();
		FontRenderer.compile(this, text, font, color);
	}

	public void render(){
		TextureManager.bind("font/" + font);
		//PolyRenderer.mode(PolyRenderer.DrawMode.UI_TINTED);
		hedron.render();
		//PolyRenderer.mode(PolyRenderer.DrawMode.UI);
	}

	public Text text(Object ntext){
		text = ntext + "";
		recompile();
		return this;
	}

	public String text(){
		return text;
	}

	public Text centered(boolean bool){
		centered = bool;
		root.postext();
		return this;
	}

	public void color(RGB col){
		color(col.packed);
	}

	public void color(int col){
		color.packed = col;
		if(hedron.polygons.size() > 0){
			for(Polygon polygon : hedron.polygons){
				polygon.color(color);
			}
			hedron.recompile = true;
		}
	}

}
