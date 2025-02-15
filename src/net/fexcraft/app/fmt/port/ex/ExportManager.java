package net.fexcraft.app.fmt.port.ex;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.GroupSelectionPanel;
import net.fexcraft.app.fmt.ui.SettingsDialog;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.json.JsonMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class ExportManager {
	
	private static final ArrayList<Exporter> EXPORTERS = new ArrayList<>();
	private static final ArrayList<String> CATEGORIES = new ArrayList<>();

	public static void init(JsonMap map){
		EXPORTERS.clear();
		CATEGORIES.clear();
		addExporter(new FMFExporter(map));
		addExporter(new BObjExporter(map));
		addExporter(new PNGExporter(map));
		addExporter(new ModelDataExporter());
		addExporter(new FVTMExporter());
		addExporter(new TiMExporter());
		addExporter(new AABBExporter());
		addExporter(new ObjExporter());
		addExporter(new PivotExporter());
		addExporter(new MarkerAsPartSlotExporter());
	}
	
	private static void addExporter(Exporter exporter){
		EXPORTERS.add(exporter);
		for(String str : exporter.categories()){
			if(!CATEGORIES.contains(str)) CATEGORIES.add(str);
		}
	}
	
	public static void export(){
		Dialog dialog = new Dialog(translate("export.choose.dialog"), 400, 190);
		dialog.setResizable(false);
		Settings.applyComponentTheme(dialog.getContainer());
		dialog.getContainer().add(new Label(translate("export.choose.category"), 10, 10, 380, 25));
		SelectBox<String> selcat = new SelectBox<>(10, 35, 380, 25);
		for(String cat : CATEGORIES){
			selcat.addElement(cat);
		}
		selcat.setSelected(0, true);
		selcat.setVisibleCount(6);
		dialog.getContainer().add(selcat);
		dialog.getContainer().add(new Label(translate("export.choose.exporter"), 10, 70, 380, 25));
		SelectBox<String> select = new SelectBox<>(10, 95, 380, 25);
		select.setVisibleCount(6);
		String fircat = CATEGORIES.get(0);
		for(Exporter exporter : EXPORTERS){
			if(exporter.categories().contains(fircat)) select.addElement(exporter.name());
		}
		dialog.getContainer().add(select);
		selcat.addSelectBoxChangeSelectionEventListener(lis -> {
			while(select.getElements().size() > 0) select.removeElement(0);
			for(Exporter exporter : EXPORTERS){
				if(exporter.categories().contains(lis.getNewValue())) select.addElement(exporter.name());
			}
		});
		dialog.getContainer().add(new RunButton("dialog.button.continue", 10, 135, 100, 25, () -> {
			dialog.close();
			Exporter exporter = null;
			String sel = select.getSelection();
			for(Exporter ex : EXPORTERS){
				if(ex.name().equals(sel)) exporter = ex;
			}
			if(exporter == null) return;
			if(exporter.nogroups()){
				showFileChooserDialog(exporter, Collections.EMPTY_LIST);
			}
			else showGroupSelectionDialog(exporter);
		}));
		dialog.show(FMT.FRAME);
	}

	private static void showGroupSelectionDialog(Exporter exporter){
		Dialog dialog = new Dialog(translate("export.choose.groups"), 400, 440);
		dialog.setResizable(false);
		Settings.applyComponentTheme(dialog.getContainer());
		GroupSelectionPanel panel = new GroupSelectionPanel(10, 10, 380, 360);
		dialog.getContainer().add(panel);
		dialog.getContainer().add(new RunButton("dialog.button.continue", 10, 380, 100, 30, () -> {
			dialog.close();
			showFileChooserDialog(exporter, panel.getSelectedGroups());
		}));
		dialog.show(FMT.FRAME);
	}

	private static void showFileChooserDialog(Exporter exporter, List<Group> groups){
		FileChooser.chooseFile("export.choose.file", "", exporter.extensions(), true, file -> {
			if(file == null){
				GenericDialog.showYN(null, () -> showFileChooserDialog(exporter, groups), null, "export.choose.nofile");
				return;
			}
			Runnable run = () -> GenericDialog.showOK("export.result", null, null, exporter.export(FMT.MODEL, file, groups));
			if(exporter.settings().size() > 0){
				SettingsDialog.open("export.settings.dialog", exporter.settings(), exporter.id(), run);
			}
			else run.run();
		});
	}

}
