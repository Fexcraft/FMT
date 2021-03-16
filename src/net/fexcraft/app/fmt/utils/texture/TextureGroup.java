package net.fexcraft.app.fmt.utils.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;

public class TextureGroup {

	public String group;
	public Texture texture;
	public TreeGroup button;
	public boolean minimized;
	public boolean helper;

	public TextureGroup(String id, File file){
		group = id;
		String texid = "group-" + id;
		loadTexture(texid, file);
		texture = TextureManager.getTexture(texid, false);
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
		texture = TextureManager.getTexture(existing, false);
		button = new TreeGroup(Trees.textures, this);
	}

	public void reAssignTexture(){
		texture = TextureManager.getTexture("group-" + group, false);
	}

	public void loadTexture(String texid, File file){
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		if(!file.exists()){
			Texture texture = new Texture(texid, 256, 256);
			log("Generated blank texgroup texture.");
			texture.setFile(file);
			TextureManager.putTexture(texid, texture);
			TextureManager.saveTexture(texid);
		}
		if(!TextureManager.containsTexture(texid)){
			TextureManager.loadTextureFromFile(texid, file);
		}
	}

}