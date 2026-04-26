package net.fexcraft.app.fmt.ui.tree;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.FileChooser;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Pivot;
import net.fexcraft.app.fmt.port.im.ImportManager;
import net.fexcraft.app.fmt.port.im.Importer;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.utils.PreviewHandler;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonMap;

import java.io.File;

import static net.fexcraft.app.fmt.oui.EditorComponent.F2S;
import static net.fexcraft.app.fmt.oui.FileChooser.*;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.*;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class HelperTreeTab extends TreeTab {

	public static String TOTALS_FORMAT;
	public Element totals;

	public HelperTreeTab(){
		super(TreeRoot.TreeMode.PREVIEW);
	}

	@Override
	public void init(Object... objs){
		super.init(90);
		TOTALS_FORMAT = Translator.translate("tree.info.preview_loaded");
		over.add(totals = new Element().pos(5, 0).size(FF, FS).translate(TOTALS_FORMAT, "...").text_autoscale());
		over.add(new Element().pos(F20 + 15, 28).size(F2S, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			FileChooser.chooseFile("...", "./saves", TYPE_FMTB, false, file -> {
				if(file == null) return;
				PreviewHandler.loadFMTB(file);
			});
		}).translate("tree.preview.load_fmtb").text_centered(true));
		over.add(new Element().pos(F21 + 15, 28).size(F2S, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			FileChooser.chooseFile("...", ".", TYPE_IMG, false, file -> {
				if(file == null) return;
				PreviewHandler.loadFrame(file);
			});
		}).translate("tree.preview.load_image").text_centered(true));
		over.add(new Element().pos(F20 + 15, 60).size(F2S, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			FileChooser.chooseFile("...", ".", TYPE_ANY, false, file -> {
				if(file == null) return;
				if(file.getName().endsWith(".")) file = new File(file.toString().substring(0, file.toString().length() - 1));
				Importer porter = ImportManager.getImporterFor(file);
				if(porter == null){
					log("ERROR: Could not find importer for helper/preview '" + file.getPath() + "'!");
					return;
				}
				PreviewHandler.load(file, porter, new JsonMap());
			});
		}).translate("tree.preview.import").text_centered(true));
		over.add(new Element().pos(F21 + 15, 60).size(F2S, FS).color(GENERIC_FIELD.value).hoverable(true).onclick(ci -> {
			PreviewHandler.clear();
		}).translate("tree.preview.clear").text_centered(true));
		//
		updcom.add(UpdateEvent.HelperAdded.class, event -> addHelper(event.model()));
		updcom.add(UpdateEvent.HelperRemoved.class, event -> remHelper(event.model()));
		updcom.add(UpdateEvent.HelperRenamed.class, event -> {
			HelperCom com = getHelperCom(event.model());
			if(com != null) com.updateTextColor();
		});
		updcom.add(UpdateEvent.HelperSelected.class, event -> {
			for(Element elm : container.elements) if(elm instanceof HelperCom com) com.updateTextColor();
		});
		updcom.add(UpdateEvent.ModelLoad.class, event -> reorderComponents());
		updcom.add(UpdateEvent.ModelUnload.class, event -> removeHelpers());
	}

	private HelperCom getHelperCom(Model model){
		for(Element elm : container.elements){
			if(elm instanceof HelperCom com){
				if(com.model == model) return com;
			}
		}
		return null;
	}

	private void addHelper(Model model){
		container.add(new HelperCom(model));
		reorderComponents();
	}

	private void remHelper(Model model){
		container.remElmIf(e -> e instanceof HelperCom com && com.model == model);
		reorderComponents();
	}

	private void removeHelpers(){
		container.remElmIf(e -> e instanceof HelperCom);
		reorderComponents();
	}

	@Override
	public void reinsertComponents(){
		container.remElmIf(e -> e instanceof HelperCom);
		for(Model model : PreviewHandler.getLoaded()){
			container.add(new HelperCom(model));
		}
		reorderComponents();
	}

	@Override
	public void updateCounter(){
		totals.translate(TOTALS_FORMAT, PreviewHandler.getLoaded().size());
	}

}
