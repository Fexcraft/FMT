package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.workspace.ConfigUtils;
import net.fexcraft.app.fmt.workspace.IconFrameFrame;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigEditorTab extends EditorTab {

	private ETabCom pack_utils;
	private ETabCom file_utils;
	private ETabCom run_utils;

	public ConfigEditorTab(){
		super(EditorRoot.EditorMode.CONFIG);
	}

	@Override
	public void init(Object... objs){
		super.init(objs);
		next_y_elm_pos = 5;
		container.add(pack_utils = new ETabCom("pack_utils"), "editor.config.pack_utils", 220);
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.pack_new", ci -> ConfigUtils.createNewFvtmPack()));
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.gen_asset_dirs", ci -> ConfigUtils.genAssetDirs()));
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.gen_icons", ci -> ConfigUtils.genIconsInPack()));
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.content_new", ci -> ConfigUtils.createNewContent()));
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.icon_from_view",
			ci -> FMT.WORKSPACE.selectPack(pack -> FMT.UI.add(new IconFrameFrame(), pack))));
		pack_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.pack_utils.road_assets", ci -> ConfigUtils.genRoadAssets()));
		//
		next_y_elm_pos = 5;
		container.add(file_utils = new ETabCom("file_utils"), "editor.config.file_utils", 100);
		file_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.file_utils.open_json", ci -> ConfigUtils.openJson()));
		file_utils.add(new RunElm(FO, next_y_pos(1), FF, "editor.config.file_utils.mirror_lang", ci -> ConfigUtils.mirrorLang()));
		//
		next_y_elm_pos = 5;
		container.add(run_utils = new ETabCom("run_utils"), "editor.config.run_utils", 100);
		run_utils.add(new RunElm(FO, next_y_pos(1), FF, "Run 1.12", ci -> WorkspaceViewer.run(true)));
		run_utils.add(new RunElm(FO, next_y_pos(1), FF, "Run 1.20+", ci -> WorkspaceViewer.run(false)));

	}



}
