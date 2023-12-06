package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;

import java.util.ArrayList;

import net.fexcraft.app.fmt.update.UpdateEvent.GroupRenamed;
import net.fexcraft.app.fmt.update.UpdateEvent.PolygonRemoved;
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
	public RGB color = RGB.WHITE.copy();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public boolean joined_polygons;
	//

	public Group(Model model, String key){
		this.model = model;
		this.id = key;
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
		else model.bindtex();
	}

	public void render(DrawMode mode, FltElm alpha){
		if(!visible) return;
		bindtex();
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
	}

	public void renderPicking(){
		if(!visible) return;
		for(Polygon poly : this){
			if(poly.visible) poly.renderPicking();
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

}
