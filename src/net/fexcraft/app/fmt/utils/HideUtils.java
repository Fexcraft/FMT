package net.fexcraft.app.fmt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.Vec3f;

public class HideUtils {
	
	public static void hideEqual(){
		ArrayList<PolygonWrapper> all = getAllPolygons();
		ArrayList<PolygonWrapper> polygons = FMTB.MODEL.getSelected();
		if(polygons.isEmpty()) polygons.addAll(all);
		Axis3DL axis = new Axis3DL();
		TreeMap<String, String[]> spaced = getSpaced();
		boolean anychange = false;
		for(PolygonWrapper wrapper : polygons){
			String id = wrapper.getTurboList().id + ":" + wrapper.getTurboList().indexOf(wrapper);
			if(wrapper.getType() != ShapeType.BOX && wrapper.getType() != ShapeType.SHAPEBOX) continue;
			for(int side = 0; side < 6; side++){
				TexturedPolygon poly = wrapper.getTurboObject(0).getFaces()[side];
				String tr = getWorldSpaceAsString(axis, poly.getVertices()[0].vector, wrapper.pos, wrapper.rot);
				String tl = getWorldSpaceAsString(axis, poly.getVertices()[1].vector, wrapper.pos, wrapper.rot);
				String bl = getWorldSpaceAsString(axis, poly.getVertices()[2].vector, wrapper.pos, wrapper.rot);
				String br = getWorldSpaceAsString(axis, poly.getVertices()[3].vector, wrapper.pos, wrapper.rot);
				String[] arr = new String[]{ tr, tl, bl, br };
				Arrays.sort(arr);
				for(Entry<String, String[]> entry : spaced.entrySet()){
					if(entry.getKey().equals(id + "_" + side)) continue;
					if(Arrays.equals(arr, entry.getValue())){
						((BoxWrapper)wrapper).sides[side] = true;
						anychange = true;
					}
				}
			}
		}
		if(anychange){
			FMTB.MODEL.recompile();
		}
		FMTB.MODEL.updateFields();
	}

	private static TreeMap<String, String[]> getSpaced(){
		TreeMap<String, String[]> map = new TreeMap<>();
		Axis3DL axis = new Axis3DL();
		for(TurboList list : FMTB.MODEL.getGroups()){
			for(int i = 0; i < list.size(); i++){
				PolygonWrapper wrapper = list.get(i);
				if(wrapper.getType() != ShapeType.BOX && wrapper.getType() != ShapeType.SHAPEBOX) continue;
				for(int side = 0; side < 6; side++){
					String id = wrapper.getTurboList().id + ":" + wrapper.getTurboList().indexOf(wrapper);
					TexturedPolygon poly = wrapper.getTurboObject(0).getFaces()[side];
					String tr = getWorldSpaceAsString(axis, poly.getVertices()[0].vector, wrapper.pos, wrapper.rot);
					String tl = getWorldSpaceAsString(axis, poly.getVertices()[1].vector, wrapper.pos, wrapper.rot);
					String bl = getWorldSpaceAsString(axis, poly.getVertices()[2].vector, wrapper.pos, wrapper.rot);
					String br = getWorldSpaceAsString(axis, poly.getVertices()[3].vector, wrapper.pos, wrapper.rot);
					String[] arr = new String[]{ tr, tl, bl, br };
					Arrays.sort(arr);
					map.put(id + "_" + side, arr);
				}
			}
		}
		return map;
	}

	/*private static Vec3f getNormals(TexturedPolygon poly){
        Vec3f vec0 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[0].vector));
        Vec3f vec1 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[2].vector));
		return vec1.crossProduct(vec0).normalize();
	}*/

	private static String getWorldSpaceAsString(Axis3DL axis, Vec3f vector, Vec3f pos, Vec3f rot){
		axis.setAngles(-rot.yCoord, -rot.zCoord, -rot.xCoord);
		vector = axis.getRelativeVector(vector).add(pos);
		return vector.xCoord + "," + vector.yCoord + "," + vector.zCoord;
	}

	private static ArrayList<PolygonWrapper> getAllPolygons(){
		ArrayList<PolygonWrapper> polygons = FMTB.MODEL.getSelected();
		for(TurboList list : FMTB.MODEL.getGroups()){
			polygons.addAll(list);
		}
		return polygons;
	}

	public static void hideCovered(){
		//TODO
	}

}
