package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.port.ex.ExportManager;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

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
		updcom.add(UpdateEvent.ModelLoad.class, event -> updateFields());
		updcom.add(UpdateEvent.TexGroupAdded.class, event -> refreshTexGroups());
		updcom.add(UpdateEvent.TexGroupRenamed.class, event -> refreshTexGroups());
		updcom.add(UpdateEvent.TexGroupRemoved.class, event -> refreshTexGroups());
	}

	private void updateFields(){
		name.text(FMT.MODEL.name);
		texx.selectKey(FMT.MODEL.texSizeX + "");
		texy.selectKey(FMT.MODEL.texSizeY + "");
		orient.selectValue(FMT.MODEL.orient);
		refreshTexGroups();
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

}
