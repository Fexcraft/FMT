package net.fexcraft.app.fmt.wrappers;

import java.util.ArrayList;

import net.fexcraft.lib.common.math.RGB;

public class TurboList extends ArrayList<PolygonWrapper> {
	
	private static final long serialVersionUID = -6386049255131269547L;
	
	public String id; public RGB color;
	private boolean rotXb, rotYb, rotZb;
	//private float rotX, rotY, rotZ, posX, posY, posZ;//FMR stuff
	public boolean visible = true, minimized;
	public int tempheight;
	
	public TurboList(String id){
		this.id = id;
	}

	public void render(){
		if(!visible) return;
		if(color != null) color.glColorApply();
		this.forEach(elm -> elm.render(rotXb, rotYb, rotZb));
		//for(int i = 0; i < size(); i++){ get(i).render(isSelected(i), rotXb, rotYb, rotZb); }
		if(color != null) RGB.glColorReset();
	}
	
	/*private boolean isSelected(int elm){
		for(Selection sel : FMTB.MODEL.getSelected()){ if(sel.group.equals(id) && sel.element == elm) return true; }
		return false;
	}*/

	@Override
	public PolygonWrapper get(int id){
		return id >= size() || id < 0 ? null : super.get(id);
	}

}
