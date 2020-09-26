package net.fexcraft.app.fmt.wrappers;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.TexViewBox;
import net.fexcraft.app.fmt.ui.TexViewBox.PolyFace;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.editor.UVEditor;
import net.fexcraft.app.fmt.ui.tree.SubTreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.texture.Texture;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.app.fmt.wrappers.face.UVMap;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public abstract class PolygonWrapper {
	
	protected static final ModelRendererTurbo rotmarker = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.25f, -.25f, -.25f, .5f, .5f, .5f).setTextured(false).setColor(Settings.getSelectedColor());
	protected static final ModelRendererTurbo rotmarker2 = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.2f, -.2f, -.2f, .4f, .4f, .4f).setTextured(false).setColor(Settings.getSelectedColor2());
	private static final ModelRendererTurbo something = new ModelRendererTurbo(null, 0, 0, 16, 16).setTextured(false);
	//
	public Vec3f pos = new Vec3f(), off = new Vec3f(), rot = new Vec3f();
	//public float[][][] texpos = new float[0][][];
	public int textureX = -1, textureY = -1;
	protected ModelRendererTurbo turbo, lines, sellines, picker;
	protected final GroupCompound compound;
	protected static boolean widelines;
	public boolean visible = true;
	private TurboList turbolist;
	public boolean mirror, flip;
	public boolean selected;
	public byte[][] color;
	public String name;
	public UVMap cuv;
	//
	public SubTreeGroup button;
	public PolyFace texviewcomponent;
	
	public PolygonWrapper(GroupCompound compound){
		this.compound = compound; button = new SubTreeGroup(Trees.polygon, this);
		cuv = new UVMap(this);
	}
	
	public void recompile(){
		this.clearMRT(turbo, lines, sellines, picker); this.setupMRT();
	}

	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render();
	}
	
	public void renderLines(boolean rotXb, boolean rotYb, boolean rotZb){
		//if(Settings.lines()) (selected ? sellines : lines).render();
		//GL11.glDisable(GL11.GL_TEXTURE_2D);
		if((selected || turbolist.selected) && Settings.polygonMarker()){
			rotmarker.setRotationPoint(lines.rotationPointX, lines.rotationPointY, lines.rotationPointZ);
			rotmarker.render();
		}
		if(Settings.lines()){
			if(selected || turbolist.selected){
				if(!widelines){ GL11.glLineWidth(4f); widelines = true; }
				if(sellines != null) sellines.render();
			}
			else{
				if(widelines){ GL11.glLineWidth(1f); widelines = false; }
				if(lines != null) lines.render();
			}
		}
		//GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public void renderPicking(boolean rotXb, boolean rotYb, boolean rotZb){
		if(visible && picker != null){
			picker.render();
		}
	}
	
	public abstract ShapeType getType();

	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "tex": return x ? textureX : y ? textureY : 0;
			case "pos": return x ? pos.xCoord : y ? pos.yCoord : z ? pos.zCoord : 0;
			case "off": return x ? off.xCoord : y ? off.yCoord : z ? off.zCoord : 0;
			case "rot": return x ? rot.xCoord : y ? rot.yCoord : z ? rot.zCoord : 0;
		}
		if(id.startsWith("o")){
			if(id.startsWith("oo_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null) return arr[x ? 0 : 1];
			}
			else if(id.startsWith("oe_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null){
					boolean end = id.endsWith("e");
					return arr[end ? x ? 2 : 3 : x ? 0 : 1];
				}
			}
			else if(id.startsWith("of_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null){
					int index = Integer.parseInt(id.substring(id.length() - 1));
					return arr[index * 2 + (x ? 0 : 1)];
				}
			}
		}
		return 0;
	}

	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		switch(id){
			case "tex":{
				if(x){ textureX = (int)value; return true; }
				if(y){ textureY = (int)value; return true; }
				if(z) return false;
			}
			case "pos":{
				if(x){ pos.xCoord = value; return true; }
				if(y){ pos.yCoord = value; return true; }
				if(z){ pos.zCoord = value; return true; }
			}
			case "off":{
				if(x){ off.xCoord = value; return true; }
				if(y){ off.yCoord = value; return true; }
				if(z){ off.zCoord = value; return true; }
			}
			case "rot":{
				if(x){ rot.xCoord = value; return true; }
				if(y){ rot.yCoord = value; return true; }
				if(z){ rot.zCoord = value; return true; }
			}
			default: break;
		}
		if(id.startsWith("o")){
			if(id.startsWith("oo_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null){
					arr[x ? 0 : 1] = value;
					return true;
				}
			}
			else if(id.startsWith("oe_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null){
					boolean end = id.endsWith("e");
					arr[end ? x ? 2 : 3 : x ? 0 : 1] = value;
					return true;
				}
			}
			else if(id.startsWith("of_")){
				float[] arr = getUVCoords(UVEditor.getSelection()).value();
				if(arr != null){
					int index = Integer.parseInt(id.substring(id.length() - 1));
					arr[index * 2 + (x ? 0 : 1)] = value;
					return true;
				}
			}
		}
		return false;
	}

	public String name(){
		return name == null ? "unnamed " + this.getType().name().toLowerCase() : name;
	}

	public JsonObject toJson(boolean export){
		JsonObject obj = new JsonObject();
		obj.addProperty("texture_x", textureX);
		obj.addProperty("texture_y", textureY);
		obj.addProperty("type", this.getType().name().toLowerCase());
		if(name != null) obj.addProperty("name", name);
		if(pos.xCoord != 0f) obj.addProperty("pos_x", pos.xCoord);
		if(pos.yCoord != 0f) obj.addProperty("pos_y", pos.yCoord);
		if(pos.zCoord != 0f) obj.addProperty("pos_z", pos.zCoord);
		//
		if(off.xCoord != 0f) obj.addProperty("off_x", off.xCoord);
		if(off.yCoord != 0f) obj.addProperty("off_y", off.yCoord);
		if(off.zCoord != 0f) obj.addProperty("off_z", off.zCoord);
		//
		if(rot.xCoord != 0f) obj.addProperty("rot_x", rot.xCoord);
		if(rot.yCoord != 0f) obj.addProperty("rot_y", rot.yCoord);
		if(rot.zCoord != 0f) obj.addProperty("rot_z", rot.zCoord);
		if(mirror != false) obj.addProperty("mirror", true);
		if(flip != false) obj.addProperty("flip", true);
		if(cuv.anyCustom()){
			JsonObject jsn = new JsonObject();
			for(Entry<String, UVCoords> entry : cuv.entrySet()){
				FaceUVType type = entry.getValue().type();
				if(type != FaceUVType.AUTOMATIC){
					JsonArray array = new JsonArray();
					array.add(type.name().toLowerCase().toString());
					for(int i = 0; i < entry.getValue().length(); i++){
						array.add(entry.getValue().value()[i]);
					}
					jsn.add(entry.getKey(), array);
				}
			}
			obj.add("cuv", jsn);
		}
		//temporary data
		if(!export){
			obj.addProperty("visible", visible);
		}
		return populateJson(obj, export);
	}

	public void parseCustomUV(JsonObject obj){
		for(Entry<String, JsonElement> entry : obj.entrySet()){
			if(!isValidFace(entry.getKey())) continue;
			JsonArray array = entry.getValue().getAsJsonArray();
			FaceUVType type = FaceUVType.validate(array.get(0).getAsString());
			if(type == FaceUVType.AUTOMATIC) continue;
			UVCoords coord = cuv.get(Face.byId(entry.getKey(), true)).set(type);
			for(int i = 0; i < type.arraylength; i++){
				if(i + 1 >= array.size()) break;
				coord.value()[i] = array.get(i + 1).getAsFloat();
			}
		}
	}

	protected abstract JsonObject populateJson(JsonObject obj, boolean export);
	
	protected void clearMRT(ModelRendererTurbo... mrts){
		if(mrts == null) mrts = new ModelRendererTurbo[]{ turbo, lines, sellines, picker };
		for(ModelRendererTurbo mrt : mrts){
			if(mrt != null && mrt.displaylist() != null){
				GL11.glDeleteLists(mrt.displaylist(), 1); mrt = null;
			}
		}
	}
	
	protected void setupMRT(){
		boolean textured = compound.helpertex != null || compound.texgroup != null;
		if(!textured) textured = getTurboList() != null && (getTurboList().getTextureGroup() != null || getTurboList().helpertex != null);
		turbo = newMRT().setTextured(textured);
		lines = newMRT().setLines(getType().isBoundingBox() ? Settings.getBoundingBoxColor() : RGB.BLACK);
		sellines = newMRT().setLines(getType().isBoundingBox() ? Settings.getBoundingBoxColor() : Settings.getSelectedColor());
		//
		picker = new ModelRendererTurbo(null, 0, 0, 16, 16){
			@Override
			public RGB getColor(int i){
				return new RGB(genColor(this, i));
			}
		};
		picker.copyTo(sellines.getFaces());
		picker.setRotationPoint(sellines.rotationPointX, sellines.rotationPointY, sellines.rotationPointZ);
		picker.setRotationAngle(sellines.rotationAngleX, sellines.rotationAngleY, sellines.rotationAngleZ);
		picker.setTextured(false).setLines(false);
	}
	
	//private static int maxcol = 16777215;
	private static int lastint = 0;
	
	private RGB genColor(ModelRendererTurbo turbo, int face){
		if(color == null || face >= color.length){
			color = new byte[turbo.getFaces().size()][];
			for(int i = 0; i < color.length; i++){
				color[i] = new RGB(lastint += 1).toByteArray();
				if(color[i][0] == 31 && color[i][1] == 65 && color[i][2] == 127){
					color[i] = new RGB(lastint += 1).toByteArray();
					//I don't remember the reason behind this check but updating it.
				}
			}
		}
		return new RGB(color[face]);
	}

	protected abstract ModelRendererTurbo newMRT();
	
	public abstract float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached);

	public boolean apply(String id, float value, boolean x, boolean y, boolean z){
		boolean bool = false;
		switch(id){
			case "size": case "side0": case "side1":{
				if(this.getType().isRectagular()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
			case "tex":{
				bool = this.setFloat(id, x, y, z, value);
				TexViewBox.update();
				break;
			}
			case "pos": case "off":{
				bool = this.setFloat(id, x, y, z, value); break;
			}
			case "rot":{
				bool = this.setFloat(id, x, y, z, value); break; //(float)Math.toRadians(value)); break;
			}
			case "cor0": case "cor1": case "cor2": case "cor3": case "cor4": case "cor5": case "cor6": case "cor7": case "face0": case "face1":{
				if(this.getType().isShapebox()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
			case "cyl0": case "cyl1": case "cyl2": case "cyl3": case "cyl4": case "cyl5": case "cyl6": case "cyl7": {
				if(this.getType().isCylinder()){
					bool = this.setFloat(id, x, y, z, value);
				} break;
			}
		}
		if(id.startsWith("texpos") && this.getType().isTexRect()){ bool = this.setFloat(id, x, y, z, value); }
		if(id.startsWith("marker") && this.getType().isMarker()){ bool = this.setFloat(id, x, y, z, value); }
		if(id.startsWith("oo_")){
			float[] arr = getUVCoords(UVEditor.getSelection()).value();
			if(arr != null){
				arr[x ? 0 : 1] = value;
				bool = true;
			}
			TexViewBox.update();
		}
		else if(id.startsWith("oe_")){
			float[] arr = getUVCoords(UVEditor.getSelection()).value();
			if(arr != null){
				boolean end = id.endsWith("e");
				arr[end ? x ? 2 : 3 : x ? 0 : 1] = value;
				bool = true;
			}
			TexViewBox.update();
		}
		else if(id.startsWith("of_")){
			float[] arr = getUVCoords(UVEditor.getSelection()).value();
			if(arr != null){
				int index = Integer.parseInt(id.substring(id.length() - 1));
				arr[index * 2 + (x ? 0 : 1)] = value;
				bool = true;
			}
			TexViewBox.update();
		}
		this.recompile(); return bool;
	}

	public PolygonWrapper setList(TurboList trlist){
		this.turbolist = trlist; return this;
	}
	
	public ModelRendererTurbo getTurboObject(int i){
		if(i < 0 || i > 2) i = 0; return i == 0 ? turbo : i == 1 ? lines : sellines;
	}
	
	public boolean burnToTexture(Texture tex, Integer face){
		return burnToTexture(tex, face, null, false, null);
	}
	
	public boolean burnToTexture(Texture tex, Integer face, float[][][] coords, boolean detached, Integer sface){
		if(coords == null) coords = newTexturePosition(true, false);
		if(coords == null || coords.length == 0){
			//log("Polygon '" + turbolist.id + ":" + this.name() + "' has no texture data, skipping.");
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
				byte[] color = (negative ? TextureEditor.CURRENTCOLOR : something.getColor(sface)).toByteArray();
				burn(tex, ends, color, detached);
			}
			else{
				for(UVCoords coord : cuv.values()){
					if(!isFaceActive(coord.side())) continue;//disabled
					float[][] ends = coords[coord.side().index()];
					if(ends == null || ends.length == 0){
						log("paint data for face " + coord.side().id() + " not found, skipping");
						continue;
					}
					byte[] color = (negative ? TextureEditor.CURRENTCOLOR : something.getColor(coord.side().index())).toByteArray();
					burn(tex, ends, color, coord.absolute());
				}
			}
		}
		else{
			if(this.getType().isCylinder()){
				int segs = (int)this.getFloat("cyl1", true, false, false);//segments
				float[][] ends = null;
				if(face < segs){
					ends = coords[0];
				}
				else if(face < (segs * 2)){
					ends = coords[2];
				}
				else if(face < (segs * 3)){
					ends = coords[1];
				} else return false;
				if(ends == null || ends.length == 0) return false;
				burn(tex, ends, TextureEditor.CURRENTCOLOR.toByteArray(), false);
			}
			else if(this.getType().isTexturable()){
				float[][] ends = coords[face];
				if(ends == null || ends.length == 0) return false;
				burn(tex, ends, TextureEditor.CURRENTCOLOR.toByteArray(), cuv.get(getTexturableFaces()[face]).absolute());
			}
			else{
				log("There is no known way of how to handle texture burning of '" + this.getType().name() + "'!");
			}
		}
		return true;
	}
	
	private void burn(Texture tex, float[][] ends, byte[] bs, boolean detached){
		float scale_x = tex.getWidth() / turbo.textureWidth;
		float scale_y = tex.getHeight() / turbo.textureHeight;
		float tx = detached ? 0 : textureX;
		float ty = detached ? 0 : textureY;
		//log(turbo.textureWidth + " " + tex.getWidth() + " " + scale_x);
		//log(turbo.textureHeight + " " + tex.getHeight() + " " + scale_y);
		float[][] ands = { { ends[0][0] * scale_x, ends[0][1] * scale_y }, { ends[1][0] * scale_x, ends[1][1] * scale_y } };
		//int tx = 
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
	
	public TurboList getTurboList(){ return turbolist; }

	public PolygonWrapper clone(){
		return clone(null);
	}

	public PolygonWrapper clone(GroupCompound compound){
		if(compound == null) compound = this.compound;
		PolygonWrapper wrapper = this.createClone(compound);
		return copyTo(wrapper, false);
	}
	
	public PolygonWrapper copyTo(PolygonWrapper wrapper, boolean copyvisibility){
		wrapper.pos = new Vec3f(pos); wrapper.off = new Vec3f(off); wrapper.rot = new Vec3f(rot);
		wrapper.textureX = textureX; wrapper.textureY = textureY;
		wrapper.visible = copyvisibility ? visible : true;
		wrapper.name = name == null ? null : name.endsWith("cp") ? name : name + "cp";
		wrapper.mirror = mirror; wrapper.flip = flip;
		return wrapper;
	}

	protected abstract PolygonWrapper createClone(GroupCompound compound);

	public abstract PolygonWrapper convertTo(ShapeType type);

	public void resetPosRot(){
		turbo.rotationPointX = lines.rotationPointX = sellines.rotationPointX = picker.rotationPointX = pos.xCoord;
		turbo.rotationPointY = lines.rotationPointY = sellines.rotationPointY = picker.rotationPointY = pos.yCoord;
		turbo.rotationPointZ = lines.rotationPointZ = sellines.rotationPointZ = picker.rotationPointZ = pos.zCoord;
		turbo.rotationAngleX = lines.rotationAngleX = sellines.rotationAngleX = picker.rotationAngleX = rot.xCoord;
		turbo.rotationAngleY = lines.rotationAngleY = sellines.rotationAngleY = picker.rotationAngleY = rot.yCoord;
		turbo.rotationAngleZ = lines.rotationAngleZ = sellines.rotationAngleZ = picker.rotationAngleZ = rot.zCoord;
	}
	
	public void addPosRot(boolean pos, float x, float y, float z){
		if(pos){
			turbo.rotationPointX = lines.rotationPointX = sellines.rotationPointX = picker.rotationPointX += x;
			turbo.rotationPointY = lines.rotationPointY = sellines.rotationPointY = picker.rotationPointY += y;
			turbo.rotationPointZ = lines.rotationPointZ = sellines.rotationPointZ = picker.rotationPointZ += z;
		}
		else{
			turbo.rotationAngleX = lines.rotationAngleX = sellines.rotationAngleX = picker.rotationAngleX += x;
			turbo.rotationAngleY = lines.rotationAngleY = sellines.rotationAngleY = picker.rotationAngleY += y;
			turbo.rotationAngleZ = lines.rotationAngleZ = sellines.rotationAngleZ = picker.rotationAngleZ += z;
		}
	}
	
	public void setPosRot(boolean pos, float x, float y, float z){
		if(pos){
			turbo.rotationPointX = lines.rotationPointX = sellines.rotationPointX = picker.rotationPointX = x;
			turbo.rotationPointY = lines.rotationPointY = sellines.rotationPointY = picker.rotationPointY = y;
			turbo.rotationPointZ = lines.rotationPointZ = sellines.rotationPointZ = picker.rotationPointZ = z;
		}
		else{
			turbo.rotationAngleX = lines.rotationAngleX = sellines.rotationAngleX = picker.rotationAngleX = x;
			turbo.rotationAngleY = lines.rotationAngleY = sellines.rotationAngleY = picker.rotationAngleY = y;
			turbo.rotationAngleZ = lines.rotationAngleZ = sellines.rotationAngleZ = picker.rotationAngleZ = z;
		}
	}

	public TextureGroup getTextureGroup(){
		return getTurboList().texgroup == null ? FMTB.MODEL.texgroup : getTurboList().getTextureGroup();
	}

	public long getFacesAmount(boolean visonly){
		if(visonly){
			return turbo.getFaces().size();
		}
		if(getType().isCylinder()){
			CylinderWrapper cyl = (CylinderWrapper)this;
			return cyl.seglimit > 0 && cyl.seglimit < cyl.segments ? cyl.seglimit * 4 + 2 : cyl.segments * (cyl.radius2 > 0 ? 4 : 3);
		}
		return 6;
	}

	public abstract Face[] getTexturableFaces();

	public boolean isValidFace(String str){
		for(Face face : getTexturableFaces()){
			if(face.id().equals(str)) return true;
		}
		return false;
	}

	public boolean isValidFace(Face other){
		for(Face face : getTexturableFaces()){
			if(face == other) return true;
		}
		return false;
	}

	public boolean isFaceActive(String str){
		for(Face face : getTexturableFaces()){
			if(face.id().equals(str)) return true;
		}
		return false;
	}

	public boolean isFaceActive(Face other){
		for(Face face : getTexturableFaces()){
			if(face == other) return true;
		}
		return false;
	}

	public UVCoords getUVCoords(Face side){
		return cuv.get(side);
	}
	
	public float[] getDefAutoUVCoords(Face side){
		UVCoords coords = getUVCoords(side);
		int index = coords.side().index();
		float[][][] arr = newTexturePosition(false, false);
		if(arr[index] == null || arr[index].length == 0) return new float[coords.type().arraylength];
		switch(coords.type()){
			case ABSOLUTE:
			case OFFSET_ONLY:
				return new float[]{ arr[index][0][0], arr[index][0][1] };
			case ABSOLUTE_ENDS:
			case OFFSET_ENDS:
				return new float[]{ arr[index][0][0], arr[index][0][1], arr[index][1][0], arr[index][1][1] };
			case ABSOLUTE_FULL:
			case OFFSET_FULL:
				return new float[]{
					arr[index][1][0], arr[index][0][1],
					arr[index][0][0], arr[index][0][1],
					arr[index][0][0], arr[index][1][1],
					arr[index][1][0], arr[index][1][1]
				};
			case AUTOMATIC:
			default:
				return new float[0];
		}
	}
	
	public int textureX(){
		return textureX == -1 ? 0 : textureX;
	}
	
	public int textureY(){
		return textureY == -1 ? 0 : textureY;
	}

	public boolean anyFaceUVAbsolute(){
		for(UVCoords cuv : cuv.values()){
			if(cuv.absolute()) return true;
		}
		return false;
	}

	public boolean isAllFaceUVAbsolute(){
		for(Face face : getTexturableFaces()){
			if(!isFaceActive(face)) continue;//consider this face/side is disabled
			if(!cuv.get(face).absolute()) return true;
		}
		return false;
	}
	
	public int getUnShiftedIndex(int shifted){
		return shifted;//TO BE OVERRIDEN
	}

	public PolygonWrapper setTexViewComponent(PolyFace polyFace){
		texviewcomponent = polyFace;
		return this;
	}
	
}
