package net.fexcraft.app.fmt.port.im;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.ObjView;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.utils.ObjParser;
import net.fexcraft.lib.common.utils.ObjParser.ObjModel;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_OBJ;

public class ObjImporter implements Importer {

	private static final List<String> categories = Arrays.asList("model");

	@Override
	public String _import(Model model, File file){
		try{
			ObjModel omodel = new ObjParser(new FileInputStream(file)).readComments(false).readModel(true).parse();
			ArrayList<String> list = new ArrayList<>(omodel.polygons.keySet());
			for(Entry<String, ArrayList<TexturedPolygon>> entry : omodel.polygons.entrySet()){
				model.add(entry.getKey(), new ObjView(model, entry.getValue()));
			}
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String id() {
		return "obj";
	}

	@Override
	public String name() {
		return ".OBJ (Wavefront Object | VIEW ONLY)";
	}

	@Override
	public FileChooser.FileType extensions() {
		return TYPE_OBJ;
	}

	@Override
	public List<String> categories() {
		return categories;
	}

	@Override
	public List<Setting<?>> settings() {
		return Collections.emptyList();
	}

}
