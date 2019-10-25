package net.fexcraft.app.fmt.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FontRenderer {
	
	/** https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts - first reference, afterwards reworked. */
	
	private static Map<Character, Glyph> italic_glyphs = new HashMap<>();
	private static Map<Character, Glyph> plain_glyphs = new HashMap<>();
	private static Map<Character, Glyph> bold_glyphs = new HashMap<>();
	private static Map<Character, Glyph> mono_glyphs = new HashMap<>();
	private static boolean antialiens = true;
	private static ArrayList<Character> CHARS = new ArrayList<>();
	private static Font[] ALL = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	public static String DEFAULT_CHARS = ".,!@#$%^&*()_+{};:'\"><?=-[]0987654321~`|\\/ ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public static enum FontType {
		
		PLAIN (new Font(Font.SANS_SERIF, Font.PLAIN,  16)),
		BOLD  (new Font(Font.SANS_SERIF, Font.BOLD,   16)),
		ITALIC(new Font(Font.SANS_SERIF, Font.ITALIC, 16)),
		MONO  (new Font(Font.MONOSPACED, Font.PLAIN,  16));
		
		private Font font;
		private int height, width;
		
		private FontType(Font font){
			this.font = font;
		}

		public Font getFittingFont(char c){
			if(font.canDisplay(c)) return font;
			for(Font font : ALL){
				if(font.canDisplay(c)){
					Print.console("Using Font '" + font.getFontName() + "' for char: u" + Integer.toHexString(c));
					return font.deriveFont(16f).deriveFont(this == MONO ? 0 : ordinal());
				}
			} return font;
		}
		
	}
	
	public static void init(){
		for(char c : DEFAULT_CHARS.toCharArray()) CHARS.add(c);
		for(FontType type : FontType.values()) initGlyphs(type);
	}

	private static void initGlyphs(FontType type){
		int imageWidth = 0, imageHeight = 0;
		for(char c : CHARS){
		    BufferedImage ch = createCharImage(type.getFittingFont(c), c, antialiens);
		    imageWidth += ch.getWidth() + (ch.getWidth() < 4 ? 1 : 0);
		    imageHeight = Math.max(imageHeight, ch.getHeight());
		}
		//
		type.height = imageHeight;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
        int x = 0;
        for(char c : CHARS){
            BufferedImage charImage = createCharImage(type.getFittingFont(c), c, antialiens);
            if(charImage == null){ continue; }
            int charWidth = charImage.getWidth() + (charImage.getWidth() < 4 ? 1 : 0);
            int charHeight = charImage.getHeight();
            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            switch(type.ordinal()){
            	case 0: plain_glyphs.put(c, ch); break;
            	case 1: bold_glyphs.put(c, ch); break;
            	case 2: italic_glyphs.put(c, ch); break;
            	case 3: mono_glyphs.put(c, ch); break;
            }
        }
        g.dispose();
        File file = new File("./resources/textures/font/" + type + ".png");
        if(!file.exists()){ file.getParentFile().mkdirs(); } //Print.console("Font Type " + type + " Image not found, saving a new one.");
        //
        try{ ImageIO.write(image, "png", file); } catch(Exception e){ e.printStackTrace(); }
        type.width = image.getWidth();
        TextureManager.loadTextureFromFile("font/" + type, file);
	}

	private static BufferedImage createCharImage(Font font, char c, boolean antialiens){
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		if(antialiens){ g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics();
		g.dispose();
		int charWidth = metrics.charWidth(c);
		int charHeight = metrics.getHeight();
		image = new BufferedImage(charWidth <= 0 ? 1 : charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		if(antialiens){ g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); }
		g.setFont(font);
		g.setPaint(Color.WHITE);
		g.drawString(Character.toString(c), 0, metrics.getAscent());
		g.dispose();
		return image;
	}
	
	public static class Glyph {
		
	    public static final Glyph NULL = new Glyph(8, 16, 0, 0, 0);

		public final int x, y, width, height; public final float adv;

	    public Glyph(int width, int height, int x, int y, float adv){
	        this.width = width; this.height = height; this.x = x; this.y = y; this.adv = adv;
	    }
	    
	}

	public static Glyph getGlyph(FontType type, char c){
		Glyph glyph = null;
		switch(type){
			case MONO:   glyph = mono_glyphs.get(c); break;
			case ITALIC: glyph = italic_glyphs.get(c); break;
			case BOLD:   glyph = bold_glyphs.get(c); break;
			case PLAIN:  glyph = plain_glyphs.get(c); break;
			default: glyph = Glyph.NULL; break;
		}
		return glyph == null ? Glyph.NULL : glyph;
	}
	
	/*public static class Text {
		
		private String text;
		private Glyph[] chars;
		private int type;
		
		public Text(String string, int type){
			this.text = string; chars = new Glyph[string.length()];
			char[] strchars = string.toCharArray();
			for(int i = 0; i < chars.length; i++){
				chars[i] = FontRenderer.getGlyph(type, strchars[i]);
			}
		}
		
	}*/
	
	public static int getWidth(CharSequence text, FontType type){
        int width = 0, lineWidth = 0;
        for(int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            if(c == '\n'){
                width = Math.max(width, lineWidth);
                lineWidth = 0; continue;
            }
            if(c == '\r'){ continue; }
            Glyph g = getGlyph(type, c);
            lineWidth += g.width;
        }
        width = Math.max(width, lineWidth); return width;
    }
	
    public static int getHeight(CharSequence text, FontType type){
        int height = 0, lineHeight = 0;
        for(int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            if(c == '\n'){
                height += lineHeight;
                lineHeight = 0; continue;
            }
            if(c == '\r'){ continue; }
            Glyph g = getGlyph(type, c);
            lineHeight = Math.max(lineHeight, g.height);
        } height += lineHeight; return height;
    }
    
    public static void drawText(CharSequence text, float x, float y, FontType type, RGB color){
    	if(text.length() == 0) return; //int textHeight = getHeight(text, type);
        float drawX = x, drawY = y; if(color == null) color = RGB.BLACK;
        //if(textHeight > FONT_HEIGHT[type]){ drawY -= textHeight - FONT_HEIGHT[type]; }
        TextureManager.bindTexture("font/" + type);
        for(int i = 0; i < text.length(); i++) {
            char cher = text.charAt(i);
            if(cher == '\n'){ drawY += type.height; drawX = x; continue; }
            if(cher == '\r'){ continue; }
            Glyph g = getGlyph(type, cher);
            float tw = 1f / type.width, th = 1f / type.height;
            float tx = tw * g.x, ty = th * g.y;
            color.glColorApply();
    		GL11.glBegin(GL11.GL_QUADS);
    		GL11.glTexCoord2f(tx, ty);
    			GL11.glVertex2f(drawX, drawY);
    		GL11.glTexCoord2f(tx + (g.width * tw), ty);
    			GL11.glVertex2f(drawX + g.width, drawY);
    		GL11.glTexCoord2f(tx + (g.width * tw), ty + (g.height * th));
    			GL11.glVertex2f(drawX + g.width, drawY + g.height);
    		GL11.glTexCoord2f(tx, ty + (g.height * th));
    			GL11.glVertex2f(drawX, drawY + g.height);
            GL11.glEnd();
            RGB.glColorReset();
            drawX += g.width;
        }
    }
    
    public static void drawText(CharSequence text, float x, float y, FontType type){
        drawText(text, x, y, type, null);
    }
    
    public static void drawText(CharSequence text, float x, float y){
        drawText(text, x, y, FontType.PLAIN, null);
    }

}
