package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CylinderWrapper extends PolygonWrapper {
	
	public float radius = 2, radius2, length = 2, base = 1, top = 1;
	public int segments = 8, seglimit, direction = ModelRendererTurbo.MR_TOP;
	public Vec3f topoff = new Vec3f(0, 0, 0), toprot = new Vec3f(0, 0, 0);
	public boolean[] bools = new boolean[4];
	//
	public static String[] faces = { "base", "top", "outer", "inner", "seg_side_0", "seg_side_1" };
	//
	public boolean radial;
	public float seg_width, seg_height;
	
	public CylinderWrapper(GroupCompound compound){
		super(compound);
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		CylinderWrapper wrapper = new CylinderWrapper(compound);
		wrapper.radius = radius; wrapper.radius2 = radius2;
		wrapper.length = length; wrapper.base = base; wrapper.top = top;
		wrapper.segments = segments; wrapper.direction = direction;
		wrapper.seglimit = seglimit; wrapper.topoff = new Vec3f(topoff);
		wrapper.bools = new boolean[]{ bools[0], bools[1], bools[2], bools[2] };
		wrapper.radial = radial; wrapper.seg_width = seg_width; wrapper.seg_height = seg_height;
		return wrapper;
	}
	
	protected ModelRendererTurbo newMRT(){
		ModelRendererTurbo turbo = new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()));
		if(radial || usesTopRotation()){
			turbo.newCylinderBuilder().setPosition(off.xCoord, off.yCoord, off.zCoord).setRadius(radius, radius2).setLength(length).setSegments(segments, seglimit)
			.setScale(base, top).setDirection(direction).setTopOffset(topoff).setSidesVisible(bools).setRadialTexture(seg_width, seg_height)
			.setTopRotation(toprot).build();
		}
		else if(radius2 != 0){
			turbo.addHollowCylinder(off.xCoord, off.yCoord, off.zCoord, radius, radius2, length, segments, seglimit, base, top, direction, getTopOff(), bools);
		}
		else{
			turbo.addCylinder(off.xCoord, off.yCoord, off.zCoord, radius, length, segments, base, top, direction, getTopOff());
		}
		return turbo.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord).setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	private Vec3f getTopOff(){
		return topoff.xCoord == 0f && topoff.yCoord == 0f && topoff.zCoord == 0f ? null : topoff;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.CYLINDER;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		switch(id){
			case "cyl0": return x ? radius : y ? length : z ? radius2 : 0;
			case "cyl1": return x ? segments : y ? direction : z ? seglimit : 0;
			case "cyl2": return x ? base : y ? top : 0;
			case "cyl3": return x ? topoff.xCoord : y ? topoff.yCoord : z ? topoff.zCoord : 0;
			case "cyl4": return x ? (bools[0] ? 1 : 0) : y ? (bools[1] ? 1 : 0) : 0;
			case "cyl5": return x ? (bools[2] ? 1 : 0) : y ? (bools[3] ? 1 : 0) : 0;
			case "cyl6": return x ? (radial ? 1 : 0) : y ? seg_width : z ? seg_height : 0;
			case "cyl7": return x ? toprot.xCoord : y ? toprot.yCoord : z ? toprot.zCoord : 0;
			default: return super.getFloat(id, x, y, z);
		}
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		switch(id){
			case "cyl0":{
				if(x){
					radius = value; seg_width = radius / 2;
					GeneralEditor.cyl6_y.apply(seg_width);
					return true;
				}
				if(y){
					length = value; seg_height = radius - radius2;
					GeneralEditor.cyl6_z.apply(seg_height);
					return true;
				}
				if(z){
					radius2 = value; seg_height = radius - radius2;
					GeneralEditor.cyl6_z.apply(seg_height);
					return true;
				}
			}
			case "cyl1":{
				if(x){ segments = (int)value; return true; }
				if(y){ direction = (int)value; return true; }
				if(z){ seglimit = (int)value; if(seglimit > segments) seglimit = segments; return true; }
			}
			case "cyl2":{
				if(x){ base = value; return true; }
				if(y){ top = value; return true; }
				if(z){ return false; }//topangle = value; if(topangle < -360) topangle = -360; if(topangle > 360) topangle = 360; return false; }
			}
			case "cyl3":{
				if(x){ topoff.xCoord = value; return true; }
				if(y){ topoff.yCoord = value; return true; }
				if(z){ topoff.zCoord = value; return true; }
			}
			case "cyl4":{
				if(x){ bools[0] = value == 1; return true; }
				if(y){ bools[1] = value == 1; return true; }
				if(z){ return false; }
			}
			case "cyl5":{
				if(x){ bools[2] = value == 1; return true; }
				if(y){ bools[3] = value == 1; return true; }
				if(z){ return false; }
			}
			case "cyl6":{
				if(x){ radial = value == 1; return true; }
				if(y){ seg_width = (int)value; return true; }
				if(z){ seg_height = (int)value; return true; }
			}
			case "cyl7":{
				if(x){ toprot.xCoord = value; return true; }
				if(y){ toprot.yCoord = value; return true; }
				if(z){ toprot.zCoord = value; return true; }
			}
			default: return false;
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("radius", radius);
		if(radius2 != 0f){
			obj.addProperty("radius2", radius2);
		}
		obj.addProperty("length", length);
		obj.addProperty("segments", segments);
		if(seglimit != 0){
			obj.addProperty("seglimit", seglimit);
		}
		obj.addProperty("direction", direction);
		obj.addProperty("basescale", base);
		obj.addProperty("topscale", top);
		if(topoff.xCoord != 0f) obj.addProperty("top_offset_x", topoff.xCoord);
		if(topoff.yCoord != 0f) obj.addProperty("top_offset_y", topoff.yCoord);
		if(topoff.zCoord != 0f) obj.addProperty("top_offset_z", topoff.zCoord);
		if(toprot.xCoord != 0f) obj.addProperty("top_rotation_x", toprot.xCoord);
		if(toprot.yCoord != 0f) obj.addProperty("top_rotation_y", toprot.yCoord);
		if(toprot.zCoord != 0f) obj.addProperty("top_rotation_z", toprot.zCoord);
		boolean bool = false; for(boolean bl : bools) if(bl) bool = true;
		if(bool){
			JsonArray array = new JsonArray();
			for(boolean bl : bools) array.add(bl);
			obj.add("faces_off", array);
		}
		//if(topangle != 0f) obj.addProperty("top_angle", topangle);
		if(radial){
			obj.addProperty("radialtex", radial);
			obj.addProperty("seg_width", seg_width);
			obj.addProperty("seg_height", seg_height);
		}
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(){
		float radius = this.radius < 1 ? 1 : this.radius;
		float length = this.length < 1 ? 1 : this.length;
		float tx = 0/*textureX*/, ty = 0/*textureY*/, qrad = radius / 2, rad = radius * 2, rad2 = rad + rad;
		float[][][] vecs = new float[radius2 != 0f ? seglimit > 0 && seglimit < segments ? 20 : 18 : 10][][];
		float height = radial ? seg_height + seg_height : rad;
		if(radial){
			vecs[0] = new float[][]{
				new float[]{ tx, ty },
				new float[]{ tx + (seg_width * segments), ty + height / 2 }
			};
			vecs[1] = new float[][]{
				new float[]{ tx, ty + seg_height },
				new float[]{ tx + (seg_width * segments), ty + height }
			};
		}
		else{
			vecs[0] = new float[][]{
				new float[]{ tx, ty },
				new float[]{ tx + rad, ty + rad }
			};
			vecs[1] = new float[][]{
				new float[]{ tx + rad, ty},
				new float[]{ tx + rad2, ty + rad }
			};
		}
		for(int i = 0; i < 8; i++){
			vecs[2 + i] = new float[][]{
				new float[]{ tx + (qrad * i), ty + height },
				new float[]{ tx + (qrad * (i + 1)), ty + height + length }
			};
		}
		if(radius2 != 0f){
			for(int i = 0; i < 8; i++){
				vecs[10 + i] = new float[][]{
					new float[]{ tx + (qrad * i), ty + height + length },
					new float[]{ tx + (qrad * (i + 1)), ty + height + length + length }
				};
			}
			if(seglimit > 0 && seglimit < segments){
				vecs[18] = new float[][]{
					new float[]{ tx + rad2, ty + height },
					new float[]{ tx + rad2 + (radius - radius2), ty + height + length }
				};
				vecs[19] = new float[][]{
					new float[]{ tx + rad2, ty + height + length },
					new float[]{ tx + rad2 + (radius - radius2), ty + height + length + length }
				};
			}
		}
		return vecs;
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == ShapeType.CYLINDER ? this.clone() : null;
	}

	public boolean usesTopRotation(){
		return toprot.xCoord != 0f || toprot.yCoord != 0f || toprot.zCoord != 0f;
	}

	@Override
	public String[] getTexturableFaceIDs(){
		return faces;
	}
	
}
