package net.fexcraft.app.fmt.attributes;

public enum PolyVal {
	
	/* General */
	POS, ROT, OFF, TEX,
	
	/* Box, Shapebox */
	SIZE, SIDES, CORNER_0, CORNER_1, CORNER_2, CORNER_3, CORNER_4, CORNER_5, CORNER_6, CORNER_7,
	
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
		
		X, Y, Z, X2, Y2, Z2, N;

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
		
		public boolean x2(){
			return this == X2;
		}
		
		public boolean y2(){
			return this == Y2;
		}
		
		public boolean z2(){
			return this == Z2;
		}
		
	}

}
