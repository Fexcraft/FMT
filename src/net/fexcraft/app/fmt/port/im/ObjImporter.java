package net.fexcraft.app.fmt.port.im;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.ObjView;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.FRLObjParser;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_OBJ;

public class ObjImporter implements Importer {

	private static final List<String> categories = Arrays.asList("model");

	@Override
	public String _import(Model model, File file){
		try{
			var map = new FRLObjParser("", new FileInputStream(file)).parse();
			for(Entry<String, ArrayList<Polyhedron>> entry : map.entrySet()){
				for(Polyhedron hedron : entry.getValue()){
					model.add(null, entry.getKey(), new ObjView(model, hedron));
				}
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

}
