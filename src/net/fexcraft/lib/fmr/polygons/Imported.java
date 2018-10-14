package net.fexcraft.lib.fmr.polygons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.app.fmt.utils.Vec3f;
import net.fexcraft.lib.fmr.FexcraftModelRenderer;
import net.fexcraft.lib.fmr.PolygonShape;
import net.fexcraft.lib.fmr.Shape;
import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
**/
public class Imported extends PolygonShape {

	public Imported(Shape type){
		super(type);
		if(!type.isExternal()){
			//Static.exception(new Exception(String.format("Invalid usage of ImportedPolygon! Wrong PolygonType: %s", type.name())), true);
		}
	}

	public Imported(Shape type, boolean flip, boolean mirror){
		super(type, flip, mirror);
		if(!type.isExternal()){
			//Static.exception(new Exception(String.format("Invalid usage of ImportedPolygon! Wrong PolygonType: %s", type.name())), true);
		}
	}
	
	public Imported importTMT(ModelRendererTurbo turbo){
    	faces = turbo.getFaces(); vertices = turbo.getVertices(); this.name = turbo.boxName;
        rotationPointX = turbo.rotationPointX; rotationPointY = turbo.rotationPointY; rotationPointZ = turbo.rotationPointZ;
        rotateAngleX = turbo.rotateAngleX; rotateAngleY = turbo.rotateAngleY; rotateAngleZ = turbo.rotateAngleZ;
        return this;
	}
	

	/*public Imported importOBJ(String string_loc){
		return importOBJ(new ResourceLocation(string_loc));
	}*/
	
	/** Based on the TMT one. **/
	public Imported importOBJ(String loc){
		/*IResource resource = null;
		try{
			resource = Minecraft.getMinecraft().getResourceManager().getResource(loc);
			if(resource == null){
				throw new IOException(String.format("OBJ Model with Locatiion %s not found!", loc.toString()));
			}
		}
		catch(IOException e){
			e.printStackTrace(); Static.halt();
		}*/
		try{
			BufferedReader in = new BufferedReader(new FileReader(new File(loc))); String s;
			ArrayList<TexturedPolygon> face = new ArrayList<TexturedPolygon>();
			ArrayList<TexturedVertex> verts = new ArrayList<TexturedVertex>();
			ArrayList<float[]> uvs = new ArrayList<float[]>(), normals = new ArrayList<float[]>();
			while((s = in.readLine()) != null){
				if(s.contains("#")) s = s.substring(0, s.indexOf("#"));
				s = s.trim();
				if(s.equals("")){ continue; }
				if(s.startsWith("v ")){
					s = s.substring(s.indexOf(" ") + 1).trim();
					float[] v = new float[3];
					for(int i = 0; i < 3; i++){
						int ind = s.indexOf(" ");
						v[i] = Float.parseFloat(ind > -1 ? s.substring(0, ind) : s.substring(0));
						s = s.substring(s.indexOf(" ") + 1).trim();
					}
					float flt = v[2];
					v[2] = -v[1];
					v[1] = flt;
					verts.add(new TexturedVertex(v[0], v[1], v[2], 0, 0));
					continue;
				}
				if(s.startsWith("vt ")){
					s = s.substring(s.indexOf(" ") + 1).trim();
					float[] v = new float[2];
					for(int i = 0; i < 2; i++){
						int ind = s.indexOf(" ");
						v[i] = Float.parseFloat(ind > -1 ? s.substring(0, ind) : s.substring(0));
						s = s.substring(s.indexOf(" ") + 1).trim();
					}
					uvs.add(new float[] {v[0], 1F - v[1]});
					continue;
				}
				if(s.startsWith("vn ")){
					s = s.substring(s.indexOf(" ") + 1).trim();
					float[] v = new float[3];
					for(int i = 0; i < 3; i++){
						int ind = s.indexOf(" ");
						v[i] = Float.parseFloat(ind > -1 ? s.substring(0, ind) : s.substring(0));
						s = s.substring(s.indexOf(" ") + 1).trim();
					}
					float flt = v[2];
					v[2] = v[1];
					v[1] = flt;
					normals.add(new float[] {v[0], v[1], v[2]});
					continue;					
				}
				if(s.startsWith("f ")){
					s = s.substring(s.indexOf(" ") + 1).trim();
					ArrayList<TexturedVertex> v = new ArrayList<TexturedVertex>();
					String s1;
					int finalPhase = 0;
					float[] normal = new float[] {0F, 0F, 0F};
					ArrayList<Vec3f> iNormal = new ArrayList<Vec3f>();
					do{
						int vInt;
						float[] curUV;
						float[] curNormals;
						int ind = s.indexOf(" ");
						s1 = s;
						if(ind > -1) s1 = s.substring(0, ind);
						if(s1.indexOf("/") > -1){
							String[] f = s1.split("/");
							vInt = Integer.parseInt(f[0]) - 1;
							if(f[1].equals("")) f[1] = f[0];
							int vtInt = Integer.parseInt(f[1]) - 1;
							if(uvs.size() > vtInt){ curUV = uvs.get(vtInt); }
							else{ curUV = new float[] {0, 0}; }
							int vnInt = 0;
							if(f.length == 3){
								if(f[2].equals("")) f[2] = f[0];
								vnInt = Integer.parseInt(f[2]) - 1;
							}
							else{ vnInt = Integer.parseInt(f[0]) - 1; }
							if(normals.size() > vnInt){ curNormals = normals.get(vnInt); }
							else{ curNormals = new float[] {0, 0, 0}; }
						}
						else{
							vInt = Integer.parseInt(s1) - 1;
							if(uvs.size() > vInt){ curUV = uvs.get(vInt); }
							else{ curUV = new float[] {0, 0}; }
							if(normals.size() > vInt){ curNormals = normals.get(vInt); }
							else{ curNormals = new float[] {0, 0, 0}; }
						}
						iNormal.add(new Vec3f(curNormals[0], curNormals[1], curNormals[2]));
						normal[0]+= curNormals[0];
						normal[1]+= curNormals[1];
						normal[2]+= curNormals[2];
						if(vInt < verts.size()) v.add(verts.get(vInt).setTexturePosition(curUV[0], curUV[1]));
						if(ind > -1){ s = s.substring(s.indexOf(" ") + 1).trim(); }
						else{ finalPhase++; }
					}
					while(finalPhase < 1);
					float d = (float)Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
					normal[0]/= d;
					normal[1]/= d;
					normal[2]/= d;
					TexturedVertex[] vToArr = new TexturedVertex[v.size()];
					for(int i = 0; i < v.size(); i++){ vToArr[i] = v.get(i); }
					TexturedPolygon poly = new TexturedPolygon(vToArr);
					poly.setNormals(normal[0], normal[1], normal[2]);
					poly.setNormals(iNormal);
					face.add(poly); //texture.addPoly(poly);
					continue;					
				}
			}
			vertices = new TexturedVertex[verts.size()];
			for(int i = 0; i < verts.size(); i++){
				vertices[i] = verts.get(i);
			}
			faces = new TexturedPolygon[face.size()];
			for(int i = 0; i < face.size(); i++){
				faces[i] = face.get(i);
				faces[i].clearNormals();
			}
			in.close();
	    	if(flip){ for(int l = 0; l < faces.length; l++){ faces[l].flipFace(); } }
		}
		catch(Throwable e){
			e.printStackTrace();
			//TODO Static.stop();
		}
		return this;
	}
	
	/** UNTESTED **/
	public Imported importFMRJSON(JsonObject obj){
		vertices = new TexturedVertex[]{};
		JsonArray array = obj.has("faces") ? obj.get("faces").getAsJsonArray() : new JsonArray();
		faces = new TexturedPolygon[array.size()];
		for(int k = 0; k < faces.length; k++){
			JsonObject elm = array.get(k).getAsJsonObject();
			JsonArray vertices = elm.get("vertices").getAsJsonArray();
			TexturedVertex[] verts = new TexturedVertex[vertices.size()];
			for(int i = 0; i < vertices.size(); i++){
				JsonObject fjsn = vertices.get(i).getAsJsonObject();
				float tx = JsonUtil.getIfExists(fjsn, "tx", 0).floatValue();
				float ty = JsonUtil.getIfExists(fjsn, "ty", 0).floatValue();
				float  x = JsonUtil.getIfExists(fjsn,  "x", 0).floatValue();
				float  y = JsonUtil.getIfExists(fjsn,  "y", 0).floatValue();
				float  z = JsonUtil.getIfExists(fjsn,  "z", 0).floatValue();
				verts[i] = new TexturedVertex(new Vec3f(x, y, z), tx, ty);
			}
			ArrayList<Vec3f> list = new ArrayList<Vec3f>();
			if(elm.has("vectors")){
				for(JsonElement jsn : elm.get("vectors").getAsJsonArray()){
					JsonObject objj = jsn.getAsJsonObject();
					list.add(new Vec3f(
						JsonUtil.getIfExists(objj, "x", 0).floatValue(),
						JsonUtil.getIfExists(objj, "y", 0).floatValue(),
						JsonUtil.getIfExists(objj, "z", 0).floatValue()
					));
				}
			}
			float[] normals = new float[3];
			if(elm.has("normals")){
				JsonArray err = elm.getAsJsonObject().get("normals").getAsJsonArray();
				normals = new float[err.size()];
				for(int i = 0; i < err.size(); i++) normals[i] = err.get(i).getAsFloat();
			} else { normals[0] = 0; normals[1] = 0; normals[2] = 0; }
			faces[k] = new TexturedPolygon(verts);
			faces[k].setInvert(elm.has("invert") && elm.get("invert").getAsBoolean());
			faces[k].setNormals(normals[0], normals[1], normals[2]);
			faces[k].setNormals(list);
		}
		return this;
	}

	@Override
	protected void populateJsonObject(JsonObject obj){
		if(!FexcraftModelRenderer.GENERIC_BOOLEAN){ return; }
		JsonObject elm = null; JsonArray array = null;
		if(vertices.length > 0){
			array = new JsonArray();
			for(TexturedVertex vertex : vertices){
				elm = new JsonObject();
				elm.addProperty("tx", vertex.textureX);
				elm.addProperty("ty", vertex.textureY);
				elm.addProperty("x", vertex.vector.xCoord);
				elm.addProperty("y", vertex.vector.yCoord);
				elm.addProperty("z", vertex.vector.zCoord);
			}
			obj.add("vertices", array);
		}
		array = new JsonArray();
		for(TexturedPolygon poly : faces){
			elm = new JsonObject();
			JsonArray elms = new JsonArray();
			for(TexturedVertex vertex : poly.getVertices()){
				JsonObject jsn = new JsonObject();
				jsn.addProperty("tx", vertex.textureX);
				jsn.addProperty("ty", vertex.textureY);
				jsn.addProperty("x", vertex.vector.xCoord);
				jsn.addProperty("y", vertex.vector.yCoord);
				jsn.addProperty("z", vertex.vector.zCoord);
				elms.add(jsn);
			}
			elm.add("vertices", elms);
			if(poly.isInverted()){
				elm.addProperty("invert", poly.isInverted());
			}
			if(poly.getNormals() != null && poly.getNormals().length > 0){
				elms = new JsonArray();
				for(float f : poly.getNormals()) elms.add(new JsonPrimitive(f));
				if(elms.size() > 0) elm.add("normals", elms);
			}
			if(poly.getVectors().size() > 0){
				elms = new JsonArray();
				for(Vec3f vec : poly.getVectors()){
					JsonObject jsn = new JsonObject();
					jsn.addProperty("x", vec.xCoord);
					jsn.addProperty("y", vec.yCoord);
					jsn.addProperty("z", vec.zCoord);
					elms.add(jsn);
				}
				elm.add("vectors", elms);
			}
			array.add(elm);
		}
		obj.add("faces", array);
	}

	@Override
	protected PolygonShape compileShape(){
		compiled = true; return this;//No need to do more.
	}

}
