package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.HidingElm;
import net.fexcraft.app.fmt.ui.editor.EditorRoot;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;
import net.fexcraft.app.json.JsonMap;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AGroupCom extends TTabCom {

	protected Group group;

	public AGroupCom(Group group){
		this.group = group;
	}

	@Override
	public void init(Object... args){
		super.init(group.id, EDITOR_CONTENT);
		onclick(ci -> {
			FMT.MODEL.select(group);
			updateTextColor();
		});
		add(new HidingElm().hoverable(true).texture("icons/component/visible").size(28, 28).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			group.visible = !group.visible;
			UpdateHandler.update(new UpdateEvent.GroupVisibility(group, group.visible));
		}).hint("tree.polygon.group.visible").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/add").size(28, 28).pos(EDITOR_CONTENT - 32 * 3, 1)
			.onclick(ci -> openAddAnimation()).hint("tree.animation.add").hide());
		add(new HidingElm().hoverable(true).texture("icons/component/edit").size(28, 28).pos(EDITOR_CONTENT - 32 * 4, 1)
			.onclick(ci -> EditorRoot.setMode(EditorRoot.EditorMode.GROUP)).hint("tree.polygon.group.editor").hide());
		group.animations.forEach(anim -> container.add(new AnimationCom(anim)));
		orderComponents();
		if(group.animations.isEmpty()) hide();
	}

	@Override
	protected void orderComponents(){
		fullheight = container.visible ? 5 : 0;
		if(container.elements != null){
			AnimationCom com;
			for(Element elm : container.elements){
				com = (AnimationCom)elm;
				com.pos(5, fullheight);
				fullheight += 35;
			}
		}
		container.size(w, fullheight);
		container.recompile();
		((TreeTab)root.root).reorderComponents();
	}

	protected void updateTextColor(){
		color((group.visible ? group.selected ? Settings.GROUP_SELECTED : Settings.GROUP_NORMAL : group.selected ? Settings.GROUP_INV_SEL : Settings.GROUP_INVISIBLE).value);
		text_color((group.selected ? GENERIC_TEXT_1 : GENERIC_TEXT_2).value.packed);
	}

	public void addAnim(Animation anim){
		container.add(new AnimationCom(anim));
		orderComponents();
	}

	public void remAnim(Animation anim){
		container.remElmIf(elm -> elm instanceof AnimationCom com && com.animation == anim);
		orderComponents();
	}

	private void openAddAnimation(){
		DropList<String> cats = new DropList<>(490);
		DropList<FvtmTypes.ProgRef> refs = new DropList<>(490);
		FMT.UI.createDialog(500, 180, "tree.animation.add.title")
			.addText(0, "tree.animation.add.category")
			.addRowElm(1, cats)
			.addText(2, "tree.animation.add.type")
			.addRowElm(3, refs)
			.consumer(co -> {
				if(refs.noEntries() ) return;
				FvtmTypes.ProgRef ref = refs.getSelVal();
				if(ref == null) return;
				Animation anim = ref.anim().create(new JsonMap());
				group.animations.add(anim);
				if(group.animations.size() == 1) show();
				UpdateHandler.update(new UpdateEvent.GroupAnimationAdded(group, anim));
			}, null)
			.buttons(100, Dialog.DialogButton.ADD, Dialog.DialogButton.CANCEL);
		for(String cat : FvtmTypes.PROGRAM_CATS) cats.addEntry(cat, cat);
		cats.onchange((key, val) -> {
			refs.clear();
			var var = FvtmTypes.PROGRAMS.stream().filter(r -> r.cat().equals(val)).toList();
			for(FvtmTypes.ProgRef ref : var) refs.addEntry(ref.name(), ref);
			refs.selectEntry(0);
		});
		cats.selectEntry(0);
		cats.applyChange();
		refs.selectEntry(0);
	}

}
