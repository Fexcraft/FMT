package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.OldTextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class CylinderEditor extends Editor {

	public CylinderEditor(){
		super("cylinder_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		String[] vals = new String[]{ "cyl0", "cyl1", "cyl2", "cyl3" }, xyz = new String[]{ "x", "y", "z" };
		for(int r = 0; r < vals.length; r++){
			for(int i = 0; i < 3; i++){
				int k = 70, j = k + 12 + 12 + 4; final int rr = r, ii = i;
				this.elements.put(vals[r] + xyz[i] + "-", new Button(this, vals[r] + xyz[i] + "-", 12, 26, 4 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						FMTB.MODEL.updateValue((OldTextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" < ", true).setTexture("ui/background").setLevel(-1).setEnabled(r == 3 ? true : ii != 2));
				OldTextField field = new OldTextField(this, vals[r] + xyz[i], k, 16 + (j * i), 30 + (r * 50)).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
				if(ii == 0){
					if(r == 0){ field.setAsNumberfield(0.01f, Integer.MAX_VALUE, true); field.applyChange(1f); }
					if(r == 1){ field.setAsNumberfield(3, Integer.MAX_VALUE, true); field.applyChange(8); }
					if(r == 2){ field.setAsNumberfield(0f, Integer.MAX_VALUE, true); field.applyChange(1); }
					if(r == 3){ field.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true); field.applyChange(0); }
				}
				if(ii == 1){
					if(r == 0){ field.setAsNumberfield(0.01f, Integer.MAX_VALUE, true); field.applyChange(1); }
					if(r == 1){ field.setAsNumberfield(0, 5, true); field.applyChange(ModelRendererTurbo.MR_FRONT); }
					if(r == 2){ field.setAsNumberfield(0f, Integer.MAX_VALUE, true); field.applyChange(1); }
					if(r == 3){ field.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true); field.applyChange(0); }
				}
				if(ii == 2){
					if(r == 0){ field.setAsNumberfield(0, Integer.MAX_VALUE, true); field.applyChange(0f); }
					if(r == 1){ field.setAsNumberfield(0, Integer.MAX_VALUE, true); field.applyChange(0); }
					if(r == 2){ field.enabled = false; }
					if(r == 3){ field.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true); field.applyChange(0); }
				}
				this.elements.put(vals[r] + xyz[i], field.setLevel(-1));
				this.elements.put(vals[r] + xyz[i] + "+", new Button(this, vals[r] + xyz[i] + "+", 12, 26, k + 16 + (j * i), 30 + (r * 50), rgb){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						FMTB.MODEL.updateValue((OldTextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" > ", true).setTexture("ui/background").setLevel(-1).setEnabled(r == 3 ? true : ii != 2));
			}
		}
		this.addMultiplicator(230);
	}
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		FontRenderer.drawText("Radius / Length / R2", 4, 40, 1);
		FontRenderer.drawText("Segments / Direction / SL", 4, 90, 1);
		FontRenderer.drawText("Base Scale / Top Scale", 4, 140, 1);
		FontRenderer.drawText("Top Offset (x/y/z)", 4, 190, 1);
		FontRenderer.drawText("Multiplicator/Rate", 4, 240, 1);
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){
		return new String[]{ "general_editor", "cylinder_editor", "group_editor", "model_editor", "texture_editor" };
	}

}
