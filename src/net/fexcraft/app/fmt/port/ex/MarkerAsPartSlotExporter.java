package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.StringArraySetting;
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

public class MarkerAsPartSlotExporter implements Exporter {

	private static final List<String> categories = Arrays.asList("config", "fvtm");

	public MarkerAsPartSlotExporter(){}

	@Override
	public String id(){
		return "fvtm-marker-partslot";
	}

	@Override
	public String name(){
		return "FVTM Config - Marker as PartSlot";
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
		JsonMap obj = new JsonMap();
		JsonMap map = new JsonMap();
		for(Group group : groups){
			int idx = 0;
			for(Polygon poly : group){
				if(!poly.getShape().isMarker()) continue;
				JsonArray array = new JsonArray.Flat();
				array.add((model.orient.rect() ? poly.pos.x : -poly.pos.z) * .0625f);
				array.add((model.orient.rect() ? poly.pos.y : -poly.pos.y) * .0625f);
				array.add((model.orient.rect() ? poly.pos.z : -poly.pos.x) * .0625f);
				array.add(group.id);
				array.add(0.25f);
				if(poly.rot.x != 0f || poly.rot.y != 0f || poly.rot.z != 0f){
					array.add(poly.rot.x);
					array.add(poly.rot.y);
					array.add(poly.rot.z);
				}
				map.add(poly.name(true) == null ? group.id + "_" + idx : poly.name(), array);
				idx++;
			}
		}
		obj.add("PartSlots", map);
		JsonHandler.print(file, obj, PrintOption.SPACED);
		return "export.complete";
	}

}
