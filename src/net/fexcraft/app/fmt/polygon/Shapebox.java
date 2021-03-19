package net.fexcraft.app.fmt.polygon;

import org.joml.Vector3f;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.lib.tmt.BoxBuilder;

public class Shapebox extends Box {
	
	public Vector3f cor0;
	public Vector3f cor1;
	public Vector3f cor2;
	public Vector3f cor3;
	public Vector3f cor4;
	public Vector3f cor5;
	public Vector3f cor6;
	public Vector3f cor7;
	
	public Shapebox(Model model){
		super(model);
		cor0 = new Vector3f();
		cor1 = new Vector3f();
		cor2 = new Vector3f();
		cor3 = new Vector3f();
		cor4 = new Vector3f();
		cor5 = new Vector3f();
		cor6 = new Vector3f();
		cor7 = new Vector3f();
	}

	public Shapebox(Model model, JsonObject obj){
		super(model, obj);
		cor0 = Jsoniser.getVector(obj, "%s0", 0);
		cor1 = Jsoniser.getVector(obj, "%s1", 0);
		cor2 = Jsoniser.getVector(obj, "%s2", 0);
		cor3 = Jsoniser.getVector(obj, "%s3", 0);
		cor4 = Jsoniser.getVector(obj, "%s4", 0);
		cor5 = Jsoniser.getVector(obj, "%s5", 0);
		cor6 = Jsoniser.getVector(obj, "%s6", 0);
		cor7 = Jsoniser.getVector(obj, "%s7", 0);
	}

	@Override
	public Shape getShape(){
		return Shape.SHAPEBOX;
	}

	@Override
	protected void buildMRT(){
		BoxBuilder builder = new BoxBuilder(turbo).setOffset(off.x, off.y, off.z).setSize(size.x, size.y, size.z).removePolygons(sides);
		builder.setCorner(0, cor0.x, cor0.y, cor0.z);
		builder.setCorner(1, cor1.x, cor1.y, cor1.z);
		builder.setCorner(2, cor2.x, cor2.y, cor2.z);
		builder.setCorner(3, cor3.x, cor3.y, cor3.z);
		builder.setCorner(4, cor4.x, cor4.y, cor4.z);
		builder.setCorner(5, cor5.x, cor5.y, cor5.z);
		builder.setCorner(6, cor6.x, cor6.y, cor6.z);
		builder.setCorner(7, cor7.x, cor7.y, cor7.z);
		//TODO custom uv
		builder.build();
	}

}
