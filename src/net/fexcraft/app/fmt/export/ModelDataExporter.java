package net.fexcraft.app.fmt.export;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;

public class ModelDataExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("config", "fvtm");

	@Override
	public String id(){
		return "fvtm-modeldata";
	}

	@Override
	public String name(){
		return "FVTM Config - ModelData";
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
		return true;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		JsonMap map = new JsonMap();
		model.export_listed_values.forEach((key, val) -> {
			JsonArray array = new JsonArray();
			val.forEach(v -> array.add(v));
			map.add(key, array);
		});
		model.export_values.forEach((key, val) -> {
			try{
				if(NumberUtils.isNumber(val)){
					if(val.contains(".")) map.add(key, Float.parseFloat(val));
					else map.add(key, Integer.parseInt(val));
				}
				else if(val.equals("true") || val.equals("false")) map.add(key, Boolean.parseBoolean(val));
				else map.add(key, val);
			}
			catch(Exception e){
				Logging.log(e);
			}
		});
		JsonMap obj = new JsonMap();
		obj.add("ModelData", map);
		JsonHandler.print(file, obj, PrintOption.SPACED);
		return "export.complete";
	}

}
