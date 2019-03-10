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
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureManager {
	
	private static final Map<String, Texture> TEXTURES = new HashMap<>();
	private static Texture texture, nulltex;
	
	public static void loadTextures(String root){
		TEXTURES.clear(); String name; File folder = new File("./resources/textures/" + (root == null ? "" : root));
		for(File file : folder.listFiles()){
			if(file.isDirectory()) continue;
			if((name = file.getName()).endsWith(".png") || name.endsWith(".PNG")){
				try{
					name = (root == null ? "" : root + "/") + name;
					TEXTURES.put(name = name.replace(".png", ""), new Texture(name, new FileInputStream(file), file));
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
			TEXTURES.put(string, new Texture(string, new FileInputStream(file), file));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, file));
		}
		catch(IOException e){ e.printStackTrace(); }
	}

	public static void loadTextureFromZip(InputStream stream, String string, boolean save){
		try{
			TEXTURES.put(string, new Texture(string, ImageIO.read(stream)));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM IMPORTED MTB/ZIP>"));
			if(save){
				File file = new File(String.format("./resources/textures/%s.png", string)); if(!file.exists()) file.getParentFile().mkdirs();
				ImageIO.write(TEXTURES.get(string).image, "PNG", file); TEXTURES.get(string).file = file;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadTextureFromZip(BufferedImage image, String string, boolean save){
		try{
			TEXTURES.put(string, new Texture(string, image));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM IMPORTED MTB/ZIP>"));
			if(save){
				File file = new File(String.format("./resources/textures/%s.png", string)); if(!file.exists()) file.getParentFile().mkdirs();
				ImageIO.write(TEXTURES.get(string).image, "PNG", file); TEXTURES.get(string).file = file;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void loadTextureFromFile(String id, File file){
		try{
			String name = id == null ? file.getPath() : id;
			TEXTURES.put(name, new Texture(name, new FileInputStream(file), file));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, file));
		}
		catch(IOException e){ e.printStackTrace(); }
	}

	/** Usually expects in form of "temp/NAME" */
	public static void newBlankTexture(String name){
		Texture tex = new Texture(name, FMTB.MODEL.textureX, FMTB.MODEL.textureY, 0x00ffffff); TEXTURES.put(name, tex);
		tex.file = new File("./resources/textures/" + name + ".png"); TextureManager.saveTexture(name);
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, tex.file));
	}
	
	public static void bindTexture(String string){
		if(string.equals(texture.name)) return; 
		(texture = TEXTURES.containsKey(string) ? TEXTURES.get(string) : nulltex).bind();
	}
	
	public static void bindTexture(Texture newtex){
		if(newtex == null || newtex.name.equals(texture.name)) return; (texture = newtex).bind();
	}
	
	public static void unbind(){
		/*TextureImpl.bindNone();*/ texture = nulltex;//TODO fonttex
	}
	
	public static class Texture {
		
		private static ByteBuffer buffer;
		private BufferedImage image;
		private Integer glTextureId;
		private int width, height;
		private boolean rebind = true, reload;
		public final String name;//was required for debug
		private File file;
		
		public Texture(String name, InputStream file, File loc) throws IOException {
			this.name = name; image = ImageIO.read(file); width = image.getWidth(); height = image.getHeight(); this.file = loc;
		}
		
		public Texture(String name, int width, int height){
			this(name, width, height, Color.WHITE.getRGB());
		}
		
		public Texture(String name, int width, int height, int color){
			image = new BufferedImage(this.width = width, this.height = height, BufferedImage.TYPE_INT_ARGB);
			for(int x = 0; x < width; x++) for(int y = 0; y < height; y++) image.setRGB(x, y, color);
			this.name = name;
		}
		
		public Texture(String name, BufferedImage image){
			this.name = name; this.image = image; this.width = image.getWidth(); this.height = image.getHeight();
		}
		
		public void resize(int width, int height, Integer color){
			BufferedImage newimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			for(int x = 0; x < width; x++){
				for(int y = 0; y < height; y++){
					if(y >= image.getHeight() || x >= image.getWidth()){
						newimg.setRGB(x, y, color == null ? 0xffffffff : color);
					}
					else newimg.setRGB(x, y, image.getRGB(x, y));
				}
			}
			this.image = newimg; this.width = image.getWidth(); this.height = image.getHeight(); rebind();
		}
		
		public ByteBuffer getBuffer(){
			if(reload && image != null && file != null){
				try{ image = ImageIO.read(file); } catch(IOException e){ e.printStackTrace(); }
			}
			buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
			for(int y = 0; y < image.getHeight(); y++){
				for(int x = 0; x < image.getWidth(); x++){
					Color color = new Color(image.getRGB(x, y), true);
					buffer.put((byte)color.getRed());
					buffer.put((byte)color.getGreen());
					buffer.put((byte)color.getBlue());
					buffer.put((byte)color.getAlpha());
					/*int i = image.getRGB(x, y); buffer.put(FMTB.print((byte)(i >>> 16))); buffer.put(FMTB.print((byte)(i >>> 8))); buffer.put(FMTB.print((byte)i)); buffer.put(FMTB.print((byte)(i >>> 24)));*/
				}
			} buffer.flip(); rebind = false; return buffer;
		}
		
		public BufferedImage getImage(){ return image; }
		
		public boolean rebind(){ return rebind = true; }
		
		public boolean reload(){ rebind(); return reload = true; }
		
		public void bind(){
			if(glTextureId == null){ glTextureId = GL11.glGenTextures(); rebind(); }
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
			if(rebind){
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, getBuffer());
		        buffer.clear();
			}
		}

		public int getWidth(){ return width; }
		
		public int getHeight(){ return height; }

		public File getFile(){ return file; }
		
		@Override
		public String toString(){
			return String.format("Texture[ %s (%s, %s) ]", name, width, height);
		}
		
	}

	public static Texture removeTexture(String texture){
		return TEXTURES.remove(texture);
	}

	public static void saveTexture(String texture){
		Texture tex = TEXTURES.get(texture);
		if(tex == null){
			Print.console(String.format("Tried to save texture '%s', but it is not loaded as it seems.", texture)); return;
		}
		if(tex.file == null){
			Print.console(String.format("Tried to save texture '%s', but it has no file linked.", texture)); return;
		}
		try{
			if(!tex.getFile().getParentFile().exists()){ tex.getFile().getParentFile().mkdirs(); }
			Print.console("Saving Texture (" + texture + ")!");
			ImageIO.write(tex.image, "PNG", tex.file); TextureUpdate.updateLastEdit(Time.getDate());
		}
		catch(IOException e){ e.printStackTrace(); }
	}

	public static Texture getNullTex(){
		return nulltex;
	}
	
}