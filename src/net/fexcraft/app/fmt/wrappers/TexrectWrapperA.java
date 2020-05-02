package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;

public class TexrectWrapperA extends TexrectWrapperB {

	public TexrectWrapperA(GroupCompound compound){
		super(compound);
		for(int i = 0; i < 6; i++){
			texcor[i] = new float[8];
			texcor[i][0] = 0;
			texcor[i][1] = 0;
			texcor[i][2] = 0;
			texcor[i][3] = 0;
			texcor[i][4] = 0;
			texcor[i][5] = 0;
			texcor[i][6] = 0;
			texcor[i][7] = 0;
		}
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		TexrectWrapperA wrapper = new TexrectWrapperA(compound);
		wrapper.cor0 = new Vec3f(cor0);
		wrapper.cor1 = new Vec3f(cor1);
		wrapper.cor2 = new Vec3f(cor2);
		wrapper.cor3 = new Vec3f(cor3);
		wrapper.cor4 = new Vec3f(cor4);
		wrapper.cor5 = new Vec3f(cor5);
		wrapper.cor6 = new Vec3f(cor6);
		wrapper.cor7 = new Vec3f(cor7);
		wrapper.size = new Vec3f(size);
		wrapper.texcor = copy(texcor);
		return wrapper;
	}

	private float[][] copy(float[][] in){
		float[][] out = new float[6][];
		for(int i = 0; i < 6; i++){
			out[i] = new float[8];
			out[i][0] = in[i][0];
			out[i][1] = in[i][1];
			out[i][2] = in[i][2];
			out[i][3] = in[i][3];
			out[i][4] = in[i][4];
			out[i][5] = in[i][5];
			out[i][6] = in[i][6];
			out[i][7] = in[i][7];
		}
		return out;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.TEXRECT_A;
	}

	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		if(id.startsWith("texpos") && id.contains(":")){
			String[] str = id.replace("texpos", "").split(":");
			int i = Integer.parseInt(str[0]), j = Integer.parseInt(str[1]);
			return texcor[i][j];
		}
		return super.getFloat(id, x, y, z);
	}

	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		if(id.startsWith("texpos") && id.contains(":")){ // Print.console(id, x, value);
			String[] str = id.replace("texpos", "").split(":");
			int i = Integer.parseInt(str[0]), j = Integer.parseInt(str[1]);
			texcor[i][j] = value;
			return true;
		}
		return false;
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		JsonArray texpos = new JsonArray(), array = null;
		for(int i = 0; i < 6; i++){
			array = new JsonArray();
			array.add(this.texcor[i][0]);
			array.add(this.texcor[i][1]);
			array.add(this.texcor[i][2]);
			array.add(this.texcor[i][3]);
			array.add(this.texcor[i][4]);
			array.add(this.texcor[i][5]);
			array.add(this.texcor[i][6]);
			array.add(this.texcor[i][7]);
			texpos.add(array);
		}
		obj.add("texpos", texpos);
		return obj;
	}

	@Override
	public float[][][] newTexturePosition(){
		// float tx = 0, ty = 0, w = size.xCoord, h = size.yCoord, d = size.zCoord;
		/*
		 * float[][][] vecs = new float[6][][]; for(int i = 0; i < 6; i++){ vecs[i] = new float[][]{ new float[]{ texcor[i][0], texcor[i][1] }, new float[]{ texcor[i][4], texcor[i][5] } }; } return vecs;
		 */
		return new float[0][][];
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		if(!type.getConversionGroup().equals(this.getType().getConversionGroup())) return null;
		if(type == ShapeType.QUAD){
			QuadWrapper box = new QuadWrapper(compound);
			box.size = new Vec3f(size);
			return copyTo(box, true);
		}
		if(type == ShapeType.BOX){
			BoxWrapper box = new BoxWrapper(compound);
			box.size = new Vec3f(size);
			return copyTo(box, true);
		}
		ShapeboxWrapper wrapper = null;
		switch(type){
			case TEXRECT_B:
				wrapper = new TexrectWrapperB(compound);
				break;
			case SHAPEBOX:
				wrapper = new ShapeboxWrapper(compound);
				break;
			default:
				return null;
		}
		wrapper.size = new Vec3f(size);
		wrapper.setCoords(cor0, cor1, cor2, cor3, cor4, cor5, cor6, cor7);
		return copyTo(wrapper, true);
	}

}
