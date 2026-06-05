package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimationTreeTab extends TreeTab {

	public static AnimationCom SELECTED;

	public AnimationTreeTab(){
		super(TreeRoot.TreeMode.ANIMATION);
	}

	@Override
	public void init(Object... objs){
		super.init(0);
		updcom.add(UpdateEvent.GroupAdded.class, event -> addGroup(event.group()));
		updcom.add(UpdateEvent.GroupRemoved.class, event -> remGroup(event.group()));
		updcom.add(UpdateEvent.GroupRenamed.class, event -> {
			AGroupCom com = getGroupCom(event.group());
			if(com != null) com.text(event.group().id);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			AGroupCom com = getGroupCom(event.group());
			if(com != null) com.updateTextColor();
		});
		updcom.add(UpdateEvent.GroupAnimationAdded.class, event -> {
			AGroupCom com = getGroupCom(event.group());
			if(com != null) com.addAnim(event.anim());
		});
		updcom.add(UpdateEvent.GroupAnimationRemoved.class, event -> {
			AGroupCom com = getGroupCom(event.group());
			if(com != null) com.remAnim(event.anim());
			if(event.anim() == SELECTED.animation) selAnim(null);
		});
		updcom.add(UpdateEvent.ModelLoad.class, event -> reorderComponents());
		updcom.add(UpdateEvent.ModelUnload.class, event -> removeGroups());
	}

	private AGroupCom getGroupCom(Group group){
		for(Element elm : scrollable.elements){
			if(elm instanceof AGroupCom com){
				if(com.group == group) return com;
			}
		}
		return null;
	}

	private void addGroup(Group group){
		scrollable.container.add(new AGroupCom(group));
		reorderComponents();
	}

	private void remGroup(Group group){
		scrollable.container.remElmIf(e -> e instanceof AGroupCom com && com.group == group);
		reorderComponents();
	}

	private void removeGroups(){
		scrollable.container.remElmIf(e -> e instanceof AGroupCom);
		reorderComponents();
	}

	@Override
	public void reinsertComponents(){
		scrollable.container.remElmIf(e -> e instanceof AGroupCom);
		for(Group group : FMT.MODEL.allgroups()){
			scrollable.container.add(new AGroupCom(group));
		}
		reorderComponents();
	}

	@Override
	public void updateCounter(){
		//
	}

	public static void selAnim(AnimationCom com){
		AnimationCom old = SELECTED;
		SELECTED = SELECTED == com ? null : com;
		if(old != null) old.updateLabelColor();
		if(com != null) com.updateLabelColor();
		if(SELECTED != null){
			UpdateHandler.update(new UpdateEvent.GroupAnimationSelected(((AGroupCom)com.root.root).group, com.animation));
		}
		else{
			UpdateHandler.update(new UpdateEvent.GroupAnimationSelected(null, null));
		}
	}

}
