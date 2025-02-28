package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;

import java.util.ArrayList;
import java.util.List;

import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.update.UpdateEvent.GroupRenamed;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonRemoved;
import net.fexcraft.app.fmt.utils.Selector;
import net.fexcraft.lib.script.elm.FltElm;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.lib.common.math.RGB;

public class Group extends ArrayList<Polygon> {
	
	public String id;
	public final Model model;
	public boolean minimized, selected, visible = true;
	public List<Animation> animations = new ArrayList<>();
	public RGB color = RGB.WHITE.copy();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public String pivot;
	//

	public Group(Model model, String key, String pid){
		this.model = model;
		this.id = key;
		pivot = pid == null ? model.getRootPivot().id : pid;
	}
	
	@Override
	public boolean add(Polygon poly){
		if(poly == null) return false;
		return super.add(poly) && poly.group(this);
	}
	
	@Override
	public boolean remove(Object obj){
		return obj instanceof Polygon ? remove(obj) : false;
	}
	
	@Override
	public Polygon remove(int index){
		Polygon poly = super.remove(index);
		poly.group(null);
		if(poly.selected) model.select(poly);
		update(new PolygonRemoved(this, poly));
		return poly;
	}
	
	public boolean remove(Polygon poly){
		if(poly == null) return false;
		if(super.remove(poly)){
			poly.group(null);
			if(poly.selected) model.select(poly);
			update(new PolygonRemoved(this, poly));
			return true;
		}
		return false;
	}

	public void recompile(){
		for(Polygon poly : this){
			poly.recompile();
		}
	}

	private void bindtex(){
		if(texgroup != null) texgroup.texture.bind();
		else if(texhelper != null) TextureManager.bind(texhelper);
		else model.bindtex();
	}

	public void render(DrawMode mode, FltElm alpha){
		if(!visible) return;
		bindtex();
		for(Animation animation : animations) animation.pre(this, mode, alpha);
		if(mode == DrawMode.LINES){
			PolyRenderer.mode(selected ? DrawMode.SELLINES : DrawMode.LINES);
			for(Polygon poly : this){
				if(!selected && poly.selected) PolyRenderer.mode(DrawMode.SELLINES);
				poly.render(alpha);
				if(poly.selected && !selected) PolyRenderer.mode(DrawMode.LINES);
			}
		}
		else{
			PolyRenderer.mode(mode);
			for(Polygon poly : this){
				if(poly.visible) poly.render(alpha);
			}
		}
		for(Animation animation : animations) animation.pst(this, mode, alpha);
	}

	public void renderPicking(){
		if(!visible) return;
		for(Polygon poly : this){
			if(poly.visible) poly.renderPicking();
		}
	}

	public void renderVertexPicking(boolean preview){
		if(!visible) return;
		for(Polygon poly : this){
			if(poly.visible && (!preview || poly.selected)) poly.renderVertexPicking();
		}
	}
	
	@Override
	public boolean equals(Object other){
		return other == this;
	}

	public void reid(String name){
		this.id = name;
		UpdateHandler.update(new GroupRenamed(this, name));
	}

	public String exportId(){
		return exportId(false);
	}

    public String exportId(boolean an){
		String str = id.trim().replace(" ", "_").replaceAll("[^a-zA-Z0-9 _]", "");
		return an || !(str.charAt(0) >= '0' && str.charAt(0) <= '9') ? str : "g" + str;
    }

	@Override
	public String toString(){
		return "Group([" + id + "], " + size() + (selected ? ", selected" : "") + ")";
	}

	public Polygon get(String str){
		for(Polygon poly : this){
			if(poly.name(true) != null && poly.name(true).equals(str)) return poly;
		}
		return null;
	}

}
