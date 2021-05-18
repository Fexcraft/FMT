package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import net.fexcraft.app.json.JsonObject;

public class TextureGroup {
	
	public Texture texture;
	public String name;

	public TextureGroup(String id){
		name = id;
	}
	
	public TextureGroup(String id, File file){
		this(id);
		String texid = "group-" + id;
		loadTexture(texid, file);
		texture = TextureManager.get(texid, false);
	}

	public TextureGroup(String id, String existing){
		this(id);
		texture = TextureManager.get(existing, false);
	}
	
	/** To be used while loading in FMTBs */
	public TextureGroup(JsonObject<?> elm){
		name = elm.string_value();
		texture = null;//assigned/loaded later;
	}

	public void reAssignTexture(){
		texture = TextureManager.get("group-" + name, false);
	}

	public void loadTexture(String texid, File file){
		if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		if(!file.exists()){
			Texture texture = new Texture(texid, 256, 256);
			log("Generated blank texgroup texture.");
			texture.setFile(file);
			TextureManager.putTexture(texid, texture);
			TextureManager.save(texid);
		}
		if(!TextureManager.containsTexture(texid)){
			TextureManager.loadFromFile(texid, file);
		}
	}

}
