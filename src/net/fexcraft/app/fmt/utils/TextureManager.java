package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.TextureImpl;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureManager {
	
	/*private static final Map<String, Texture> textures = new HashMap<String, Texture>();

	public static final void loadTextures(){
		textures.clear(); String name;
		File folder = new File("./resources/textures/");
		for(File file : folder.listFiles()){
			if(file.isDirectory()) continue;
			if((name = file.getName()).endsWith(".png") || name.endsWith(".PNG")){
				loadResourcePNG(name = name.replace(".png", ""));
			} else continue;
		}
	}
	
	public static final Texture getTexture(String name){
		return textures.containsKey(name) ? textures.get(name) : textures.get("null");
	}
	
	public static final Map<String, Texture> getTextures(){
		return textures;
	}
	
	public static final Texture loadTexture(String id, String path, String type){
		try{ //FMTB.print(id, path, type);
			Texture texture = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(path));
			textures.put(id, texture); return texture;
		}
		catch(IOException e){ e.printStackTrace();  return null; }
	}
	
	public static final Texture loadPNG(String id, String string){
		return loadTexture(id, string, "PNG");
	}
	
	public static final Texture loadResourcePNG(String string){
		return loadPNG(string, "./resources/textures/" + string + ".png");
	}
	
	private static String last;

	public static void bindTexture(String string){
		if(string.equals(last)) return; last = string;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTexture(string).getTextureID());
	}

	public static void unbind(){
		TextureImpl.bindNone(); last = "";
	}*/
	
	private static final Map<String, Texture> TEXTURES = new HashMap<>();
	private static Texture texture, nulltex;
	
	public static void loadTextures(){
		TEXTURES.clear(); String name; File folder = new File("./resources/textures/");
		for(File file : folder.listFiles()){
			if(file.isDirectory()) continue;
			if((name = file.getName()).endsWith(".png") || name.endsWith(".PNG")){
				try{
					TEXTURES.put(name = name.replace(".png", ""), new Texture(name, new FileInputStream(file)));
					System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, file));
				}
				catch(IOException e){ e.printStackTrace(); }
			} else continue;
		}
		texture = nulltex = TEXTURES.get("null");
	}
	
	public static Texture getTexture(String string, boolean allownull){
		Texture texture = TEXTURES.get(string); return texture == null ? allownull ? null : TEXTURES.get("null") : texture;
	}
	
	public static void loadTexture(String string){
		try{
			File file = new File(String.format("./resources/textures/%s.png", string));
			TEXTURES.put(string, new Texture(string, new FileInputStream(file)));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, file));
		}
		catch(IOException e){ e.printStackTrace(); }
	}
	
	public static void bindTexture(String string){
		if(string.equals(texture.name)) return; 
		(texture = TEXTURES.containsKey(string) ? TEXTURES.get(string) : nulltex).bind();
	}
	
	public static void unbind(){
		TextureImpl.bindNone(); texture = nulltex;//TODO fonttex
	}
	
	public static class Texture {
		
		private ByteBuffer buffer;
		private BufferedImage image;
		private Integer glTextureId;
		private int width, height;
		private boolean rebind = true;
		public final String name;//was required for debug
		
		public Texture(String name, InputStream file) throws IOException {
			this.name = name; image = ImageIO.read(file); width = image.getWidth(); height = image.getHeight(); buffer = newBuffer();
		}
		
		public Texture(String name, int width, int height){
			image = new BufferedImage(this.width = width, this.height = height, BufferedImage.TYPE_INT_ARGB);
			for(int x = 0; x < width; x++) for(int y = 0; y < height; y++) image.setRGB(x, y, Color.WHITE.getRGB());
			buffer = newBuffer(); this.name = name;
		}
		
		public Texture(String name, BufferedImage image){
			this.name = name; this.image = image; this.width = image.getWidth(); this.height = image.getHeight(); this.buffer = newBuffer();
		}
		
		public void resize(int width, int height){
			BufferedImage newimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					if(y >= image.getHeight() || x >= image.getWidth()){
						newimg.setRGB(x, y, 0xffffffff);
					}
					else newimg.setRGB(x, y, image.getRGB(x, y));
				}
			}
			this.image = newimg; this.width = image.getWidth(); this.height = image.getHeight();
			this.buffer = newBuffer(); this.rebind = true;
		}
		
		private ByteBuffer newBuffer(){
			return BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
		}
		
		private ByteBuffer getBuffer(){
			if(!rebind) return buffer; rebind = false;
			if(!buffer.hasRemaining() || buffer.position() > 0) buffer.clear();
			for(int x = 0; x < image.getWidth(); x++){
				for(int y = 0; y < image.getHeight(); y++){
					Color color = new Color(image.getRGB(x, y));
					buffer.put((byte)color.getRed());
					buffer.put((byte)color.getGreen());
					buffer.put((byte)color.getBlue());
					buffer.put((byte)color.getAlpha());
					/*int i = image.getRGB(x, y);
					buffer.put(FMTB.print((byte)(i >>> 16)));
					buffer.put(FMTB.print((byte)(i >>> 8)));
					buffer.put(FMTB.print((byte)i));
					buffer.put(FMTB.print((byte)(i >>> 24)));*/
				}
			} buffer.flip(); return buffer;
		}
		
		public BufferedImage getImage(){ return image; }
		
		public boolean rebind(){ return rebind = true; }
		
		public void bind(){
			if(glTextureId == null || rebind){
				glTextureId = glTextureId == null ? GL11.glGenTextures() : glTextureId;
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, getBuffer());
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
		}
		
		public int getWidth(){ return width; }
		
		public int getHeight(){ return height; }
		
		@Override
		public String toString(){
			return String.format("Texture[ %s (%s, %s) ]", name, width, height);
		}
		
	}
	
}