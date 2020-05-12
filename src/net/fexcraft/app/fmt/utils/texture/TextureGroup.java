package net.fexcraft.app.fmt.utils.texture;

import java.io.File;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.lib.common.utils.Print;

public class TextureGroup {

	public String group;
	public Texture texture;
	public TreeGroup button;
	public boolean minimized;

	public TextureGroup(String id, File file){
		group = id;
		String texid = "group-" + id;
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		if(!file.exists()){
			Texture texture = new Texture(texid, 256, 256, null);
			Print.console("Generated blank texgroup texture.");
			texture.setFile(file);
			TextureManager.putTexture(texid, texture);
			TextureManager.saveTexture(texid);
		}
		if(!TextureManager.containsTexture(texid)){
			TextureManager.loadTextureFromFile(texid, file);
		}
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

}