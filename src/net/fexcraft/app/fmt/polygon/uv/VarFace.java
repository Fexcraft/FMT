package net.fexcraft.app.fmt.polygon.uv;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VarFace implements Face {

	public int color;
	public int idx;

	public VarFace(int var, int c){
		idx = var;
		color = c;
	}

	@Override
	public int index(){
		return idx;
	}

	@Override
	public String id(){
		return "var-" + idx;
	}

}
