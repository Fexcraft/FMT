package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.POLYGON_ADDED;

import org.joml.Vector3f;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.fmt.utils.MRTRenderer;
import net.fexcraft.app.fmt.utils.MRTRenderer.GlCache;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class Polygon {

	public ModelRendererTurbo turbo = new ModelRendererTurbo(this);
	private Model model;
	private Group group;
	private String name;
	public int textureX, textureY;
	public Vector3f pos, off, rot;
	public int colorIdx;
	public int[] colorIds;
	public boolean visible;
	public boolean selected;
	
	public Polygon(Model model){
		this.model = model == null ? FMT.MODEL : model;
		pos = new Vector3f();
		off = new Vector3f();
		rot = new Vector3f();
	}
	
	protected Polygon(Model model, JsonObject obj){
		this.model = model == null ? FMT.MODEL : model;
		if(obj.has("name")) name = obj.get("name").getAsString();
		pos = Jsoniser.getVector(obj, "pos_%s", 0f);
		off = Jsoniser.getVector(obj, "off_%s", 0f);
		rot = Jsoniser.getVector(obj, "rot_%s", 0f);
		visible = Jsoniser.get(obj, "visible", true);
		textureX = Jsoniser.get(obj, "texture_x", -1);
		textureY = Jsoniser.get(obj, "texture_y", -1);
		if(obj.has("cuv")){
			//TODO
		}
	}

	public abstract Shape getShape();
	
	public String name(){
		return name == null ? String.format(Translator.UNNAMED_POLYGON, getShape().name().toLowerCase()) : name;
	}
	
	public void name(String name){
		this.name = name;
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

	public static Polygon from(Model model, JsonObject obj){
		Shape shape = Shape.get(obj.get("type").getAsString());
		switch(shape){
			case BB:
				break;
			case BOX: return new Box(model, obj);
			case CYLINDER:
				break;
			case MARKER:
				break;
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
	
	public void recompile(){
		turbo.forcedRecompile = true;
		turbo.clear();
		GlCache cache;
		if((cache = turbo.glObject()) == null) cache = turbo.glObject(new GlCache());
		cache.polycolor = MRTRenderer.EMPTY;//TODO
		cache.polygon = this;
		if(textureX < 0 || textureY < 0) turbo.setTextured(false);
		else turbo.setTextureOffset(textureX, textureY);
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

}
