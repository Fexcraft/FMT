package net.fexcraft.app.fmt.polygon;

public enum ModelOrientation {
	
	CLASSIC;

	public static ModelOrientation fromString(String string){
		if(string == null) return CLASSIC;
		string = string.toUpperCase();
		for(ModelOrientation orient : values()){
			if(orient.name().equals(string)) return orient;
		}
		return CLASSIC;
	}

}
