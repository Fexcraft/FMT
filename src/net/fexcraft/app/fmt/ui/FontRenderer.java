package net.fexcraft.app.fmt.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
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
	
	private static int[] FONT_HEIGHT = new int[]{ 0, 0, 0, 0 };
	private static boolean antialiens = true;
	private static Map<Character, Glyph> italic_glyphs = new HashMap<>();
	private static Map<Character, Glyph> plain_glyphs = new HashMap<>();
	private static Map<Character, Glyph> bold_glyphs = new HashMap<>();
	private static Map<Character, Glyph> mono_glyphs = new HashMap<>();
	public static int[] TYPE_WIDTH = new int[]{ 0, 0, 0, 0 };
	
	/** https://github.com/SilverTiger/lwjgl3-tutorial/wiki/Fonts */
	public static void init(){
		initGlyphs(0); initGlyphs(1); initGlyphs(2); initGlyphs(3);
	}

	private static void initGlyphs(int type){
		Font font = new Font(type == 3 ? Font.MONOSPACED : Font.SANS_SERIF,
			type == 0 || type == 3 ? Font.PLAIN : type == 1 ? Font.BOLD : Font.ITALIC, 16);
		//
		int imageWidth = 0, imageHeight = 0;
		for(int i = 32; i < 256; i++){
		    if(i == 127){ continue; }
		    char c = (char)i;
		    BufferedImage ch = createCharImage(font, c, antialiens);
		    imageWidth += ch.getWidth() + (ch.getWidth() < 4 ? 1 : 0);
		    imageHeight = Math.max(imageHeight, ch.getHeight());
		}
		FONT_HEIGHT[type] = imageHeight;
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
        int x = 0;
        for(int i = 32; i < 256; i++) {
            if(i == 127){ continue; }
            char c = (char)i;
            BufferedImage charImage = createCharImage(font, c, antialiens);
            if(charImage == null){ continue; }
            int charWidth = charImage.getWidth() + (charImage.getWidth() < 4 ? 1 : 0);
            int charHeight = charImage.getHeight();
            Glyph ch = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += ch.width;
            switch(type){
            	case 0: plain_glyphs.put(c, ch); break;
            	case 1: bold_glyphs.put(c, ch); break;
            	case 2: italic_glyphs.put(c, ch); break;
            	case 3: mono_glyphs.put(c, ch); break;
            }
        }
        g.dispose();
        File file = new File("./resources/textures/font/ascii" + type + ".png");
        if(!file.exists()){ file.getParentFile().mkdirs();
        	Print.console("Font Type " + type + " Image not found, saving a new one.");
            try{ ImageIO.write(image, "png", file); }
            catch(Exception e){ e.printStackTrace(); }
        }
        TYPE_WIDTH[type] = image.getWidth();
        TextureManager.loadTextureFromFile("font/ascii" + type, file);
	}

	private static BufferedImage createCharImage(Font font, char c, boolean b){
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
		g.drawString(String.valueOf(c), 0, metrics.getAscent());
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

	public static Glyph getGlyph(int type, char c){
		switch(type){
			case 3: return mono_glyphs.get(c);
			case 2: return italic_glyphs.get(c);
			case 1: return bold_glyphs.get(c);
			case 0: return plain_glyphs.get(c);
			default: return Glyph.NULL;
		}
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
	
	public static int getWidth(CharSequence text, int type){
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
	
    public static int getHeight(CharSequence text, int type){
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
    
    public static void drawText(CharSequence text, float x, float y, int type, RGB color){
    	if(text.length() == 0) return; //int textHeight = getHeight(text, type);
        float drawX = x, drawY = y; if(color == null) color = RGB.BLACK;
        //if(textHeight > FONT_HEIGHT[type]){ drawY -= textHeight - FONT_HEIGHT[type]; }
        TextureManager.bindTexture("font/ascii" + type);
        for(int i = 0; i < text.length(); i++) {
            char cher = text.charAt(i);
            if(cher == '\n'){ drawY += FONT_HEIGHT[type]; drawX = x; continue; }
            if(cher == '\r'){ continue; }
            Glyph g = getGlyph(type, cher);
            float tw = 1f / TYPE_WIDTH[type], th = 1f / FONT_HEIGHT[type];
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
    
    public static void drawText(CharSequence text, float x, float y, int type){
        drawText(text, x, y, type, null);
    }
    
    public static void drawText(CharSequence text, float x, float y){
        drawText(text, x, y, 0, null);
    }

}
