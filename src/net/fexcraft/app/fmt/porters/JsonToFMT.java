package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.*;
import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

/**
* Tool to parse `ModelRendererTurbo` objects from JSON.
* @Author Ferdinand Calo' (FEX___96)
*/
public class JsonToFMT {
	
	//def
	public static final float def = 0f;
	public static final int idef = 0;
	//common
	public static final String[] format  = new String[]{"format", "form", "f"};//1.1f
	public static final String[] width  = new String[]{"width",  "wid", "w"};
	public static final String[] height = new String[]{"height", "hgt", "h"};
	public static final String[] depth  = new String[]{"depth",  "dep", "d"};
	public static final String[] offx = new String[]{"offset_x", "off_x", "offx", "ox"};
	public static final String[] offy = new String[]{"offset_y", "off_y", "offy", "oy"};
	public static final String[] offz = new String[]{"offset_z", "off_z", "offz", "oz"};
	public static final String[] expansion = new String[]{"expansion", "exp", "e"};
	public static final String[] scale = new String[]{"scale", "s"};
	public static final String[] texturex = new String[]{"texture_x", "texturex", "tex_x", "tx"};
	public static final String[] texturey = new String[]{"texture_y", "texturey", "tex_y", "ty"};
	//
	public static final String[] posx = new String[]{"rotation_point_x", "pos_x", "posx", "px", "x"};
	public static final String[] posy = new String[]{"rotation_point_y", "pos_y", "posy", "py", "y"};
	public static final String[] posz = new String[]{"rotation_point_z", "pos_z", "posz", "pz", "z"};
	public static final String[] rotx = new String[]{"rotation_angle_x", "rotangle_x", "rotanglex", "rot_x", "rx"};
	public static final String[] roty = new String[]{"rotation_angle_y", "rotangle_y", "rotangley", "rot_y", "ry"};
	public static final String[] rotz = new String[]{"rotation_angle_z", "rotangle_z", "rotanglez", "rot_z", "rz"};
	//settings
	public static final String[] oldrot = new String[]{"old_ration", "old_rotation_order", "oro"};
	public static final String[] mirror = new String[]{"mirror", "mir", "m"};
	public static final String[] flip = new String[]{"flip", "fl", "usd"};
	//cyl
	public static final String[] radius = new String[]{"radius", "rad", "r"};
	public static final String[] radius2 = new String[]{"radius2", "rad2", "r2"};
	public static final String[] length = new String[]{"length", "len", "l"};
	public static final String[] segments = new String[]{"segments", "seg", "sg"};
	public static final String[] seglimit = new String[]{"segment_limit", "segments_limit", "seglimit", "seg_limit", "sgl"};
	public static final String[] basescale = new String[]{"base_scale", "basescale", "bs"};
	public static final String[] topscale = new String[]{"top_scale", "topscale", "ts"};
	public static final String[] direction = new String[]{"direction", "dir", "facing"};
	public static final String[] topoffx = new String[]{"top_offset_x", "topoff_x", "topoffx"};
	public static final String[] topoffy = new String[]{"top_offset_y", "topoff_y", "topoffy"};
	public static final String[] topoffz = new String[]{"top_offset_z", "topoff_z", "topoffz"};
	public static final String[] toprotx = new String[]{"top_rotation_x", "toprot_x", "toprotx"};
	public static final String[] toproty = new String[]{"top_rotation_y", "toprot_y", "toproty"};
	public static final String[] toprotz = new String[]{"top_rotation_z", "toprot_z", "toprotz"};
	public static final String[] segwidth = new String[]{"segment_width", "seg_width", "sw"};
	public static final String[] segheight = new String[]{"segment_height", "seg_height", "sh"};
	public static final String[] segx = new String[]{"segments", "segments_x", "seg_x", "segx", "sgx"};
	public static final String[] segy = new String[]{"segments", "segments_y", "seg_y", "segy", "sgy"};
	public static final String[] segz = new String[]{"segments", "segments_z", "seg_z", "segz", "sgz"};
	
	public static final float get(String s, JsonObject obj, float def){
		if(obj.has(s)){
			return obj.get(s).getAsFloat();
		}
		return def;
	}
	
	public static final float get(String[] s, JsonObject obj, float def){
		for(String str : s){
			if(obj.has(str)){
				return obj.get(str).getAsFloat();
			}
		}
		return 0;
	}
	
	public static final int get(String[] s, JsonObject obj, int def){
		for(String str : s){
			if(obj.has(str)){
				return obj.get(str).getAsInt();
			}
		}
		return 0;
	}
	
	//---//WRAPPERS//---//
	
	public final static PolygonWrapper parseWrapper(GroupCompound compound, JsonObject obj){
		PolygonWrapper polygon = null;
		switch(obj.get("type").getAsString()){
			case "box": case "cube": case "b": case "quad": case "q": {
				BoxWrapper cuboid = new BoxWrapper(compound);
				cuboid.size.xCoord = get(width, obj, def);
				cuboid.size.yCoord = get(height, obj, def);
				cuboid.size.zCoord = get(depth, obj, def);
				cuboid.sides = parseSides(obj);
				polygon = cuboid; break;
			}
			case "boundingbox": case "bb": {
				BBWrapper cuboid = new BBWrapper(compound);
				cuboid.size.xCoord = get(width, obj, def);
				cuboid.size.yCoord = get(height, obj, def);
				cuboid.size.zCoord = get(depth, obj, def);
				cuboid.sides = parseSides(obj);
				polygon = cuboid; break;
			}
			case "shapebox": case "sbox": case "sb": {
				polygon = loadShapebox(compound, obj);
				break;
			}
			case "texrect": case "texrect_a": case "texrect_b":{
				ShapeboxWrapper shapebox = loadShapebox(compound, obj);
				if(obj.has("texpos")){
					JsonArray array = obj.get("texpos").getAsJsonArray();
					for(int i = 0; i < 6; i++){
						JsonArray arr = array.get(i).getAsJsonArray();
						float[] farr = new float[arr.size()];
						for(int a = 0; a < farr.length; a++) farr[a] = arr.get(a).getAsFloat();
						shapebox.cuv.get(BoxFace.values()[i]).set(FaceUVType.bySize(farr.length, true)).value(farr);
					}
				}
				polygon = shapebox;
				break;
			}
			case "shapequad": case "squad": case "sq": {
				ShapeQuadWrapper advface = new ShapeQuadWrapper(compound);
				advface.size.xCoord = get(width, obj, def);
				advface.size.yCoord = get(height, obj, def);
				advface.size.zCoord = get(depth, obj, def);
				//
				advface.cor0 = new Vec3f(get("x0", obj, def), get("y0", obj, def), get("z0", obj, def));
				advface.cor1 = new Vec3f(get("x1", obj, def), get("y1", obj, def), get("z1", obj, def));
				advface.cor2 = new Vec3f(get("x2", obj, def), get("y2", obj, def), get("z2", obj, def));
				advface.cor3 = new Vec3f(get("x3", obj, def), get("y3", obj, def), get("z3", obj, def));
				polygon = advface; break;
			}
			case "cylinder": case "cyl": case "c": case "cone": case "cn": {
				CylinderWrapper cylinder = new CylinderWrapper(compound);
				cylinder.radius = get(radius, obj, 1f);
				cylinder.radius2 = get(radius2, obj, 0f);
				cylinder.length = get(length, obj, 1f);
				cylinder.segments = get(segments, obj, 16);
				cylinder.seglimit = get(seglimit, obj, cylinder.segments);
				cylinder.direction = get(direction, obj, 4);
				cylinder.base = get(basescale, obj, 1f);
				cylinder.top = get(topscale, obj, 1f);
				cylinder.topoff.xCoord = get(topoffx, obj, 0f);
				cylinder.topoff.yCoord = get(topoffy, obj, 0f);
				cylinder.topoff.zCoord = get(topoffz, obj, 0f);
				cylinder.toprot.xCoord = get(toprotx, obj, 0f);
				cylinder.toprot.yCoord = get(toproty, obj, 0f);
				cylinder.toprot.zCoord = get(toprotz, obj, 0f);
				if(obj.has("faces_off")){
					JsonArray array = obj.get("faces_off").getAsJsonArray();
					for(int i = 0; i < cylinder.bools.length; i++){
						cylinder.bools[i] = i >= array.size() ? false : array.get(i).getAsBoolean();
					}
				}
				cylinder.radial = JsonUtil.getIfExists(obj, "radialtex", false);
				cylinder.seg_width = get(segwidth, obj, 0);
				cylinder.seg_height = get(segheight, obj, 0);
				polygon = cylinder; break;
			}
			case "marker":{
				MarkerWrapper marker = new MarkerWrapper(compound);
				marker.color = obj.has("color") ? obj.get("color").getAsInt() : RGB.GREEN.packed;
				marker.biped = JsonUtil.getIfExists(obj, "biped", false);
				marker.angle = JsonUtil.getIfExists(obj, "biped_angle", -90).intValue();
				marker.scale = JsonUtil.getIfExists(obj, "biped_scale", 1f).floatValue();
				marker.detached = JsonUtil.getIfExists(obj, "detached", false);
				polygon = marker; break;
			}
			case "voxel":{
				VoxelWrapper voxel = new VoxelWrapper(compound, get(segx, obj, 16), get(segy, obj, 16), get(segz, obj, 16), false);
				for(JsonElement elm : obj.get("coords").getAsJsonArray()){
					JsonArray arr = elm.getAsJsonArray();
					//log("coord" + elm);
					int xx = arr.get(0).getAsInt(), yy = arr.get(1).getAsInt(), zz = arr.get(2).getAsInt();
					int x0 = xx + arr.get(3).getAsInt(), y0 = yy + arr.get(4).getAsInt(), z0 = zz + arr.get(5).getAsInt();
					for(int x = xx; x < x0; x++){
						for(int y = yy; y < y0; y++){
							for(int z = zz; z < z0; z++){
								voxel.content[x][y][z] = true;
							}
						}
					}
					//log("passed");
				}
				polygon = voxel;
				break;
			}
		}
		if(polygon == null){
			log("POLYGON TYPE '" + obj.get("type").getAsString() + "' NOT FOUND, ATTEMPTING TO PARSE AS BOX. ID: " + (obj.has("name") ? obj.get("name").getAsString() : "null"));
			obj.addProperty("type", "box"); return parseWrapper(compound, obj);
		}
		polygon.textureX = get(texturex, obj, idef);
		polygon.textureY = get(texturey, obj, idef);
		polygon.mirror = JsonUtil.getIfExists(obj, mirror, false);
		polygon.flip = JsonUtil.getIfExists(obj, flip, false);
		polygon.rot.xCoord = get(rotx, obj, def);
		polygon.rot.yCoord = get(roty, obj, def);
		polygon.rot.zCoord = get(rotz, obj, def);
		polygon.off.xCoord = get(offx, obj, def);
		polygon.off.yCoord = get(offy, obj, def);
		polygon.off.zCoord = get(offz, obj, def);
		polygon.pos.xCoord = get(posx, obj, def);
		polygon.pos.yCoord = get(posy, obj, def);
		polygon.pos.zCoord = get(posz, obj, def);
		polygon.name = obj.has("name") ? obj.get("name").getAsString() : null;
		polygon.visible = obj.has("visible") ? obj.get("visible").getAsBoolean() : true;
		if(obj.has("cuv")){
			polygon.parseCustomUV(obj.get("cuv").getAsJsonObject());
		}
		polygon.button.updateColor();
		return polygon;
	}

	private static ShapeboxWrapper loadShapebox(GroupCompound compound, JsonObject obj){
		ShapeboxWrapper shapebox = new ShapeboxWrapper(compound);
		shapebox.size.xCoord = get(width, obj, def);
		shapebox.size.yCoord = get(height, obj, def);
		shapebox.size.zCoord = get(depth, obj, def);
		//
		shapebox.cor0 = new Vec3f(get("x0", obj, def), get("y0", obj, def), get("z0", obj, def));
		shapebox.cor1 = new Vec3f(get("x1", obj, def), get("y1", obj, def), get("z1", obj, def));
		shapebox.cor2 = new Vec3f(get("x2", obj, def), get("y2", obj, def), get("z2", obj, def));
		shapebox.cor3 = new Vec3f(get("x3", obj, def), get("y3", obj, def), get("z3", obj, def));
		shapebox.cor4 = new Vec3f(get("x4", obj, def), get("y4", obj, def), get("z4", obj, def));
		shapebox.cor5 = new Vec3f(get("x5", obj, def), get("y5", obj, def), get("z5", obj, def));
		shapebox.cor6 = new Vec3f(get("x6", obj, def), get("y6", obj, def), get("z6", obj, def));
		shapebox.cor7 = new Vec3f(get("x7", obj, def), get("y7", obj, def), get("z7", obj, def));
		/*if(obj.has("face_triangle_flip")){
			JsonArray array = obj.getAsJsonArray("face_triangle_flip");
			for(int i = 0; i < 6; i++) shapebox.bool[i] = array.get(i).getAsBoolean();
		}*/
		shapebox.sides = parseSides(obj);
		return shapebox;
	}

	private static boolean[] parseSides(JsonObject obj){
		boolean[] sides = new boolean[6];
		if(obj.has("sides_off")){
			JsonArray array = obj.get("sides_off").getAsJsonArray();
			for(int i = 0; i < sides.length; i++){
				if(i >= array.size()) break;
				sides[i] = array.get(i).getAsBoolean();
			}
		}
		return sides;
	}
	
}