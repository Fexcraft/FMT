package net.fexcraft.app.fmt.polygon;

public enum ModelFormat {
	
	UNIVERSAL("universal", "Universal / FVTM"), MC_JSON("mc_json", "MC Vanilla Json");

	public final String id, name;

	ModelFormat(String id, String name){
		this.id = id;
		this.name = name;
	}

	public static ModelFormat fromName(String name){
		for(ModelFormat format : values()){
			if(format.name.equals(name)) return format;
		}
		return UNIVERSAL;
	}

	public static ModelFormat fromString(String string){
		if(string == null) return UNIVERSAL;
		string = string.toUpperCase();
		for(ModelFormat format : values()){
			if(format.name().equals(string)) return format;
		}
		return UNIVERSAL;
	}

}
