package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureGroup {
	
	public Texture texture;
	public Texture painter;
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
		genPainterTex();
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
		genPainterTex();
	}

	public void genPainterTex(){
		painter = TextureManager.get("pt-" + model.tex_prefix() + "-" + name, true);
		if(painter == null){
			painter = TextureManager.createTexture("pt-" + model.tex_prefix() + "-" + name, texture.getWidth(), texture.getHeight());
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
		return model.tex_prefix() + "-" + name + "/" + width + "x" + height;
	}

	public String typeid(){
		return model.tex_prefix() + "-" + name;
	}

}
