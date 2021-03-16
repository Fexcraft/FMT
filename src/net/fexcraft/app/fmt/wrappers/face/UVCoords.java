package net.fexcraft.app.fmt.wrappers.face;

import java.util.Arrays;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class UVCoords {
	
	private PolygonWrapper poly;
	private FaceUVType type;
	private Face side;
	private boolean cylinder;
	//
	private float[] cuv;
	
	public UVCoords(PolygonWrapper wrapper, Face face, FaceUVType uvtype, boolean cyl){
		poly = wrapper;
		side = face;
		type = FaceUVType.validate(uvtype);
		cylinder = cyl;
	}

	public boolean automatic(){
		return type == FaceUVType.AUTOMATIC;
	}

	public FaceUVType type(){
		return type;
	}

	public int length(){
		return cuv.length;
	}
	
	public float[] value(){
		return cuv;
	}

	public UVCoords set(FaceUVType newtype){
		newtype = FaceUVType.validate(newtype);
		if(cylinder && (newtype.full() || newtype.ends())){
			Logging.log("Invalid FaceUVType '" + newtype + "' for Polygon Type '" + poly.getType().name() + "'!");
			return this;
		}
		type = newtype;
		cuv = new float[type.arraylength];
		return this;
	}

	public Face face(){
		return side;
	}

	public Face side(){
		return side;
	}

	public boolean absolute(){
		return type.absolute();
	}

	public void value(float[] newcuv){
		if(cuv.length != newcuv.length) Logging.log("ERROR: Setting new CUV value but array length differs!", poly.getTurboList().id + ":" + poly.name() + ":" + side.id());
		cuv = newcuv;
	}

	public UVCoords copy(PolygonWrapper wrapper){
		UVCoords coords = new UVCoords(poly, side, type, cylinder);
		coords.cuv = cuv == null ? null : Arrays.copyOf(cuv, cuv.length);
		return coords;
	}

}
