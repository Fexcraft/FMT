package net.fexcraft.app.fmt_old.wrappers;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt_old.wrappers.face.Face;
import net.fexcraft.app.fmt_old.wrappers.face.S3DFace;
import net.fexcraft.lib.tmt.Coord2D;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class Shape3DWrapper extends PolygonWrapper {
	
	public float depth = 1;
	public int direction = ModelRendererTurbo.MR_TOP, corners = 4;
	public int shape_tex_width, shape_tex_height;
	public int side_tex_width, side_tex_height;
	public Coord2D[] coords = new Coord2D[]{ new Coord2D(0, 0), new Coord2D(4, 0), new Coord2D(4, 4), new Coord2D(-3, 5) };
	
	public Shape3DWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		Shape3DWrapper wrapper = new Shape3DWrapper(compound);
		wrapper.depth = depth;
		wrapper.direction = direction;
		wrapper.corners = corners;
		wrapper.coords = new Coord2D[corners];
		for(int i = 0; i < corners; i++){
			wrapper.coords[i] = new Coord2D(coords[i].xCoord, coords[i].yCoord);
		}
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = new ModelRendererTurbo(null, textureX(), textureY(), compound.tx(getTurboList()), compound.ty(getTurboList()));
		ArrayList<Coord2D> ceerds = new ArrayList<>();
		for(int i = 0; i < corners; i++) ceerds.add(new Coord2D(coords[i].xCoord, coords[i].yCoord));
		turbo.addShape3D(off.xCoord, off.yCoord, off.zCoord, ceerds, depth, shape_tex_width, shape_tex_height, side_tex_width, side_tex_height, direction);
		//TODO if(cuv.anyCustom()){}
		return turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord).setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.SHAPE3D;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "shape0": return x ? depth : y ? direction : z ? corners : 0;
			case "shape1": return x ? shape_tex_width : y ? shape_tex_height : 0;
			case "shape2": return x ? side_tex_width : y ? side_tex_height : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "cyl0":{
				if(x){
					depth = value;
					return true;
				}
				if(y){
					direction = (int)value;
					return true;
				}
				if(z){
					corners = (int)value;
					if(corners < 3) corners = 3;
					if(corners > S3DFace.amount()) corners = S3DFace.amount();
					Coord2D[] caards = new Coord2D[corners];
					for(int i = 0; i < corners; i++){
						if(i >= coords.length) caards[0] = new Coord2D(0, 0);
						else caards[i] = new Coord2D(coords[i].xCoord, coords[i].yCoord);
					}
					return true;
				}
			}
			case "cyl1":{
				if(x){ shape_tex_width = (int)value; return true; }
				if(y){ shape_tex_height = (int)value; return true; }
				if(z){ return false; }
			}
			case "cyl2":{
				if(x){ side_tex_width = (int)value; return true; }
				if(y){ side_tex_height = (int)value; return true; }
				if(z){ return false; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("depth", depth);
		obj.addProperty("direction", direction);
		obj.addProperty("corners", corners);
		if(shape_tex_width != 0f) obj.addProperty("shape_tex_width", 1);
		if(shape_tex_height != 0f) obj.addProperty("shape_tex_height", 1);
		if(side_tex_width != 0f) obj.addProperty("side_tex_width", 1);
		if(side_tex_height != 0f) obj.addProperty("side_tex_height", 1);
		JsonArray array = new JsonArray();
		for(Coord2D coord : coords){
			array.add(coord.xCoord);
			array.add(coord.yCoord);
		}
		obj.add("coords", array);
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		float[][][] vecs = new float[0][][];
		//TODO
		return vecs;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == ShapeType.SHAPE3D ? this.clone() : null;
	}

	@Override
	public Face[] getTexturableFaces(){
		return S3DFace.values();
	}

	@Override
	public boolean isFaceActive(String str){
		return isFaceActive(Face.byId(str, true));
	}

	@Override
	public boolean isFaceActive(Face other){
		if(other instanceof S3DFace == false) return false;
		//TODO
		return true;
	}
	
}
