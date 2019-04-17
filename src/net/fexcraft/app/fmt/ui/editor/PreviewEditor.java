package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.ui.tree.HelperTree;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class PreviewEditor extends Editor {

	public PreviewEditor(){
		super("preview_editor");
	}

	@Override
	protected ContainerButton[] setupSubElements(){
		ContainerButton container = new ContainerButton(this, "general", 300, 28, 4, y, new int[]{ 1, 3, 1, 3, 1, 3 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Position (full units)", false).setRowCol(0, 0));
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Rotation (degrees)", false).setRowCol(2, 0));
				this.elements.add(new Button(this, "text2", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Scale (OpenGL)", false).setRowCol(4, 0));
				for(int i = 0; i < 3; i++){
					final int j = i;
					this.elements.add(new TextField(this, "helper_pos" + xyz[i], 70, 16 + (98 * i), 30){
						@Override public void updateNumberField(){ updatePos(this, j, null); }
						@Override protected boolean processScrollWheel(int wheel){ return updatePos(j, wheel > 0); }
					}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(1, i));
					//
					this.elements.add(new TextField(this, "helper_rot" + xyz[i], 70, 16 + (98 * i), 80){
						@Override public void updateNumberField(){ updateRot(this, j, null); }
						@Override protected boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
					}.setAsNumberfield(-360, 360, true).setRowCol(3, i));
					//
					this.elements.add(new TextField(this, "helper_scale" + xyz[i], 70, 16 + (98 * i), 130){
						@Override public void updateNumberField(){ updateScale(this, j, null); }
						@Override protected boolean processScrollWheel(int wheel){ return updateScale(j, wheel > 0); }
					}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(5, i));
				}
			}
		}; container.setText("General Settings", true); container.setExpanded(true);
		return new ContainerButton[]{ container };
	}
	
	protected boolean updateScale(int axis, Boolean positive){
		return updateScale(null, axis, positive);
	}
	
	protected boolean updateScale(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperTree.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_scale" + xyz[axis]);
		if(compound.scale == null) compound.scale = new Vec3f(1, 1, 1);
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{ compound.scale.xCoord += am / 16; field.applyChange(compound.scale.xCoord * 16); break; }
			case 1:{ compound.scale.yCoord += am / 16; field.applyChange(compound.scale.yCoord * 16); break; }
			case 2:{ compound.scale.zCoord += am / 16; field.applyChange(compound.scale.zCoord * 16); break; }
		}
		return true;
	}
	
	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperTree.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_pos" + xyz[axis]);
		if(compound.pos == null) compound.pos = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{ compound.pos.xCoord += am; field.applyChange(compound.pos.xCoord); break; }
			case 1:{ compound.pos.yCoord += am; field.applyChange(compound.pos.yCoord); break; }
			case 2:{ compound.pos.zCoord += am; field.applyChange(compound.pos.zCoord); break; }
		}
		return true;
	}
	
	protected boolean updateRot(int axis, Boolean positive){
		return updateRot(null, axis, positive);
	}
	
	protected boolean updateRot(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperTree.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_rot" + xyz[axis]);
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

}
