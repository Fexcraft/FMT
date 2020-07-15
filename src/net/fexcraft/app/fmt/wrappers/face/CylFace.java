package net.fexcraft.app.fmt.wrappers.face;

public enum CylFace implements Face {
	
	BASE, TOP, OUTER, INNER, SEG_SIDE_0, SEG_SIDE_1;
	
	private String id;
	
	CylFace(){
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
