package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;

import java.io.File;
import java.util.List;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public interface Importer {
	
	public String id();
	
	public String name();
	
	public FileType extensions();
	
	public List<String> categories();
	
	public List<Setting<?>> settings();
	
	public String _import(Model model, File file);

}
