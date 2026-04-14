package net.fexcraft.app.fmt.port.ex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.oui.FileChooser;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.FMTInterface;
import net.fexcraft.app.fmt.ui.FontRenderer;
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
		addExporter(new AABBExporter());
		addExporter(new ObjExporter());
		addExporter(new PivotExporter());
		addExporter(new MarkerAsPartSlotExporter());
		addExporter(new MarkerAsSeatConfExporter());
		addExporter(new FvtmBoundingBoxExporter());
	}
	
	private static void addExporter(Exporter exporter){
		EXPORTERS.add(exporter);
		for(String str : exporter.categories()){
			if(!CATEGORIES.contains(str)) CATEGORIES.add(str);
		}
	}
	
	public static void export(){
		Dialog dia = FMT.UI.createDialog(500, 180, "export.choose.dialog");
		DropList<String> cat = new DropList<>(490);
		DropList<Exporter> exp = new DropList<>(490);
		dia.addText(0, "export.choose.category");
		dia.addRowElm(1, cat);
		for(String c : CATEGORIES){
			cat.addEntry(c, c);
		}
		cat.selectEntry(0);
		dia.addText(2, "export.choose.exporter");
		dia.addRowElm(3, exp);
		String fircat = CATEGORIES.get(0);
		for(Exporter exporter : EXPORTERS){
			if(exporter.categories().contains(fircat)) exp.addEntry(exporter.name(), exporter);
		}
		exp.selectEntry(0);
		cat.onchange((key, val) -> {
			exp.clear();
			for(Exporter exporter : EXPORTERS){
				if(exporter.categories().contains(key)) exp.addEntry(exporter.name(), exporter);
			}
			exp.selectEntry(0);
		});
		dia.consumer(d -> {
			Exporter exporter = exp.getSelVal();
			if(exporter.nogroups()){
				showFileChooserDialog(exporter, Collections.EMPTY_LIST);
			}
			else showGroupSelectionDialog(exporter);
		}, null);
		dia.buttons(100, Dialog.DialogButton.CONTINUE);
	}

	private static void showGroupSelectionDialog(Exporter exporter){
		Dialog dia = FMT.UI.createDialog(500, 400, "export.choose.groups");
		dia.addText(0, "//TODO");
		dia.addText(1, "group selection panel");
		dia.consumer(d -> {
			showFileChooserDialog(exporter, FMT.MODEL.allgroups());//TODO panel selected groups
		}, null);
		dia.buttons(100, Dialog.DialogButton.CONTINUE);
	}

	private static void showFileChooserDialog(Exporter exporter, List<Group> groups){
		FileChooser.chooseFile("export.choose.file", "", exporter.extensions(), true, file -> {
			if(file == null){
				Dialog dia = FMT.UI.createDialog(400, 50, "export.choose.nofile");
				dia.buttons(100, Dialog.DialogButton.YES, Dialog.DialogButton.NO);
				dia.consumer(d -> showFileChooserDialog(exporter, groups), null);
				return;
			}
			Runnable run = () -> {
				String res = exporter.export(FMT.MODEL, file, groups);
				Dialog dia = FMT.UI.createDialog(FontRenderer.getWidth(res, FontRenderer.FontType.PLAIN) * 3 + 30, 100, "export.result");
				dia.addText(0, res);
				dia.buttons(100, Dialog.DialogButton.OK);
			};
			if(Settings.SETTINGS.containsKey("exporter-" + exporter.id())){
				exporter.initSettings();
				FMTInterface.settings.show("exporter-" + exporter.id(), run);
			}
			else run.run();
		});
	}

}
