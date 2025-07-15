package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimTree extends Editor {

	private static UpdateCompound updcom = new UpdateCompound();

	public AnimTree(){
		super(Trees.ANIMATION.id, "Animation Tree", true);
		addTreeIcons(Trees.ANIMATION);
		updcom.add(ModelLoad.class, event -> {});
		updcom.add(ModelUnload.class, event -> {});
		updcom.add(GroupAdded.class, event -> {});
		updcom.add(GroupRenamed.class, event -> {});
		updcom.add(GroupRemoved.class, event -> {});
		UpdateHandler.register(updcom);
		//
	}

	@Override
	protected float topSpace(){
		return 90f;
	}

}
