package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.POLYGON_ADDED;
import static net.fexcraft.app.fmt.utils.Jsoniser.getVector;
import static net.fexcraft.app.fmt.utils.Jsoniser.setVector;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.attributes.PolyVal.ValAxe;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.MRTRenderer.GlCache;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class Polygon {

	public static int polyIdx = 1;//temporary
	public ModelRendererTurbo turbo = new ModelRendererTurbo(this);
	private Model model;
	private Group group;
	private String name;
	public int textureX = -1, textureY = -1;
	public Vector3f pos, off, rot;
	public int colorIdx;
	public int[] colorIds;
	public boolean visible;
	public boolean selected;
	public boolean mirror;
	public boolean flip;
	
	public Polygon(Model model){
		this.model = model == null ? FMT.MODEL : model;
		pos = new Vector3f();
		off = new Vector3f();
		rot = new Vector3f();
		visible = true;
	}
	
	protected Polygon(Model model, JsonMap obj){
		this.model = model == null ? FMT.MODEL : model;
		name = obj.get("name", null);
		pos = getVector(obj, "pos_%s", 0f);
		off = getVector(obj, "off_%s", 0f);
		rot = getVector(obj, "rot_%s", 0f);
		visible = obj.get("visible", true);
		textureX = obj.get("texture_x", -1);
		textureY = obj.get("texture_y", -1);
		mirror = obj.get("mirror", false);
		flip = obj.get("flip", false);
		if(obj.has("cuv")){
			//TODO
		}
	}

	public JsonMap save(boolean export){
		JsonMap obj = new JsonMap();
		obj.add("texture_x", textureX);
		obj.add("texture_y", textureY);
		obj.add("type", getShape().getName());
		if(name != null) obj.add("name", name);
		setVector(obj, "pos_%s", pos);
		setVector(obj, "off_%s", off);
		setVector(obj, "rot_%s", rot);
		if(mirror) obj.add("mirror", true);
		if(flip) obj.add("flip", true);
		//TODO cuv
		if(!export){
			obj.add("visible", visible);
		}
		return obj;
	}

	public abstract Shape getShape();
	
	public String name(){
		return name == null ? String.format(Translator.UNNAMED_POLYGON, getShape().name().toLowerCase()) : name;
	}
	
	public void name(String name){
		this.name = name;
		UpdateHandler.update(UpdateType.POLYGON_RENAMED, this, name);
	}

	public boolean group(Group group){
		/*if(this.group != null){
			update(POLYGON_REMOVED, new Object[]{ this.group, this });
		}*///handled by group.remove() instead!
		this.group = group;
		if(this.group != null){
			update(POLYGON_ADDED, new Object[]{ group, this });
			this.recompile();
		}
		return true;
	}

	public Group group(){
		return group;
	}

	public static Polygon from(Model model, JsonMap obj){
		if(!obj.has("type")) return null;
		Shape shape = Shape.get(obj.get("type").string_value());
		if(shape == null){
			Logging.log("Unknown Shape type '" + obj.get("type").string_value() + "' in model file, skipping.");
			return null;
		}
		switch(shape){
			case BOUNDING_BOX:
				break;
			case BOX: return new Box(model, obj);
			case CYLINDER: return new Cylinder(model, obj);
			case MARKER: return new Marker(model, obj);
			case OBJECT:
				break;
			case SHAPEBOX: return new Shapebox(model, obj);
			case SPHERE:
				break;
			case VOXEL:
				break;
			default: return null;
		}
		return null;
	}
	
	public static Polygon from(Model model, Shape shape){
		switch(shape){
			case BOUNDING_BOX:
				break;
			case BOX: return new Box(model);
			case CYLINDER: return new Cylinder(model);
			case MARKER: return new Marker(model);
			case OBJECT:
				break;
			case SHAPEBOX: return new Shapebox(model);
			case SPHERE:
				break;
			case VOXEL:
				break;
			default: return null;
		}
		return null;
	}

	public Polygon copy(Polygon poly){
		if(poly == null) poly = from(model, this.getShape());
		poly.pos.set(pos);
		poly.off.set(off);
		poly.rot.set(rot);
		poly.visible = visible;
		poly.textureX = textureX;
		poly.textureY = textureY;
		poly.mirror = mirror;
		poly.flip = flip;
		if(name != null) poly.name = String.format(Settings.COPIED_POLYGON.value, name);
		//TODO cuv
		return copyInternal(poly);
	}
	
	protected abstract Polygon copyInternal(Polygon poly);

	public void recompile(){
		turbo.forcedRecompile = true;
		turbo.clear();
		GlCache cache;
		if((cache = turbo.glObject()) == null) cache = turbo.glObject(new GlCache());
		if(cache.polycolor == null) cache.polycolor = new RGB(colorIdx == 0 ? colorIdx = polyIdx++ : colorIdx).toFloatArray();
		cache.polygon = this;
		if(textureX < 0 || textureY < 0) turbo.setTextured(false);
		else turbo.setTextured(true).setTextureOffset(textureX, textureY);
		turbo.textureWidth = group.texgroup == null ? model.texSizeX : group.texSizeX;
		turbo.textureHeight = group.texgroup == null ? model.texSizeY : group.texSizeY;
		if(group.joined_polygons){
			turbo.setPosition(group.pos.x, group.pos.y, group.pos.z);
			turbo.setRotationAngle(group.rot.x + rot.x, group.rot.y + rot.y, group.rot.z + rot.z);
		}
		else {
			turbo.setPosition(pos.x, pos.y, pos.z);
			turbo.setRotationAngle(rot.x, rot.y, rot.z);
		}
		buildMRT();
	}

	protected abstract void buildMRT();

	public abstract float[] getFaceColor(int i);

	public void render(){
		turbo.render();
	}

	public void renderPicking(){
		turbo.render();
	}

	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case POS: return getVectorValue(pos, polyval.axe());
			case OFF: return getVectorValue(off, polyval.axe());
			case ROT: return getVectorValue(rot, polyval.axe());
			case TEX: return polyval.axe().x() ? textureX : textureY;
			default: return 0;
		}
	}

	protected float getVectorValue(Vector3f vec, ValAxe axe){
		switch(axe){
			case X: return vec.x;
			case Y: return vec.y;
			case Z: return vec.z;
			default: return 0;
		}
	}
	
	protected int getIndexValue(boolean[] array, int index){
		if(index < 0 || index >= array.length) return 0;
		return array[index] ? 1 : 0;
	}
	
	protected int getBooleanAsIntValue(boolean bool){
		return bool ? 1 : 0;
	}

	public void setValue(PolygonValue polyval, float value){
		switch(polyval.val()){
			case POS: setVectorValue(pos, polyval.axe(), value); break;
			case OFF: setVectorValue(off, polyval.axe(), value); break;
			case ROT: setVectorValue(rot, polyval.axe(), value); break;
			case TEX:
				if(polyval.axe().x()) textureX = (int)value;
				else textureY = (int)value;
				break;
			default: return;
		}
		this.recompile();
	}

	protected void setVectorValue(Vector3f vec, ValAxe axe, float value){
		switch(axe){
			case X: vec.x = value; return;
			case Y: vec.y = value; return;
			case Z: vec.z = value; return;
			default: return;
		}
	}
	
	protected void setIndexValue(boolean[] array, int index, float value){
		if(index < 0 || index >= array.length) return;
		array[index] = value > .5;
	}
	
	protected boolean parseBooleanValue(float value){
		return value > .5;
	}

	protected Polygon convert(Shape shape){
		switch(shape){
			case BOUNDING_BOX:
				//TODO
				break;
			case BOX: return copy(new Box(model));
			case CYLINDER: return copy(new Cylinder(model));
			case MARKER: return copy(new Marker(model));
			case OBJECT:
				//TODO
				break;
			case SHAPEBOX: return copy(new Shapebox(model));
			case SPHERE:
				//TODO
				break;
			case VOXEL:
				//TODO
				break;
			default: return null;
		}
		return null;
	}

}
