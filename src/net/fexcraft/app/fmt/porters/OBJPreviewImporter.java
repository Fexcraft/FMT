package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.ObjPreviewWrapper;

public class OBJPreviewImporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ ".obj" };

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		GroupCompound compound = new GroupCompound(file);
		compound.add(new ObjPreviewWrapper(compound, file), null, true);
		return compound;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String getId(){
		return "obj_preview_importer";
	}

	@Override
	public String getName(){
		return "Wavefront OBJ Preview";
	}

	@Override
	public String[] getExtensions(){
		return extensions;
	}

	@Override
	public boolean isImporter(){
		return true;
	}

	@Override
	public boolean isExporter(){
		return false;
	}

	@Override
	public List<Setting> getSettings(boolean export){
		return nosettings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}

}
