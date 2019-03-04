package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.FontRenderer.Text;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
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
						FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" < ", true).setTexture("ui/background").setLevel(-1).setEnabled(r == 3 ? true : ii != 2));
				TextField field = new TextField(this, vals[r] + xyz[i], k, 16 + (j * i), 30 + (r * 50)).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
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
						FMTB.MODEL.updateValue((TextField)this.parent.getElement(vals[rr] + xyz[ii]), this.id); return true;
					}
				}.setText(" > ", true).setTexture("ui/background").setLevel(-1).setEnabled(r == 3 ? true : ii != 2));
			}
		}
		this.addMultiplicator(230);
	}
	
	private Text RLR2 = new Text("Radius / Length / R2", RGB.GREEN);
	private Text SDSL = new Text("Segments / Direction / SL", RGB.BLUE);
	private Text BSTS = new Text("Base Scale / Top Scale", RGB.RED);
	private Text TOFF = new Text("Top Offset (x/y/z)", RGB.BLACK);
	private Text MULTI = new Text("Multiplicator/Rate", RGB.BLACK);
	
	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		FontRenderer.render(4, 40, RLR2);
		FontRenderer.render(4, 90, SDSL);
		FontRenderer.render(4, 140, BSTS);
		FontRenderer.render(4, 190, TOFF);
		FontRenderer.render(4, 240, MULTI);
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){
		return new String[]{ "general_editor", "cylinder_editor", "group_editor", "model_editor", "texture_editor" };
	}

}
