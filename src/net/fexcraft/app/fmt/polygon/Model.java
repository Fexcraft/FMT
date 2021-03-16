package net.fexcraft.app.fmt.polygon;

import java.io.File;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {
	
	public File file;
	public String name;
	public boolean loaded;
	
	public Model(File file, String name){
		this.file = file;
		this.name = name;
	}
	
	/** For now just for FMTB files. */
	public Model load(){
		//TODO
		return null;
	}
	
	public Model loaded(boolean bool){
		this.loaded = bool;
		return this;
	}

}
