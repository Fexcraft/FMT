package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class GroupSelector extends Element {

	private List<Group> selected = new ArrayList<>();

	@Override
	public void init(Object... args){
		size(((Number)args[0]).floatValue(), 400);
		color(GENERIC_BACKGROUND_0.value);
		float half = w * 0.5f;
		add(new TextElm(5, 0, half - 40, "group_sel_panel.select_preset"));
		add(new TextElm(half + 10, 0, half - 40, "group_sel_panel.name_preset"));
		Field field = new Field(Field.FieldType.TEXT, half - 10);
		add(field.pos(half + 10, 35));
		DropList<String> drop = new DropList<>(half);
		add(drop.pos(5, 35));
		redrop(drop, field, null);
		drop.onchange((key, val) -> {
			field.text(key);
		});
		add(new Element().pos(w - 32, 0).texture("icons/toolbar/save").size(32, 32)
			.hint("group_sel_panel.save_preset").onclick(ci -> {
				String sel = drop.getSelKey();
				String nkey = field.get_text();
				if(nkey.length() < 1) return;
				int idx = FMT.MODEL.export_group_preset_keys.indexOf(sel);
				if(idx >= 0){
					FMT.MODEL.export_group_preset_keys.remove(idx);
					ArrayList<String> groups = FMT.MODEL.export_group_presets.remove(idx);
					FMT.MODEL.export_group_preset_keys.add(nkey);
					FMT.MODEL.export_group_presets.add(groups);
				}
				else{
					FMT.MODEL.export_group_preset_keys.add(nkey);
					FMT.MODEL.export_group_presets.add(getSelectedIds());
				}
				redrop(drop, field, nkey);
			}));
		add(new Element().pos(half - 32, 0).texture("icons/component/remove").size(32, 32)
			.hint("group_sel_panel.remove_preset").onclick(ci -> {
				String sel = drop.getSelKey();
				int idx = FMT.MODEL.export_group_preset_keys.indexOf(sel);
				if(idx >= 0){
					FMT.MODEL.export_group_preset_keys.remove(idx);
					FMT.MODEL.export_group_presets.remove(idx);
					redrop(drop, field, null);
				}
			}));
	}

	private void redrop(DropList<String> drop, Field field, String select){
		drop.clear();
		drop.addEntry("new_preset", "new_preset");
		for(String key : FMT.MODEL.export_group_preset_keys){
			drop.addEntry(key, key);
		}
		if(select != null) drop.selectKey(select);
		else drop.selectEntry(0);
		field.text(drop.getSelKey());
	}

	public List<Group> getSelected(){
		return selected;
	}

	public ArrayList<String> getSelectedIds(){
		ArrayList<String> list = new ArrayList<>();
		for(Group group : selected) list.add(group.id);
		return list;
	}

}
