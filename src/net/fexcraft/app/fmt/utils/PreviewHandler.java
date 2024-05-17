package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.Importer;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.json.JsonMap;

public class PreviewHandler {

	public static final ArrayList<Model> previews = new ArrayList<>();

	public static Model loadFrame(File file){
		// TODO Auto-generated method stub
		return null;
	}

	public static Model loadFMTB(File file){
		Model model = new Model(null, "");
		SaveHandler.open(model, file, true);
		previews.add(model);
		UpdateHandler.update(new UpdateEvent.HelperLoad(model));
		return model;
	}

	public static Model load(File file, Importer porter, JsonMap map){
		Model model = new Model(null, map.getString("name", file.getName()));
		porter._import(model, file);
		previews.add(model);
		UpdateHandler.update(new UpdateEvent.HelperLoad(model));
		return model;
	}

	public static void clear(){
		previews.removeIf(preview -> {
			UpdateHandler.update(new UpdateEvent.HelperUnload(preview));
			return true;
		});
	}

	public static List<Model> getLoaded(){
		return previews;
	}

}
