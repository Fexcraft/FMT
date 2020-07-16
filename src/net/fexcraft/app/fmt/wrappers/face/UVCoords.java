package net.fexcraft.app.fmt.wrappers.face;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;

public class UVCoords {
	
	private PolygonWrapper poly;
	private FaceUVType type;
	private Face side;
	//
	private float[] cuv;
	
	public UVCoords(PolygonWrapper wrapper, Face face, FaceUVType uvtype){
		poly = wrapper;
		side = face;
		type = FaceUVType.validate(uvtype);
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
		type = FaceUVType.validate(newtype);
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

}
