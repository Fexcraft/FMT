package net.fexcraft.app.fmt.ui.editor;

import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.HelperTree;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class PreviewEditor extends Editor {
	
	public static final String[] xyz = new String[]{ "x", "y", "z" };

	public PreviewEditor(){
		super("preview_editor");
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
			this.elements.put("pos" + xyz[i] + "+", new Button(this, "rgb" + xyz[i] + "+", 12, 26, 86 + (98 * i), 30, rgb){
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
		this.addMultiplicator(130);
	}
	
	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperTree.getSelected(); if(compound == null) return true;
		if(field == null) field = this.getField("pos" + xyz[axis]);
		if(compound.pos == null) compound.pos = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{ compound.pos.xCoord += am / 16; field.applyChange(compound.pos.xCoord * 16); break; }
			case 1:{ compound.pos.yCoord += am / 16; field.applyChange(compound.pos.yCoord * 16); break; }
			case 2:{ compound.pos.zCoord += am / 16; field.applyChange(compound.pos.zCoord * 16); break; }
		}
		return true;
	}
	
	protected boolean updateRot(int axis, Boolean positive){
		return updateRot(null, axis, positive);
	}
	
	protected boolean updateRot(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperTree.getSelected(); if(compound == null) return true;
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
		font.drawString(4, 140, "Multiplicator/Rate", Color.black);
		RGB.glColorReset();
	}

}
