package net.fexcraft.app.fmt.ui.tree;

import com.google.common.io.Files;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.FileChooser;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.settings.Settings.*;
import static net.fexcraft.app.fmt.ui.FMTInterface.EDITOR_CONTENT;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FF;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TexGroupCom extends TTabCom {

	protected TextureGroup group;

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
					.consumer(d -> TextureManager.remGroup(group), null)
					.buttons(100, Dialog.DialogButton.CONFIRM, Dialog.DialogButton.CANCEL);
			}
			else TextureManager.remGroup(group);
		}).hint("tree.texture.group_remove").hide());
		container.add(new Element().size(FF, 30).pos(5, 5).translate("tree.texture.group_rename")
			.onclick(ci -> renameGroup(group.name)).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, 40).translate("tree.texture.group_resize")
			.onclick(ci -> resizeGroup()).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, 75).translate("tree.texture.group_generate")
			.onclick(ci -> generateGroup()).hoverable(true).color(GROUP_NORMAL.value).text_color(GENERIC_TEXT_2.value.packed));
		container.add(new Element().size(FF, 30).pos(5, 110).translate("tree.texture.group_select")
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
				if(TextureManager.getGroup(name) != null){
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
		final DropList<String> from = new DropList<>(480);
		final DropList<Integer> scale = new DropList<>(480);
		FMT.UI.createDialog(500, 180, "tree.texture.resize.title")
			.addText(0, "tree.texture.resize.copy_from")
			.addRowElm(1, from)
			.addText(2, "tree.texture.resize.upscale")
			.addRowElm(3, scale)
			.consumer(d -> {
				try{
					boolean model = from.getSelVal().equals("model");
					Group mg = model ? null : FMT.MODEL.get(from.getSelVal().substring(6));
					int scl = scale.getSelVal();
					int x = model ? FMT.MODEL.texSizeX : mg.texSizeX, ox = x;
					int y = model ? FMT.MODEL.texSizeY : mg.texSizeY, oy = y;
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
		from.addEntry(Translator.translate("tree.texture.resize.copy_from.model"), "model");
		for(Group mg : FMT.MODEL.allgroups()){
			from.addEntry(Translator.format("tree.texture.resize.copy_from.group", mg.id), "group-" + mg.id);
		}
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.0"), 0);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.1"), 1);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.2"), 2);
		scale.addEntry(Translator.translate("tree.texture.resize.upscale.3"), 3);
		from.selectEntry(0);
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
		FileChooser.chooseFile(Translator.translate("tree.texture.select.title"), "./", FileChooser.TYPE_IMG, false, file -> {
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
		fullheight = 145;
		container.size(w, fullheight);
		container.recompile();
		((TreeTab)root.root).reorderComponents();
	}

	protected void updateTextColor(){
		text(group.name);
		color(PIVOT_NORMAL.value);
		text_color(GENERIC_TEXT_2.value.packed);
	}

}
