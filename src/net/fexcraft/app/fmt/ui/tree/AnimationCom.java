package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;

import static net.fexcraft.app.fmt.settings.Settings.ASK_ANIMATION_REMOVAL;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_TEXT_2;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimationCom extends Element {

	protected Animation animation;

	public AnimationCom(Animation anim){
		animation = anim;
		onclick = ci -> AnimationTreeTab.selAnim(this);
		size(EDITOR_CONTENT - 10, 28);
	}

	@Override
	public void init(Object... args){
		text(animation.id());
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(26, 26).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			animation.enabled = !animation.enabled;
		}).hint("tree.animation.enabled").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(26, 26).pos(EDITOR_CONTENT - 32 * 3, 1).onclick(ci -> {
			Runnable run = () -> {
				Group group = ((AGroupCom)root.root).group;
				group.animations.remove(animation);
				UpdateHandler.update(new UpdateEvent.GroupAnimationRemoved(group, animation));
			};
			if(ASK_ANIMATION_REMOVAL.value){
				FMT.UI.createDialog(500, 80, "tree.mode.animation")
					.addText(0, "tree.animation.removal")
					.consumer(d -> run.run(), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else run.run();
		}).hint("tree.animation.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(26, 26).pos(EDITOR_CONTENT - 32 * 4, 1).onclick(ci -> {
			EditorRoot.setMode(EditorRoot.EditorMode.ANIMATION);
		}).hint("tree.animation.editor").hide());
		updateLabelColor();
	}

	protected void updateLabelColor(){
		color((animation.enabled ? Settings.POLYGON_NORMAL : Settings.POLYGON_INVISIBLE).value);
		text_color(GENERIC_TEXT_2.value.packed);
	}

}
