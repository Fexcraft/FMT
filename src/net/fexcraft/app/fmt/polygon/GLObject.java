package net.fexcraft.app.fmt.polygon;

import net.fexcraft.lib.frl.GLO;

public class GLObject extends GLO<GLObject> {

	public float[] pickercolor, polycolor, linecolor;
	public Polygon polygon;
	public boolean textured;
	public boolean grouptex;
	public GPUData[] gpu = new GPUData[2];
	
	public GLObject(){
		gpu[0] = new GPUData();
		gpu[1] = new GPUData();
		
	}
	
	public static class GPUData {
		
	    public int uvss;
	    public int normss;
	    public int colorss;
	    public int lightss;
	    public float[] verts;
	    public float[] uvs;
	    public float[] norms;
	    public float[] colors;
	    public float[] lights;
	    public int size;
	    public Integer glid;
		
	}

}
