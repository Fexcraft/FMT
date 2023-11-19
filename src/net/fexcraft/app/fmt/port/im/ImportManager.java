package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelLoad;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelUnload;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.SettingsDialog;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.utils.DiscordUtil;
import net.fexcraft.app.json.JsonMap;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class ImportManager {
	
	private static final ArrayList<Importer> IMPORTERS = new ArrayList<>();
	private static final ArrayList<String> CATEGORIES = new ArrayList<>();

	public static void init(JsonMap map){
		IMPORTERS.clear();
		CATEGORIES.clear();
		addImporter(new MTBImporter());
		addImporter(new DFMImporter());
		addImporter(new FVTM_OLD_Importer());
		addImporter(new ObjImporter());
	}
	
	private static void addImporter(Importer importer){
		IMPORTERS.add(importer);
		for(String str : importer.categories()){
			if(!CATEGORIES.contains(str)) CATEGORIES.add(str);
		}
	}
	
	public static void _import(){
		Dialog dialog = new Dialog(translate("import.choose.dialog"), 400, 190);
		dialog.setResizable(false);
		Settings.applyComponentTheme(dialog.getContainer());
		dialog.getContainer().add(new Label(translate("import.choose.category"), 10, 10, 380, 25));
		SelectBox<String> selcat = new SelectBox<>(10, 35, 380, 25);
		for(String cat : CATEGORIES){
			selcat.addElement(cat);
		}
		selcat.setSelected(0, true);
		selcat.setVisibleCount(6);
		dialog.getContainer().add(selcat);
		dialog.getContainer().add(new Label(translate("import.choose.importer"), 10, 70, 380, 25));
		SelectBox<String> select = new SelectBox<>(10, 95, 380, 25);
		select.setVisibleCount(6);
		String fircat = CATEGORIES.get(0);
		for(Importer importer : IMPORTERS){
			if(importer.categories().contains(fircat)) select.addElement(importer.name());
		}
		dialog.getContainer().add(select);
		selcat.addSelectBoxChangeSelectionEventListener(lis -> {
			while(select.getElements().size() > 0) select.removeElement(0);
			for(Importer importer : IMPORTERS){
				if(importer.categories().contains(lis.getNewValue())) select.addElement(importer.name());
			}
		});
		dialog.getContainer().add(new RunButton("dialog.button.continue", 10, 135, 100, 25, () -> {
			dialog.close();
			Importer importer = null;
			String sel = select.getSelection();
			for(Importer im : IMPORTERS){
				if(im.name().equals(sel)) importer = im;
			}
			if(importer == null) return;
			showFileChooserDialog(importer, Collections.EMPTY_LIST);
		}));
		dialog.show(FMT.FRAME);
	}

	private static void showFileChooserDialog(Importer importer, List<Group> groups){
		FileChooser.chooseFile("import.choose.file", "", importer.extensions(), false, file -> {
			if(file == null){
				GenericDialog.showYN(null, () -> showFileChooserDialog(importer, groups), null, "import.choose.nofile");
				return;
			}
			Runnable run = () -> {
				Model old = FMT.MODEL;
				UpdateHandler.update(new ModelUnload(FMT.MODEL));
				FMT.MODEL = new Model(null, "imported model");
				FMT.MODEL.orient = old.orient;
				FMT.MODEL.format = old.format;
				DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
				GenericDialog.showOK("import.result", null, null, importer._import(FMT.MODEL, file));
				FMT.updateTitle();
				UpdateHandler.update(new ModelLoad(FMT.MODEL));
				FMT.MODEL.recompile();
			};
			if(importer.settings().size() > 0){
				SettingsDialog.open("import.settings.dialog", importer.settings(), importer.id(), run);
			}
			else run.run();
		});
	}

}
