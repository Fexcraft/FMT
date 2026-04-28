package net.fexcraft.app.fmt.workspace;

import java.io.File;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.Frame;
import net.fexcraft.app.fmt.ui.Scrollable;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceName;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceRoot;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.settings.Settings.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Workspace extends Frame {

	private boolean loaded;
	private Scrollable packs;
	public String name;
	public File root_folder;
	
	public Workspace(){
		name = Settings.WORKSPACE_NAME.value;
		root_folder = new File(Settings.WORKSPACE_ROOT.value);
		pos(20, 20);
		size(1400, 800);
		color(GENERIC_BACKGROUND_0.value);
		border(RGB.BLACK);
	}

	@Override
	public void init(Object... args){
		add(new Element().size(w - 31, 30).color(GENERIC_BACKGROUND_1.value)
			.onclick(ci -> FMT.UI.setFrameOnTop(this))
			.translate("workspace.title", name).text_autoscale());
		add(new Element().size(30, 30).pos(w - 31, 0).texture("icons/component/exit")
			.hoverable(true).onclick(ci -> hide()));
		add((packs = new Scrollable(true, 30)).size(400, h - 31).pos(0, 31));
		packs.border(RGB.BLACK);
		packs.updateBar();
	}

	public void update(Setting<?> setting){
		if(setting.id.equals("name")){
			String oname = name;
			name = setting.value();
			FMT.updateTitle();
			UpdateHandler.update(new WorkspaceName(oname, name));
		}
		if(setting.id.equals("root")){
			File oroot = root_folder;
			root_folder = new File((String)setting.value());
			UpdateHandler.update(new WorkspaceRoot(oroot, root_folder));
		}
	}

	@Override
	public Element show(){
		if(!loaded){
			loaded = true;
			pos((FMT.SCALED_WIDTH - w) * 0.5f, (FMT.SCALED_HEIGHT - h) * 0.5f);
			reloadPacks();
		}
		return super.show();
	}

	public void reloadPacks(){

	}

}
