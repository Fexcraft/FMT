package net.fexcraft.app.fmt.polygon.uv;

public interface Face {
	
	public int index();
	
	public String id();

	public static Face get(String id, boolean none){
		for(Face box : BoxFace.values()){
			if(box.id().equals(id)) return box;
		}
		for(Face cyl : CylFace.values()){
			if(cyl.id().equals(id)) return cyl;
		}
		if(NoFace.NONE.id().equals(id)) return NoFace.NONE;
		return none ? NoFace.NONE : null;
	}

}
