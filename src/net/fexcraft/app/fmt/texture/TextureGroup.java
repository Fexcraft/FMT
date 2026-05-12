package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

public class TextureGroup {
	
	public Texture texture;
	public Texture painter;
	public boolean helper;
	public int width = 256;
	public int height = 256;
	public String type;
	public String name;

	public TextureGroup(String type, String id){
		this.type = type;
		this.name = id;
	}

	public TextureGroup(String type, String id, JsonMap map){
		this(type, id);
		width = map.getInteger("width", width);
		height = map.getInteger("height", height);
	}
	
	public TextureGroup(String type, String id, File root){
		this(type, id);
		loadTexture(type + "-" + id, root);
	}

	public void reAssignTexture(){
		texture = TextureManager.get(type + "-" + name, false);
		genPainterTex();
	}

	public void loadTexture(String texid, File root){
		if(!root.exists()) root.mkdirs();
		File file = new File(root, type + "-" + name + ".png");
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
		genPainterTex();
	}

	public void genPainterTex(){
		painter = TextureManager.get("pt-" + type + "-" + name, true);
		if(painter == null){
			painter = TextureManager.createTexture("pt-" + type + "-" + name, texture.getWidth(), texture.getHeight());
			painter.setFile(new File("./temp/" + painter.name + ".png"));
		}
		else{
			painter.resize(texture.getWidth(), texture.getHeight());
		}
		int lastint = 0;
		for(int x = 0; x < painter.getWidth(); x++){
			for(int y = 0; y < painter.getHeight(); y++){
				painter.set(x, y, new RGB(lastint).toByteArray());
				lastint++;
			}
		}
		painter.save();
		painter.rebind();
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("width", width);
		map.add("height", height);
		return map;
	}

	@Override
	public String toString(){
		return type + "-" + name + "/" + width + "x" + height;
	}

	public String typeid(){
		return type + "-" + name;
	}

}
