package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;

public class PreviewEditor extends Editor {

	public PreviewEditor(){
		super("preview_editor", "editor"); this.setVisible(false); Container container = null;
		this.elements.add((container = new Container(this, "container", width - 4, 28, 4, 0, null))
			.setText(translate("editor.preview.container.title", "General Settings"), false));
		//
		int passed = 0;
		{//container
			container.getElements().add(new Button(container, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.preview.container.position", "Position (full units)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				container.getElements().add(new TextField(container, "helper_pos" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updatePos(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updatePos(j, wheel > 0); }
				}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			container.getElements().add(new Button(container, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.preview.container.rotation", "Rotation (degrees)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				container.getElements().add(new TextField(container, "helper_rot" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRot(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
				}.setAsNumberfield(-360, 360, true, true));
			}
			container.getElements().add(new Button(container, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.preview.container.scale", "Scale (OpenGL)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				container.getElements().add(new TextField(container, "helper_scale" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateScale(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateScale(j, wheel > 0); }
				}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			container.getElements().add(new Button(container, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.preview.container.scale16", "Scale (16 x GL)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				container.getElements().add(new TextField(container, "helper_scale16" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateScale16(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateScale16(j, wheel > 0); }
				}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			//
			container.setExpanded(true); passed = 0;
		}
		this.containers = new Container[]{ container }; this.repos();
	}
	
	protected boolean updateScale(int axis, Boolean positive){
		return updateScale(null, axis, positive);
	}
	
	protected boolean updateScale(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_scale" + xyz[axis]);
		TextField field0 = TextField.getFieldById(field == null ? "helper_scale16" + xyz[axis] : field.getId().replace("scale", "scale16"));
		if(compound.scale == null) compound.scale = new Vec3f(1, 1, 1);
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{
				if(positive == null) compound.scale.xCoord = am; else compound.scale.xCoord += am;
				field.applyChange(compound.scale.xCoord);
				field0.applyChange(compound.scale.xCoord * 16);
				break;
			}
			case 1:{
				if(positive == null) compound.scale.yCoord = am; else compound.scale.yCoord += am;
				field.applyChange(compound.scale.yCoord);
				field0.applyChange(compound.scale.yCoord * 16); 
				break;
			}
			case 2:{
				if(positive == null) compound.scale.zCoord = am; else compound.scale.zCoord += am;
				field.applyChange(compound.scale.zCoord);
				field0.applyChange(compound.scale.zCoord * 16); 
				break;
			}
		}
		return true;
	}
	
	protected boolean updateScale16(int axis, Boolean positive){
		return updateScale16(null, axis, positive);
	}
	
	protected boolean updateScale16(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_scale16" + xyz[axis]);
		TextField field0 = TextField.getFieldById(field == null ? "helper_scale" + xyz[axis] : field.getId().replace("16", ""));
		if(compound.scale == null) compound.scale = new Vec3f(1, 1, 1);
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true; float temp;
		switch(axis){
			case 0:{
				if(positive == null) temp = am; else temp = (compound.scale.xCoord * 16) + am; field.applyChange(temp);
				field0.applyChange(compound.scale.xCoord = temp * Static.sixteenth); break;
			}
			case 1:{
				if(positive == null) temp = am; else temp = (compound.scale.yCoord * 16) + am; field.applyChange(temp);
				field0.applyChange(compound.scale.yCoord = temp * Static.sixteenth); break;
			}
			case 2:{
				if(positive == null) temp = am; else temp = (compound.scale.zCoord * 16) + am; field.applyChange(temp);
				field0.applyChange(compound.scale.zCoord = temp * Static.sixteenth); break;
			}
		}
		return true;
	}
	
	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_pos" + xyz[axis]);
		if(compound.pos == null) compound.pos = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		if(am == 0f) return true;
		switch(axis){
			case 0:{
				if(positive == null) compound.pos.xCoord = am; else compound.pos.xCoord += am;
				field.applyChange(compound.pos.xCoord); break;
			}
			case 1:{
				if(positive == null) compound.pos.yCoord = am; else compound.pos.yCoord += am;
				field.applyChange(compound.pos.yCoord); break;
			}
			case 2:{
				if(positive == null) compound.pos.zCoord = am; else compound.pos.zCoord += am;
				field.applyChange(compound.pos.zCoord); break;
			}
		}
		return true;
	}
	
	protected boolean updateRot(int axis, Boolean positive){
		return updateRot(null, axis, positive);
	}
	
	protected boolean updateRot(TextField field, int axis, Boolean positive){
		GroupCompound compound = HelperCollector.getSelected(); if(compound == null) return true;
		if(field == null) field = TextField.getFieldById("helper_rot" + xyz[axis]);
		if(compound.rot == null) compound.rot = new Vec3f();
		float am = positive == null ? field.getFloatValue() : positive ? FMTB.MODEL.rate : -FMTB.MODEL.rate;
		switch(axis){
			case 0:{
				if(positive == null) compound.rot.xCoord = am; else compound.rot.xCoord += am;
				if(compound.rot.xCoord > 360) compound.rot.xCoord = 360;
				if(compound.rot.xCoord < -360) compound.rot.xCoord = -360;
				field.applyChange(compound.rot.xCoord);
				break;
			}
			case 1:{
				if(positive == null) compound.rot.yCoord = am; else compound.rot.yCoord += am;
				if(compound.rot.yCoord > 360) compound.rot.yCoord = 360;
				if(compound.rot.yCoord < -360) compound.rot.yCoord = -360;
				field.applyChange(compound.rot.yCoord);
				break;
			}
			case 2:{
				if(positive == null) compound.rot.zCoord = am; else compound.rot.zCoord += am;
				if(compound.rot.zCoord > 360) compound.rot.zCoord = 360;
				if(compound.rot.zCoord < -360) compound.rot.zCoord = -360;
				field.applyChange(compound.rot.zCoord);
				break;
			}
		}
		return true;
	}

}
