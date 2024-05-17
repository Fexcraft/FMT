package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.PreviewHandler;

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
	}

	@Override
	protected float topSpace(){
		return 60f;
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
		components.forEach(com -> ((PivotComponent)com).resize());
	}

	private void removeHelpers(){
		for(Model model : PreviewHandler.previews) remHelper(model);
	}

}
