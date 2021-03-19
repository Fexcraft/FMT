package net.fexcraft.app.fmt.polygon;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.MRTRenderer;
import net.fexcraft.app.fmt.utils.MRTRenderer.DrawMode;
import net.fexcraft.lib.common.math.RGB;

public class Group extends ArrayList<Polygon> {
	
	public String id;
	public final Model model;
	public boolean minimized, selected, visible;
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

	public void recompile(){
		for(Polygon poly : this){
			poly.recompile();
		}
	}

	private void bindtex(){
		if(texgroup != null) texgroup.texture.bind();
		else model.bindtex();
	}

	public void render(DrawMode mode){
		if(!visible) return;
		bindtex();
		if(mode == DrawMode.LINES){
			MRTRenderer.mode(selected ? DrawMode.LINES : DrawMode.SELLINES);
			for(Polygon poly : this){
				if(!selected && poly.selected) MRTRenderer.mode(DrawMode.SELLINES);
				poly.turbo.render();
				if(poly.selected && !selected) MRTRenderer.mode(DrawMode.LINES);
			}
		}
		else{
			MRTRenderer.mode(mode);
			for(Polygon poly : this){
				poly.turbo.render();
			}
		}
	}

}
