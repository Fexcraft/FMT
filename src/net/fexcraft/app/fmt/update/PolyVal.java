package net.fexcraft.app.fmt.update;

public enum PolyVal {
	
	/* General */
	POS, ROT, OFF, TEX,
	
	/* Box, Shapebox */
	SIZE, SIDES, CORNER_0, CORNER_1, CORNER_2, CORNER_3, CORNER_4, CORNER_5, CORNER_6, CORNER_7,
	
	/* Cylinder */
	RADIUS, RADIUS2, LENGTH, SEGMENTS, SEG_LIMIT, DIRECTION, BASE_SCALE, TOP_SCALE, TOP_OFF, TOP_ROT, RADIAL, SEG_WIDTH, SEG_HEIGHT, SEG_OFF,
	
	/* Marker */
	COLOR, BIPED, BIPED_ANGLE, BIPED_SCALE, DETACHED, SCALE,
	
	/* Custom UV */
	CUV, CUV_START, CUV_END, CUV_TL, CUV_TR, CUV_BL, CUV_BR,
	
	/* Curve */
	CUR_AMOUNT, CUR_PLANES, CUR_POINTS, CUR_ACTIVE, CUR_ACTIVE_POINT, CUR_ACTIVE_PLANES, PLANE_ROT, PLANE_LOC, PLANE_LOC_LIT, CUR_LENGTH
	
	/* */
	;

    public static class PolygonValue {
		
		private PolyVal val;
		private ValAxe axe;
		
		public PolygonValue(PolyVal val, ValAxe axe){
			this.val = val;
			this.axe = axe;
		}

		public static PolygonValue of(PolyVal val, ValAxe axe){
			return new PolygonValue(val, axe);
		}

		@Override
		public boolean equals(Object other){
			if(other instanceof PolygonValue){
				PolygonValue value = (PolygonValue)other;
				return value.val == val && value.axe == axe;
			}
			return false;
		}
		
		@Override
		public String toString(){
			return (val().name() + "_" + axe().name()).toLowerCase();
		}
		
		public PolyVal val(){
			return val;
		}
		
		public ValAxe axe(){
			return axe;
		}

		public boolean doesUpdateMoreFields(){
			return val.ordinal() >= CUR_AMOUNT.ordinal();
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

	public boolean uv(){
		return this == TEX || (this.ordinal() >= CUV.ordinal() && this.ordinal() <= CUV_BR.ordinal());
	}

}
