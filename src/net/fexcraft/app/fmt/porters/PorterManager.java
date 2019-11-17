/**
 * 
 */
package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.FileChooser.AfterTask;
import net.fexcraft.app.fmt.ui.general.FileChooser.ChooserMode;
import net.fexcraft.app.fmt.ui.general.FileChooser.FileRoot;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class PorterManager {
	
	private static final PorterMap porters = new PorterMap();
	
	public static final void load() throws NoSuchMethodException, FileNotFoundException, ScriptException {
		porters.add(new MTBImporter());
		porters.add(new FVTMExporter());
		porters.add(new OBJPreviewImporter());
		porters.add(new JTMTPorter());
		porters.add(new PNGExporter());
		porters.add(new OBJPrototypeExporter());
		porters.add(new MarkerExporter());
		porters.add(new TiMExporter());
	}

	public static void handleImport(){
		UserInterface.FILECHOOSER.show(new String[]{ Translator.translate("filechooser.import.title", "Select file/model to import."),
			Translator.translate("filechooser.import.confirm", "Import") }, FileRoot.IMPORT, new AfterTask(){
			@Override
			public void run(){
				try{
					if(file == null){
						FMTB.showDialogbox(Translator.translate("dialog.import.nofile", "No valid file choosen.<nl>Import is cancelled."),
							Translator.translate("dialog.import.nofile.confirm", "ok.."), null, DialogBox.NOTHING, null);
						return;
					}
					GroupCompound compound = porter.importModel(file, mapped_settings);
					if(mapped_settings.get("integrate").getBooleanValue()){
						for(String creator : compound.creators){
							if(!FMTB.MODEL.creators.contains(creator)){
								FMTB.MODEL.creators.add(creator);
							}
						}
						for(TurboList list : compound.getGroups()){
							String name = compound.name + "_" + list.id;
							while(FMTB.MODEL.getGroups().contains(name)){
								name += "_"; if(name.length() > 64) break;
							}
							FMTB.MODEL.getGroups().add(list);
						}
					}
					else FMTB.MODEL = compound;
					FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
				}
				catch(Exception e){
					String str = Translator.format("dialog.import.fail", "Errors while importing Model.<nl>%s", e.getLocalizedMessage());
					FMTB.showDialogbox(str, Translator.translate("dialog.import.fail.confirm", "ok."), null, DialogBox.NOTHING, null);//TODO add "open console" as 2nd button
					e.printStackTrace();
				}
				FMTB.showDialogbox(Translator.translate("dialog.import.success", "Import complete."), Translator.translate("dialog.import.success.confirm", "OK!"), null, DialogBox.NOTHING, null);
			}
		}, ChooserMode.IMPORT);
	}

	public static void handleExport(){
		UserInterface.EXPORTER.show();
	}

	/**
	 * @param file
	 * @return porter compatible with this file extension
	 */
	public static ExImPorter getPorterFor(File file, boolean export){
		for(ExImPorter porter : porters.values()){
			if((export && porter.isExporter()) || (!export && porter.isImporter())){
				for(String ext : porter.getExtensions()){
					if(file.getName().endsWith(ext)) return porter;
				}
			}
		}
		return null;
	}
	
	public static abstract class ExImPorter {
		
		public abstract String getId();
		
		public abstract String getName();
		
		public abstract String[] getExtensions();
		
		public abstract boolean isImporter();
		
		public abstract boolean isExporter();
		
		public boolean isValidFile(File pre){
			if(pre.isDirectory()) return true;
			for(String str : this.getExtensions())
				if(pre.getName().endsWith(str)) return true;
			return false;
		}

		public abstract List<Setting> getSettings(boolean export);
		
		protected static final List<Setting> nosettings = Collections.unmodifiableList(new ArrayList<>());
		
		/** @return new groupcompound based on data in the file */
		public abstract GroupCompound importModel(File file, Map<String, Setting> settings);
		
		/** @return result/status text; */
		public abstract String exportModel(GroupCompound compound, File file, Map<String, Setting> settings);
		
	}

	/**
	 * @return
	 */
	public static List<ExImPorter> getPorters(boolean export){
		return porters.values().stream().filter(pre -> export ? pre.isExporter() : pre.isImporter()).collect(Collectors.<ExImPorter>toList());
	}
	
	private static class PorterMap extends TreeMap<String, ExImPorter> {
		
		private static final long serialVersionUID = 1L;

		public void add(ExImPorter porter){
			this.put(porter.getId(), porter);
		}
		
	}

}
