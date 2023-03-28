package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.PolyVal.*;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.*;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.gen.Generator;
import org.joml.Vector3f;

public class ScructBox extends Box {

	//

	public ScructBox(Model model){
		super(model);
	}

	public ScructBox(Model model, JsonMap obj){
		super(model, obj);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		//
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.SHAPEBOX;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		Generator<GLObject> gen = super.getGenerator();
		/*ArrayList<Vec3f> list = new ArrayList<>();
		list.add(new Vec3f(cor0.x, cor0.y, cor0.z));
		list.add(new Vec3f(cor1.x, cor1.y, cor1.z));
		list.add(new Vec3f(cor2.x, cor2.y, cor2.z));
		list.add(new Vec3f(cor3.x, cor3.y, cor3.z));
		list.add(new Vec3f(cor4.x, cor4.y, cor4.z));
		list.add(new Vec3f(cor5.x, cor5.y, cor5.z));
		list.add(new Vec3f(cor6.x, cor6.y, cor6.z));
		list.add(new Vec3f(cor7.x, cor7.y, cor7.z));
		gen.set("corners", list);*/
		return gen;
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			//
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			//
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof ScructBox == false) return super.copyInternal(poly);
		ScructBox box = (ScructBox)super.copyInternal(poly);
		//
		return poly;
	}

}
