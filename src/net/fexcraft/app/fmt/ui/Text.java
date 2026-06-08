package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Vector3F;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_TEXT_0;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Text {

	public final Element root;
	public List<CharInfo> chardata = new ArrayList<>();
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
	public float rx;
	public float ry;

	public Text(Element elm){
		root = elm;
	}

	public void recompile(){
		chardata.clear();
		if(autoscale){
			w = FontRenderer.getWidth(text, font);
			scale = (root.w - 5 - x) < w ? (root.w - 5 - x) / w : 1;
			w *= scale;
			h = FontRenderer.getHeight(text, font) * scale;
		}
		else{
			w = FontRenderer.getWidth(text, font) * scale;
			h = FontRenderer.getHeight(text, font) * scale;
		}
		root.postext();
		FontRenderer.compile(this, text, font, color.toFloatArray());
	}

	public void render(){
		TextureManager.bind("font/" + font);
		GLObject obj;
		for(CharInfo info : chardata){
			obj = info.hedron.glObj();
			obj.polycolor = info.col;
			info.hedron.pos(rx + info.pos.x, ry + info.pos.y, info.pos.z).render();
		}
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
		float[] arr = color.toFloatArray();;
		for(CharInfo info : chardata){
			info.col = arr;
		}
	}

	public void postext(){
		rx = root.hedron.posX;
		ry = root.hedron.posY;
		rx += centered ? (root.w - w) * 0.5f : x;
		ry += centered ? (root.h - h) * 0.5f : y;
	}

	public static class CharInfo {

		private Polyhedron hedron;
		private Vector3F pos;
		private float[] col;

		public CharInfo(Polyhedron hed, Vector3F vec, float[] arr){
			hedron = hed;
			pos = vec;
			col = arr;
		}

	}

}
