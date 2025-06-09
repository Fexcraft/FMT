package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmBoundingBoxExporter implements Exporter {

	private static final List<String> categories = Arrays.asList("config", "fvtm");

	public FvtmBoundingBoxExporter(){}

	@Override
	public String id(){
		return "fvtm-marker-boundingboxes";
	}

	@Override
	public String name(){
		return "FVTM Config - Bounding Boxes";
	}

	@Override
	public FileType extensions(){
		return FileChooser.TYPE_JSON;
	}

	@Override
	public List<String> categories(){
		return categories;
	}

	@Override
	public List<Setting<?>> settings(){
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean nogroups(){
		return false;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		JsonMap map = new JsonMap();
		for(Group group : groups){
			for(Polygon poly : group){
				if(!poly.getShape().isBoundingBox()) continue;
				Box box = (Box)poly;
				JsonMap bb = new JsonMap();
				JsonArray array = new JsonArray.Flat();
				array.add((model.orient.rect() ? poly.pos.x : -poly.pos.z) * .0625f);
				array.add((model.orient.rect() ? poly.pos.y : -poly.pos.y) * .0625f);
				array.add((model.orient.rect() ? poly.pos.z : -poly.pos.x) * .0625f);
				bb.add("pos", array);
				array = new JsonArray.Flat();
				array.add((model.orient.rect() ? box.size.x : -box.size.z) * .0625f);
				array.add((model.orient.rect() ? box.size.y : -box.size.y) * .0625f);
				array.add((model.orient.rect() ? box.size.z : -box.size.x) * .0625f);
				bb.add("size", array.copy());
				map.add(poly.name(), bb);
			}
		}
		JsonHandler.print(file, map, PrintOption.SPACED);
		return "export.complete";
	}

}
