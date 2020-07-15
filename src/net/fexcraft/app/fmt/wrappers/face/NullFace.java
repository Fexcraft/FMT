package net.fexcraft.app.fmt.wrappers.face;

public enum NullFace implements Face {
	
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
