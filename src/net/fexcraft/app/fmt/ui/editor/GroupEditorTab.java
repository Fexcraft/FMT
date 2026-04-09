package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.TextElm;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;
import static net.fexcraft.app.fmt.ui.editor.EditorRoot.NOGROUPSEL;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class GroupEditorTab extends EditorTab {

	public ETabCom general;
	private Field name;
	private DropList<Integer> texx;
	private DropList<Integer> texy;
	private DropList<String> texg;
	private DropList<Pivot> pivots;

	public GroupEditorTab(){
		super(EditorRoot.EditorMode.GROUP);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((general = new ETabCom()), lang_prefix + "general", 280);
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.name"));
		general.add((name = new Field(TEXT, FF, field -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			if(groups.isEmpty()) return;
			String name = field.get_text();
			if(groups.size() == 1){
				groups.get(0).id = name;
				UpdateHandler.update(new UpdateEvent.GroupRenamed(groups.get(0), name));
			}
			else{
				for(int i = 0; i < groups.size(); i++){
					groups.get(i).id = name + String.format(Settings.GROUP_SUFFIX.value, i);
					UpdateHandler.update(new UpdateEvent.GroupRenamed(groups.get(i), name));
				}
			}
		})).pos(FO, next_y_pos(1)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture_size"));
		general.add((texx = new DropList<Integer>(F2S).onchange((key, val) -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			for(Group group : groups){
				group.texSizeX = val;
				group.recompile();
			}
		})).pos(F20, next_y_pos(1)));
		general.add((texy = new DropList<Integer>(F2S).onchange((key, val) -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			for(Group group : groups){
				group.texSizeY = val;
				group.recompile();
			}
		})).pos(F21, next_y_pos(0)));
		for(int res : TextureManager.RESOLUTIONS){
			texx.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
			texy.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
		}
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture_group"));
		general.add((texg = new DropList<String>(FF).onchange((key, val) -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			for(Group group : groups){
				group.texgroup = TextureManager.getGroup(val);
			}
		})).pos(FO, next_y_pos(1)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.pivot"));
		general.add((pivots = new DropList<Pivot>(FF).onchange((key, val) -> {
			ArrayList<Group> groups = FMT.MODEL.selected_groups();
			Pivot pivot = val;
			for(Group gr : groups){
				Pivot old = FMT.MODEL.getP(gr.pivot);
				if(old != null) old.groups.remove(gr);
				gr.pivot = pivot.id;
				pivot.groups.add(gr);
				UpdateHandler.update(new UpdateEvent.PivotChanged(gr, old, pivot));
			}
		})).pos(FO, next_y_pos(1)));
		//
		updcom.add(UpdateEvent.ModelLoad.class, event -> updateFields());
		updcom.add(UpdateEvent.GroupRenamed.class, event -> {
			if(name.selected()) Element.select(null);
			if(FMT.MODEL.first_selected_group() != null) name.text(event.group().id);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> updateFields());
		updcom.add(UpdateEvent.PolygonSelected.class, event -> updateFields());
		updcom.add(UpdateEvent.TexGroupAdded.class, event -> refreshTexGroups(null, true));
		updcom.add(UpdateEvent.TexGroupRenamed.class, event -> refreshTexGroups(null, true));
		updcom.add(UpdateEvent.TexGroupRemoved.class, event -> refreshTexGroups(null, true));
		updcom.add(UpdateEvent.PivotAdded.class, event -> refreshPivots(null, true));
		updcom.add(UpdateEvent.PivotRenamed.class, event -> refreshPivots(null, true));
		updcom.add(UpdateEvent.PivotRemoved.class, event -> refreshPivots(null, true));
	}

	private void updateFields(){
		Group group = FMT.MODEL.first_selected_group();
		if(group == null){
			name.text(NOGROUPSEL);
			texx.selectEntry(0);
			texy.selectEntry(0);
		}
		else{
			name.text(group.id);
			texx.selectKey(group.texSizeX + "");
			texy.selectKey(group.texSizeY + "");
		}
		refreshTexGroups(group, false);
		refreshPivots(group, false);
	}

	private void refreshTexGroups(Group group, boolean ck){
		texg.clear();
		texg.addEntry("none", null);
		for(TextureGroup tg : TextureManager.getGroups()){
			texg.addEntry(tg.name, tg.name);
		}
		if(ck) group = FMT.MODEL.first_selected_group();
		if(group == null || group.texgroup == null) texg.selectEntry(0);
		else texg.selectKey(group.texgroup.name);
	}

	private void refreshPivots(Group group, boolean ck){
		pivots.clear();
		for(Pivot pv : FMT.MODEL.pivots()){
			pivots.addEntry(pv.id, pv);
		}
		if(ck) group = FMT.MODEL.first_selected_group();
		if(group == null) pivots.selectEntry(0);
		else pivots.selectKey(group.pivot);
	}

}
