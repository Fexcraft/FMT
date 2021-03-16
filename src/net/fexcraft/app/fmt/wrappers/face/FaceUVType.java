package net.fexcraft.app.fmt.wrappers.face;

public enum FaceUVType {
	
	AUTOMATIC(0),
	OFFSET_ONLY(2),
	OFFSET_ENDS(4),
	OFFSET_FULL(8),
	ABSOLUTE(2),
	ABSOLUTE_ENDS(4),
	ABSOLUTE_FULL(8);
	
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
	
	public boolean absolute(){
		return this == ABSOLUTE || this == ABSOLUTE_ENDS || this == ABSOLUTE_FULL;
	}

	public boolean automatic(){
		return this == AUTOMATIC;
	}

	public boolean full(){
		return arraylength == 8;
	}

	public boolean ends(){
		return arraylength == 4;
	}

	public boolean basic(){
		return arraylength == 2;
	}

	public static FaceUVType bySize(int length, boolean absolute){
		switch(length){
			case 2: return absolute ? ABSOLUTE : OFFSET_ONLY;
			case 4: return absolute ? ABSOLUTE_ENDS : OFFSET_ENDS;
			case 8: return absolute ? ABSOLUTE_FULL : OFFSET_FULL;
		}
		return AUTOMATIC;
	}

}
