package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.PreviewHandler;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HelperCom extends TTabCom {

	protected Model model;

	public HelperCom(Model model){
		this.model = model;
	}

	@Override
	public void init(Object... args){
		super.init(model.name, EDITOR_CONTENT);
		onclick(ci -> {
			PreviewHandler.SELECTED = model;
			updateTextColor();
			UpdateHandler.update(new UpdateEvent.HelperSelected(model));
		});
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			model.visible = !model.visible;
			UpdateHandler.update(new UpdateEvent.HelperVisiblity(model, model.visible));
		}).hint("tree.preview.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 32 * 3, 1).onclick(ci -> {
			if(ASK_HELPER_REMOVAL.value){
				FMT.UI.createDialog(500, 120, "tree.mode.preview")
					.addText(0, "tree.preview.removal")
					.addText(1, model.name + " (" + model.allgroups().size() + " groups)")
					.consumer(d -> PreviewHandler.remove(model), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else PreviewHandler.remove(model);
		}).hint("tree.preview.remove").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(28, 28).pos(EDITOR_CONTENT - 32 * 4, 1).onclick(ci -> {
			EditorRoot.setMode(EditorRoot.EditorMode.PREVIEW);
		}).hint("tree.preview.editor").hide());
		for(Group group : model.allgroups()) container.add(new HGroupCom(group));
		orderComponents();
	}

	@Override
	protected void orderComponents(){
		if(container.elements == null) return;
		fullheight = 5;
		HGroupCom com;
		for(Element elm : container.elements){
			com = (HGroupCom)elm;
			com.pos(5, fullheight);
			fullheight += 35;
		}
		container.size(w, fullheight);
		container.recompile();
		((TreeTab)root.root).reorderComponents();
	}

	protected void updateTextColor(){
		text(model.name);
		boolean sel = PreviewHandler.SELECTED == model;
		color((model.visible ? sel ? GROUP_SELECTED : GROUP_NORMAL : sel ? GROUP_INV_SEL : GROUP_INVISIBLE).value);
		text_color((sel ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

}
