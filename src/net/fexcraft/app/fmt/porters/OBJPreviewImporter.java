package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.ObjPreviewWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.utils.ObjParser;
import net.fexcraft.lib.common.utils.ObjParser.ObjModel;

public class OBJPreviewImporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Wavefront Obj Model", "*.obj" };

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		GroupCompound compound = new GroupCompound(file);
		try{
			ObjModel model = new ObjParser(new FileInputStream(file)).readComments(false).readModel(true).parse();
			ArrayList<String> list = new ArrayList<>(model.polygons.keySet());
			for(Entry<String, ArrayList<TexturedPolygon>> entry : model.polygons.entrySet()){
				compound.add(new ObjPreviewWrapper(compound, file, entry.getKey(), entry.getValue(), list.indexOf(entry.getKey())), entry.getKey(), true);
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return compound;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, ArrayList<TurboList> groups, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String getId(){
		return "obj_importer";
	}

	@Override
	public String getName(){
		return "Wavefront OBJ (ViewOnly)";
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
