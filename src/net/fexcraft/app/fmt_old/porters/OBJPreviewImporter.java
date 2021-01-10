package net.fexcraft.app.fmt_old.porters;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.fexcraft.app.fmt_old.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt_old.utils.Setting;
import net.fexcraft.app.fmt_old.wrappers.GroupCompound;

public class OBJPreviewImporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Wavefront Obj Model", "*.obj" };

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		GroupCompound compound = new GroupCompound(file);
		/*try{
			//ObjModel model = new ObjParser(new FileInputStream(file)).readComments(false).readModel(true).parse();
			//TODO
			String[] groups = WavefrontObjUtil.getGroups(new FileInputStream(file));
			boolean objmode = false;
			if(groups.length == 0){
				groups = WavefrontObjUtil.getObjects(new FileInputStream(file));
				objmode = true;
			}
			for(int i = 0; i < groups.length; i++){
				compound.add(new ObjPreviewWrapper(compound, file, groups[i], objmode, i), groups[i], true);
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}*/
		return compound;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String getId(){
		return "obj_importer";
	}

	@Override
	public String getName(){
		return "Wavefront OBJ - Preview/Static";
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
