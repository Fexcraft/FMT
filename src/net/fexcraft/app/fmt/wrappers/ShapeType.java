package net.fexcraft.app.fmt.wrappers;

public enum ShapeType {
	
	BOX, SHAPEBOX, CYLINDER, SPHERE, OBJ;

	public boolean isCuboid(){
		return this == BOX || this == SHAPEBOX;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX;
	}

	public boolean isCylinder(){
		return this == CYLINDER;
	}

}
