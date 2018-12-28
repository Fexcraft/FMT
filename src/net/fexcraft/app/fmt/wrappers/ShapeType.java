package net.fexcraft.app.fmt.wrappers;

public enum ShapeType {
	
	BOX, SHAPEBOX, TEXRECT_B, TEXRECT_A, FLEXBOX, TRAPEZOID, FLEXTRAPEZOID, CYLINDER, SPHERE, OBJ;

	public boolean isCuboid(){
		return this == BOX || this == SHAPEBOX || this == TEXRECT_B || this == TEXRECT_A || this == FLEXBOX || this == TRAPEZOID || this == FLEXTRAPEZOID;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX || this == TEXRECT_B || this == TEXRECT_A;
	}

	public boolean isCylinder(){
		return this == CYLINDER;
	}

	public boolean isCustomTexturedRectangle(){
		return this == TEXRECT_B || this == TEXRECT_A;
	}

}
