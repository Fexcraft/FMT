package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.RGB;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.fmr.ModelCompound;

public class CylinderEditor extends Editor {

	public CylinderEditor(){
		super("cylinder_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		String[] vals = new String[]{ "cyl0", "cyl1", "cyl2"}, xyz = new String[]{ "x", "y", "z" };
		for(int r = 0; r < vals.length; r++){
			for(int i = 0; i < 3; i++){
				int k = 70, j = k + 12 + 12 + 4; final int rr = r, ii = i;
				this.elements.put(vals[r] + xyz[i] + "-", new Button(this, vals[r] + xyz[i] + "-", 12, 26, 4 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						return ii == 2 ? false : FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id);
					}
				}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
				TextField field = new TextField(this, vals[r] + xyz[i], k, 16 + (j * i), 30 + (r * 50)).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
				if(ii == 0){
					if(r == 0){ field.setAsNumberfield(0.01f, Integer.MAX_VALUE, true); field.applyChange(1f); }
					if(r == 1){ field.setAsNumberfield(4, Integer.MAX_VALUE, true); field.applyChange(8); }
					if(r == 3){ field.setAsNumberfield(1f, Integer.MAX_VALUE, true); field.applyChange(1); }
				}
				if(ii == 1){
					if(r == 0){ field.setAsNumberfield(0.01f, Integer.MAX_VALUE, true); field.applyChange(1); }
					if(r == 1){ field.setAsNumberfield(0, 5, true); field.applyChange(ModelCompound.DIR_FRONT); }
					if(r == 2){ field.setAsNumberfield(1f, Integer.MAX_VALUE, true); field.applyChange(1); }
				}
				if(ii == 2) field.enabled = false;
				this.elements.put(vals[r] + xyz[i], field.setLevel(-1));
				this.elements.put(vals[r] + xyz[i] + "+", new Button(this, vals[r] + xyz[i] + "+", 12, 26, k + 16 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						return ii == 2 ? false : FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id);
					}
				}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
			}
		}
		this.addMultiplicator(180);
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		font.drawString(4,  40, "Radius / Length", Color.black);
		font.drawString(4,  90, "Segments / Direction", Color.black);
		font.drawString(4, 140, "Base Scale / Top Scale", Color.black);
		font.drawString(4, 190, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

}
