package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.fmt.utils.Translator;

public abstract class Polygon {
	
	private Model model;
	private String name;
	public int textureX, textureY;
	public Vector3f pos, off, rot;
	public int colorIdx;
	public int[] colorIds;
	private boolean visible;
	
	public Polygon(Model model){
		this.model = model;
		pos = new Vector3f();
		off = new Vector3f();
		rot = new Vector3f();
	}
	
	protected Polygon(Model model, JsonObject obj){
		this.model = model;
		if(obj.has("name")) name = obj.get("name").getAsString();
		pos = Jsoniser.getVector(obj, "pos_", 0f);
		off = Jsoniser.getVector(obj, "off_", 0f);
		rot = Jsoniser.getVector(obj, "rot_", 0f);
		visible = Jsoniser.get(obj, "visible", true);
		if(obj.has("cuv")){
			//TODO
		}
	}

	public abstract Shape getShape();
	
	public String name(){
		return name == null ? String.format(Translator.UNNAMED_POLYGON, getShape().name()) : name;
	}

	public static Polygon from(Model model, JsonObject obj){
		Shape shape = Shape.get(obj.get("type").getAsString());
		Polygon poly = null;
		switch(shape){
			case BB:
				break;
			case BOX:
				break;
			case CYLINDER:
				break;
			case MARKER:
				break;
			case OBJECT:
				break;
			case SHAPEBOX:
				break;
			case SPHERE:
				break;
			case VOXEL:
				break;
			default:
				break;
		}
		return poly;
	}

}
