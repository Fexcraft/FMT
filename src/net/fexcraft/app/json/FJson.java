package net.fexcraft.app.json;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public interface FJson {
	
	public default boolean isArray(){
		return false;
	}
	
	public default boolean isMap(){
		return false;
	}
	
	public default boolean isObject(){
		return false;
	}
	
	public default JsonArray asArray(){
		return (JsonArray)this;
	}
	
	public default JsonMap asMap(){
		return (JsonMap)this;
	}

}
