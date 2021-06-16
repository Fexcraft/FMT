package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.RunButton;

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
		RunButton sel_button = new RunButton("group_sel_panel.select_all", 10, 10, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(true));
		});
		RunButton des_button = new RunButton("group_sel_panel.deselect_all", hw + 5, 10, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(false));
		});
		Settings.applyMenuTheme(sel_button);
		Settings.applyMenuTheme(des_button);
		this.add(sel_button);
		this.add(des_button);
		int i = 0, gh = FMT.MODEL.groups().size() * 25;
		ScrollablePanel panel = new ScrollablePanel(10, 40, width - 20, height - 50);
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
