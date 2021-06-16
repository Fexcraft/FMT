package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.tmt.BoxBuilder;

public class Box extends Polygon {
	
	public Vector3f size = new Vector3f(1);
	public boolean[] sides = new boolean[6];
	
	public Box(Model model){
		super(model);
	}

	protected Box(Model model, JsonMap obj){
		super(model, obj);
		size.x = obj.get("width", 1f);
		size.y = obj.get("height", 1f);
		size.z = obj.get("depth", 1f);
		if(obj.has("sides_off")){
			JsonArray array = obj.getArray("sides_off");
			for(int i = 0; i < sides.length; i++){
				if(i >= array.size()) break;
				sides[i] = array.get(i).value();
			}
		}
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		map.add("width", size.x);
		map.add("height", size.y);
		map.add("depth", size.z);
		boolean anysides = false;
		for(boolean bool : sides) if(bool) anysides = true;
		if(anysides){
			JsonArray array = new JsonArray();
			for(boolean bool : sides) array.add(bool);
			map.add("sides_off", array);
		}
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.BOX;
	}

	@Override
	protected void buildMRT(){
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.x, off.y, off.z).setSize(size.x, size.y, size.z).removePolygons(sides);
		//TODO custom uv
		builder.build();
	}

	@Override
	public float[] getFaceColor(int i){
		return turbo.getColor(i).toFloatArray();
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case SIZE: return getVectorValue(size, polyval.axe());
			case SIDES: return getIndexValue(sides, polyval.axe().ordinal());
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case SIZE: setVectorValue(size, polyval.axe(), value); break;
			case SIDES: setIndexValue(sides, polyval.axe().ordinal(), value); break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Box == false) return poly;
		Box box = (Box)poly;
		box.size.set(size);
		for(int i = 0; i < sides.length; i++) box.sides[i] = sides[i];
		return poly;
	}

}
