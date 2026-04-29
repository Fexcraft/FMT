package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;

import java.io.File;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.workspace.Workspace.FILES_PANEL_DIR;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileElm extends Element {

	protected VFileType type;
	protected File file;

	public FileElm(Object type, File file){
		super();
		this.type = (VFileType)(type instanceof VFileType ? type : ((Object[])type)[0]);
		this.file = file;
		text(file.getName());
		text_pos(35, 0);
		text_autoscale();
		color(GENERIC_BACKGROUND_1.value);
		hoverable(true);
		pos(5, 0);
	}

	@Override
	public void init(Object... args){
		check_mode(CheckMode.IN_SPECIFIC);
		pickposroot = FMT.WORKSPACE.packs;
		size(root.root instanceof DirElm ? root.root.w - 5 : FILES_PANEL_DIR, 30);
		add(new Element().pos(-1, -1).size(32, 32).texture("icons/filetree/" + type.filename()));
	}

}
