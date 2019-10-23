package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import java.io.File;
import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.ui.old.NFC.AfterTask;
import net.fexcraft.app.fmt.ui.old.NFC.ChooserMode;
import net.fexcraft.app.fmt.utils.Animator;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class ModelGroupEditor extends Editor {
	
	private static final int[] accepted_texsiz = new int[]{ 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };//, 8192 };
	private Container group, model, animations;

	public ModelGroupEditor(){
		super("model_group_editor", "editor"); this.setVisible(false);
		this.elements.add((model = new Container(this, "model", width - 4, 28, 4, 0, null)).setText("Model Settings", false));
		this.elements.add((group = new Container(this, "group", width - 4, 28, 4, 0, null)).setText("Group Settings", false));
		this.elements.add((animations = new Container(this, "animations", width - 4, 28, 4, 0, null)).setText("Group Animations", false));
		//
		int passed = 0;
		{//model
			model.getElements().add(new Button(model, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Position Offset (full unit)", false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new TextField(model, "model_pos" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updatePos(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updatePos(j, wheel > 0); }
				}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			model.getElements().add(new Button(model, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Rotation Offset (degrees)", false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new TextField(model, "model_rot" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRot(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
				}.setAsNumberfield(8, 4096, true, true));
			}
			model.getElements().add(new Button(model, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Texture [U/V/Scale]", false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new TextField(model, "model_tex" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateTexSize(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateTexSize(j, wheel > 0); }
				}.setAsNumberfield(i == 2 ? 1 : 8, i == 2 ? 4 : 4096, true, true));
			}
			model.getElements().add(new Button(model, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Model Texture", false));
			model.getElements().add(new TextField(model, "model_texture", "editor:field", 300, 4, passed += 24){
				@Override
				protected boolean processButtonClick(int x, int y, boolean left){
					if(!left){
						if(FMTB.MODEL.texture != null && TextureManager.getTexture(FMTB.MODEL.texture, true) != null){
							FMTB.MODEL.setTexture(null); TextureManager.removeTexture(FMTB.MODEL.texture);
						} FMTB.MODEL.updateFields(); return true;
					}
					UserInterface.FILECHOOSER.show(new String[]{ "Select a group texture file." }, new File("./resources/textures"), new AfterTask(){
						@Override
						public void run(){
							String name = file.getPath(); TextureManager.loadTextureFromFile(name, file);
							FMTB.MODEL.setTexture(name); FMTB.MODEL.updateFields(); 
						}
					}, ChooserMode.PNG);return true;
				}
			}.setText("null", true));
			model.getElements().add(new Button(model, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Model Name", false));
			model.getElements().add(new TextField(model, "model_name", "editor:field", 300, 4, passed += 24) {
				@Override public void updateTextField(){ if(FMTB.MODEL == null) return; FMTB.get().setTitle(FMTB.MODEL.name = this.getTextValue()); }
			}.setText(FMTB.MODEL.name, true));
			//
			model.setExpanded(false); passed = 0;
		}
		{//group
			group.getElements().add(new Button(group, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Group Preview Color/Overlay", false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				group.getElements().add(new TextField(group, "group_rgb" + i, "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRGB(null, j); }
					@Override public boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
				}.setAsNumberfield(0, 255, true, true));
			}
			group.getElements().add(new Button(group, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Group Name/ID", false));
			group.getElements().add(new TextField(group, "group_name", "editor:field", 300, 4, passed += 24){
				@Override
				public void updateTextField(){
					if(FMTB.MODEL.getSelected().isEmpty()) return;
					TurboList list = null;
					if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 1){
						if(FMTB.MODEL.getCompound().isEmpty()) return;
						list = FMTB.MODEL.getFirstSelectedGroup();
						list = FMTB.MODEL.getCompound().remove(list.id);
						list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
						while(FMTB.MODEL.getCompound().containsKey(list.id)){ list.id += "_"; }
						FMTB.MODEL.getCompound().put(list.id, list);
					}
					else{
						ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
						for(int i = 0; i < arrlist.size(); i++){
							list = FMTB.MODEL.getCompound().remove(arrlist.get(i).id); if(list == null) continue;
							list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
							list.id += list.id.contains("_") ? "_" + i : i + "";
							while(FMTB.MODEL.getCompound().containsKey(list.id)){ list.id += "_"; }
							FMTB.MODEL.getCompound().put(list.id, list);
						}
					}
					FMTB.MODEL.getSelected().clear();
				}
			}.setText("null", true));
			//
			group.getElements().add(new Button(group, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Texture [U/V/Scale]", false));
			group.getElements().add(new TextField(group, "group_texx", "editor:field", 96, 4, passed += 24){
				@Override public void updateNumberField(){ updateGroupTexSize(this, 0, null); }
				@Override public boolean processScrollWheel(int wheel){ return updateGroupTexSize(0, wheel > 0); }
			}.setAsNumberfield(8, 4096, true, true));
			group.getElements().add(new TextField(group, "group_texy", "editor:field", 96, 106, passed){
				@Override public void updateNumberField(){ updateGroupTexSize(this, 1, null); }
				@Override public boolean processScrollWheel(int wheel){ return updateGroupTexSize(1, wheel > 0); }
			}.setAsNumberfield(8, 4096, true, true));
			group.getElements().add(new TextField(group, "group_texz", "editor:field", 96, 208, passed){
				@Override public void updateNumberField(){ updateGroupTexSize(this, 2, null); }
				@Override public boolean processScrollWheel(int wheel){ return updateGroupTexSize(2, wheel > 0); }
			}.setAsNumberfield(1, 4, true, true));
			//
			group.getElements().add(new Button(group, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Group Texture", false));
			group.getElements().add(new TextField(group, "group_texture", "editor:field", 300, 4, passed += 24){
				@Override
				protected boolean processButtonClick(int x, int y, boolean left){
					if(FMTB.MODEL.getSelected().isEmpty()) return true;
					if(!left){
						ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
						for(TurboList group : arrlist){
							if(TextureManager.getTexture(group.getGroupTexture(), true) != null){
								FMTB.MODEL.setTexture(null); TextureManager.removeTexture(group.getGroupTexture());
							} group.setTexture(null, 0, 0); group.forEach(mrt -> mrt.recompile());
						} FMTB.MODEL.updateFields(); return true;
					}
					UserInterface.FILECHOOSER.show(new String[]{ "Select a group texture file." }, new File("./resources/textures"), new AfterTask(){
						@Override
						public void run(){
							String name = file.getPath(); TextureManager.loadTextureFromFile(name, file);
							TextureManager.Texture texture = TextureManager.getTexture(name, false);
							ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
							for(TurboList group : arrlist){
								group.setTexture(name, texture.getWidth(), texture.getHeight());
								group.recompile();
							} FMTB.MODEL.updateFields(); 
						}
					}, ChooserMode.PNG); return true;
				}
			}.setText("null", true));
			//
			group.getElements().add(new Button(group, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Add Animator", false));
			group.getElements().add(new TextField(group, "group_animator", "editor:field", 300, 4, passed += 24){
				@Override
				protected boolean processButtonClick(int x, int y, boolean left){
					if(FMTB.MODEL.getSelected().isEmpty()) return true;
					if(!left){
						FMTB.showDialogbox(this.getText(), "ok", null, DialogBox.NOTHING, null);
						this.setText("", true);
						return true;
					}
					else return super.processButtonClick(x, y, left);
				}
				@Override
				public void updateTextField(){
					this.deselect(); if(FMTB.MODEL.getSelected().isEmpty()) return;
					Animation anim = Animator.get(this.getTextValue());
					if(anim == null){
						FMTB.showDialogbox("Animation not found!", "ok", null, DialogBox.NOTHING, null);
						return;
					} final Animation ani = anim.copy();
					ArrayList<TurboList> lists = FMTB.MODEL.getDirectlySelectedGroups();
					AfterTask task = new AfterTask(){
						@Override
						public void run(){
							for(TurboList list : lists){
								list.animations.add(ani);
							} FMTB.MODEL.updateFields();
						}
					}; task.settings = ani.settings;
					UserInterface.SETTINGSBOX.show("Animator Settings", task);
				}
			}.setText("null", true));
			//
			group.setExpanded(false); passed = 0;
		}
		{//animations
			/*animations.getElements().clear(); TurboList list = FMTB.MODEL.getFirstSelectedGroup();
			int[] rows = new int[list == null ? 1 : list.animations.size() + 1];
			for(int i = 0; i < rows.length; i++) rows[i] = 1; this.initRowData(rows);
			if(list == null){ return; } 
			for(int i = 0; i < rows.length - 1; i++){ int j = i;
				animations.getElements().add(new TextField(animations, "group_animation_" + i, "animation:field", 0, 0, 0){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						if(left){
							Animation anim = list.animations.get(j); this.deselect(); if(anim == null) return true;
							AfterTask task = new AfterTask(){
								@Override public void run(){ anim.onSettingsUpdate(); FMTB.MODEL.updateFields(); }
							}; task.settings = anim.settings; FMTB.MODEL.updateFields();
							UserInterface.SETTINGSBOX.show("[" + anim.id + "] Settings", task);
						}
						else{
							list.animations.remove(j); this.deselect(); FMTB.MODEL.updateFields();
						}
						return true;
					}
				}.setText("[" + i + "] " + list.animations.get(i).id, true));
			}
			this.initHeight(); return;*/
			animations.getElements().add(new Button(animations, "text0", "editor:title", 290, 20, 4, 30, BLACK).setBackgroundless(true).setText("Queued for reimplementation", false));
			animations.setExpanded(false);
		}
		this.containers = new Container[]{ model, group, animations }; this.repos();
	}
	
	protected boolean updateRGB(Boolean apply, int j){
		TextField field = (TextField)group.getElement("group_rgb" + j);
		if(apply != null) field.applyChange(field.tryChange(apply, FMTB.MODEL.rate));
		TurboList sel = FMTB.MODEL.getFirstSelectedGroup();
		if(sel != null){
			if(sel.color == null) sel.color = new RGB(BLACK);
			byte[] arr = sel.color.toByteArray();
			byte colorr = (byte)(field.getIntegerValue() - 128);
			switch(j){
				case 0: sel.color = new RGB(colorr, arr[1], arr[2]); break;
				case 1: sel.color = new RGB(arr[0], colorr, arr[2]); break;
				case 2: sel.color = new RGB(arr[0], arr[1], colorr); break;
			}
			arr = sel.color.toByteArray();
			if(arr[0] == 127 && arr[1] == 127 && arr[2] == 127) sel.color = null;
		} return true;
	}
	
	protected boolean updateTexSize(int axis, Boolean positive){
		return updateTexSize(null, axis, positive);
	}
	
	protected boolean updateTexSize(TextField field, int axis, Boolean positive){
		if(FMTB.MODEL == null) return true; if(field == null) field = (TextField)model.getElement("model_tex" + xyz[axis]);
		if(axis < 2){
			int index = getIndex(field.getIntegerValue());
			if(positive && index < (accepted_texsiz.length - 1)) field.applyChange(accepted_texsiz[index + 1]);
			else if(!positive && index > 0) field.applyChange(accepted_texsiz[index - 1]);
		} else{ field.tryChange(positive, 1); }
		//
		FMTB.MODEL.textureSizeX = ((TextField)model.getElement("model_texx")).getIntegerValue();
		FMTB.MODEL.textureSizeY = ((TextField)model.getElement("model_texy")).getIntegerValue();
		FMTB.MODEL.textureScale = ((TextField)model.getElement("model_texz")).getIntegerValue();
		TextureUpdate.updateSize(null); return true;
	}
	
	protected boolean updateGroupTexSize(int axis, Boolean positive){
		return updateGroupTexSize(null, axis, positive);
	}
	
	protected boolean updateGroupTexSize(TextField field, int axis, Boolean positive){
		if(FMTB.MODEL == null) return true; if(field == null) field = (TextField)group.getElement("group_tex" + xyz[axis]);
		if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 0) return true;
		if(axis < 2){
			int index = getIndex(field.getIntegerValue());
			if(positive && index < (accepted_texsiz.length - 1)) field.applyChange(accepted_texsiz[index + 1]);
			else if(!positive && index > 0) field.applyChange(accepted_texsiz[index - 1]);
		} else{ field.tryChange(positive, 1); }
		//
		for(TurboList list : FMTB.MODEL.getDirectlySelectedGroups()){
			list.textureX = ((TextField)group.getElement("group_texx")).getIntegerValue();
			list.textureY = ((TextField)group.getElement("group_texy")).getIntegerValue();
			list.textureS = ((TextField)group.getElement("group_texz")).getIntegerValue();
			TextureUpdate.updateSize(list); list.forEach(mrt -> mrt.recompile());
		} return true;
	}
	
	private int getIndex(int val){
		if(val < accepted_texsiz[0]) val = accepted_texsiz[0];
		for(int i = 0; i < accepted_texsiz.length; i++){ if(val == accepted_texsiz[i]) return i; } return 0;
	}
	
	protected boolean updatePos(int axis, Boolean positive){
		return updatePos(null, axis, positive);
	}
	
	protected boolean updatePos(TextField field, int axis, Boolean positive){
		if(FMTB.MODEL == null) return true;
		if(field == null) field = (TextField)model.getElement("model_pos" + xyz[axis]);
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
		if(field == null) field = (TextField)model.getElement("model_rot" + xyz[axis]);
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
