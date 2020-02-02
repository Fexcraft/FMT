package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.DropDown;
import net.fexcraft.app.fmt.ui.general.DropDownField;
import net.fexcraft.app.fmt.ui.general.FileSelector.AfterTask;
import net.fexcraft.app.fmt.ui.general.FileSelector.ChooserMode;
import net.fexcraft.app.fmt.ui.general.FileSelector.FileRoot;
import net.fexcraft.app.fmt.ui.general.TextField;
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
		this.elements.add((model = new Container(this, "model", width - 4, 28, 4, 0, null)).setText(translate("editor.model_group.model.title", "Model Settings"), false));
		this.elements.add((group = new Container(this, "group", width - 4, 28, 4, 0, null)).setText(translate("editor.model_group.group.title", "Group Settings"), false));
		//this.elements.add((animations = new Container(this, "animations", width - 4, 28, 4, 0, null)).setText(translate("editor.model_group.animations.title", "Group Animations"), false));
		//
		int passed = 0;
		{//model
			model.getElements().add(new Button(model, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.model.position", "Position Offset (full unit)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new TextField(model, "model_pos" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updatePos(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updatePos(j, wheel > 0); }
				}.setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			model.getElements().add(new Button(model, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.model.rotation", "Rotation Offset (degrees)"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new TextField(model, "model_rot" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRot(this, j, null); }
					@Override public boolean processScrollWheel(int wheel){ return updateRot(j, wheel > 0); }
				}.setAsNumberfield(8, 4096, true, true));
			}
			model.getElements().add(new Button(model, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.model.texture_size", "Texture [U/V/Scale]"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				model.getElements().add(new DropDownField(model, "model_tex" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override
					public ArrayList<Element> getDropDownButtons(DropDown inst){
						ArrayList<Element> elements = new ArrayList<>(); DropDownField field = this;
						int[] arr = j == 2 ? new int[]{ 1, 2, 3, 4 } : accepted_texsiz;
						for(int i = 0; i < arr.length; i++){ int k = i;
							elements.add(new DropDown.Button(inst, "model_tex:" + arr[k], "dropdown:button", 0, 26, 0, 0){
								@Override public boolean processButtonClick(int x, int y, boolean left){ updateModelTexSize(field, j, arr[k]); return true; }
							}.setText(arr[k] + "", false));
						}
						return elements;
					}
				});
			}
			model.getElements().add(new Button(model, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.model.texture", "Model Texture"), false));
			model.getElements().add(new TextField(model, "model_texture", "editor:field", 300, 4, passed += 24){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(!left){
						if(FMTB.MODEL.texture != null && TextureManager.getTexture(FMTB.MODEL.texture, true) != null){
							FMTB.MODEL.setTexture(null); TextureManager.removeTexture(FMTB.MODEL.texture);
						} FMTB.MODEL.updateFields(); return true;
					}
					UserInterface.FILECHOOSER.show(translate("filechooser.editor.model_group.model.texture", "Select a model texture file."), null, null, null, FileRoot.TEXTURES, new AfterTask(){
						@Override
						public void run(){
							String name = file.getPath(); TextureManager.loadTextureFromFile(name, file);
							FMTB.MODEL.setTexture(name); FMTB.MODEL.updateFields(); 
						}
					}, ChooserMode.PNG);return true;
				}
			}.setText("null", true));
			model.getElements().add(new Button(model, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.model.name", "Model Name"), false));
			model.getElements().add(new TextField(model, "model_name", "editor:field", 300, 4, passed += 24) {
				@Override public void updateTextField(){ if(FMTB.MODEL == null) return; FMTB.get().setTitle(FMTB.MODEL.name = this.getTextValue()); }
			}.setText(FMTB.MODEL.name, true));
			//
			model.setExpanded(false); passed = 0;
		}
		{//group
			group.getElements().add(new Button(group, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.color","Group Preview Color/Overlay"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				group.getElements().add(new TextField(group, "group_rgb" + i, "editor:field", 96, 4 + (i * 102), passed){
					@Override public void updateNumberField(){ updateRGB(null, j); }
					@Override public boolean processScrollWheel(int wheel){ return updateRGB(wheel > 0, j); }
				}.setAsNumberfield(0, 255, true, true));
			}
			group.getElements().add(new Button(group, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.name", "Group Name/ID"), false));
			group.getElements().add(new TextField(group, "group_name", "editor:field", 300, 4, passed += 24){
				@Override
				public void updateTextField(){
					if(FMTB.MODEL.getSelected().isEmpty()) return;
					TurboList list = null;
					if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 1){
						if(FMTB.MODEL.getGroups().isEmpty()) return;
						list = FMTB.MODEL.getFirstSelectedGroup();
						list = FMTB.MODEL.getGroups().remove(list.id);
						list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
						while(FMTB.MODEL.getGroups().contains(list.id)){ list.id += "_"; }
						FMTB.MODEL.getGroups().add(list);
					}
					else{
						ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
						for(int i = 0; i < arrlist.size(); i++){
							list = FMTB.MODEL.getGroups().remove(arrlist.get(i).id); if(list == null) continue;
							list.id = this.getTextValue().replace(" ", "_").replace("-", "_").replace(".", "");
							list.id += list.id.contains("_") ? "_" + i : i + "";
							while(FMTB.MODEL.getGroups().contains(list.id)){ list.id += "_"; }
							FMTB.MODEL.getGroups().add(list);
						}
					}
					FMTB.MODEL.getSelected().clear();
				}
			}.setText("null", true));
			//
			group.getElements().add(new Button(group, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.texture_size", "Texture [U/V/Scale]"), false));
			passed += 24; for(int i = 0; i < 3; i++){ final int j = i;
				group.getElements().add(new DropDownField(group, "group_tex" + xyz[i], "editor:field", 96, 4 + (i * 102), passed){
					@Override
					public ArrayList<Element> getDropDownButtons(DropDown inst){
						ArrayList<Element> elements = new ArrayList<>(); DropDownField field = this;
						int[] arr = j == 2 ? new int[]{ 1, 2, 3, 4 } : accepted_texsiz;
						for(int i = 0; i < arr.length; i++){ int k = i;
							elements.add(new DropDown.Button(inst, "model_tex:" + arr[k], "dropdown:button", 0, 26, 0, 0){
								@Override public boolean processButtonClick(int x, int y, boolean left){ updateGroupTexSize(field, j, arr[k]); return true; }
							}.setText(arr[k] + "", false));
						}
						return elements;
					}
				});
			}
			//
			group.getElements().add(new Button(group, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.texture", "Group Texture"), false));
			group.getElements().add(new TextField(group, "group_texture", "editor:field", 300, 4, passed += 24){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(FMTB.MODEL.getSelected().isEmpty()) return true;
					if(!left){
						ArrayList<TurboList> arrlist = FMTB.MODEL.getDirectlySelectedGroups();
						for(TurboList group : arrlist){
							if(TextureManager.getTexture(group.getGroupTexture(), true) != null){
								FMTB.MODEL.setTexture(null); TextureManager.removeTexture(group.getGroupTexture());
							} group.setTexture(null, 0, 0); group.forEach(mrt -> mrt.recompile());
						} FMTB.MODEL.updateFields(); return true;
					}
					UserInterface.FILECHOOSER.show(translate("filechooser.editor.model_group.group.texture", "Select a group texture file."), null, null, null, FileRoot.TEXTURES, new AfterTask(){
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
			group.getElements().add(new Button(group, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.add_animator", "Add Animator"), false));
			group.getElements().add(new DropDownField(group, "group_animator", "editor:field", 300, 4, passed += 24){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(FMTB.MODEL.getSelected().isEmpty()) return true;
					else return super.processButtonClick(x, y, left);
				}
				@Override
				public ArrayList<Element> getDropDownButtons(DropDown inst){
					ArrayList<Element> elements = new ArrayList<>();
					for(Animation am : Animator.nani){
						elements.add(new DropDown.Button(inst, "group_animator:" + am.id, "dropdown:button", 0, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								/*if(am == null){
									String str = translate("dialog.editor.model_group.group.animator.not_found", "Animation not found!");
									FMTB.showDialogbox(str, translate("dialog.editor.model_group.group.animator.not_found.confirm", "ok"), null, DialogBox.NOTHING, null);
									return true;
								}*/
								final Animation ani = am.copy(null);
								ArrayList<TurboList> lists = FMTB.MODEL.getDirectlySelectedGroups();
								AfterTask task = new AfterTask(){
									@Override
									public void run(){
										for(TurboList list : lists){
											list.animations.add(ani.copy(list));
										} FMTB.MODEL.updateFields();
									}
								}; task.settings = ani.settings.values();
								UserInterface.SETTINGSBOX.show(translate("editor.model_group.group.animator_settings", "Animator Settings"), task);
								return true;
							}
						}.setText(am.id, false));
					}
					return elements;
				}
			});
			//
			group.setExpanded(false); passed = 0;
		}
		{//animations
			this.elements.add((animations = new Container(this, "animations", width - 4, 28, 4, 0, null){
				@Override
				public void addSubElements(){
					for(int i = 1; i < elements.size(); i++) elements.get(i).dispose(); elements.clear();
					TurboList list = FMTB.MODEL.getFirstSelectedGroup(); if(list == null) return; int passed = 8;
					for(int i = 0; i < list.animations.size(); i++){ int j = i;
						this.getElements().add(new TextField(animations, "group_animation_" + j, "animation:field", 300, 4, passed += 24){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								if(left){
									Animation anim = list.animations.get(j); this.deselect(); if(anim == null) return true;
									AfterTask task = new AfterTask(){
										@Override public void run(){ anim.onSettingsUpdate(); FMTB.MODEL.updateFields(); }
									}; task.settings = anim.settings.values(); FMTB.MODEL.updateFields();
									UserInterface.SETTINGSBOX.show("[" + anim.id + "] Settings", task);
								}
								else{
									list.animations.remove(j); this.deselect(); FMTB.MODEL.updateFields();
								}
								return true;
							}
						}.setText("[" + j + "] " + list.animations.get(j).id, true));
					}
				}
			}).setText(translate("editor.model_group.animations.title", "Group Animations"), false));
			animations.setExpanded(false);
		}
		this.containers = new Container[]{ model, group, animations }; this.repos();
	}
	
	protected boolean updateRGB(Boolean apply, int xyz){
		TextField field = (TextField)group.getElement("group_rgb" + xyz);
		if(apply != null) field.applyChange(field.tryChange(apply, FMTB.MODEL.rate));
		TurboList sel = FMTB.MODEL.getFirstSelectedGroup(); if(sel == null) return true;
		if(sel.color == null) sel.color = RGB.WHITE.copy();
		byte[] arr = sel.color.toByteArray(); byte colorr = (byte)(field.getIntegerValue() - 128);
		switch(xyz){
			case 0: sel.color = new RGB(colorr, arr[1], arr[2]); break;
			case 1: sel.color = new RGB(arr[0], colorr, arr[2]); break;
			case 2: sel.color = new RGB(arr[0], arr[1], colorr); break;
		}
		arr = sel.color.toByteArray();
		if(arr[0] == 127 && arr[1] == 127 && arr[2] == 127) sel.color = null;
		return true;
	}
	
	protected boolean updateModelTexSize(DropDownField field, int axis, int value){
		if(FMTB.MODEL == null) return true; field.setText(value + "", true);
		FMTB.MODEL.textureSizeX = ((DropDownField)model.getElement("model_texx")).getTextAsInt();
		FMTB.MODEL.textureSizeY = ((DropDownField)model.getElement("model_texy")).getTextAsInt();
		FMTB.MODEL.textureScale = ((DropDownField)model.getElement("model_texz")).getTextAsInt();
		TextureUpdate.updateSize(null); return true;
	}
	
	protected boolean updateGroupTexSize(DropDownField field, int axis, int value){
		if(FMTB.MODEL == null) return true; field.setText(value + "", true);
		if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 0) return true;
		for(TurboList list : FMTB.MODEL.getDirectlySelectedGroups()){
			list.textureX = ((DropDownField)group.getElement("group_texx")).getTextAsInt();
			list.textureY = ((DropDownField)group.getElement("group_texy")).getTextAsInt();
			list.textureS = ((DropDownField)group.getElement("group_texz")).getTextAsInt();
			TextureUpdate.updateSize(list); list.forEach(mrt -> mrt.recompile());
		} return true;
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
			case 0:{
				if(positive == null) FMTB.MODEL.pos.xCoord = am; else FMTB.MODEL.pos.xCoord += am;
				field.applyChange(FMTB.MODEL.pos.xCoord); break;
			}
			case 1:{
				if(positive == null) FMTB.MODEL.pos.yCoord = am; else FMTB.MODEL.pos.yCoord += am;
				field.applyChange(FMTB.MODEL.pos.yCoord); break;
			}
			case 2:{
				if(positive == null) FMTB.MODEL.pos.zCoord = am; else FMTB.MODEL.pos.zCoord += am;
				field.applyChange(FMTB.MODEL.pos.zCoord); break;
			}
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
				if(positive == null) compound.rot.xCoord = am; else compound.rot.xCoord += am;
				if(compound.rot.xCoord > 360) compound.rot.xCoord = 360;
				if(compound.rot.xCoord < -360) compound.rot.xCoord = -360;
				field.applyChange(compound.rot.xCoord);
				break;
			}
			case 1:{
				if(positive == null) compound.rot.yCoord = am; compound.rot.yCoord += am;
				if(compound.rot.yCoord > 360) compound.rot.yCoord = 360;
				if(compound.rot.yCoord < -360) compound.rot.yCoord = -360;
				field.applyChange(compound.rot.yCoord);
				break;
			}
			case 2:{
				if(positive == null) compound.rot.zCoord = am; compound.rot.zCoord += am;
				if(compound.rot.zCoord > 360) compound.rot.zCoord = 360;
				if(compound.rot.zCoord < -360) compound.rot.zCoord = -360;
				field.applyChange(compound.rot.zCoord);
				break;
			}
		}
		return true;
	}

}
