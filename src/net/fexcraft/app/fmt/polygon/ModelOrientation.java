package net.fexcraft.app.fmt.polygon;

public enum ModelOrientation {
	
	CLASSIC_VEHICLE(10, "floor_vehicle"),
	CLASSIC_BLOCK(0.125f, "floor_block");
	
	public final float floor_height;
	public final String floor_texture;

	ModelOrientation(float floor, String tex){
		floor_height = floor;
		floor_texture = tex;
	}

	public static ModelOrientation fromString(String string){
		if(string == null) return CLASSIC_VEHICLE;
		string = string.toUpperCase();
		for(ModelOrientation orient : values()){
			if(orient.name().equals(string)) return orient;
		}
		return CLASSIC_VEHICLE;
	}

}
