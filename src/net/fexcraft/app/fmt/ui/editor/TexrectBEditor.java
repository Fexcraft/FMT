package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class TexrectBEditor extends Editor {

	public TexrectBEditor(){
		super("texrectb_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		String[] vals = new String[]{
			"texpos0s", "texpos0e", "texpos1s", "texpos1e",
			"texpos2s", "texpos2e", "texpos3s", "texpos3e",
			"texpos4s", "texpos4e", "texpos5s", "texpos5e" },
			xyz = new String[]{ "x", "y", "z" };
		for(int r = 0; r < vals.length; r++){
			for(int i = 0; i < 3; i++){
				int k = 70, j = k + 12 + 12 + 4; final int rr = r, ii = i;
				this.elements.put(vals[r] + xyz[i] + "-", new Button(this, vals[r] + xyz[i] + "-", 12, 26, 4 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" < ", true).setTexture("ui/background").setLevel(-1).setEnabled(i != 2));
				TextField field = new TextField(this, vals[r] + xyz[i], k, 16 + (j * i), 30 + (r * 50)).setAsNumberfield(0, Integer.MAX_VALUE, true);
				this.elements.put(vals[r] + xyz[i], field.setLevel(-1).setEnabled(i != 2));
				this.elements.put(vals[r] + xyz[i] + "+", new Button(this, vals[r] + xyz[i] + "+", 12, 26, k + 16 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" > ", true).setTexture("ui/background").setLevel(-1).setEnabled(i != 2));
			}
		}
		this.addMultiplicator(630);
	}
	
	private static final String[] faces = new String[]{ "Front", "Back", "Up", "Down", "Right", "Left" };//To be adjusted yet.
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		for(int i = 0; i < 12; i++){
			int j = i % 2;
			font.drawString(4, 40 + (i * 50), faces[i / 2] + " [" + (j == 0 ? "start" : "end") + "]", Color.black);
		}
		font.drawString(4, 640, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

}
