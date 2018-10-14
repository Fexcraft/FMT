package net.fexcraft.lib.tmt;

import java.io.File;

import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

public abstract class ModelPoolEntry {
	
	public String name;
	public TexturedVertex[] vertices;
	public TexturedPolygon[] faces;
	protected String[] fileExtensions;
	
	public File checkValidPath(String path){
		File file = null;
		for(int index = 0; index < fileExtensions.length && (file == null || !file.exists()); index++){
			String absPath = path;
			if(!path.endsWith("." + fileExtensions[index])){
				absPath+= "." + fileExtensions[index];
			}
			file = new File(absPath);
		}
		if(file == null || !file.exists()){
			return null;
		}
		return file;
	}
	
	public abstract void getModel(File resource);
    
}
