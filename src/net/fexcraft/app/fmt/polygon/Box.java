package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.gen.Generator;

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
	protected Generator<GLObject> getGenerator(){
		Generator<GLObject> gen = new Generator<GLObject>(glm, glm.glObj.grouptex ? group().texSizeX : model().texSizeX, glm.glObj.grouptex ? group().texSizeY : model().texSizeY)
			.set("type", Generator.Type.CUBOID)
			.set("x", off.x)
			.set("y", off.y)
			.set("z", off.z)
			.set("width", size.x)
			.set("height", size.y)
			.set("depth", size.z);
		for(int i = 0; i < sides.length; i++) if(sides[i]) gen.removePolygon(i);
		return gen;
	}

	@Override
	public float[] getFaceColor(int idx){
		switch(idx){
			case 0: return blu0.toFloatArray();
			case 1: return blu1.toFloatArray();
			case 2: return red1.toFloatArray();
			case 3: return red0.toFloatArray();
			case 4: return gre1.toFloatArray();
			case 5: return gre0.toFloatArray();
		}
		return RGB.GREEN.toFloatArray();
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
			default: super.setValue(polyval, value); break;
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
