package net.fexcraft.app.fmt.polygon;

public enum ModelOrientation {

	FVTM4_DEFAULT(-0.125f, "floor_vehicle"),
	FVTM4_BLOCK(-0.125f, "floor_block"),
	FVTM3_DEFAULT(10, "floor_vehicle_old"),
	FVTM3_BLOCK(0.125f, "floor_block_old");
	
	public final float floor_height;
	public final String floor_texture;

	ModelOrientation(float floor, String tex){
		floor_height = floor;
		floor_texture = tex;
	}

	public static ModelOrientation fromString(String string){
		if(string == null) return FVTM3_DEFAULT;//probably old model file
		string = string.toUpperCase();
		for(ModelOrientation orient : values()){
			if(orient.name().equals(string)) return orient;
		}
		return FVTM4_DEFAULT;
	}

	public boolean rect(){
		return ordinal() < 2;
	}

}
