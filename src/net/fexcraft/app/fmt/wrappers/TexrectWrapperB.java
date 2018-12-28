package net.fexcraft.app.fmt.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class TexrectWrapperB extends ShapeboxWrapper {
	
	public float[][] texcor = new float[6][0];
	
	public TexrectWrapperB(GroupCompound compound){
		super(compound);
		for(int i = 0; i < 6; i++){
			texcor[i] = new float[4]; texcor[i][0] = 0; texcor[i][1] = 0; texcor[i][2] = 0; texcor[i][3] = 0;
		}
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		TexrectWrapperB wrapper = new TexrectWrapperB(compound);
		wrapper.cor0 = new Vec3f(cor0); wrapper.cor1 = new Vec3f(cor1); wrapper.cor2 = new Vec3f(cor2); wrapper.cor3 = new Vec3f(cor3);
		wrapper.cor4 = new Vec3f(cor4); wrapper.cor5 = new Vec3f(cor5); wrapper.cor6 = new Vec3f(cor6); wrapper.cor7 = new Vec3f(cor7);
		wrapper.size = new Vec3f(size); wrapper.texcor = copy(texcor); return wrapper;
	}
	
	private float[][] copy(float[][] in){
		float[][] out = new float[6][];
		for(int i = 0; i < 6; i++){
			out[i] = new float[4];
			out[i][0] = in[i][0];
			out[i][1] = in[i][1];
			out[i][2] = in[i][2];
			out[i][3] = in[i][3];
		} return out;
	}

	@Override
	protected ModelRendererTurbo newMRT(){
		return new ModelRendererTurbo(null, 0, 0, compound.textureX, compound.textureY)
			.addTexRect(off.xCoord, off.yCoord, off.zCoord, size.xCoord, size.yCoord, size.zCoord, 0,
				cor0.xCoord, cor0.yCoord, cor0.zCoord,
				cor1.xCoord, cor1.yCoord, cor1.zCoord,
				cor2.xCoord, cor2.yCoord, cor2.zCoord,
				cor3.xCoord, cor3.yCoord, cor3.zCoord,
				cor4.xCoord, cor4.yCoord, cor4.zCoord,
				cor5.xCoord, cor5.yCoord, cor5.zCoord,
				cor6.xCoord, cor6.yCoord, cor6.zCoord,
				cor7.xCoord, cor7.yCoord, cor7.zCoord, texcor)
			.setRotationPoint(pos.xCoord, pos.yCoord, pos.zCoord)
			.setRotationAngle(rot.xCoord, rot.yCoord, rot.zCoord);
	}

	@Override
	public ShapeType getType(){
		return ShapeType.TEXRECT_B;
	}
	
	@Override
	public float getFloat(String id, boolean x, boolean y, boolean z){
		if(id.startsWith("texpos") && !id.contains(":")){ //Print.console(id, x);
			String str = id.replace("texpos", ""); str = str.substring(0, str.length() - 1); int i = Integer.parseInt(str);
			int j = id.endsWith("s") ? 0 : 2; return texcor[i][j + (x ? 0 : 1)];
		} return super.getFloat(id, x, y, z);
	}
	
	@Override
	public boolean setFloat(String id, boolean x, boolean y, boolean z, float value){
		if(super.setFloat(id, x, y, z, value)) return true;
		if(id.startsWith("texpos") && !id.contains(":")){ //Print.console(id, x, value);
			int i = Integer.parseInt(id.replace("texpos", "").replace("s", "").replace("e", ""));
			int j = id.endsWith("s") ? 0 : 2; texcor[i][j + (x ? 0 : 1)] = value; return true;
		} return false;
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj = super.populateJson(obj, export);
		obj.addProperty("type", export ? "texrect" : getType().name().toLowerCase());
		Print.console(obj);
		JsonArray texpos = new JsonArray(), array = null;
		for(int i = 0; i < 6; i++){
			array = new JsonArray();
			array.add(this.texcor[i][0]);
			array.add(this.texcor[i][1]);
			array.add(this.texcor[i][2]);
			array.add(this.texcor[i][3]);
			texpos.add(array);
		}
		obj.add("texpos", texpos); return obj;
	}
	
	@Override
	protected float[][][] newTexturePosition(){
		//float tx = 0, ty = 0, w = size.xCoord, h = size.yCoord, d = size.zCoord;
		float[][][] vecs = new float[6][][];
		for(int i = 0; i < 6; i++){
			vecs[i] = new float[][]{
				new float[]{ texcor[i][0], texcor[i][1] },
				new float[]{ texcor[i][2], texcor[i][3] }
			};
		} return vecs;
	}
	
}
