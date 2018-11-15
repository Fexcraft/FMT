package net.fexcraft.app.fmt.wrappers;

import java.util.ArrayList;

import net.fexcraft.lib.common.math.RGB;

public class TurboList extends ArrayList<PolygonWrapper> {
	
	private static final long serialVersionUID = -6386049255131269547L;
	
	public String id; public RGB color;
	private boolean rotXb, rotYb, rotZb;
	//private float rotX, rotY, rotZ, posX, posY, posZ;//FMR stuff
	public boolean visible = true, minimized, selected;
	public int tempheight;
	
	public TurboList(String id){
		this.id = id;
	}

	public void render(){
		if(!visible) return;
		if(color != null) color.glColorApply();
		this.forEach(elm -> elm.render(rotXb, rotYb, rotZb));
		if(color != null) RGB.glColorReset();
	}

	public void renderLines(){
		if(!visible) return;
		this.forEach(elm -> elm.renderLines(rotXb, rotYb, rotZb));
	}

	@Override
	public PolygonWrapper get(int id){
		return id >= size() || id < 0 ? null : super.get(id);
	}
	
	@Override
	public boolean add(PolygonWrapper poly){
		poly.setList(this); return super.add(poly);
	}

}
