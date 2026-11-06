package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.Importer;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PreviewHandler {

	public static final ArrayList<Model> previews = new ArrayList<>();
	public static boolean VISIBLE = true;
	public static Model SELECTED = null;

	public static Model loadFrame(File file){
		Model model = new Model(file, "");
		model.helper = true;
		model.name = "frame/" + model.name;
		//
		add(model);
		return model;
	}

	public static Model loadFMTB(File file){
		Model model = new Model(file, "");
		model.helper = true;
		SaveHandler.open(model, file, true);
		if(!model.name.startsWith("fmtb/")) model.name = "fmtb/" + model.name;
		add(model);
		return model;
	}

	public static Model load(File file, Importer porter, JsonMap map){
		Model model = new Model(file, map.getString("name", file.getName()));
		model.helper = true;
		model.helper_importer = porter;
		model.genDefTexGroup();
		if(!model.name.startsWith("import/")) model.name = "import/" + model.name;
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

	public static void duplicate(Model ref){
		Model model;
		if(ref.name.startsWith("frame/")) model = loadFrame(ref.file);
		else if(ref.name.startsWith("fmtb/")) model = loadFMTB(ref.file);
		else model = load(ref.file, ref.helper_importer, new JsonMap());
		model.pos.set(ref.pos);
		model.rot.set(ref.rot);
		model.scl.set(ref.scl);
	}

}
