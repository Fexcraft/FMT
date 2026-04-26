package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.utils.AutoUVPositioner;
import net.fexcraft.app.fmt.utils.Translator;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureTreeTab extends TreeTab {

	public static String TOTALS_FORMAT;
	public Element totals;

	public TextureTreeTab(){
		super(TreeRoot.TreeMode.PREVIEW);
	}

	@Override
	public void init(Object... objs){
		super.init(150);
		TOTALS_FORMAT = Translator.translate("tree.info.textures_groups");
		over.add(totals = new Element().pos(20, 0).size(FF, FS).translate(TOTALS_FORMAT, "...").text_autoscale());
		over.add(new Element().pos(FO + 20, 28).size(FF, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			TextureManager.addGroup(null, true);
		}).translate("tree.texture.add_group").text_centered(true));
		over.add(new Element().pos(FO + 20, 60).size(FF, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			AutoUVPositioner.runAutoPos();
		}).translate("tree.texture.auto_pos").text_centered(true));
		over.add(new Element().pos(FO + 20, 90).size(FF, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			AutoUVPositioner.runReset(false);
		}).translate("tree.texture.reset_pos").text_centered(true));
		over.add(new Element().pos(FO + 20, 120).size(FF, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			AutoUVPositioner.runReset(true);
		}).translate("tree.texture.reset_type").text_centered(true));
		//
		updcom.add(UpdateEvent.ModelLoad.class, event -> reorderComponents());
		updcom.add(UpdateEvent.ModelUnload.class, event -> removeGroups());
		updcom.add(UpdateEvent.TexGroupAdded.class, event -> addTexGroup(event.group()));
		updcom.add(UpdateEvent.TexGroupRemoved.class, event -> remTexGroup(event.group()));
		updcom.add(UpdateEvent.TexGroupRenamed.class, event -> {
			TexGroupCom com = getTexGroupCom(event.group());
			if(com != null) com.updateTextColor();
		});
	}

	private TexGroupCom getTexGroupCom(TextureGroup group){
		for(Element elm : container.elements){
			if(elm instanceof TexGroupCom com){
				if(com.group == group) return com;
			}
		}
		return null;
	}

	private void addTexGroup(TextureGroup group){
		container.add(new TexGroupCom(group));
		reorderComponents();
	}

	private void remTexGroup(TextureGroup group){
		container.remElmIf(e -> e instanceof TexGroupCom com && com.group == group);
		reorderComponents();
	}

	private void removeGroups(){
		container.remElmIf(e -> e instanceof TexGroupCom);
		reorderComponents();
	}

	@Override
	public void reinsertComponents(){
		container.remElmIf(e -> e instanceof TexGroupCom);
		for(TextureGroup group : TextureManager.getGroups()){
			container.add(new TexGroupCom(group));
		}
		reorderComponents();
	}

	@Override
	public void updateCounter(){
		totals.translate(TOTALS_FORMAT, TextureManager.getGroups().size());
	}

}
