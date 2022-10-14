package net.fexcraft.app.fmt.export;

import java.io.File;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public interface Exporter {
	
	public String id();
	
	public String name();
	
	public FileType extensions();
	
	public List<String> categories();
	
	public List<Setting<?>> settings();
	
	public boolean nogroups();
	
	public String export(Model model, File file, List<Group> groups);

}
