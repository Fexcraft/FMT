package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;

import net.fexcraft.lib.common.math.RGB;

public class TextureGroup {
	
	public Texture texture;
	public Texture painter;
	public String name;

	public TextureGroup(String id){
		name = id;
	}
	
	public TextureGroup(String id, File root){
		this(id);
		String texid = "group-" + id;
		loadTexture(texid, root);
	}

	public void reAssignTexture(){
		texture = TextureManager.get("group-" + name, false);
		genPainterTex();
	}

	public void loadTexture(String texid, File root){
		if(!root.exists()) root.mkdirs();
		File file = new File(root, "group-" + name + ".png");
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
		texture = TextureManager.get(texid, false);
		genPainterTex();
	}

	public void genPainterTex(){
		painter = TextureManager.get("pt-group-" + name, true);
		if(painter == null){
			painter = TextureManager.createTexture("pt-group-" + name, texture.getWidth(), texture.getHeight());
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

}
