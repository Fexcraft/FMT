package net.fexcraft.app.fmt.workspace;

import java.io.File;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceName;
import net.fexcraft.app.fmt.update.UpdateEvent.WorkspaceRoot;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;

public class Workspace {
	
	public String name;
	public File root_folder;
	
	public Workspace(){
		name = Settings.WORKSPACE_NAME.value;
		root_folder = new File(Settings.WORKSPACE_ROOT.value);
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
	

}
