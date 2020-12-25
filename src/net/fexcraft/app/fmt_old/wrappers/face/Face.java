package net.fexcraft.app.fmt_old.wrappers.face;

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
		for(Face s3d : S3DFace.values()){
			if(s3d.id().equals(id)) return s3d;
		}
		if(NullFace.NONE.id().equals(id)) return NullFace.NONE;
		return none ? NullFace.NONE : null;
	}

}
