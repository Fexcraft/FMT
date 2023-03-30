package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.PolyVal.*;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;

import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.gen.Generator;

public class StructBox extends Box {

	public RGB rgb = new RGB(0x8aeda4);

	public StructBox(Model model){
		super(model);
	}

	public StructBox(Model model, JsonMap obj){
		super(model, obj);
		rgb.packed = obj.getInteger("color", 0x8aeda4);
	}
	
	@Override
	public JsonMap save(boolean export){
		JsonMap map = super.save(export);
		if(!export){
			map.add("color", rgb.packed);
		}
		return map;
	}

	@Override
	public Shape getShape(){
		return Shape.BOUNDING_BOX;
	}

	@Override
	protected Generator<GLObject> getGenerator(){
		Generator<GLObject> gen = super.getGenerator();
		float s = 0.125f, m = 0.125f / 8;
		if(size.x < 1) size.x = 1;
		if(size.y < 1) size.y = 1;
		if(size.z < 1) size.z = 1;
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m)
				.set("y", off.y - m)
				.set("z", off.z - m)
				.set("width", size.x).set("height", s).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m)
				.set("y", off.y - m)
				.set("z", off.z - m)
				.set("width", s).set("height", size.y).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m)
				.set("y", off.y - m)
				.set("z", off.z - m)
				.set("width", s).set("height", s).set("depth", size.z).make();
		//
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m)
				.set("y", off.y + m + size.y - s)
				.set("z", off.z - m)
				.set("width", size.x).set("height", s).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m)
				.set("y", off.y + m + size.y - s)
				.set("z", off.z - m)
				.set("width", s).set("height", s).set("depth", size.z).make();
		//
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x + m + size.x - s)
				.set("y", off.y - m + 0f)
				.set("z", off.z + m + size.z - s)
				.set("width", s).set("height", off.y + size.y).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m + 0f)
				.set("y", off.z - m + 0f)
				.set("z", off.z + m + size.z - s)
				.set("width", s).set("height", off.y + size.y).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x + m + size.x - s)
				.set("y", off.y - m + 0f)
				.set("z", off.z - m + 0f)
				.set("width", s).set("height", off.y + size.y).set("depth", s).make();
		//
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x + m + size.x - s)
				.set("y", off.y - m + 0f)
				.set("z", off.z - m + 0f)
				.set("width", s).set("height", s).set("depth", size.z).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x + m + size.x - s)
				.set("y", off.y + m + size.y - s)
				.set("z", off.z - m + 0f)
				.set("width", s).set("height", s).set("depth", size.z).make();
		//
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m + 0f)
				.set("y", off.y - m + 0f)
				.set("z", off.z + m + size.z - s)
				.set("width", size.x).set("height", s).set("depth", s).make();
		new Generator<GLObject>(glm, 1, 1).set("type", Generator.Type.CUBOID)
				.set("x", off.x - m + 0f)
				.set("y", off.y + m + size.y - s)
				.set("z", off.z + m + size.z - s)
				.set("width", size.x).set("height", s).set("depth", s).make();
		//
		glm.glObj.polycolor = rgb.toFloatArray();
		return new Generator<>(glm, 1, 1);
	}

	@Override
	public RGB getFaceColor(int i){
		return RGB.BLACK;
	}

	@Override
	public Face getFaceByColor(int i){
		return NoFace.NONE;
	}

	@Override
	public void render(){
		PolyRenderer.DrawMode mode = PolyRenderer.mode();
		PolyRenderer.mode(PolyRenderer.DrawMode.RGBCOLOR);
		glm.render();
		PolyRenderer.mode(mode);
		//super.render();
	}
	
	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case COLOR: return rgb.packed;
			default: return super.getValue(polyval);
		}
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case COLOR: rgb.packed = (int)value; break;
			default: super.setValue(polyval, value);
		}
		this.recompile();
	}

	@Override
	protected Polygon copyInternal(Polygon poly){
		if(poly instanceof StructBox == false) return super.copyInternal(poly);
		StructBox box = (StructBox)super.copyInternal(poly);
		box.rgb.packed = rgb.packed;
		return poly;
	}

}
