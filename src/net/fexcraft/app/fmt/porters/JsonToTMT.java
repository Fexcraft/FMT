package net.fexcraft.app.fmt.porters;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.MarkerWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TexrectWrapperA;
import net.fexcraft.app.fmt.wrappers.TexrectWrapperB;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

/**
* Tool to parse `ModelRendererTurbo` objects from JSON.
* @Author Ferdinand Calo' (FEX___96)
*/
public class JsonToTMT {
	
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
			case "box": case "cube": case "b":{
				BoxWrapper cuboid = new BoxWrapper(compound);
				cuboid.size.xCoord = get(width, obj, def);
				cuboid.size.yCoord = get(height, obj, def);
				cuboid.size.zCoord= get(depth, obj, def);
				polygon = cuboid; break;
			}
			case "shapebox": case "sbox": case "sb": {
				ShapeboxWrapper shapebox = new ShapeboxWrapper(compound);
				shapebox.size.xCoord = get(width, obj, def);
				shapebox.size.yCoord = get(height, obj, def);
				shapebox.size.zCoord= get(depth, obj, def);
				//
				shapebox.cor0 = new Vec3f(get("x0", obj, def), get("y0", obj, def), get("z0", obj, def));
				shapebox.cor1 = new Vec3f(get("x1", obj, def), get("y1", obj, def), get("z1", obj, def));
				shapebox.cor2 = new Vec3f(get("x2", obj, def), get("y2", obj, def), get("z2", obj, def));
				shapebox.cor3 = new Vec3f(get("x3", obj, def), get("y3", obj, def), get("z3", obj, def));
				shapebox.cor4 = new Vec3f(get("x4", obj, def), get("y4", obj, def), get("z4", obj, def));
				shapebox.cor5 = new Vec3f(get("x5", obj, def), get("y5", obj, def), get("z5", obj, def));
				shapebox.cor6 = new Vec3f(get("x6", obj, def), get("y6", obj, def), get("z6", obj, def));
				shapebox.cor7 = new Vec3f(get("x7", obj, def), get("y7", obj, def), get("z7", obj, def));
				if(obj.has("face_triangle_flip")){
					JsonArray array = obj.getAsJsonArray("face_triangle_flip");
					for(int i = 0; i < 6; i++) shapebox.bool[i] = array.get(i).getAsBoolean();
				}
				polygon = shapebox; break;
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
				polygon = cylinder; break;
			}
			case "texrect": case "texrect_a": case "texrect_b": {
				TexrectWrapperB texrect = null;
				if(obj.has("texpos")){
					JsonArray array = obj.get("texpos").getAsJsonArray();
					for(int i = 0; i < 6; i++){
						JsonArray arr = array.get(i).getAsJsonArray();
						if(texrect == null){
							texrect = arr.size() > 4 ? new TexrectWrapperA(compound) : new TexrectWrapperB(compound);
						}
						texrect.texcor[i][0] = arr.get(0).getAsFloat();
						texrect.texcor[i][1] = arr.get(1).getAsFloat();
						texrect.texcor[i][2] = arr.get(2).getAsFloat();
						texrect.texcor[i][3] = arr.get(3).getAsFloat();
						if(arr.size() > 4){
							texrect.texcor[i][4] = arr.get(4).getAsFloat();
							texrect.texcor[i][5] = arr.get(5).getAsFloat();
							texrect.texcor[i][6] = arr.get(6).getAsFloat();
							texrect.texcor[i][7] = arr.get(7).getAsFloat();
						}
					}
				}
				if(texrect == null){ texrect = new TexrectWrapperB(compound); }
				texrect.size.xCoord = get(width, obj, def); texrect.size.yCoord = get(height, obj, def); texrect.size.zCoord= get(depth, obj, def);
				//
				texrect.cor0 = new Vec3f(get("x0", obj, def), get("y0", obj, def), get("z0", obj, def));
				texrect.cor1 = new Vec3f(get("x1", obj, def), get("y1", obj, def), get("z1", obj, def));
				texrect.cor2 = new Vec3f(get("x2", obj, def), get("y2", obj, def), get("z2", obj, def));
				texrect.cor3 = new Vec3f(get("x3", obj, def), get("y3", obj, def), get("z3", obj, def));
				texrect.cor4 = new Vec3f(get("x4", obj, def), get("y4", obj, def), get("z4", obj, def));
				texrect.cor5 = new Vec3f(get("x5", obj, def), get("y5", obj, def), get("z5", obj, def));
				texrect.cor6 = new Vec3f(get("x6", obj, def), get("y6", obj, def), get("z6", obj, def));
				texrect.cor7 = new Vec3f(get("x7", obj, def), get("y7", obj, def), get("z7", obj, def));
				polygon = texrect; break;
			}
			case "marker":{
				MarkerWrapper marker = new MarkerWrapper(compound);
				marker.color = obj.has("color") ? obj.get("color").getAsInt() : RGB.GREEN.packed;
				polygon = marker; break;
			}
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
		return polygon;
	}
	
}