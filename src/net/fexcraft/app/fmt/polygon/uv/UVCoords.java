package net.fexcraft.app.fmt.polygon.uv;

import java.util.Arrays;

import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.utils.Logging;

public class UVCoords {
	
	private Polygon poly;
	private UVType type;
	private Face face;
	//
	private float[] cuv;
	
	public UVCoords(Polygon polygon, Face face, UVType uvtype){
		poly = polygon;
		this.face = face;
		type = UVType.validate(uvtype);
	}

	public boolean automatic(){
		return type == UVType.AUTOMATIC;
	}

	public UVType type(){
		return type;
	}

	public int length(){
		return cuv.length;
	}
	
	public float[] value(){
		return cuv;
	}

	public UVCoords set(UVType newtype){
		newtype = UVType.validate(newtype);
		if(poly.getShape().isCylinder() && !newtype.basic()){
			Logging.log("Invalid UVType '" + newtype + "' for Polygon Shape '" + poly.getShape().name() + "'!");
			return this;
		}
		boolean sale = type.length == newtype.length;
		type = newtype;
		if(!sale) cuv = new float[type.length];
		return this;
	}

	public Face face(){
		return face;
	}

	public Face side(){
		return face;
	}

	public boolean detached(){
		return type.detached();
	}

	public void value(float[] newcuv){
		if(cuv.length != newcuv.length) Logging.log("ERROR: Setting new CUV value but array length differs!", poly.group().id + ":" + poly.name() + ":" + face.id());
		cuv = newcuv;
	}

	public void copy(UVCoords from){
		set(from.type);
		cuv = from.cuv == null ? null : Arrays.copyOf(from.cuv, cuv.length);
	}

}
