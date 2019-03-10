package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.OldTextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class ModelEditor extends Editor {
	
	private static final int[] accepted = new int[]{ 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192 };

	public ModelEditor(){
		super("model_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		for(int i = 0; i < 3; i++){
			final int j = i;
			this.elements.put("pos" + xyz[i] + "-", new Button(this, "pos" + xyz[i] + "-", 12, 26, 4 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updatePos(j, false); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
			this.elements.put("pos" + xyz[i], new OldTextField(this, "pos" + xyz[i], 70, 16 + (98 * i), 30){
				@Override public void updateNumberField(){ updatePos(this, j, null); }
				@Override protected boolean processScrollWheel(int wheel){ return updatePos(j, wheel > 0); }
			}.setAsNumberfield(0, 255, true).setLevel(-1));
			this.elements.put("pos" + xyz[i] + "+", new Button(this, "pos" + xyz[i] + "+", 12, 26, 86 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updatePos(j, true); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
			//
			this.elements.put("rot" + xyz[i] + "-", new Button(this, "rot" + xyz[i] + "-", 12, 26, 4 + (98 * i), 80, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRot(j, false); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
			this.elements.put("rot" + xyz[i], new OldTextField(this, "rot" + xyz[i], 70, 16 + (98 * i), 80){
				@Override public void updateNumberField(){ updateRot(this, j, null); }
				@Override protected boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
			}.setAsNumberfield(8, 4096, true).setLevel(-1));
			this.elements.put("rot" + xyz[i] + "+", new Button(this, "rot" + xyz[i] + "+", 12, 26, 86 + (98 * i), 80, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRot(j, true); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
			//
			this.elements.put("tex" + xyz[i] + "-", new Button(this, "tex" + xyz[i] + "-", 12, 26, 4 + (98 * i), 130, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateTexSize(j, false); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1).setEnabled(i != 2));
			this.elements.put("tex" + xyz[i], new OldTextField(this, "tex" + xyz[i], 70, 16 + (98 * i), 130){
				@Override public void updateNumberField(){ updateRot(this, j, null); }
				@Override protected boolean processScrollWheel(int wheel){ return updateTexSize(j, wheel > 0); }
			}.setAsNumberfield(8, 4096, true).setLevel(-1).setEnabled(i != 2));
			this.elements.put("tex" + xyz[i] + "+", new Button(this, "tex" + xyz[i] + "+", 12, 26, 86 + (98 * i), 130, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateTexSize(j, true); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1).setEnabled(i != 2));
		}
		//
		this.elements.put("modelname", new OldTextField(this, "modelname", 294, 4, 180) {
			@Override public void updateTextField(){ if(FMTB.MODEL == null) return; FMTB.get().setTitle(FMTB.MODEL.name = this.getTextValue()); }
		}.setText("null", true).setLevel(-1));
		//
		this.addMultiplicator(230);
	}
	
	protected boolean updateTexSize(int axis, Boolean positive){
		return updateTexSize(null, axis, positive);
	}
	
	protected boolean updateTexSize(OldTextField field, int axis, Boolean positive){
		if(FMTB.MODEL == null) return true; if(field == null) field = this.getField("tex" + xyz[axis]);
		int index = getIndex(field.getIntegerValue());
		if(positive && index < (accepted.length - 1)) field.applyChange(accepted[index + 1]);
		else if(!positive && index > 0) field.applyChange(accepted[index - 1]);
		//
		FMTB.MODEL.textureX = this.getField("texx").getIntegerValue();
		FMTB.MODEL.textureY = this.getField("texy").getIntegerValue();
		TextureUpdate.updateSizes(); return true;
	}
	
	private int getIndex(int val){
		for(int i = 0; i < accepted.length; i++){
			if(val == accepted[i]) return i;
		} return 0;
	}

	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(OldTextField field, int axis, Boolean positive){
		if(FMTB.MODEL == null) return true;
		if(field == null) field = this.getField("pos" + xyz[axis]);
		if(FMTB.MODEL.pos == null) FMTB.MODEL.pos = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{ FMTB.MODEL.pos.xCoord += am; field.applyChange(FMTB.MODEL.pos.xCoord); break; }
			case 1:{ FMTB.MODEL.pos.yCoord += am; field.applyChange(FMTB.MODEL.pos.yCoord); break; }
			case 2:{ FMTB.MODEL.pos.zCoord += am; field.applyChange(FMTB.MODEL.pos.zCoord); break; }
		}
		return true;
	}
	
	protected boolean updateRot(int axis, Boolean positive){
		return updateRot(null, axis, positive);
	}
	
	protected boolean updateRot(OldTextField field, int axis, Boolean positive){
		GroupCompound compound = FMTB.MODEL; if(compound == null) return true;
		if(field == null) field = this.getField("rot" + xyz[axis]);
		if(compound.rot == null) compound.rot = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		switch(axis){
			case 0:{
				compound.rot.xCoord += am;
				if(compound.rot.xCoord > 360) compound.rot.xCoord = 360;
				if(compound.rot.xCoord < -360) compound.rot.xCoord = -360;
				field.applyChange(compound.rot.xCoord);
				break;
			}
			case 1:{
				compound.rot.yCoord += am;
				if(compound.rot.yCoord > 360) compound.rot.yCoord = 360;
				if(compound.rot.yCoord < -360) compound.rot.yCoord = -360;
				field.applyChange(compound.rot.yCoord);
				break;
			}
			case 2:{
				compound.rot.zCoord += am;
				if(compound.rot.zCoord > 360) compound.rot.zCoord = 360;
				if(compound.rot.zCoord < -360) compound.rot.zCoord = -360;
				field.applyChange(compound.rot.zCoord);
				break;
			}
		}
		return true;
	}

	@Override
	public void renderSelf(int rw, int rh){
		super.renderSelf(rw, rh); TextureManager.unbind();
		/*font.drawString(4, 40, "Position Offset", Color.black);
		font.drawString(4, 90, "Rotation Offset", Color.black);
		font.drawString(4, 140, "Texture Size", Color.black);
		font.drawString(4, 190, "Model Name", Color.black);
		font.drawString(4, 240, "Multiplicator/Rate", Color.black);*///TODO
		RGB.glColorReset();
	}

	@Override
	protected String[] getExpectedQuickButtons(){ return null; }

}
