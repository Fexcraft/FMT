package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.settings.Settings.GROUP_SUFFIX;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Logging;

public class GroupGeneral extends EditorComponent {
	
	private SelectBox<String> texgroups = new SelectBox<>();
	private SelectBox<Integer> texsx = new SelectBox<>(), texsy = new SelectBox<>();
	private SelectBox<String> pivots = new SelectBox<>();
	private static final String NOGROUPSEL = "< no group directly selected >";
	protected static final String genid = "group.general";
	private TextField name;
	
	public GroupGeneral(){
		super(genid, 240, false, true);
		this.add(new Label(translate(LANG_PREFIX + genid + ".name/id"), L5, row(1), LW, HEIGHT));
		this.add(name = new TextField(NOGROUPSEL, L5, row(1), LW, HEIGHT, false).accept(con -> rename(con)));
		this.add(new Label(translate(LANG_PREFIX + genid + ".pivot"), L5, row(1), LW, HEIGHT));
		pivots.setPosition(L5, row(1));
		pivots.setSize(LW, HEIGHT);
		updcom.add(ModelLoad.class, event -> refreshPivotEntries());
		updcom.add(PivotAdded.class, event -> refreshPivotEntries());
		updcom.add(PivotRenamed.class, event -> refreshPivotEntries());
		updcom.add(PivotRemoved.class, event -> refreshPivotEntries());
		pivots.addSelectBoxChangeSelectionEventListener(listener -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			if(groups.isEmpty()) return;
			Pivot pivot = FMT.MODEL.getP(listener.getNewValue());
			for(Group group : groups){
				Pivot old = FMT.MODEL.getP(group.pivot);
				if(old != null) old.groups.remove(group);
				group.pivot = pivot.id;
				pivot.groups.add(group);
				UpdateHandler.update(new PivotChanged(group, pivot));
			}
		});
		pivots.setVisibleCount(6);
		refreshPivotEntries();
		add(pivots);
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex_size"), L5, row(1), LW, HEIGHT));
		texsx.setSize(F2S, HEIGHT);
		texsx.setPosition(F20, row(1));
		this.add(texsx);
		texsy.setSize(F2S, HEIGHT);
		texsy.setPosition(F21, row(0));
		this.add(texsy);
		for(int res : TextureManager.RESOLUTIONS){
			texsx.addElement(res);
			texsy.addElement(res);
		}
		texsx.setVisibleCount(6);
		texsy.setVisibleCount(6);
		texsx.addSelectBoxChangeSelectionEventListener(listener -> {
			FMT.MODEL.selected_groups().forEach(group -> {
				group.texSizeX = listener.getNewValue();
				group.recompile();
			});
		});
		texsx.addSelectBoxChangeSelectionEventListener(listener -> {
			FMT.MODEL.selected_groups().forEach(group -> {
				group.texSizeY = listener.getNewValue();
				group.recompile();
			});
		});
		updcom.add(GroupSelected.class, event -> {
			ArrayList<Group> list = FMT.MODEL.selected_groups();
			if(list.size() == 0){
				name.getTextState().setText(NOGROUPSEL);
				texsx.setSelected((Integer)FMT.MODEL.texSizeX, true);
				texsy.setSelected((Integer)FMT.MODEL.texSizeY, true);
				texgroups.setSelected(0, true);
			}
			else{
				Group group = list.get(0);
				name.getTextState().setText(group.id);
				texsx.setSelected((Integer)group.texSizeX, true);
				texsy.setSelected((Integer)group.texSizeY, true);
				if(group.texgroup == null) texgroups.setSelected(0, true);
				else texgroups.setSelected(group.texgroup.name, true);
			}
		});
		this.add(new Label(translate(LANG_PREFIX + genid + ".tex_group"), L5, row(1), LW, HEIGHT));
		texgroups.setPosition(L5, row(1));
		texgroups.setSize(LW, HEIGHT);
		updcom.add(TexGroupAdded.class, event -> refreshTexGroupEntries());
		updcom.add(TexGroupRenamed.class, event -> refreshTexGroupEntries());
		updcom.add(TexGroupRemoved.class, event -> refreshTexGroupEntries());
		texgroups.addSelectBoxChangeSelectionEventListener(listener -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			if(groups.isEmpty()) return;
			if(listener.getNewValue().equals("none")){
				for(Group group : groups){
					group.texgroup = null;
				}
			}
			else{
				TextureGroup texgroup = TextureManager.getGroup(listener.getNewValue());
				for(Group group : groups){
					group.texgroup = texgroup;
				}
			}
		});
		texgroups.setVisibleCount(6);
		refreshTexGroupEntries();
		this.add(texgroups);
	}

	private void refreshTexGroupEntries(){
		while(texgroups.getElements().size() > 0) texgroups.removeElement(0);
		texgroups.addElement("none");
		for(TextureGroup group : TextureManager.getGroups()){
			texgroups.addElement(group.name);
		}
		if(FMT.MODEL == null) return;
		Group group = FMT.MODEL.first_selected_group();
		if(group == null || group.texgroup == null) texgroups.setSelected(0, true);
		else texgroups.setSelected(group.texgroup.name, true);
	}

	private void refreshPivotEntries(){
		while(pivots.getElements().size() > 0) pivots.removeElement(0);
		for(Pivot pivot : FMT.MODEL.pivots()){
			pivots.addElement(pivot.id);
		}
		Group group = FMT.MODEL.first_selected_group();
		if(group == null) pivots.setSelected(0, true);
		else pivots.setSelected(group.pivot, true);
	}

	private void rename(String string){
		ArrayList<Group> groups = FMT.MODEL.selected_groups();
		if(groups.isEmpty()) return;
		else if(groups.size() == 1){
			groups.get(0).reid(string);
		}
		else{
			for(int i = 0; i < groups.size(); i++){
				groups.get(i).reid(string + String.format(GROUP_SUFFIX.value, i));
			}
		}
	}

}
