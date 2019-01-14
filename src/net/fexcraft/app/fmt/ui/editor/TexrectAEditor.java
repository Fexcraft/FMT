package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class TexrectAEditor extends Editor {

	public TexrectAEditor(){
		super("texrecta_editor");
		String[] vals = new String[]{ "texpos0", "texpos0", "texpos1", "texpos1", "texpos2", "texpos2", "texpos3", "texpos3", "texpos4", "texpos4", "texpos5", "texpos5" };
		for(int r = 0; r < vals.length; r++){
			for(int i = 0; i < 4; i++){
				int k = 70, j = 75; String id = vals[r] + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y");
				RGB rgb = r == 2 || r == 3 || r == 6 || r == 7 || r == 10 || r == 11 ? new RGB(204, 97, 91) : new RGB(102, 102, 173);
				this.elements.put(id, new TextField(this, id, k, 4 + (j * i), 30 + (r * 50)).setAsNumberfield(0, Integer.MAX_VALUE, true).setColor("inactive", rgb).setLevel(-1));
			}
		}
		this.addMultiplicator(630);
	}
	
	private static final String[] faces = new String[]{ "Front", "Back", "Up", "Down", "Right", "Left" };
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		for(int i = 0; i < 12; i++){
			font.drawString(4, 40 + (i * 50), faces[i / 2] + " [" + (i % 2 == 0 ? "x-pos" : "y-pos") + " | TR, TL, BL, BR]", Color.black);
		}
		font.drawString(4, 640, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){
		return new String[]{ "general_editor", "shapebox_editor", "texrecta_editor", "group_editor", "model_editor", "texture_editor" };
	}

}
