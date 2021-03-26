package net.fexcraft.app.fmt.attributes;

public enum PolyVal {
	
	/* General */
	POS, ROT, OFF, TEX,
	
	/* Box / Shapebox */
	SIZE, SIDES, CORNER,
	
	/* Cylinder */
	RADIUS, RADIUS2, LENGTH,
	
	/* */
	;
	
	public static record PolygonValue(PolyVal val, ValAxe axe){
		
		@Override
		public boolean equals(Object other){
			if(other instanceof PolygonValue value){
				return value.val == val && value.axe == axe;
			}
			return false;
		}
		
		@Override
		public String toString(){
			return (val().name() + "_" + axe().name()).toLowerCase();
		}
		
	}
	
	public static enum ValAxe {
		
		X, Y, Z, N;

		public boolean x(){
			return this == X;
		}
		
		public boolean y(){
			return this == Y;
		}
		
		public boolean z(){
			return this == Z;
		}
		
		public boolean none(){
			return this == N;
		}
		
	}

}
