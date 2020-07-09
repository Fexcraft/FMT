package net.fexcraft.app.fmt.wrappers;

public enum FaceUVType {
	
	AUTOMATIC(0),
	OFFSET_ONLY(2),
	OFFSET_ENDS(4),
	OFFSET_FULL(8);
	
	public final int arraylength;
	
	FaceUVType(int arraysize){
		this.arraylength = arraysize;
	}

	public static final FaceUVType validate(FaceUVType type){
		return type == null ? AUTOMATIC : type;
	}

	public static final FaceUVType validate(String newval){
		try{
			return valueOf(newval.toUpperCase());
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return AUTOMATIC;
	}

}
