package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.PolyVal.CORNER_0;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_1;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_2;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_3;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_4;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_5;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_6;
import static net.fexcraft.app.fmt.update.PolyVal.CORNER_7;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;

import java.util.ArrayList;

import net.fexcraft.app.fmt.polygon.Vertoff.VOKey;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.gen.Generator;

public class Shapebox extends Box {
	
	public static PolyVal[] CORNERS = { CORNER_0, CORNER_1, CORNER_2, CORNER_3, CORNER_4, CORNER_5, CORNER_6, CORNER_7 };
	public Vector3f cor0;
	public Vector3f cor1;
	public Vector3f cor2;
	public Vector3f cor3;
	public Vector3f cor4;
	public Vector3f cor5;
	public Vector3f cor6;
	public Vector3f cor7;
	
	public Shapebox(Model model){
		super(model);
		cor0 = new Vector3f();
		cor1 = new Vector3f();
		cor2 = new Vector3f();
		cor3 = new Vector3f();
		cor4 = new Vector3f();
		cor5 = new Vector3f();
		cor6 = new Vector3f();
		cor7 = new Vector3f();
	}

	public Shapebox(Model model, JsonMap obj){
		super(model, obj);
		cor0 = getVector(obj, "%s0", 0);
		cor1 = getVector(obj, "%s1", 0);
		cor2 = getVector(obj, "%s2", 0);
		cor3 = getVector(obj, "%s3", 0);
		cor4 = getVector(obj, "%s4", 0);
		cor5 = getVector(obj, "%s5", 0);
		cor6 = getVector(obj, "%s6", 0);
		cor7 = getVector(obj, "%s7", 0);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		setVector(map, "%s0", cor0);
		setVector(map, "%s1", cor1);
		setVector(map, "%s2", cor2);
		setVector(map, "%s3", cor3);
		setVector(map, "%s4", cor4);
		setVector(map, "%s5", cor5);
		setVector(map, "%s6", cor6);
		setVector(map, "%s7", cor7);
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.SHAPEBOX;
	}

	@Override
	protected void generate(){
		Generators.genBox(this);
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case CORNER_0: return getVectorValue(cor0, polyval.axe());
			case CORNER_1: return getVectorValue(cor1, polyval.axe());
			case CORNER_2: return getVectorValue(cor2, polyval.axe());
			case CORNER_3: return getVectorValue(cor3, polyval.axe());
			case CORNER_4: return getVectorValue(cor4, polyval.axe());
			case CORNER_5: return getVectorValue(cor5, polyval.axe());
			case CORNER_6: return getVectorValue(cor6, polyval.axe());
			case CORNER_7: return getVectorValue(cor7, polyval.axe());
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case CORNER_0: setVectorValue(cor0, polyval.axe(), value); break;
			case CORNER_1: setVectorValue(cor1, polyval.axe(), value); break;
			case CORNER_2: setVectorValue(cor2, polyval.axe(), value); break;
			case CORNER_3: setVectorValue(cor3, polyval.axe(), value); break;
			case CORNER_4: setVectorValue(cor4, polyval.axe(), value); break;
			case CORNER_5: setVectorValue(cor5, polyval.axe(), value); break;
			case CORNER_6: setVectorValue(cor6, polyval.axe(), value); break;
			case CORNER_7: setVectorValue(cor7, polyval.axe(), value); break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Shapebox == false) return super.copyInternal(poly);
		Shapebox box = (Shapebox)super.copyInternal(poly);
		box.cor0.set(cor0);
		box.cor1.set(cor1);
		box.cor2.set(cor2);
		box.cor3.set(cor3);
		box.cor4.set(cor4);
		box.cor5.set(cor5);
		box.cor6.set(cor6);
		box.cor7.set(cor7);
		return poly;
	}

	public Vector3f[] corners(){
		return new Vector3f[]{ cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7 };
	}

	public Vector3f getCorner(VOKey key){
		if(key == VO_0) return cor0;
		if(key == VO_1) return cor1;
		if(key == VO_2) return cor2;
		if(key == VO_3) return cor3;
		if(key == VO_4) return cor4;
		if(key == VO_5) return cor5;
		if(key == VO_6) return cor6;
		if(key == VO_7) return cor7;
		return null;
	}

}
