package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.AutoUVPositioner;
import net.fexcraft.app.fmt.utils.PreviewHandler;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_FMTB;
import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_IMG;

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
			PreviewHandler.loadFrame(file);
		}), false));
		this.add(new RunButton("editor.tree.helper.add_fmtb", 155, 30, 135, 24, () -> FileChooser.chooseFile("...", "./saves", TYPE_FMTB, false, file -> {
			PreviewHandler.loadFMTB(file);
		}), false));
		this.add(new RunButton("editor.tree.helper.add_import", 10, 60, 135, 24, () -> {}, false));
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
