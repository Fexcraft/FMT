package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.JsonEditor;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.fvtm.FVTMConfigEditor;

import java.io.File;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.workspace.Workspace.FILES_PANEL_DIR;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileElm extends Element {

	protected VFileType type;
	public final File file;

	public FileElm(VFileType type, File file){
		super();
		this.type = type;
		this.file = file;
		text(file.getName());
		text_pos(35, 0);
		text_autoscale();
		color(GENERIC_BACKGROUND_1.value);
		hoverable(true);
		pos(5, 0);
		onclick(ci -> {
			if(ci.button() == 0){
				switch(type){
					case FVTM_CONFIG:{
						new FVTMConfigEditor(file, null);
						break;
					}
					case FVTM_FILE:
					case JSON:{
						new JsonEditor(file);
						break;
					}
					case LANG:
					case TOML:
					case OBJ:{
						Workspace.open(file);
						break;
					}
				}
			}
			else if(ci.button() == 1){
				//TODO r-menu
			}
		});
	}

	@Override
	public void init(Object... args){
		check_mode(FMT.WORKSPACE.cm_in_packs);
		size(root.root instanceof DirElm ? root.root.w - 5 : FILES_PANEL_DIR, 30);
		add(new Element().pos(-1, -1).size(32, 32).texture("icons/filetree/" + type.filename())
			.check_mode(FMT.WORKSPACE.cm_in_packs));
	}

}
