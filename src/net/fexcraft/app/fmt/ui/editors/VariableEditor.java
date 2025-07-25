package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.DynAttrComponent;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.fvtm.VehAttr;

import java.util.Map;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VariableEditor extends Editor {

	public VariableEditor(){
		super("var_editor", "Var./Attr. Editor", false);
		UpdateHandler.UpdateCompound updcom = new UpdateHandler.UpdateCompound();
		updcom.add(UpdateEvent.ModelLoad.class, event -> refreshVarData(event.model()));
		updcom.add(UpdateEvent.ModelUnload.class, event -> refreshVarData(event.model()));
		UpdateHandler.register(updcom);
	}

	public void refreshVarData(Model model){
		clearComponents();
		for(Map.Entry<String, VehAttr> entry : model.vehattrs.entrySet()){
			addComponent(new DynAttrComponent(entry.getKey(), entry.getValue()));
		}
	}

}
