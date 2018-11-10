package net.fexcraft.app.fmt.porters;

import java.io.File;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.ObjPreviewWrapper;

public class OBJPreviewImporter extends InternalPorter {
	
	private static final String[] extensions = new String[]{ ".obj" };

	@Override
	public GroupCompound importModel(File file){
		GroupCompound compound = new GroupCompound();
		compound.add(new ObjPreviewWrapper(compound, file));
		return compound;
	}

	@Override
	public String exportModel(GroupCompound compound, File file){
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

}
