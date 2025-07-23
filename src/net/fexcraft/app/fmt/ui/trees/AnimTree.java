package net.fexcraft.app.fmt.ui.trees;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.trees.AnimComponent.AnimationLabel;
import net.fexcraft.app.fmt.update.UpdateEvent.*;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimTree extends Editor {

	private static UpdateCompound updcom = new UpdateCompound();
	public static AnimationLabel SELECTED;

	public AnimTree(){
		super(Trees.ANIMATION.id, "Animation Tree", true);
		addTreeIcons(Trees.ANIMATION);
		updcom.add(GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(GroupRemoved.class, event -> remGroup(event.group()));
		updcom.add(ModelLoad.class, event -> resizeGroups(event.model()));
		updcom.add(ModelUnload.class, event -> removeGroups(event.model()));
		UpdateHandler.register(updcom);
	}

	public static void select(AnimationLabel anim){
		AnimationLabel old = SELECTED;
		SELECTED = SELECTED == anim ? null : anim;
		if(old != null) old.update_color();
		if(anim != null) anim.update_color();
		Editor.ANIM_EDITOR.refreshAnimData();
	}

	@Override
	protected float topSpace(){
		return 90f;
	}

	private void addGroup(Group group){
		this.addComponent(new AnimComponent(group));
	}

	private void remGroup(Group group){
		this.removeComponent(getComponent(group));
	}

	private EditorComponent getComponent(Group group){
		for(EditorComponent com : this.components){
			if(((AnimComponent)com).group() == group) return com;
		}
		return null;
	}

	private void resizeGroups(Model model){
		components.forEach(com -> ((AnimComponent)com).resize());
	}

	private void removeGroups(Model model){
		for(Group group : model.allgroups()) remGroup(group);
	}

	public void reAddGroups(){
		for(Group group : FMT.MODEL.allgroups()) remGroup(group);
		for(Group group : FMT.MODEL.allgroups()) addGroup(group);
		for(EditorComponent component : components) ((AnimComponent)component).resize();
	}

}
