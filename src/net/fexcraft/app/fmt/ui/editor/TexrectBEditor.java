package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class TexrectBEditor extends Editor {

	public TexrectBEditor(){
		super("texrectb_editor");
		//
		String[] vals = new String[]{ "texpos0", "texpos1", "texpos2", "texpos3", "texpos4", "texpos5" };
		for(int r = 0; r < vals.length; r++){
			for(int i = 0; i < 4; i++){
				int k = 70, j = 75; String id = vals[r] + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y"); 
				this.elements.put(id, new TextField(this, id, k, 4 + (j * i), 30 + (r * 50)).setAsNumberfield(0, Integer.MAX_VALUE, true).setLevel(-1));
			}
		}
		this.addMultiplicator(330);
	}
	
	private static final String[] faces = new String[]{ "Front", "Back", "Up", "Down", "Right", "Left" };//To be adjusted yet.
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		for(int i = 0; i < 6; i++){
			font.drawString(4, 40 + (i * 50), faces[i] + " [start x/y, end x/y]", Color.black);
		}
		font.drawString(4, 340, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){
		return new String[]{ "general_editor", "shapebox_editor", "texrectb_editor", "group_editor", "model_editor", "texture_editor" };
	}

}
