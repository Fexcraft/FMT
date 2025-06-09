package net.fexcraft.app.fmt.port.ex;

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
public class MarkerAsSeatConfExporter implements Exporter {

	private static final List<String> categories = Arrays.asList("config", "fvtm");

	public MarkerAsSeatConfExporter(){}

	@Override
	public String id(){
		return "fvtm-marker-seatconf";
	}

	@Override
	public String name(){
		return "FVTM Config - Marker as Seat Conf";
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
				if(!poly.getShape().isMarker()) continue;
				JsonMap seat = new JsonMap();
				JsonArray array = new JsonArray.Flat();
				array.add((model.orient.rect() ? poly.pos.x : -poly.pos.z) * .0625f);
				array.add((model.orient.rect() ? poly.pos.y : -poly.pos.y) * .0625f);
				array.add((model.orient.rect() ? poly.pos.z : -poly.pos.x) * .0625f);
				seat.add("pos", array);
				seat.add("dismount", array.copy());
				map.add(poly.name(), seat);
			}
		}
		JsonHandler.print(file, map, PrintOption.SPACED);
		return "export.complete";
	}

}
