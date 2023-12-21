package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
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
import java.util.List;

public class PivotExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("config", "fvtm");
	private static final ExSetList settings = new ExSetList();

	public PivotExporter(){
		settings.add(new StringArraySetting("as", "partslots", "exporter-pivot", "partslots", "swivelpoints"));
	}

	@Override
	public String id(){
		return "fvtm-pivot";
	}

	@Override
	public String name(){
		return "FVTM Config - Pivot Based";
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
		return settings;
	}

	@Override
	public boolean nogroups(){
		return true;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		JsonMap obj = new JsonMap();
		String val = settings.get(0).value();
		switch(val){
			case "partslots":{
				JsonMap map = new JsonMap();
				for(Pivot pivot : model.pivots()){
					if(pivot.root) continue;
					JsonArray array = new JsonArray.Flat();
					array.add((model.orient.rect() ? pivot.pos.x : -pivot.pos.z) * .0625f);
					array.add((model.orient.rect() ? pivot.pos.y : -pivot.pos.y) * .0625f);
					array.add((model.orient.rect() ? pivot.pos.z : -pivot.pos.x) * .0625f);
					array.add(pivot.id);
					array.add(0.25f);
					if(pivot.rot.x != 0f || pivot.rot.y != 0f || pivot.rot.z != 0f){
						array.add(pivot.rot.x);
						array.add(pivot.rot.y);
						array.add(pivot.rot.z);
					}
					map.add(pivot.id, array);
				}
				obj.add("PartSlots", map);
				break;
			}
			case "swivelpoints":{
				JsonMap map = new JsonMap();
				for(Pivot pivot : model.pivots()){
					if(pivot.root) continue;
					JsonMap sp = new JsonMap();
					JsonArray array = new JsonArray.Flat();
					array.add((model.orient.rect() ? pivot.pos.x : -pivot.pos.z) * .0625f);
					array.add((model.orient.rect() ? pivot.pos.y : -pivot.pos.y) * .0625f);
					array.add((model.orient.rect() ? pivot.pos.z : -pivot.pos.x) * .0625f);
					sp.add("pos", array);
					sp.add("parent", pivot.parent().root ? "vehicle" : pivot.parent().id);
					if(pivot.rot.y != 0f) sp.add("yaw", pivot.rot.y);
					if(pivot.rot.x != 0f) sp.add("pitch", pivot.rot.x);
					if(pivot.rot.z != 0f) sp.add("roll", pivot.rot.z);
					map.add(pivot.id, sp);
				}
				obj.add("SwivelPoints", map);
				break;
			}
		}
		JsonHandler.print(file, obj, PrintOption.SPACED);
		return "export.complete";
	}

}
