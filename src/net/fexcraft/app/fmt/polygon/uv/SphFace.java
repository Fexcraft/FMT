package net.fexcraft.app.fmt.polygon.uv;

public enum SphFace implements Face {

	SPH_TOP, SPH_BOT, SPH_OUTER;

	private String id;

	SphFace(){
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
