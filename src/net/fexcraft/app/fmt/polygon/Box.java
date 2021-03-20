package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.lib.tmt.BoxBuilder;

public class Box extends Polygon {
	
	public Vector3f size = new Vector3f(1);
	public boolean[] sides = new boolean[6];
	
	public Box(Model model){
		super(model);
	}

	protected Box(Model model, JsonObject obj){
		super(model, obj);
		size.x = Jsoniser.get(obj, "width", 1f);
		size.y = Jsoniser.get(obj, "height", 1f);
		size.z = Jsoniser.get(obj, "depth", 1f);
		if(obj.has("sides_off")){
			JsonArray array = obj.get("sides_off").getAsJsonArray();
			for(int i = 0; i < sides.length; i++){
				if(i >= array.size()) break;
				sides[i] = array.get(i).getAsBoolean();
			}
		}
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

}
