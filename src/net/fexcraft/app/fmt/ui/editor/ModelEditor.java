package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class ModelEditor extends Editor {

	public ModelEditor(){
		super("model_editor");
		final RGB rgb = new RGB(127, 127, 255);
		//
		for(int i = 0; i < 3; i++){
			final int j = i;
			this.elements.put("pos" + xyz[i] + "-", new Button(this, "pos" + xyz[i] + "-", 12, 26, 4 + (98 * i), 30, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updatePos(j, false); }
			}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
			this.elements.put("pos" + xyz[i], new TextField(this, "pos" + xyz[i], 70, 16 + (98 * i), 30){
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
			this.elements.put("rot" + xyz[i], new TextField(this, "rot" + xyz[i], 70, 16 + (98 * i), 80){
				@Override public void updateNumberField(){ updateRot(this, j, null); }
				@Override protected boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
			}.setAsNumberfield(0, 255, true).setLevel(-1));
			this.elements.put("rot" + xyz[i] + "+", new Button(this, "rot" + xyz[i] + "+", 12, 26, 86 + (98 * i), 80, rgb){
				@Override protected boolean processButtonClick(int x, int y, boolean left){ return updateRot(j, true); }
			}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
		}
		//
		this.elements.put("modelname", new TextField(this, "modelname", 294, 4, 130) {
			@Override public void updateTextField(){ if(FMTB.MODEL == null) return; FMTB.get().setTitle(FMTB.MODEL.name = this.getTextValue()); }
		}.setText("null", true).setLevel(-1));
		//
		this.addMultiplicator(180);
	}
	
	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(TextField field, int axis, Boolean positive){
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
	
	protected boolean updateRot(TextField field, int axis, Boolean positive){
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
		font.drawString(4, 40, "Position Offset", Color.black);
		font.drawString(4, 90, "Rotation Offset", Color.black);
		font.drawString(4, 140, "Model Name", Color.black);
		font.drawString(4, 190, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

}
