package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.port.im.Importer;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.PreviewHandler;
import net.fexcraft.app.json.JsonMap;

import java.io.File;

import static net.fexcraft.app.fmt.ui.FileChooser.*;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HelperTree extends Editor {

	private static UpdateCompound updcom = new UpdateCompound();

	public HelperTree(){
		super(Trees.HELPER.id, "Helper Tree", true);
		addTreeIcons(Trees.PIVOT);
		updcom.add(HelperAdded.class, event -> addHelper(event.model()));
		updcom.add(HelperRemoved.class, event -> remHelper(event.model()));
		updcom.add(ModelLoad.class, event -> resizeHelpers());
		updcom.add(ModelUnload.class, event -> removeHelpers());
		UpdateHandler.register(updcom);
		this.add(new RunButton("editor.tree.helper.add_image", 10, 30, 135, 24, () -> FileChooser.chooseFile("...", ".", TYPE_IMG, false, file -> {
			if(file == null) return;
			PreviewHandler.loadFrame(file);
		}), false));
		this.add(new RunButton("editor.tree.helper.add_fmtb", 155, 30, 135, 24, () -> FileChooser.chooseFile("...", "./saves", TYPE_FMTB, false, file -> {
			if(file == null) return;
			PreviewHandler.loadFMTB(file);
		}), false));
		this.add(new RunButton("editor.tree.helper.add_import", 10, 60, 135, 24, () -> FileChooser.chooseFile("...", ".", TYPE_ANY, false, file -> {
			if(file == null) return;
			if(file.getName().endsWith(".")) file = new File(file.toString().substring(0, file.toString().length() - 1));
			Importer porter = ImportManager.getImporterFor(file);
			if(porter == null){
				log("ERROR: Could not find importer for helper/preview '" + file.getPath() + "'!");
				return;
			}
			PreviewHandler.load(file, porter, new JsonMap());
		}), false));
		this.add(new RunButton("editor.tree.helper.clear", 155, 60, 135, 24, () -> PreviewHandler.clear(), false));
	}

	@Override
	protected float topSpace(){
		return 90f;
	}

	private void addHelper(Model model){
		addComponent(new HelperComponent(model));
	}

	private void remHelper(Model model){
		removeComponent(getComponent(model));
	}

	private EditorComponent getComponent(Model model){
		for(EditorComponent com : this.components){
			if(((HelperComponent)com).model() == model) return com;
		}
		return null;
	}

	private void resizeHelpers(){
		components.forEach(com -> ((HelperComponent)com).resize());
	}

	private void removeHelpers(){
		for(Model model : PreviewHandler.previews) remHelper(model);
	}

}
