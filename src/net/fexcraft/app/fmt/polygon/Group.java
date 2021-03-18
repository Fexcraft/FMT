package net.fexcraft.app.fmt.polygon;

import java.util.ArrayList;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.lib.common.math.RGB;

public class Group extends ArrayList<Polygon> {
	
	public String id;
	public boolean minimized, selected, visible;
	public RGB color = RGB.WHITE.copy();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();

	public Group(String key){
		this.id = key;
	}

}
