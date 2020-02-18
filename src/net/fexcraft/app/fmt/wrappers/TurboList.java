package net.fexcraft.app.fmt.wrappers;

import java.util.ArrayList;

import net.fexcraft.app.fmt.ui.tree.SubTreeGroup;
import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.lib.common.math.RGB;

public class TurboList extends ArrayList<PolygonWrapper> {
	
	private static final long serialVersionUID = -6386049255131269547L;
	
	public String id; public RGB color;
	private boolean rotXb, rotYb, rotZb;
	//private float rotX, rotY, rotZ, posX, posY, posZ;//FMR stuff
	public boolean visible = true, minimized, aminimized, selected;
	public int tempheight, textureX = 256, textureY = 256, textureS = 1;
	private String texture;
	public ArrayList<Animation> animations = new ArrayList<>();
	//
	public TreeGroup button;
	public SubTreeGroup pbutton;
	
	public TurboList(String id){
		this.id = id; button = new TreeGroup(Trees.polygon, this);
	}

	public void render(boolean aplcol){
		if(!visible) return;
		if(color != null && aplcol) color.glColorApply();
		if(Settings.animate() && animations.size() > 0) for(Animation ani : animations) ani.pre(this);
		this.forEach(elm -> elm.render(rotXb, rotYb, rotZb));
		if(Settings.animate() && animations.size() > 0) for(Animation ani : animations) ani.post(this);
		if(color != null && aplcol) RGB.glColorReset();
	}

	public void renderLines(){
		if(!visible) return;
		this.forEach(elm -> elm.renderLines(rotXb, rotYb, rotZb));
	}

	public void renderPicking(){
		if(!visible) return;
		this.forEach(elm -> elm.renderPicking(rotXb, rotYb, rotZb));
	}

	@Override
	public PolygonWrapper get(int id){
		return id >= size() || id < 0 ? null : super.get(id);
	}
	
	@Override
	public boolean add(PolygonWrapper poly){
		poly.setList(this); boolean added = super.add(poly);
		poly.button.setRoot(button); return added;
	}
	
	@Override
	public void clear(){
		super.clear(); button.recalculateSize();
	}
	
	@Override
	public PolygonWrapper remove(int index){
		PolygonWrapper wrapper = super.remove(index);
		button.remove(wrapper.button); button.recalculateSize(); return wrapper;
	}
	
	@Override
	public boolean remove(Object wrapper){
		boolean bool = super.remove(wrapper);
		if(bool && wrapper instanceof PolygonWrapper){
			button.remove(((PolygonWrapper)wrapper).button);
			button.recalculateSize();
		} return bool;
	}
	
	public String getGroupTexture(){
		return texture;
	}
	
	public void setTexture(String string, int sizex, int sizey){
		this.texture = string; this.textureX = sizex; this.textureY = sizey; this.textureS = 1;
	}

	public String getApplicableTexture(GroupCompound compound){
		return texture == null ? compound.texture == null ? "blank" : compound.texture : texture;
	}

	public void recompile(){
		for(PolygonWrapper wrapper : this) wrapper.recompile();
	}

}
