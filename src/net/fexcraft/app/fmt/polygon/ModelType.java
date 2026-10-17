package net.fexcraft.app.fmt.polygon;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum ModelType {

	FMT,
	FVTM,
	;

	public static ModelType parse(String string){
		if(string == null) return FMT;
		string = string.toUpperCase();
		for(ModelType format : values()){
			if(format.name().equals(string)) return format;
		}
		return FMT;
	}

}
