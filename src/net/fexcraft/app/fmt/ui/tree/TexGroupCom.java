package net.fexcraft.app.fmt.ui.tree;

import com.google.common.io.Files;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.*;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TexGroupCom extends TTabCom {

	protected TextureGroup group;
	protected DropList<Integer> texx;
	protected DropList<Integer> texy;

	public TexGroupCom(TextureGroup group){
		this.group = group;
	}

	@Override
	public void init(Object... args){
		super.init(group.name, EDITOR_CONTENT);
		add(new HidingElm().hoverable(true).texture("icons/component/remove").size(28, 28).pos(EDITOR_CONTENT - 32 * 2, 1).onclick(ci -> {
			if(ASK_TEXTURE_GROUP_REMOVAL.value){
				int use = FMT.MODEL.texgroup == group ? 1 : 0;
				for(Group mg : FMT.MODEL.allgroups()){
					if(mg.texgroup == group) use++;
				}
				FMT.UI.createDialog(550, 120, "tree.mode.texture")
					.addText(0, "tree.texture.group_removal")
					.addText(1, group.name + " (" + use + " usages)")
					.consumer(d -> FMT.MODEL.remTexGroup(group), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else FMT.MODEL.remTexGroup(group);
		}).hint("tree.texture.group_remove").hide());
		int inc = -30;
		//
		container.add(new TextElm(5, inc += 30, FF, "tree.texture.group_size"));
		container.add((texx = new DropList<Integer>(F2S).onchange((key, val) -> {
			group.width = val;
			FMT.MODEL.recompile();
			UpdateHandler.update(new UpdateEvent.TexGroupSize(group));
		})).pos(F20, inc += 30));
		container.add((texy = new DropList<Integer>(F2S).onchange((key, val) -> {
			group.height = val;
			FMT.MODEL.recompile();
			UpdateHandler.update(new UpdateEvent.TexGroupSize(group));
		})).pos(F21, inc));
		for(int res : TextureManager.RESOLUTIONS){
			texx.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
			texy.addEntry(res > 2000 ? res / 1024 + "K" : res + "", res);
		}
		texx.selectValue(group.width);
		texy.selectValue(group.height);
		//
		container.add(new Element().size(FF, 30).pos(5, inc += 35).translate("tree.texture.group_rename")
			.onclick(ci -> renameGroup(group.name)).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, inc += 35).translate("tree.texture.group_resize")
			.onclick(ci -> resizeGroup()).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, inc += 35).translate("tree.texture.group_generate")
			.onclick(ci -> generateGroup()).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, inc += 35).translate("tree.texture.group_select")
			.onclick(ci -> selectGroup()).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		orderComponents();
	}

	private void renameGroup(String text){
		Field field = new Field(Field.FieldType.TEXT, 480);
		FMT.UI.createDialog(500, 120, "tree.texture.rename.title")
			.addText(0, "tree.texture.rename.name")
			.addRowElm(1, field)
			.consumer(d -> {
				String name = field.get_text();
				if(FMT.MODEL.getTexGroup(name) != null){
					FMT.UI.createDialog(500, 120, "tree.texture.rename.title")
						.addText(0, "tree.texture.rename.duplicate")
						.addText(1, name)
						.consumer(dg -> renameGroup(name), null)
						.buttons(100, Dialog.DialogButton.RETRY, Dialog.DialogButton.CANCEL);
					return;
				}
				UpdateHandler.update(new UpdateEvent.TexGroupRenamed(group, group.name, group.name = name));
			}, null)
			.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
		field.text(text);
	}

	private void resizeGroup(){
		final DropList<Integer> scale = new DropList<>(480);
		FMT.UI.createDialog(500, 120, "tree.texture.resize.title")
			.addText(0, "tree.texture.resize.upscale")
			.addRowElm(1, scale)
			.consumer(d -> {
				try{
					int scl = scale.getSelVal();
					int x = group.width, ox = x;
					int y = group.height, oy = y;
					while(scl > 0){
						x *= 2;
						y *= 2;
						scl--;
					}
					group.texture.resize(x, y);
					group.texture.save();
					group.texture.reload();
					group.genPainterTex();
					log("Resized TextureGroup '" + group.name + "' to " + ox + ", " + oy + " with " + scale.getSelVal() + " times upscaling to " + x + " " + y + ".");
				}
				catch(Exception e){
					log(e);
				}
			}, null)
			.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.0"), 0);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.1"), 1);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.2"), 2);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.3"), 3);
		scale.selectEntry(0);
	}

	private void generateGroup(){
		group.texture.clear(null);
		FMT.MODEL.allgroups().forEach(elm -> {
			if(elm.texgroup == null || elm.texgroup == group){
				elm.forEach(poly -> poly.paintTex(group.texture, null));
			}
		});
		group.texture.getImage().position(0);
		group.texture.save();
		group.texture.reload();
		FMT.MODEL.recompile();
	}

	private void selectGroup(){
		FileChooser.choose(Translator.translate("tree.texture.select.title"), null, FileChooser.TYPE_IMG, false, file -> {
			if(file == null) return;
			try{
				Files.copy(file, group.texture.getFile());
			}
			catch(Throwable e){
				log(e);
			}
		});
	}

	@Override
	protected void orderComponents(){
		if(container.elements == null) return;
		fullheight = 205;
		container.size(w, fullheight);
		container.recompile();
		((TreeTab)root()).reorderComponents();
	}

	protected void updateTextColor(){
		text(group.name);
		color(PIVOT_NORMAL.value);
		text_color(GENERIC_TEXT_2.value.packed);
	}

	protected void updateTexSize(){
		if(texx == null) return;
		texx.selectValue(group.width);
		texy.selectValue(group.height);
	}

}
