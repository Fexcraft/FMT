package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import java.util.ArrayList;
import java.util.Map;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;
import static net.fexcraft.app.fmt.ui.Field.FieldType.TEXT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelEditorTab extends EditorTab {

	public ETabCom general;
	public ETabCom exports;
	private Field name;
	private DropList<Integer> texx;
	private DropList<Integer> texy;
	private DropList<String> texg;
	private DropList<ModelOrientation> orient;

	public ModelEditorTab(){
		super(EditorRoot.EditorMode.MODEL);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		container.add((general = new ETabCom()), lang_prefix + "general", 340);
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.name"));
		general.add((name = new Field(TEXT, FF, field -> FMT.MODEL.name(field.get_text()))).pos(FO, next_y_pos(1)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture_size"));
		general.add((texx = new DropList<Integer>(F2S).onchange((key, val) -> {
			FMT.MODEL.texSizeX = val;
			FMT.MODEL.recompile();
			UpdateHandler.update(new UpdateEvent.ModelTextureSize(FMT.MODEL, FMT.MODEL.texSizeX, FMT.MODEL.texSizeY));
		})).pos(F20, next_y_pos(1)));
		general.add((texy = new DropList<Integer>(F2S).onchange((key, val) -> {
			FMT.MODEL.texSizeY = val;
			FMT.MODEL.recompile();
			UpdateHandler.update(new UpdateEvent.ModelTextureSize(FMT.MODEL, FMT.MODEL.texSizeX, FMT.MODEL.texSizeY));
		})).pos(F21, next_y_pos(0)));
		for(int res : TextureManager.RESOLUTIONS){
			texx.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
			texy.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
		}
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.texture_group"));
		general.add((texg = new DropList<String>(FF).onchange((key, val) -> {
			FMT.MODEL.texgroup = TextureManager.getGroup(val);
			UpdateHandler.update(new UpdateEvent.ModelTexGroup(FMT.MODEL, FMT.MODEL.texgroup));
		})).pos(FO, next_y_pos(1)));
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.orientation"));
		general.add((orient = new DropList<ModelOrientation>(FF).onchange((key, val) -> {
			FMT.MODEL.orient = val;
			UpdateHandler.update(new UpdateEvent.ModelOrientEvent(FMT.MODEL, FMT.MODEL.orient));
		})).pos(FO, next_y_pos(1)));
		for(ModelOrientation mo : ModelOrientation.values()){
			orient.addEntry(mo.name(), mo);
		}
		general.add(new TextElm(0, next_y_pos(1), FF).translate(lang_prefix + "general.export_groups"));
		general.add(new Element().color(GENERIC_FIELD.value).pos(FO, next_y_pos(1)).size(FF, FS)
			.translate("dialog.button.open").text_centered(true).hoverable(true)
			.onclick(ci -> ExportManager.showGroupSelectionDialog(null)));
		//
		container.add((exports = new ETabCom()), lang_prefix + "export", 300);
		exports.add(new TextElm(FO, next_y_pos(-1) + 2, FF, lang_prefix + "export.add_value", GENERIC_FIELD.value)
			.hoverable(true).onclick(ci -> openExportEntryEditor("new entry", "entry value")));
		exports.add(new TextElm(FO, next_y_pos(1), FF, lang_prefix + "export.add_array_value", GENERIC_FIELD.value)
			.hoverable(true).onclick(ci -> {}));
		Scrollable scroll = new Scrollable(true, 90);
		exports.add(scroll.pos(5, next_y_pos(1)));
		scroll.updateSize(FF + 5, 290);
		//
		updcom.add(UpdateEvent.ModelLoad.class, event -> updateFields(scroll));
		updcom.add(UpdateEvent.TexGroupAdded.class, event -> refreshTexGroups());
		updcom.add(UpdateEvent.TexGroupRenamed.class, event -> refreshTexGroups());
		updcom.add(UpdateEvent.TexGroupRemoved.class, event -> refreshTexGroups());
		updcom.add(UpdateEvent.ModelExportValue.class, event -> refreshExportValues(scroll));
	}

	private void updateFields(Scrollable scroll){
		name.text(FMT.MODEL.name);
		texx.selectKey(FMT.MODEL.texSizeX + "");
		texy.selectKey(FMT.MODEL.texSizeY + "");
		orient.selectValue(FMT.MODEL.orient);
		refreshTexGroups();
		refreshExportValues(scroll);
	}

	private void refreshTexGroups(){
		texg.clear();
		texg.addEntry("none", null);
		for(TextureGroup group : TextureManager.getGroups()){
			texg.addEntry(group.name, group.name);
		}
		if(FMT.MODEL == null || FMT.MODEL.texgroup == null) texg.selectEntry(0);
		else texg.selectKey(FMT.MODEL.texgroup.name);
	}

	private void refreshExportValues(Scrollable scroll){
		scroll.remElmIf(elm -> elm instanceof TextElm);
		for(Map.Entry<String, String> entry : FMT.MODEL.export_values.entrySet()){
			scroll.add(new TextElm(5, 0, scroll.w - 30, "[V] " + entry.getKey(), GENERIC_BACKGROUND_1.value)
				.check_mode(CheckMode.IN_ROOT).onclick(ci -> openExportEntryEditor(entry.getKey(), entry.getValue())));
			scroll.lastElement().add(new HidingElm().pos(scroll.lastElement().w - FS, 0).size(FS, FS)
				.texture("icons/component/remove").hoverable(true).onclick(ci -> {
					FMT.MODEL.export_values.remove(entry.getKey());
					UpdateHandler.update(new UpdateEvent.ModelExportValue(FMT.MODEL, entry.getKey(), entry.getValue(), false));
				}));
		}
		for(Map.Entry<String, ArrayList<String>> entry : FMT.MODEL.export_listed_values.entrySet()){
			scroll.add(new TextElm(5, 0, scroll.w - 30, "[A] " + entry.getKey(), GENERIC_BACKGROUND_1.value)
				.check_mode(CheckMode.IN_ROOT).onclick(ci -> {}));
			scroll.lastElement().add(new HidingElm().pos(scroll.lastElement().w - FS, 0).size(FS, FS)
				.texture("icons/component/remove").hoverable(true).onclick(ci -> {
					FMT.MODEL.export_listed_values.remove(entry.getKey());
					UpdateHandler.update(new UpdateEvent.ModelExportValue(FMT.MODEL, entry.getKey(), null, true));
				}));
		}
		scroll.updateBar();
	}

	private void openExportEntryEditor(String key, String val){
		Field name = new Field(TEXT, 390);
		Field value = new Field(TEXT, 390);
		FMT.UI.createDialog(400, 200, lang_prefix + "export.create_entry")
			.addText(0, "editor.model.export.entry_name")
			.addRowElm(1, name)
			.addText(2, "editor.model.export.entry_value")
			.addRowElm(3, value)
			.consumer(d -> {
				FMT.MODEL.export_values.put(name.get_text(), value.get_text());
				UpdateHandler.update(new UpdateEvent.ModelExportValue(FMT.MODEL, name.get_text(), value.get_text(), false));
			}, null)
			.buttons(100, Dialog.DialogButton.ADD, Dialog.DialogButton.CANCEL);
		name.text(key);
		value.text(val);
	}

}
