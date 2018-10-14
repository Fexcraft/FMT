package net.fexcraft.lib.fmr;

/**
 * Initially called "PolygonType"
 * @author Ferdinand Calo' (FEX___96)
**/
public enum Shape {
	
	BOX, SHAPEBOX, POLYGON, CYLINDER, SPHERE, WAVEFRONT_OBJ, IMPORTED /* (from TMT usually) */, UNDEFINED;
	
	Shape(){}
	
	public static Shape fromString(String string){
		for(Shape type : values()){
			if(type.name().toLowerCase().equals(string.toLowerCase())){
				return type;
			}
		}
		return null;
	}
	
	@Override
	public String toString(){
		return name();
	}

	public boolean isExternal(){
		return this == WAVEFRONT_OBJ || this == IMPORTED || this == UNDEFINED;
	}
	
	public boolean isInternal(){
		return !isExternal();
	}

	public boolean isCuboid(){
		return this == BOX || this == SHAPEBOX;
	}

	public boolean isRound(){
		return this == CYLINDER || this == SPHERE;
	}

	public boolean isCylindric(){
		return this == CYLINDER;
	}

	public boolean isSphere(){
		return this == SPHERE;
	}

	public boolean isBox(){
		return this == BOX;
	}

	public boolean isShapebox(){
		return this == SHAPEBOX;
	}

}
