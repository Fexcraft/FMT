package net.fexcraft.app.fmt.polygon.uv;

public enum NoFace implements Face {
	
	NONE;

	@Override
	public int index(){
		return 0;
	}

	@Override
	public String id(){
		return "none";
	}

}
