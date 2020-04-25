package net.fexcraft.app.fmt.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.editor.GroupEditor;
import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureManager {

	private static final Map<String, Texture> TEXTURES = new HashMap<>();
	private static final ArrayList<TextureGroup> GROUPS = new ArrayList<>();
	private static Texture texture, nulltex;

	public static void init(){
		TEXTURES.clear();
		GROUPS.clear();
		String name;
		File folder = new File("./resources/textures/");
		for(File file : folder.listFiles()){
			if(file.isDirectory()) continue;
			if((name = file.getName()).toLowerCase().endsWith(".png")){
				try{
					TEXTURES.put(name = name.replace(".png", ""), new Texture(name, new FileInputStream(file), file));
					System.out.println(String.format("Loaded Root Texture (%-6s) [%s]", name, file));
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			else continue;
		}
		texture = nulltex = TEXTURES.get("null");
	}

	public static Texture getTexture(String string, boolean allownull){
		Texture texture = TEXTURES.get(string);
		return texture == null ? allownull ? null : TEXTURES.get("null") : texture;
	}

	public static void loadTexture(String string, Boolean rooted){
		try{
			if(rooted == null) rooted = true;
			File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : string + ".png");
			TEXTURES.put(string, new Texture(string, new FileInputStream(file), file));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, file));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static void loadTextureFromZip(InputStream stream, String string, Boolean rooted, boolean save, boolean temp){
		try{
			if(rooted == null) rooted = true;
			TEXTURES.put(string, new Texture(string, ImageIO.read(stream)));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM IMPORTED MTB/ZIP>"));
			if(save){
				File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : (temp ? "./temp/" : "") + string + ".png");
				if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
				ImageIO.write(TEXTURES.get(string).image, "PNG", file);
				TEXTURES.get(string).file = file;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadTextureFromImgBuffer(BufferedImage image, String string, Boolean rooted, boolean save){
		try{
			if(rooted == null) rooted = true;
			TEXTURES.put(string, new Texture(string, image));
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<IMG-BUFFER>"));
			if(save){
				File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : string + ".png");
				if(!file.exists()) file.getParentFile().mkdirs();
				ImageIO.write(TEXTURES.get(string).image, "PNG", file);
				TEXTURES.get(string).file = file;
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
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static Texture createTexture(String name, BufferedImage img){
		TEXTURES.put(name, new Texture(name, img));
		return TEXTURES.get(name);
	}

	/** Usually expects in form of "temp/NAME" */
	public static void newBlankTexture(String name, TurboList list){
		Texture tex = new Texture(name, FMTB.MODEL.tx(list), FMTB.MODEL.ty(list), 0x00ffffff);
		TEXTURES.put(name, tex);
		tex.file = new File("./" + name + (list == null ? "" : "_" + list.id) + ".png");
		TextureManager.saveTexture(name);
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, tex.file));
	}

	public static void bindTexture(String string){
		if(string.equals(texture.name)) return;
		(texture = TEXTURES.containsKey(string) ? TEXTURES.get(string) : nulltex).bind();
	}

	public static void bindTexture(Texture newtex){
		if(newtex == null || newtex.name.equals(texture.name)) return;
		(texture = newtex).bind();
	}

	public static void bindTexture(TextureGroup group){
		if(group == null){
			bindTexture("blank");
			return;
		}
		if(group.texture.name.equals(texture.name)) return;
		(texture = group.texture).bind();
	}

	public static void unbind(){
		/* TextureImpl.bindNone(); */ texture = nulltex;// TODO fonttex
	}

	public static class Texture {

		private static ByteBuffer buffer;
		private BufferedImage image;
		private Integer glTextureId;
		private int width, height;
		private boolean rebind = true, reload;
		public final String name;// was required for debug
		private File file;
		public long lastedit;

		public Texture(String name, InputStream file, File loc) throws IOException{
			this.name = name;
			image = ImageIO.read(file);
			width = image.getWidth();
			height = image.getHeight();
			this.file = loc;
		}

		public Texture(String name, int width, int height){
			this(name, width, height, Color.WHITE.getRGB());
		}

		public Texture(String name, int width, int height, int color){
			image = new BufferedImage(this.width = width, this.height = height, BufferedImage.TYPE_INT_ARGB);
			for(int x = 0; x < width; x++)
				for(int y = 0; y < height; y++)
					image.setRGB(x, y, color);
			this.name = name;
		}

		public Texture(String name, BufferedImage image){
			this.name = name;
			this.image = image;
			this.width = image.getWidth();
			this.height = image.getHeight();
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
			this.image = newimg;
			this.width = image.getWidth();
			this.height = image.getHeight();
			rebind();
		}

		public ByteBuffer getBuffer(){
			if(reload && image != null && file != null){
				try{
					image = ImageIO.read(file);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
			for(int y = 0; y < image.getHeight(); y++){
				for(int x = 0; x < image.getWidth(); x++){
					Color color = new Color(image.getRGB(x, y), true);
					buffer.put((byte)color.getRed());
					buffer.put((byte)color.getGreen());
					buffer.put((byte)color.getBlue());
					buffer.put((byte)color.getAlpha());
					/* int i = image.getRGB(x, y); buffer.put(FMTB.print((byte)(i >>> 16))); buffer.put(FMTB.print((byte)(i >>> 8))); buffer.put(FMTB.print((byte)i)); buffer.put(FMTB.print((byte)(i >>> 24))); */
				}
			}
			buffer.flip();
			rebind = false;
			return buffer;
		}

		public BufferedImage getImage(){
			return image;
		}

		public boolean rebind(){
			return rebind = true;
		}

		public boolean rebindQ(){
			return rebind;
		}

		public boolean reload(){
			rebind();
			return reload = true;
		}

		public void bind(){
			if(glTextureId == null){
				glTextureId = GL11.glGenTextures();
				rebind();
			}
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
			if(rebind){
				GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
				GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
				GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, getBuffer());
				buffer.clear();
			}
		}

		public int getWidth(){
			return width;
		}

		public int getHeight(){
			return height;
		}

		public File getFile(){
			return file;
		}

		@Override
		public String toString(){
			return String.format("Texture[ %s (%s, %s) ]", name, width, height);
		}

		public void setImage(BufferedImage img){
			this.image = img;
		}

		public Integer getGLID(){
			return glTextureId;
		}

		public void clearPixels(){
			for(int x = 0; x < width; x++) for(int y = 0; y < height; y++) image.setRGB(x, y, 0xffffff);
		}

		public void save(){
			TextureManager.saveTexture(this);
		}

	}

	public static Texture removeTexture(String texture){
		return TEXTURES.remove(texture);
	}

	public static void saveTexture(String texture){
		saveTexture(TEXTURES.get(texture));
	}

	public static void saveTexture(Texture tex){
		if(tex == null){
			Print.console(String.format("Tried to save texture '%s', but it is not loaded as it seems.", texture));
			return;
		}
		if(tex.file == null){
			Print.console(String.format("Tried to save texture '%s', but it has no file linked.", texture));
			return;
		}
		try{
			if(!tex.getFile().getParentFile().exists()){
				tex.getFile().getParentFile().mkdirs();
			}
			Print.console("Saving Texture (" + tex + ")!");
			ImageIO.write(tex.image, "PNG", tex.file);
			TextureUpdate.updateLastEdit(tex);
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static Texture getNullTex(){
		return nulltex;
	}

	public static class TextureGroup {

		public String group;
		public Texture texture;
		public TreeGroup button;
		public boolean minimized;

		public TextureGroup(String id, File file){
			group = id;
			String texid = "group-" + id;
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			if(!file.exists()){
				Texture texture = new Texture(texid, 256, 256, 0xffffff);
				texture.file = file;
				TEXTURES.put(texid, texture);
				saveTexture(texid);
			}
			if(!TEXTURES.containsKey(texid)){
				loadTextureFromFile(texid, file);
			}
			texture = getTexture(texid, false);
			button = new TreeGroup(Trees.textures, this);
		}

		public TextureGroup(JsonObject obj){
			this(obj.get("name").getAsString(), new File(obj.get("path").getAsString()));
		}

		/** To be used while loading in FMTBs */
		public TextureGroup(JsonElement elm){
			group = elm.getAsString();
			texture = null;//assigned/loaded later;
			button = new TreeGroup(Trees.textures, this);
		}

		public TextureGroup(String id, String existing){
			group = id;
			texture = getTexture(existing, false);
			button = new TreeGroup(Trees.textures, this);
		}

		public void reAssignTexture(){
			texture = getTexture("group-" + group, false);
		}

	}

	public static TextureGroup getGroup(String id){
		for(TextureGroup group : GROUPS) if(group.group.equals(id)) return group;
		return null;
	}

	public static boolean hasGroup(String id){
		for(TextureGroup group : GROUPS) if(group.group.equals(id)) return true;
		return false;
	}

	public static int getGroupAmount(){
		return GROUPS.size();
	}

	public static boolean anyGroupsLoaded(){
		return GROUPS.isEmpty();
	}

	public static ArrayList<TextureGroup> getGroupsFE(){
		return GROUPS;
	}

	public static void addGroup(TextureGroup group){
		Trees.textures.addSub(group.button);
		GROUPS.add(group);
		GroupEditor.updateTextureGroups();
	}

	public static void clearGroups(){
		for(TextureGroup group : GROUPS){
			group.button.removeFromTree();
		}
		GROUPS.clear();
	}

	public static void addNewGroup(){
		String name = "newgroup";
		while(hasGroup(name)) name += "0";
		addGroup(new TextureGroup(name, new File("./temp/group-" + name + ".png")));
		DialogBox.showOK(null, null, null, "tree.textures.group_added", "#" + name);
		Trees.textures.reOrderGroups();
		GroupEditor.updateTextureGroups();
	}

	public static void removeGroup(TextureGroup texgroup){
		if(texgroup == null) return;
		if(texgroup == FMTB.MODEL.texgroup){
			DialogBox.showOK(null, null, null, "tree.textures.remove_default");
		}
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(list.texgroup == texgroup){
				DialogBox.showOK(null, null, null, "tree.textures.remove_in_use", "#" + list.id);
				return;
			}
		}
		texgroup.button.removeFromTree();
		GROUPS.remove(texgroup);
		GroupEditor.updateTextureGroups();
	}

}