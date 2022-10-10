package net.fexcraft.app.fmt.polygon.uv;

public enum UVType {
	
	AUTOMATIC(0),
	OFFSET(2),
	OFFSET_ENDS(4),
	OFFSET_FULL(8),
	DETACHED(2),
	DETACHED_ENDS(4),
	DETACHED_FULL(8);
	
	public final int length;
	
	UVType(int arrsize){
		length = arrsize;
	}

	public static final UVType validate(UVType type){
		return type == null ? AUTOMATIC : type;
	}

	public static final UVType validate(String str){
		try{
			return from(str);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return AUTOMATIC;
	}
	
	public static UVType from(String string){
		switch(string){
			case "offset":
			case "offset_only":
				return OFFSET;
			case "offset_ends":
				return OFFSET_ENDS;
			case "offset_full":
				return OFFSET_FULL;
			case "detached":
			case "absolute":
				return DETACHED;
			case "detached_ends":
			case "absolute_ends":
				return DETACHED_ENDS;
			case "detached_full":
			case "absolute_full":
				return DETACHED_FULL;
			case "automatic":
			default:
				return AUTOMATIC;
		}
	}

	public boolean detached(){
		return ordinal() > 3;
	}

	public boolean automatic(){
		return this == AUTOMATIC;
	}

	public boolean basic(){
		return length == 2;
	}

	public boolean ends(){
		return length == 4;
	}

	public boolean full(){
		return length == 8;
	}

	public static UVType sizeOf(int length, boolean detached){
		switch(length){
			case 2: return detached ? DETACHED : OFFSET;
			case 4: return detached ? DETACHED_ENDS : OFFSET_ENDS;
			case 8: return detached ? DETACHED_FULL : OFFSET_FULL;
		}
		return AUTOMATIC;
	}

}
