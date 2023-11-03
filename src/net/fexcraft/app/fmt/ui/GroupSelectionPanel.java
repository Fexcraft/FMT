package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import net.fexcraft.app.fmt.ui.fields.TextField;
import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import org.lwjgl.system.CallbackI.S;

public class GroupSelectionPanel extends Panel {
	
	private HashMap<String, CheckBox> boxes = new HashMap<>();
	
	public GroupSelectionPanel(int w, int h){
		init(w, h);
	}
	
	public GroupSelectionPanel(int x, int y, int w, int h){
		setPosition(x, y);
		init(w, h);
	}

	private void init(int width, int height){
		this.setSize(width, height);
		float hw = width / 2;
		TextField field = new TextField(FMT.MODEL.getGroupPreset(0), 10, 10, width - 145, 20);
		add(field);
		add(new Icon(0, 20, 0, width - 130, 10, "./resources/textures/icons/toolbar/open.png", () -> {
			String str = field.getTextState().getText();
			if(FMT.MODEL.export_group_preset_keys.contains(str)) updateSelectedGroups(str);
		}).addTooltip("group_sel_panel.load_preset"));
		add(new Icon(0, 20, 0, width - 105, 10, "./resources/textures/icons/toolbar/save.png", () -> {
			String str = field.getTextState().getText();
			int idx = FMT.MODEL.export_group_preset_keys.indexOf(str);
			if(idx < 0){
				FMT.MODEL.export_group_preset_keys.add(str);
				FMT.MODEL.export_group_presets.add(getSelectedGroupIds());
			}
			else{
				FMT.MODEL.export_group_presets.set(idx, getSelectedGroupIds());
			}
		}).addTooltip("group_sel_panel.save_preset"));
		add(new Icon(0, 20, 0, width - 80, 10, "./resources/textures/icons/component/remove.png", () -> {
			String str = field.getTextState().getText();
			int idx = FMT.MODEL.export_group_preset_keys.indexOf(str);
			if(idx >= 0){
				FMT.MODEL.export_group_preset_keys.remove(idx);
				FMT.MODEL.export_group_presets.remove(idx);
			}
			field.getTextState().setText(FMT.MODEL.export_group_presets.isEmpty() ? "new_preset" : FMT.MODEL.export_group_preset_keys.get(0));
		}).addTooltip("group_sel_panel.remove_preset"));
		add(new Icon(0, 20, 0, width - 55, 10, "./resources/textures/icons/component/move_up.png", () -> {
			String str = field.getTextState().getText();
			String pr = FMT.MODEL.getGroupPreset(FMT.MODEL.export_group_preset_keys.indexOf(str) - 1);
			field.getTextState().setText(pr);
			if(FMT.MODEL.export_group_preset_keys.contains(pr)) updateSelectedGroups(pr);
		}).addTooltip("group_sel_panel.prev_preset"));
		add(new Icon(0, 20, 0, width - 30, 10, "./resources/textures/icons/component/move_down.png", () -> {
			String str = field.getTextState().getText();
			String pr = FMT.MODEL.getGroupPreset(FMT.MODEL.export_group_preset_keys.indexOf(str) + 1);
			field.getTextState().setText(pr);
			if(FMT.MODEL.export_group_preset_keys.contains(pr)) updateSelectedGroups(pr);
		}).addTooltip("group_sel_panel.next_preset"));
		RunButton sel_button = new RunButton("group_sel_panel.select_all", 10, 50, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(true));
		});
		RunButton des_button = new RunButton("group_sel_panel.deselect_all", hw + 5, 50, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(false));
		});
		Settings.applyMenuTheme(field, sel_button, des_button);
		this.add(sel_button);
		this.add(des_button);
		int i = 0, gh = FMT.MODEL.groups().size() * 25;
		ScrollablePanel panel = new ScrollablePanel(10, 80, width - 20, height - 90);
		panel.setHorizontalScrollBarVisible(false);
		panel.getContainer().setSize(width, gh < panel.getSize().y ? panel.getSize().y : gh);
		for(Group group : FMT.MODEL.groups()){
			CheckBox box = new CheckBox("", 10, (i * 25) + 2.5f, width - 20, 20);
			Label label = new Label(group.id, 30, (i++ * 25) + 2.5f, width - 20, 20);
			Settings.applyBorderless(box);
			panel.getContainer().add(box);
			Settings.applyBorderless(label);
			panel.getContainer().add(label);
			boxes.put(group.id, box);
			box.setChecked(true);
		}
		this.add(panel);
		Settings.applyBorderless(this);
		Settings.applyBorderless(panel);
	}

	private void updateSelectedGroups(String id){
		int idx = FMT.MODEL.export_group_preset_keys.indexOf(id);
		if(idx < 0) return;
		ArrayList<String> list = FMT.MODEL.export_group_presets.get(idx);
		boxes.forEach((key, val) -> {
			val.setChecked(list.contains(key));
		});
	}

	public ArrayList<String> getSelectedGroupIds(){
		ArrayList<String> list = new ArrayList<>();
		boxes.forEach((key, val) -> {
			if(val.isChecked()) list.add(key);
		});
		return list;
	}
	
	public ArrayList<Group> getSelectedGroups(){
		ArrayList<String> ids = getSelectedGroupIds();
		ArrayList<Group> list = new ArrayList<>();
		ids.forEach(id -> {
			Group group = FMT.MODEL.get(id);
			if(group != null) list.add(group);
		});
		return list;
	}

}
