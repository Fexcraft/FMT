package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.AutoUVPositioner;
import net.fexcraft.app.fmt.utils.Logging;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PivotTree extends Editor {

	private static UpdateCompound updcom = new UpdateCompound();

	public PivotTree(){
		super(Trees.PIVOT.id, "Pivot Tree", true);
		addTreeIcons(Trees.PIVOT);
		updcom.add(PivotAdded.class, event -> addPivot(event.pivot()));
		updcom.add(GroupAdded.class, event -> {
			Pivot pivot = FMT.MODEL.getP(event.group().pivot);
			if(getComponent(pivot) == null) addPivot(pivot);
		});
		updcom.add(PivotRemoved.class, event -> remPivot(event.pivot()));
		updcom.add(ModelLoad.class, event -> resizePivots(event.model()));
		updcom.add(ModelUnload.class, event -> removePivots(event.model()));
		add(new RunButton("editor.tree.pivot.add", 10, 30, 135, 24, () -> createPivot(), false));
		UpdateHandler.register(updcom);
	}

	private void createPivot(){
		String name = "pivot";
		if(hasPivot(name)){
			int i = 0;
			while(hasPivot(name + i)) i++;
			name += i;
		}
		Pivot pivot = new Pivot(name);
		FMT.MODEL.pivots().add(pivot);
		UpdateHandler.update(new PivotAdded(FMT.MODEL, pivot));
		GenericDialog.showOK("editor.tree.pivot", null, null, "editor.tree.pivot.added", "#" + pivot.id);
	}

	private boolean hasPivot(String name){
		for(Pivot pivot : FMT.MODEL.pivots()){
			if(pivot.id.equals(name)) return true;
		}
		return false;
	}

	@Override
	protected float topSpace(){
		return 60f;
	}

	private void addPivot(Pivot pivot){
		addComponent(new PivotComponent(pivot));
	}

	private void remPivot(Pivot pivot){
		removeComponent(getComponent(pivot));
	}

	private EditorComponent getComponent(Pivot pivot){
		for(EditorComponent com : this.components){
			if(((PivotComponent)com).pivot() == pivot) return com;
		}
		return null;
	}

	private void resizePivots(Model model){
		components.forEach(com -> ((PivotComponent)com).resize());
	}

	private void removePivots(Model model){
		for(Pivot pivot : model.pivots()) remPivot(pivot);
	}

}
