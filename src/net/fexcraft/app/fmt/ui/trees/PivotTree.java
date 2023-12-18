package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.Logging;

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
		UpdateHandler.register(updcom);
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
