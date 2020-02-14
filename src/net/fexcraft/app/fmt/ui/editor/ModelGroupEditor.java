package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.SettingsBox;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.wrappers.TurboList;

public class ModelGroupEditor extends Editor {
	
	//TODO private static final int[] accepted_texsiz = new int[]{ 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };//, 8192 };
	private Container group, model, animations;

	public ModelGroupEditor(){
		super("model_group_editor", "editor"); this.setVisible(false);
		this.elements.add((group = new Container(this, "group", width - 4, 28, 4, 0, null)).setText(translate("editor.model_group.group.title", "Group Settings"), false));
		//this.elements.add((animations = new Container(this, "animations", width - 4, 28, 4, 0, null)).setText(translate("editor.model_group.animations.title", "Group Animations"), false));
		//
		int passed = 0;
		{//group
			group.getElements().add(new Button(group, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.model_group.group.add_animator", "Add Animator"), false));
			/*group.getElements().add(new DropDownField(group, "group_animator", "editor:field", 300, 4, passed += 24){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(FMTB.MODEL.getSelected().isEmpty()) return true;
					else return super.processButtonClick(x, y, left);
				}
				@Override
				public ArrayList<Element> getDropDownButtons(DropDown inst){
					ArrayList<Element> elements = new ArrayList<>();
					for(Animation am : Animator.nani){
						boolean enabled = !am.id.startsWith("#");
						Element button = new DropDown.Button(inst, "group_animator:" + am.id, !enabled ? "dropdown:title_button" : "dropdown:button", 0, 26, 0, 0){
							@Override
							public boolean processButtonClick(int x, int y, boolean left){
								/*if(am == null){
									String str = translate("dialog.editor.model_group.group.animator.not_found", "Animation not found!");
									FMTB.showDialogbox(str, translate("dialog.editor.model_group.group.animator.not_found.confirm", "ok"), null, DialogBox.NOTHING, null);
									return true;
								}*//*
								final Animation ani = am.copy(null);
								ArrayList<TurboList> lists = FMTB.MODEL.getDirectlySelectedGroups();
								SettingsBox.open(translate("editor.model_group.group.animator_settings"), ani.settings.values(), false, settings -> {
									for(TurboList list : lists){
										list.animations.add(ani.copy(list));
									} FMTB.MODEL.updateFields();
								}); 
								return true;
							}
						}.setText(am.id, false).setEnabled(enabled);
						if(enabled) button.setColor(0xff4287f5); elements.add(button);
					}
					return elements;
				}
			});*/
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
									Animation anim = list.animations.get(j); this.deselect(); if(anim == null) return true; FMTB.MODEL.updateFields();
									SettingsBox.open("[" + anim.id + "] " + translate("editor.model_group.group.animator_settings"), anim.settings.values(), false,
										settings -> { anim.onSettingsUpdate(); FMTB.MODEL.updateFields();});
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
		this.containers = new Container[]{ group, animations }; this.repos();
	}
	
	/*protected boolean updateGroupTexSize(DropDownField field, int axis, int value){
		if(FMTB.MODEL == null) return true; field.setText(value + "", true);
		if(FMTB.MODEL.getDirectlySelectedGroupsAmount() == 0) return true;
		for(TurboList list : FMTB.MODEL.getDirectlySelectedGroups()){
			list.textureX = ((DropDownField)group.getElement("group_texx")).getTextAsInt();
			list.textureY = ((DropDownField)group.getElement("group_texy")).getTextAsInt();
			list.textureS = ((DropDownField)group.getElement("group_texz")).getTextAsInt();
			TextureUpdate.updateSize(list); list.forEach(mrt -> mrt.recompile());
		} return true;
	}*/

}
