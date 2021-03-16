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
				if(side >= wrapper.getTurboObject(0).getFaces().size()) continue;
				TexturedPolygon poly = wrapper.getTurboObject(0).getFaces().get(side);
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
						break;
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
					if(side >= wrapper.getTurboObject(0).getFaces().size()) continue;
					TexturedPolygon poly = wrapper.getTurboObject(0).getFaces().get(side);
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

	private static TreeMap<String, Object[]> getSpacedPlusVector(){
		TreeMap<String, Object[]> map = new TreeMap<>();
		Axis3DL axis = new Axis3DL();
		for(TurboList list : FMTB.MODEL.getGroups()){
			for(int i = 0; i < list.size(); i++){
				PolygonWrapper wrapper = list.get(i);
				if(wrapper.getType() != ShapeType.BOX && wrapper.getType() != ShapeType.SHAPEBOX) continue;
				for(int side = 0; side < 6; side++){
					String id = wrapper.getTurboList().id + ":" + wrapper.getTurboList().indexOf(wrapper);
					if(side >= wrapper.getTurboObject(0).getFaces().size()) continue;
					TexturedPolygon poly = wrapper.getTurboObject(0).getFaces().get(side);
					Vec3f tr = getWorldSpace(axis, poly.getVertices()[0].vector, wrapper.pos, wrapper.rot);
					Vec3f tl = getWorldSpace(axis, poly.getVertices()[1].vector, wrapper.pos, wrapper.rot);
					Vec3f bl = getWorldSpace(axis, poly.getVertices()[2].vector, wrapper.pos, wrapper.rot);
					Vec3f br = getWorldSpace(axis, poly.getVertices()[3].vector, wrapper.pos, wrapper.rot);
					Object[] arr = new Object[]{ asString(tr), asString(tl), asString(bl), asString(br), getNormals(poly), wrapper };
					map.put(id + "_" + side, arr);
				}
			}
		}
		return map;
	}

	private static Vec3f getNormals(TexturedPolygon poly){
        Vec3f vec0 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[0].vector));
        Vec3f vec1 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[2].vector));
		return vec1.crossProduct(vec0).normalize();
	}

	private static Vec3f getWorldSpace(Axis3DL axis, Vec3f vector, Vec3f pos, Vec3f rot){
		axis.setAngles(-rot.yCoord, -rot.zCoord, -rot.xCoord);
		return axis.getRelativeVector(vector).add(pos);
	}

	private static String getWorldSpaceAsString(Axis3DL axis, Vec3f vector, Vec3f pos, Vec3f rot){
		axis.setAngles(-rot.yCoord, -rot.zCoord, -rot.xCoord);
		vector = axis.getRelativeVector(vector).add(pos);
		return vector.xCoord + "," + vector.yCoord + "," + vector.zCoord;
	}

	private static String asString(Vec3f vector){
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
		ArrayList<PolygonWrapper> all = getAllPolygons();
		ArrayList<PolygonWrapper> polygons = FMTB.MODEL.getSelected();
		if(polygons.isEmpty()) polygons.addAll(all);
		Axis3DL axis = new Axis3DL();
		TreeMap<String, Object[]> spaced = getSpacedPlusVector();
		boolean anychange = false;
		for(PolygonWrapper wrapper : polygons){
			String id = wrapper.getTurboList().id + ":" + wrapper.getTurboList().indexOf(wrapper);
			if(wrapper.getType() != ShapeType.BOX && wrapper.getType() != ShapeType.SHAPEBOX) continue;
			for(int side = 0; side < 6; side++){
				if(side >= wrapper.getTurboObject(0).getFaces().size()) continue;
				TexturedPolygon poly = wrapper.getTurboObject(0).getFaces().get(side);
				String tr = getWorldSpaceAsString(axis, poly.getVertices()[0].vector, wrapper.pos, wrapper.rot);
				String tl = getWorldSpaceAsString(axis, poly.getVertices()[1].vector, wrapper.pos, wrapper.rot);
				String bl = getWorldSpaceAsString(axis, poly.getVertices()[2].vector, wrapper.pos, wrapper.rot);
				String br = getWorldSpaceAsString(axis, poly.getVertices()[3].vector, wrapper.pos, wrapper.rot);
				boolean itr = false, itl = false, ibl = false, ibr = false;
				Vec3f normals = getNormals(poly);
				for(Entry<String, Object[]> entry : spaced.entrySet()){
					if(entry.getKey().equals(id + "_" + side)) continue;
					if(!norm(entry.getKey(), normals, entry.getValue())) continue;
					if(!itr && contains(entry.getValue(), tr)){
						itr = true;
					}
					if(!itl && contains(entry.getValue(), tl)){
						itl = true;
					}
					if(!ibl && contains(entry.getValue(), bl)){
						ibl = true;
					}
					if(!ibr && contains(entry.getValue(), br)){
						ibr = true;
					}
				}
				if(itr && itl && ibl && ibr){
					((BoxWrapper)wrapper).sides[side] = true;
					anychange = true;
				}
			}
		}
		if(anychange){
			FMTB.MODEL.recompile();
		}
		FMTB.MODEL.updateFields();
	}

	private static boolean norm(String id, Vec3f normals, Object[] value){
		float dot = normals.dotProduct((Vec3f)value[4]);
		return dot <= -1;
	}

	private static boolean contains(Object[] arr, String val){
		for(int i = 0; i < 4; i++){
			if(arr[i].equals(val)) return true;
		}
		return false;
	}

	public static void reset(){
		for(TurboList list : FMTB.MODEL.getGroups()){
			for(PolygonWrapper wrapper : list){
				if(wrapper.getType() != ShapeType.BOX && wrapper.getType() != ShapeType.SHAPEBOX) continue;
				((BoxWrapper)wrapper).sides = new boolean[]{ false, false, false, false, false, false };
			}
		}
		FMTB.MODEL.recompile();
		FMTB.MODEL.updateFields();
	}

}
