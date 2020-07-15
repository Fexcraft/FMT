package net.fexcraft.app.fmt.wrappers.face;

public interface Face {
	
	public int index();
	
	public String id();

	public static Face byId(String id, boolean none){
		for(Face box : BoxFace.values()){
			if(box.id().equals(id)) return box;
		}
		for(Face cyl : CylFace.values()){
			if(cyl.id().equals(id)) return cyl;
		}
		if(NullFace.NONE.id().equals(id)) return NullFace.NONE;
		return none ? NullFace.NONE : null;
	}

}
