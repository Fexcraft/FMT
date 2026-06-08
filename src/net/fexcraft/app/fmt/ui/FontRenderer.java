package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Vector3F;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.frl.Polygon;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.fexcraft.app.fmt.settings.Settings.FONT_SIZEN;

/**
 * Based on the FontRenderer in FMT v1,
 * which was made initially in reference to this
 * <a href="https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts">LWJGL Tutorial/Wiki</a>.
 *
 * @author Ferdinand Calo' (FEX___96)
 */
public class FontRenderer {

	private static Map<FontType, Map<Character, Glyph>> glyphs = new HashMap<>();
	private static Map<FontType, Map<Character, Polyhedron>> hedrons = new HashMap<>();
	private static boolean antialiasing = true;
	private static ArrayList<Character> CHARS = new ArrayList<>();
	private static Font[] ALL = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	public static String DEFAULT_CHARS = ".,!@#$%^&*()_+{};:'\"><?=-[]0987654321~`|\\/ ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public enum FontType {

		PLAIN (new Font(Font.SANS_SERIF, Font.PLAIN, FONT_SIZEN)),
		BOLD  (new Font(Font.SANS_SERIF, Font.BOLD, FONT_SIZEN)),
		ITALIC(new Font(Font.SANS_SERIF, Font.ITALIC, FONT_SIZEN)),
		MONO  (new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZEN));

		private Font font;
		private int width;
		private int height;

		private FontType(Font fnt){
			font = fnt;
		}

		public Font getFontFor(char c){
			if(font.canDisplay(c)) return font;
			for(Font font : ALL){
				if(!font.canDisplay(c)) continue;
				Logging.log("Using Font '" + font.getFontName() + "' for char: u" + Integer.toHexString(c));
				return font.deriveFont(FONT_SIZEN).deriveFont(this == MONO ? 0 : ordinal());
			}
			return font;
		}

	}

	public static void init(){
		for(char c : DEFAULT_CHARS.toCharArray()) CHARS.add(c);
		for(FontType type : FontType.values()){
			glyphs.put(type, new HashMap<>());
			hedrons.put(type, new HashMap<>());
			initGlyphs(type, glyphs.get(type));
		}
	}

	private static void initGlyphs(FontType type, Map<Character, Glyph> map){
		int img_w = 0;
		int img_h = 0;
		for(char c : CHARS){
			BufferedImage ch = createCharImage(type.getFontFor(c), c);
			img_w += ch.getWidth() + (ch.getWidth() < 4 ? 1 : 0);
			img_h = Math.max(img_h, ch.getHeight());
		}
		//
		type.height = img_h;
		BufferedImage image = new BufferedImage(img_w, img_h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		int x = 0;
		for(char c : CHARS){
			BufferedImage char_img = createCharImage(type.getFontFor(c), c);
			if(char_img == null) continue;
			int char_w = char_img.getWidth() + (char_img.getWidth() < 4 ? 1 : 0);
			int char_h = char_img.getHeight();
			Glyph ch = new Glyph(char_w, char_h, x, image.getHeight() - char_h);
			g.drawImage(char_img, x, 0, null);
			x += ch.width;
			map.put(c, ch);
		}
		g.dispose();
		File file = new File("./temp/font/" + type + ".png");
		if(!file.exists()){
			file.getParentFile().mkdirs();
			Logging.log("Font Type " + type + " Image not found, saving a new one.");
		}
		//
		try{
			ImageIO.write(image, "png", file);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		type.width = image.getWidth();
		TextureManager.loadFromFile("font/" + type, file);
	}

	private static BufferedImage createCharImage(Font font, char c){
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		if(antialiasing){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		int char_w = metrics.charWidth(c);
		int char_h = metrics.getHeight();
		image = new BufferedImage(char_w <= 0 ? 1 : char_w, char_h, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if(antialiasing){
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		g.setFont(font);
		g.setPaint(Color.WHITE);
		g.drawString(Character.toString(c), 0, metrics.getAscent());
		g.dispose();
		return image;
	}

	public static class Glyph {

		public static final Glyph NULL = new Glyph(8, 16, 0, 0);
		public final int width;
		public final int height;
		public final int x;
		public final int y;

		public Glyph(int w, int h, int xp, int yp){
			width = w;
			height = h;
			x = xp;
			y = yp;
		}

	}

	public static Glyph getGlyph(FontType type, char c){
		Glyph glyph = glyphs.get(type).get(c);
		return glyph == null ? Glyph.NULL : glyph;
	}

	public static int getWidth(CharSequence text, FontType type){
		int width = 0;
		int lwidth = 0;
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			if(c == '\n'){
				width = Math.max(width, lwidth);
				lwidth = 0;
				continue;
			}
			if(c == '\r') continue;
			Glyph g = getGlyph(type, c);
			lwidth += g.width;
		}
		width = Math.max(width, lwidth);
		return width;
	}

	public static int getHeight(CharSequence text, FontType type){
		int height = 0;
		int lheight = 0;
		for(int i = 0; i < text.length(); i++){
			char c = text.charAt(i);
			if(c == '\n'){
				height += lheight;
				lheight = 0;
				continue;
			}
			if(c == '\r') continue;
			Glyph g = getGlyph(type, c);
			lheight = Math.max(lheight, g.height);
		}
		height += lheight;
		return height;
	}

	public static void compile(Text text, CharSequence str, FontType type, float[] color){
		if(str.length() == 0) return;
		float px = 0;
		float py = 0;
		float max = text.root.w * (1f / text.scale) - 10;
		for(int i = 0; i < str.length(); i++){
			char c = str.charAt(i);
			if(c == '\n'){
				py += type.height;
				px = 0;
				continue;
			}
			if(c == '\r') continue;
			Glyph g = getGlyph(type, c);
			if(text.cut && px + g.width >= max) break;
			text.chardata.add(new Text.CharInfo(getHedron(type, c, g), new Vector3F(px, py, text.root.z() + 0.5f), color));
			px += g.width;
		}
	}

	private static Polyhedron getHedron(FontType type, char c, Glyph g){
		Polyhedron hedron = hedrons.get(type).get(c);
		if(hedron == null){
			float tw = 1f / type.width;
			float th = 1f / type.height;
			float tx = tw * g.x;
			float ty = th * g.y;
			hedron = new Polyhedron();
			hedron.polygons.add(new Polygon(new Vertex[]{
				new Vertex(g.width, 0, 0).uv(tx + (g.width * tw), ty),
				new Vertex(0, 0, 0).uv(tx, ty),
				new Vertex(0, g.height, 0).uv(tx, ty + (g.height * th)),
				new Vertex(g.width, g.height, 0).uv(tx + (g.width * tw), ty + (g.height * th)),
			}));
			((GLObject)hedron.glObj).textured = true;
			hedrons.get(type).put(c, hedron);
		}
		return hedron;
	}

}
