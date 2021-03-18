package net.fexcraft.app.fmt.polygon;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.SaveHandler;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {

	private LinkedHashMap<String, Boolean> authors = new LinkedHashMap<>();
	public ArrayList<Group> groups = new ArrayList<>();
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public boolean loaded, visible, subhelper;
	public float opacity = 1f;
	public Vector3f scale;
	public String name;
	public File file;
	
	public Model(File file, String name){
		this.file = file;
		this.name = name;
	}
	
	/** For now just for FMTB files. */
	public Model load(){
		return loaded(SaveHandler.load(this, file, false, false) != null);
	}
	
	public Model loaded(boolean bool){
		this.loaded = bool;
		return this;
	}
	
	public void addAuthor(String name, boolean rem){
		
	}

}
