package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureManager {
	
	private static final Map<String, Texture> textures = new HashMap<String, Texture>();

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
	}
	
}