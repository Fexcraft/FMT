package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;
import static net.fexcraft.app.fmt.utils.JsonUtil.getVector;
import static net.fexcraft.app.fmt.utils.JsonUtil.setVector;
import static net.fexcraft.app.fmt.utils.Logging.log;

import net.fexcraft.app.fmt.ui.UVViewer;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonAdded;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonRenamed;
import net.fexcraft.lib.frl.Vertex;
import net.fexcraft.lib.script.ScrBlock;
import net.fexcraft.lib.script.ScrElm;
import net.fexcraft.lib.script.elm.FltElm;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.PolyVal.ValAxe;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.uv.Face;
import net.fexcraft.app.fmt.polygon.uv.NoFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.polygon.uv.UVMap;
import net.fexcraft.app.fmt.polygon.uv.UVType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Polygon implements ScrElm {

	public static final ConcurrentHashMap<Vertex, Integer> vertcolors = new ConcurrentHashMap<>();
	public static final int startIdx = 7;
	public static int polyIdx = startIdx;
	public static int vertIdx = startIdx;
	public Polyhedron<GLObject> glm = new Polyhedron<GLObject>().setGlObj(new GLObject());
	public ArrayList<VertexOffset> vertices = new ArrayList<VertexOffset>();
	private Model model;
	private Group group;
	private String name;
	public int textureX = -1, textureY = -1;
	public Vector3F pos, off, rot;
	public int colorIdx;
	public int[] colorIds;
	public boolean visible;
	public boolean selected;
	public boolean mirror;
	public boolean flip;
	public UVMap cuv;

	public Polygon(Model model){
		this.model = model == null ? FMT.MODEL : model;
		pos = new Vector3F(this, 0, 0, 0);
		off = new Vector3F();
		rot = new Vector3F();
		cuv = new UVMap(this);
		visible = true;
	}

	protected Polygon(Model model, JsonMap obj){
		this.model = model == null ? FMT.MODEL : model;
		name = obj.get("name", null);
		pos = getVector(obj, "pos_%s", 0f);
		pos.polygon = this;
		off = getVector(obj, "off_%s", 0f);
		rot = getVector(obj, "rot_%s", 0f);
		cuv = new UVMap(this);
		visible = obj.get("visible", true);
		textureX = obj.get("texture_x", -1);
		textureY = obj.get("texture_y", -1);
		mirror = obj.get("mirror", false);
		flip = obj.get("flip", false);
		if(obj.has("cuv")){
			obj.getMap("cuv").entries().forEach(entry -> {
				if(!isValidUVFace(entry.getKey())) return;
				JsonArray array = entry.getValue().asArray();
				UVType type = UVType.validate(array.get(0).string_value());
				if(type.automatic()) return;
				UVCoords coord = cuv.get(Face.get(entry.getKey(), true)).set(type);
				for(int i = 0; i < type.length; i++){
					if(i + 1 > array.size()) break;
					coord.value()[i] = array.get(i + 1).float_value();
				}
			});
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
		if(cuv.any()){
			JsonMap cap = new JsonMap();
			cuv.entrySet().forEach(entry -> {
				UVType type = entry.getValue().type();
				if(type.automatic()) return;
				JsonArray array = new JsonArray();
				array.add(type.name().toLowerCase().toString());
				for(int i = 0; i < entry.getValue().length(); i++){
					array.add(entry.getValue().value()[i]);
				}
				cap.add(entry.getKey(), array);
			});
			obj.add("cuv", cap);
		}
		if(!export){
			obj.add("visible", visible);
		}
		return obj;
	}

	public abstract Shape getShape();

	public String name(){
		return name == null ? String.format(Translator.UNNAMED_POLYGON, getShape().name().toLowerCase()) : name;
	}

	public String name(boolean nell){
		return nell ? name : name();
	}

	public void name(String name){
		this.name = name;
		UpdateHandler.update(new PolygonRenamed(this, name));
	}

	public boolean group(Group group){
        /*if(this.group != null){
			update(POLYGON_REMOVED, new Object[]{ this.group, this });
		}*///handled by group.remove() instead!
		this.group = group;
		if(this.group != null){
			update(new PolygonAdded(group, this));
			this.recompile();
		}
		return true;
	}

	public Group group(){
		return group;
	}

	public Model model(){
		return model;
	}

	public static Polygon from(Model model, JsonMap obj, int format){
		if(!obj.has("type")) return null;
		Shape shape = Shape.get(obj.get("type").string_value());
		if(shape == null){
			Logging.log("Unknown Shape type '" + obj.get("type").string_value() + "' in model file, skipping.");
			return null;
		}
		switch(shape){
			case BOUNDING_BOX: return new StructBox(model, obj);
			case BOX: return new Box(model, obj);
			case CYLINDER: return new Cylinder(model, obj, format);
			case MARKER: return new Marker(model, obj);
			case OBJECT:
				break;
			case SHAPEBOX: return new Shapebox(model, obj);
			case SPHERE:
				break;
			case VOXEL:
				break;
			case RECT_CURVE: return new RectCurve(model, obj);
			default: return null;
		}
		return null;
	}

	public static Polygon from(Model model, Shape shape){
		switch(shape){
			case BOUNDING_BOX: return new StructBox(model);
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
			case RECT_CURVE: return new RectCurve(model);
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
		cuv.copyTo(poly);
		return copyInternal(poly);
	}

	protected abstract Polygon copyInternal(Polygon poly);

	public void recompile(){
		glm.recompile = true;
		for(net.fexcraft.lib.frl.Polygon poly : glm.polygons){
			for(Vertex vertex : poly.vertices) vertcolors.remove(vertex);
		}
		glm.clear();
		for(VertexOffset off : vertices) off.vertex = null;
		if(glm.glObj.pickercolor == null) glm.glObj.pickercolor = new RGB(colorIdx == 0 ? colorIdx = polyIdx++ : colorIdx).toFloatArray();
		glm.glObj.polygon = this;
		glm.glObj.textured = textureX > 0 && textureY > 0;
		glm.texU = textureX;
		glm.texV = textureY;
		glm.glObj.grouptex = group.texgroup != null;
		if(group.joined_polygons){
			glm.pos(group.pos.x, group.pos.y, group.pos.z);
			glm.rot(group.rot.x + rot.x, group.rot.y + rot.y, group.rot.z + rot.z);
		}
		else{
			glm.pos(pos.x, pos.y, pos.z);
			glm.rot(rot.x, rot.y, rot.z);
		}
		getGenerator().make();
		int idx = 0;
		for(net.fexcraft.lib.frl.Polygon poly : glm.polygons){
			for(Vertex vertex : poly.vertices){
				if(vertices.contains(vertex)) continue;
				if(vertices.size() <= idx) vertices.add(new VertexOffset(vertex));
				else vertices.get(idx).set(vertex);
				vertcolors.put(vertex, vertIdx++);
				idx++;
			}
		}
		for(VertexOffset off : vertices) off.apply(this) ;
	}

	protected abstract Generator<GLObject> getGenerator();

	protected static RGB red1 = new RGB(138,  65,  92);//new RGB(255, 127, 175);
	protected static RGB gre1 = new RGB( 92, 138,  65);//new RGB(175, 255, 127);
	protected static RGB blu1 = new RGB( 65,  92, 138);//new RGB(127, 175, 255);
	protected static RGB red0 = new RGB(150,   0,   0);
	protected static RGB gre0 = new RGB(  0, 150,   0);
	protected static RGB blu0 = new RGB(  0,   0, 150);
	protected static RGB gray = new RGB( 89,  89,  89);
	protected static int c_red1 = red1.packed, c_red0 = red0.packed;
	protected static int c_blu1 = blu1.packed, c_blu0 = blu0.packed;
	protected static int c_gre1 = gre1.packed, c_gre0 = gre0.packed;

	public abstract RGB getFaceColor(int idx);

	public abstract Face getFaceByColor(int color);

	public void render(FltElm alpha){
		//FMT.SCRIPT.act("render").process(this, alpha);
		glm.render();
	}

	public void renderPicking(){
		glm.render();
	}

	public void renderVertexPicking(){
		glm.render();
	}

	public float getValue(PolygonValue polyval){
		switch(polyval.val()){
			case POS: return getVectorValue(pos, polyval.axe());
			case OFF: return getVectorValue(off, polyval.axe());
			case ROT: return getVectorValue(rot, polyval.axe());
			case TEX: return polyval.axe().x() ? textureX : textureY;
			case CUV:
			case CUV_START:
			case CUV_TR:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length()> 0) return cor.value()[polyval.axe().x() ? 0 : 1];
				else return 0;
			}
			case CUV_END:
			case CUV_TL:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 2) return cor.value()[polyval.axe().x() ? 2 : 3];
				else return 0;
			}
			case CUV_BL:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 4) return cor.value()[polyval.axe().x() ? 4 : 5];
				else return 0;
			}
			case CUV_BR:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 4) return cor.value()[polyval.axe().x() ? 6 : 7];
				else return 0;
			}
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
			case CUV:
			case CUV_START:
			case CUV_TR:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 0){
					cor.value()[polyval.axe().x() ? 0 : 1] = value;
				}
				break;
			}
			case CUV_END:
			case CUV_TL:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 2){
					cor.value()[polyval.axe().x() ? 2 : 3] = value;
				}
				break;
			}
			case CUV_BL:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 4){
					cor.value()[polyval.axe().x() ? 4 : 5] = value;
				}
				break;
			}
			case CUV_BR:{
				UVCoords cor = cuv.get(UVViewer.SELECTED);
				if(cor != null && cor.value() != null && cor.length() > 4){
					cor.value()[polyval.axe().x() ? 6 : 7] = value;
				}
				break;
			}
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

	public Polygon convert(Shape shape){
		switch(shape){
			case BOUNDING_BOX: return copy(new StructBox(model));
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

	public Face[] getUVFaces(){
		return NoFace.values();
	}

	public boolean isValidUVFace(String str){
		for(Face face : getUVFaces()){
			if(face.id().equals(str)) return true;
		}
		return false;
	}

	public boolean isValidUVFace(Face other){
		for(Face face : getUVFaces()){
			if(face == other) return true;
		}
		return false;
	}

	public abstract float[][][] newUV(boolean with_offsets, boolean exclude_detached);

	public boolean isActive(Face face){
		for(Face fuv : getUVFaces()){
			return fuv == face;
		}
		return false;
	}

	public boolean paintTex(Texture tex, Integer face){
		return paintTex(tex, face, true, null, false, null);
	}

	public boolean paintTex(Texture tex, Integer face, boolean primary){
		return paintTex(tex, face, primary, null, false, null);
	}

	public boolean paintTex(Texture tex, Integer face, boolean primary, float[][][] coords, boolean detached, Integer sface){
		if(coords == null) coords = newUV(true, false);
		if(coords == null || coords.length == 0){
			return false;
		}
		if(face == null || face == -1){
			boolean negative = face != null;
			if(sface != null){
				float[][] ends = coords[0];
				if(ends == null || ends.length == 0){
					log("error: requested single-face paint, but provided no coordinates");
					return false;
				}
				byte[] color = (negative ? TexturePainter.getCurrentColor(primary) : getFaceColor(sface).toByteArray());
				paint(tex, ends, color, detached);
			}
			else{
				for(UVCoords coord : cuv.values()){
					if(!isActive(coord.side())) continue;//disabled
					float[][] ends = coords[coord.side().index()];
					if(ends == null || ends.length == 0){
						log("paint data for face " + coord.side().id() + " not found, skipping");
						continue;
					}
					byte[] color = (negative ? TexturePainter.getCurrentColor(primary) : getFaceColor(coord.side().index()).toByteArray());
					paint(tex, ends, color, coord.detached());
				}
			}
		}
		else{
			if(getShape().isTexturable()){
				float[][] ends = coords[face];
				if(ends == null || ends.length == 0) return false;
				paint(tex, ends, TexturePainter.getCurrentColor(primary), cuv.get(getUVFaces()[face]).detached());
			}
			else{
				log("There is no known way of how to handle texture burning of '" + getShape().name() + "'!");
			}
		}
		return true;
	}

	private void paint(Texture tex, float[][] ends, byte[] bs, boolean detached){
		float scale_x = tex.getWidth() / (glm.glObj.grouptex ? group().texSizeX : model().texSizeX);
		float scale_y = tex.getHeight() / (glm.glObj.grouptex ? group().texSizeY : model().texSizeY);
		float tx = detached ? 0 : textureX;
		float ty = detached ? 0 : textureY;
		float[][] ands = { { ends[0][0] * scale_x, ends[0][1] * scale_y }, { ends[1][0] * scale_x, ends[1][1] * scale_y } };
		float texx = tx * scale_x, texy = ty * scale_y;
		for(float x = ands[0][0]; x < ands[1][0]; x += .5f){
			for(float y = ands[0][1]; y < ands[1][1]; y += .5f){
				int xa = (int)(x + texx), ya = (int)(y + texy);
				if(xa >= 0 && xa < tex.getWidth() && ya >= 0 && ya < tex.getHeight()){
					tex.set(xa, ya, bs);
				}
				else continue;
			}
		}
	}

	@Override
	public ScrElm scr_get(ScrBlock block, String target){
		switch(target){
			case "pos": return pos;
		}
		return NULL;
	}

	@Override
	public boolean overrides(){
		return true;
	}

}
