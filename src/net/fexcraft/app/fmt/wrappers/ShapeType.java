package net.fexcraft.app.fmt.wrappers;

public enum ShapeType {
	
	BOX, SHAPEBOX, TEXRECT_B, TEXRECT_A, FLEXBOX, TRAPEZOID, FLEXTRAPEZOID, CYLINDER, SPHERE, OBJ, MARKER;

	public boolean isCuboid(){
		return this == BOX || this == SHAPEBOX || this == TEXRECT_B || this == TEXRECT_A || this == FLEXBOX || this == TRAPEZOID || this == FLEXTRAPEZOID;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX || this == TEXRECT_B || this == TEXRECT_A;
	}

	public boolean isCylinder(){
		return this == CYLINDER;
	}

	public boolean isTexRect(){
		return this == TEXRECT_B || this == TEXRECT_A;
	}

	public boolean isTexRectB(){
		return this == TEXRECT_B;
	}

	public boolean isTexRectA(){
		return this == TEXRECT_A;
	}
	
	public boolean isMarker(){
		return this == MARKER;
	}

}
