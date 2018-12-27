package net.fexcraft.app.fmt.wrappers;

public enum ShapeType {
	
	BOX, SHAPEBOX, TEXRECT, FLEXBOX, TRAPEZOID, FLEXTRAPEZOID, CYLINDER, SPHERE, OBJ;

	public boolean isCuboid(){
		return this == BOX || this == SHAPEBOX || this == TEXRECT || this == FLEXBOX || this == TRAPEZOID || this == FLEXTRAPEZOID;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX || this == TEXRECT;
	}

	public boolean isCylinder(){
		return this == CYLINDER;
	}

	public boolean isCustomTexturedRectangle(){
		return this == TEXRECT;
	}

}
