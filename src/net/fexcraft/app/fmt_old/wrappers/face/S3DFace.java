package net.fexcraft.app.fmt_old.wrappers.face;

public enum S3DFace implements Face {
	
	S3D_BASE, S3D_TOP,
	SIDE0, SIDE1, SIDE2, SIDE3,
	SIDE4, SIDE5, SIDE6, SIDE7,
	SIDE8, SIDE9, SIDE10, SIDE11,
	SIDE12, SIDE13, SIDE14, SIDE15;
	
	private String id;
	
	S3DFace(){
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
	
	public static final int amount(){
		return values().length - 2;
	}

}
