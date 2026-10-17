package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureGroup {
	
	public Texture texture;
	public int width = 256;
	public int height = 256;
	public Model model;
	public String name;

	public TextureGroup(Model model, String id){
		this.model = model;
		this.name = id;
	}

	public TextureGroup(Model model, String id, JsonMap map){
		this(model, id);
		width = map.getInteger("width", width);
		height = map.getInteger("height", height);
	}
	
	public TextureGroup(Model model, String id, File root){
		this(model, id);
		loadTexture(model.tex_prefix() + "-" + id, root);
	}

	public void reAssignTexture(){
		texture = TextureManager.get(model.tex_prefix() + "-" + name, false);
	}

	public void loadTexture(String texid, File root){
		if(!root.exists()) root.mkdirs();
		File file = new File(root, model.tex_prefix() + "-" + name + ".png");
		if(!file.exists()){
			Texture texture = new Texture(texid, width, height);
			log("Generated blank texgroup texture.");
			texture.setFile(file);
			TextureManager.putTexture(texid, texture);
			TextureManager.save(texid);
		}
		if(!TextureManager.containsTexture(texid)){
			TextureManager.loadFromFile(texid, file);
		}
		texture = TextureManager.get(texid, false);
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("width", width);
		map.add("height", height);
		return map;
	}

	@Override
	public String toString(){
		return model.tex_prefix() + "-" + name + "/" + width + "x" + height;
	}

	public String typeid(){
		return model.tex_prefix() + "-" + name;
	}

}
