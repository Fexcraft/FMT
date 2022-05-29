package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.CheckBox;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.ScrollablePanel;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.wrappers.TurboList;

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
		RunButton slc_button = new RunButton("group_sel_panel.select_selected", 10, 40, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(false));
			ArrayList<TurboList> selected = FMTB.MODEL.getDirectlySelectedGroups();
			for(TurboList list : selected) boxes.get(list.id).setChecked(true);
		});
		RunButton vis_button = new RunButton("group_sel_panel.select_visible", hw + 5, 40, hw - 15, 20, () -> {
			boxes.values().forEach(val -> val.setChecked(false));
			ArrayList<TurboList> selected = FMTB.MODEL.getVisibleGroups();
			for(TurboList list : selected) boxes.get(list.id).setChecked(true);
		});
		this.add(sel_button);
		this.add(des_button);
		this.add(slc_button);
		this.add(vis_button);
		int i = 0, gh = FMTB.MODEL.getGroups().size() * 25;
		ScrollablePanel panel = new ScrollablePanel(10, 70, width - 20, height - 80);
		panel.setHorizontalScrollBarVisible(false);
		panel.getContainer().setSize(width, gh < panel.getSize().y ? panel.getSize().y : gh);
		for(TurboList group : FMTB.MODEL.getGroups()){
			CheckBox box = new CheckBox("", 10, (i * 25) + 2.5f, width - 20, 20);
			Label label = new Label(group.id, 30, (i++ * 25) + 2.5f, width - 20, 20);
			panel.getContainer().add(box);
			panel.getContainer().add(label);
			boxes.put(group.id, box);
			box.setChecked(true);
		}
		this.add(panel);
	}
	
	public ArrayList<String> getSelectedGroupIds(){
		ArrayList<String> list = new ArrayList<>();
		boxes.forEach((key, val) -> {
			if(val.isChecked()) list.add(key);
		});
		return list;
	}
	
	public ArrayList<TurboList> getSelectedGroups(){
		ArrayList<String> ids = getSelectedGroupIds();
		ArrayList<TurboList> list = new ArrayList<>();
		ids.forEach(id -> {
			TurboList group = FMTB.MODEL.getGroups().get(id);
			if(group != null) list.add(group);
		});
		return list;
	}

}
