package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.DropList;
import net.fexcraft.app.fmt.ui.FMTInterface;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelLoad;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelUnload;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.oui.FileChooser;
import net.fexcraft.app.fmt.utils.DiscordUtil;
import net.fexcraft.app.json.JsonMap;

import java.io.File;
import java.util.ArrayList;

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
		addImporter(new BBMImporter());
	}
	
	private static void addImporter(Importer importer){
		IMPORTERS.add(importer);
		for(String str : importer.categories()){
			if(!CATEGORIES.contains(str)) CATEGORIES.add(str);
		}
	}
	
	public static void _import(){
		Dialog dia = FMT.UI.createDialog(500, 180, "import.choose.dialog");
		DropList<String> cat = new DropList<>(490);
		DropList<Importer> imp = new DropList<>(490);
		dia.addText(0, "import.choose.category");
		dia.addRowElm(1, cat);
		for(String c : CATEGORIES) cat.addEntry(c, c);
		cat.selectEntry(0);
		dia.addText(2, "import.choose.importer");
		dia.addRowElm(3, imp);
		String fircat = CATEGORIES.get(0);
		for(Importer importer : IMPORTERS){
			if(importer.categories().contains(fircat)) imp.addEntry(importer.name(), importer);
		}
		imp.selectEntry(0);
		cat.onchange((key, val) -> {
			imp.clear();
			for(Importer importer : IMPORTERS){
				if(importer.categories().contains(key)) imp.addEntry(importer.name(), importer);
			}
			imp.selectEntry(0);
		});
		dia.consumer(d -> {
			Importer importer = imp.getSelVal();
			showFileChooserDialog(importer);
		}, null);
		dia.buttons(100, Dialog.DialogButton.CONTINUE);
	}

	private static void showFileChooserDialog(Importer importer){
		FileChooser.chooseFile("import.choose.file", "", importer.extensions(), false, file -> {
			if(file == null){
				Dialog dia = FMT.UI.createDialog(400, 50, "import.choose.nofile");
				dia.buttons(100, Dialog.DialogButton.YES, Dialog.DialogButton.NO);
				dia.consumer(d -> showFileChooserDialog(importer), null);
				return;
			}
			Runnable run = () -> {
				Model old = FMT.MODEL;
				UpdateHandler.update(new ModelUnload(FMT.MODEL));
				FMT.MODEL = new Model(null, "imported model");
				FMT.MODEL.orient = old.orient;
				FMT.MODEL.format = old.format;
				DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
				String res = importer._import(FMT.MODEL, file);
				Dialog dia = FMT.UI.createDialog(FontRenderer.getWidth(res, FontRenderer.FontType.PLAIN) * 3 + 30, 100, "import.result");
				dia.addText(0, res);
				dia.buttons(100, Dialog.DialogButton.OK);
				FMT.updateTitle();
				UpdateHandler.update(new ModelLoad(FMT.MODEL));
				FMT.MODEL.recompile();
			};
			if(Settings.SETTINGS.containsKey("importer-" + importer.id())){
				FMTInterface.settings.show("importer-" + importer.id(), run);
			}
			else run.run();
		});
	}

	public static Importer getImporterFor(File file){
		for(Importer imp : IMPORTERS){
			if(imp.extensions().supports(file)){
				return imp;
			}
		}
		return null;
	}

}
