package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_AUTHOR;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_LOAD;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.SaveHandler;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {

	public static int SELECTED_POLYGONS;
	//
	private LinkedHashMap<String, Boolean> authors = new LinkedHashMap<>();
	public ArrayList<Group> groups = new ArrayList<>();
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public boolean visible, subhelper;
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
		SaveHandler.open(this, file);
		update(MODEL_LOAD, this);
		return this;
	}
	
	public void addAuthor(String name, boolean locked){
		authors.put(name, locked);
		update(MODEL_AUTHOR, name);
	}

	public int count(boolean selected){
		return 0;
	}
	
	public boolean isHelper(){
		return subhelper || FMT.MODEL != this;
	}

	public void recompile(){
		// TODO Auto-generated method stub
		
	}

	public void bindtex(){
		// TODO Auto-generated method stub
		
	}

	public void render(){
		// TODO Auto-generated method stub
		
	}

}
