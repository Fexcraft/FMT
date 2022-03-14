package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.demo.ModelSteve;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.gen.Generator;

public class Marker extends Polygon {

	private ModelSteve model = new ModelSteve();
	public static float size = 0.5f, hs = 0.25f;
	public int angle = -90;
	public boolean biped, detached;
	public float biped_scale = 1, scale = 1;
	public RGB rgb = RGB.WHITE.copy();
	
	public Marker(Model model){
		super(model);
	}

	protected Marker(Model model, JsonMap obj){
		super(model, obj);
		rgb.packed = obj.getInteger("color", 0x7f7f7f);
		biped = obj.get("biped", false);
		angle = obj.get("biped_angle", angle);
		biped_scale = obj.get("biped_scale", biped_scale);
		detached = obj.get("detached", false);
		scale = obj.get("scale", scale);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		if(!export){
			map.add("marker", true);
			map.add("color", rgb.packed);
			map.add("biped", biped);
			map.add("biped_angle", angle);
			map.add("biped_scale", biped_scale);
			map.add("detached", detached);
			map.add("scale", scale);
		}
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.MARKER;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		float hs = Marker.hs * scale, size = Marker.size * scale;
		Generator<GLObject> gen = new Generator<GLObject>(glm, glm.glObj.grouptex ? group().texSizeX : model().texSizeX, glm.glObj.grouptex ? group().texSizeY : model().texSizeY)
			.set("type", Generator.Type.CUBOID)
			.set("x", -hs)
			.set("y", -hs)
			.set("z", -hs)
			.set("width", size)
			.set("height", size)
			.set("depth", size);
		glm.glObj.pickercolor = rgb.toFloatArray();
		return gen;
	}

	@Override
	public float[] getFaceColor(int i){
		return RGB.WHITE.toFloatArray();//turbo.getColor(i).toFloatArray();
	}
	
	@Override
	public void render(){
		/*DrawMode mode = MRTRenderer.MODE;
		MRTRenderer.mode(DrawMode.RGBCOLOR);
		//TODO marker.render();
		MRTRenderer.mode(mode);
		if(biped && !MRTRenderer.MODE.lines()){
			String tex = TextureManager.getBound();
			TextureManager.bind("steve");
			model.render();
			TextureManager.bind(tex);
		}*/
		super.render();
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case COLOR: return rgb.packed;
			case BIPED: return biped ? 1 : 0;
			case BIPED_ANGLE: return angle;
			case BIPED_SCALE: return biped_scale;
			case DETACHED: return detached ? 1 : 0;
			case SCALE: return scale;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case COLOR: rgb.packed = (int)value; break;
			case BIPED: biped = value > 0; break;
			case BIPED_ANGLE: angle = (int)value; break;
			case BIPED_SCALE: biped_scale = value; break;
			case DETACHED: detached = value > 0; break;
			case SCALE: scale = value; break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof Marker == false) return poly;
		Marker marker = (Marker)poly;
		marker.rgb.packed = rgb.packed;
		marker.biped = biped;
		marker.angle = angle;
		marker.scale = biped_scale;
		marker.detached = detached;
		marker.scale = scale;
		return poly;
	}

}
