package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.Importer;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.json.JsonMap;

public class PreviewHandler {

	public static final ArrayList<Model> previews = new ArrayList<>();
	public static Model SELECTED = null;

	public static Model loadFrame(File file){
		// TODO Auto-generated method stub
		return null;
	}

	public static Model loadFMTB(File file){
		Model model = new Model(null, "fmtb/" + file.getName().substring(0, file.getName().lastIndexOf(".")));
		model.helper = true;
		SaveHandler.open(model, file, true);
		add(model);
		return model;
	}

	public static Model load(File file, Importer porter, JsonMap map){
		Model model = new Model(null, map.getString("name", file.getName()));
		porter._import(model, file);
		add(model);
		return model;
	}

	public static void clear(){
		previews.removeIf(preview -> {
			UpdateHandler.update(new UpdateEvent.HelperRemoved(preview));
			return true;
		});
	}

	public static List<Model> getLoaded(){
		return previews;
	}

	private static void add(Model model){
		model.helper = true;
		previews.add(model);
		model.allgroups().forEach(group -> group.selected = false);
		UpdateHandler.update(new UpdateEvent.HelperAdded(model));
	}

	public static void remove(Model model){
		if(previews.remove(model)){
			UpdateHandler.update(new UpdateEvent.HelperRemoved(model));
		}
	}

	public static void select(Model model){
		SELECTED = SELECTED == model ? null : model;
		UpdateHandler.update(new UpdateEvent.HelperSelected(SELECTED));
	}

}
