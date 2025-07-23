package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.DynAnimComponent;
import net.fexcraft.app.fmt.ui.trees.AnimTree;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimationEditor extends Editor {

	public AnimationEditor(){
		super("anim_editor", "Animation Editor", false);
	}

	public void refreshAnimData(){
		clearComponents();
		if(AnimTree.SELECTED == null) return;
		FvtmTypes.ProgRef ref = FvtmTypes.getProgRef(AnimTree.SELECTED.animation().id());
		for(int idx = 0; idx < ref.args().length; idx++){
			addComponent(new DynAnimComponent(AnimTree.SELECTED.animation(), ref, idx));
		}
	}

}
