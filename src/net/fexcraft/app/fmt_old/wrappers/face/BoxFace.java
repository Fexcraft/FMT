package net.fexcraft.app.fmt_old.wrappers.face;

public enum BoxFace implements Face {
	
	FRONT, BACK, TOP, DOWN, RIGHT, LEFT;
	
	private String id;
	
	BoxFace(){
		id = name().toLowerCase();
	}

	@Override
	public int index(){
		return ordinal();
	}

	@Override
	public String id(){
		return id;
	}

}
