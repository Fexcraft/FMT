package net.fexcraft.app.fmt.wrappers;

public enum ShapeType {
	
	BOX("rect"), SHAPEBOX("rect"), TEXRECT_B("rect"), TEXRECT_A("rect"),
	FLEXBOX("flexrect"), TRAPEZOID("flexrect"), FLEXTRAPEZOID("flexrect"),
	CYLINDER("cylinder"), SPHERE("sphere"), OBJ("obj"), MARKER("marker"),
	QUAD("rect"), SHAPEQUAD("rect"), VOXEL("voxel"), BB("rect");
	
	private String conversion_group;
	
	ShapeType(String congroup){
		this.conversion_group = congroup;
	}

	public boolean isRectagular(){
		return this == BOX || this.isShapebox() || this == FLEXBOX || this == TRAPEZOID || this == FLEXTRAPEZOID || this == QUAD || this == BB;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX || this == TEXRECT_B || this == TEXRECT_A || this == SHAPEQUAD;
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
	
	public boolean isVoxel(){
		return this == VOXEL;
	}
	
	public boolean isFlat(){
		return this == QUAD || this == SHAPEQUAD;
	}

	public boolean isBoundingBox(){
		return this == BB;
	}

	public static ShapeType get(String text){
		text = text.toLowerCase();
		switch(text){
			case "box": return BOX;
			case "shapebox": return SHAPEBOX;
			case "texrect_a": return TEXRECT_A;
			case "texrect_b": return TEXRECT_B;
			case "flexbox": return FLEXBOX;
			case "trapezoid": return TRAPEZOID;
			case "flextrapezoid": return FLEXTRAPEZOID;
			case "cylinder": return CYLINDER;
			case "sphere": return SPHERE;
			case "obj": return OBJ;
			case "marker": return MARKER;
			case "voxel": return VOXEL;
			case "quad": return QUAD;
			case "shapequad": return SHAPEQUAD;
			case "boundingbox": return BB;
			default: return null;
		}
	}

	public String getConversionGroup(){
		return conversion_group;
	}

	public static ShapeType[] getSupportedValues(){
		return new ShapeType[]{ BOX, SHAPEBOX, CYLINDER, MARKER, VOXEL, BB };
	}

	public boolean isTexturable(){
		return this.isShapebox() || this == BOX || this == CYLINDER;
	}

}
