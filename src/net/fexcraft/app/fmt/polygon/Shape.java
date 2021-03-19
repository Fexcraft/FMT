package net.fexcraft.app.fmt.polygon;

public enum Shape {
	
	BOX("rect"),
	SHAPEBOX("rect"),
	CYLINDER("cylinder"),
	SPHERE("sphere"),
	OBJECT("object"),
	MARKER("marker"),
	VOXEL("voxel"),
	BB("rect");
	
	private String conversion_group;
	
	Shape(String congroup){
		this.conversion_group = congroup;
	}

	public boolean isRectagular(){
		return this == BOX || this.isShapebox() || this == BB;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX;
	}

	public boolean isCylinder(){
		return this == CYLINDER;
	}
	
	public boolean isMarker(){
		return this == MARKER;
	}
	
	public boolean isVoxel(){
		return this == VOXEL;
	}

	public boolean isBoundingBox(){
		return this == BB;
	}
	
	public boolean isObject(){
		return this == OBJECT;
	}

	public static Shape get(String text){
		text = text.toLowerCase();
		switch(text){
			case "box": return BOX;
			case "shapebox": return SHAPEBOX;
			case "cylinder": return CYLINDER;
			case "sphere": return SPHERE;
			case "object": return OBJECT;
			case "marker": return MARKER;
			case "voxel": return VOXEL;
			case "bb": return BB;
			default: return null;
		}
	}

	public String getConversionGroup(){
		return conversion_group;
	}

	public static Shape[] getSupportedValues(){
		return new Shape[]{ BOX, SHAPEBOX, CYLINDER, MARKER, VOXEL, BB };
	}

	public boolean isTexturable(){
		return this != BB && this != MARKER;
	}

}
