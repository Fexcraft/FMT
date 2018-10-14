package net.fexcraft.lib.tmt;

import net.fexcraft.lib.fmr.TexturedPolygon;
import net.fexcraft.lib.fmr.TexturedVertex;

public class Shape3D {
	
	public TexturedVertex[] vertices;
	public TexturedPolygon[] faces;
	
	public Shape3D(TexturedVertex[] verts, TexturedPolygon[] poly){
		vertices = verts; faces = poly;
	}
	
}
