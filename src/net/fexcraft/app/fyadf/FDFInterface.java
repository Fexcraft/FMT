package net.fexcraft.app.fyadf;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public interface FDFInterface {
	
	public default boolean isArray(){
		return false;
	}
	
	public default boolean isMap(){
		return false;
	}
	
	public default boolean isObject(){
		return false;
	}
	
	public default FDFArray asArray(){
		return (FDFArray)this;
	}
	
	public default FDFMap asMap(){
		return (FDFMap)this;
	}

}
