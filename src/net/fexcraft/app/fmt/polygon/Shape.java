package net.fexcraft.app.fmt.polygon;

public enum Shape {
	
	BOX("rect"),
	SHAPEBOX("rect"),
	CYLINDER("cylinder"),
	SPHERE("sphere"),
	OBJECT("object"),
	MARKER("marker", "rect"),
	VOXEL("voxel"),
	BOUNDING_BOX("rect");
	
	private String[] conversion_groups;
	
	Shape(String... congroup){
		this.conversion_groups = congroup;
	}

	public boolean isRectagular(){
		return this == BOX || this.isShapebox() || this == BOUNDING_BOX;
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
		return this == BOUNDING_BOX;
	}
	
	public boolean isObject(){
		return this == OBJECT;
	}

	public static Shape get(String text){
		text = text.toLowerCase();
		switch(text){
			case "box": return BOX;
			case "shapebox": return SHAPEBOX;
			case "cyl": 
			case "cylinder": return CYLINDER;
			case "sphere": return SPHERE;
			case "object": return OBJECT;
			case "marker": return MARKER;
			case "voxel": return VOXEL;
			case "bb":
			case "boundingbox": 
			case "bounding_box": return BOUNDING_BOX;
			default: return null;
		}
	}

	public String[] getConversionGroup(){
		return conversion_groups;
	}
	
	public boolean isInConversionGroup(String group){
		for(String con : conversion_groups) if(con.equals(group)) return true;
		return false;
	}

	public boolean sharesConversionGroup(Shape shape){
		for(String group : shape.conversion_groups) if(isInConversionGroup(group)) return true;
		return false;
	}

	public static Shape[] getSupportedValues(){
		return new Shape[]{ BOX, SHAPEBOX, CYLINDER, MARKER, VOXEL, BOUNDING_BOX };
	}

	public boolean isTexturable(){
		return this != BOUNDING_BOX && this != MARKER;
	}
	
	public String getName(){
		return name().toLowerCase();
	}

}
