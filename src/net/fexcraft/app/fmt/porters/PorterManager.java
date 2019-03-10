/**
 * 
 */
package net.fexcraft.app.fmt.porters;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.FileChooser.AfterTask;
import net.fexcraft.app.fmt.ui.general.FileChooser.ChooserMode;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.json.JsonUtil;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class PorterManager {
	
	private static final PorterMap porters = new PorterMap();
	
	public static final void load() throws NoSuchMethodException, FileNotFoundException, ScriptException {
		File root = new File("./resources/porters"); porters.clear();
		if(!root.exists()){ root.mkdirs(); }
		else{
			for(File file : root.listFiles()){
				if(file.getName().endsWith(".js")){
					try{
						ScriptEngine engine = newEngine(); engine.eval(new FileReader(file)); Invocable inv = (Invocable)engine;
						//
						ExternalPorter porter = new ExternalPorter(); porter.file = file;
						porter.id = (String)inv.invokeFunction("getId");
						porter.name = (String)inv.invokeFunction("getName");
						porter.extensions = ((ScriptObjectMirror)inv.invokeFunction("getExtensions")).to(String[].class);
						porter.importer = (boolean)inv.invokeFunction("isImporter");
						porter.exporter = (boolean)inv.invokeFunction("isExporter");
						porters.put(porter.id, porter);
					}
					catch(Exception e){
						e.printStackTrace(); System.exit(1);
					}
				}
			}
		}
		//
		porters.add(new MTBImporter());
		porters.add(new FVTMExporter(false, false));
		porters.add(new FVTMExporter(true, false));
		porters.add(new FVTMExporter(false, true));
		porters.add(new FVTMExporter(true, true));
		porters.add(new OBJPreviewImporter());
		porters.add(new JTMTPorter(false));
		porters.add(new JTMTPorter(true));
		porters.add(new PNGExporter(true));
		porters.add(new PNGExporter(false));
		porters.add(new OBJPrototypeExporter());
		porters.add(new MarkerExporter());
	}

	private static ScriptEngine newEngine(){
		return new ScriptEngineManager().getEngineByName("nashorn");
	}

	public static void handleImport(){
		UserInterface.FILECHOOSER.show(new String[]{ "Select file/model to import.", "Import" }, new File("./models"), new AfterTask(){
			@Override
			public void run(){
				try{
					if(file == null){
						FMTB.showDialogbox("No valid file choosen.", "Import is cancelled.", "ok..", null, DialogBox.NOTHING, null);
						return;
					}
					if(porter.isInternal()){
						FMTB.MODEL = ((InternalPorter)porter).importModel(file);
						FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
					}
					else{
						Invocable inv = (Invocable)((ExternalPorter)porter).eval();
						String result = (String) inv.invokeFunction("importModel", file);
						SaveLoad.loadModel(JsonUtil.getObjectFromString(result));
					}
				}
				catch(Exception e){
					FMTB.showDialogbox("Errors while importing Model.", e.getLocalizedMessage(), "ok.", null, DialogBox.NOTHING, null);//TODO add "open console" as 2nd button
					e.printStackTrace();
				}
				FMTB.showDialogbox("Import complete.", null, "OK!", null, DialogBox.NOTHING, null);
			}
		}, ChooserMode.IMPORT);
	}

	public static void handleExport(){
		UserInterface.FILECHOOSER.show(new String[]{ "Select Export Location", "Export" }, new File("./models"), new AfterTask(){
			@Override
			public void run(){
				try{
					if(file == null){
						FMTB.showDialogbox("No valid file choosen.", "Export is cancelled.", "ok..", null, DialogBox.NOTHING, null);
						return;
					} String result;
					if(porter.isInternal()){
						result = ((InternalPorter)porter).exportModel(FMTB.MODEL, file);
					}
					else{
						Invocable inv = (Invocable)((ExternalPorter)porter).eval();
						result = (String)inv.invokeFunction("exportModel", SaveLoad.modelToJTMT(null, true).toString(), file);
					}
					FMTB.showDialogbox("Export complete.", result, "OK!", null, DialogBox.NOTHING, null);
					Desktop.getDesktop().open(file.getParentFile());
				}
				catch(Exception e){
					FMTB.showDialogbox("Errors while exporting Model.", e.getLocalizedMessage(), "ok.", null, DialogBox.NOTHING, null);//TODO add "open console" as 2nd button
					e.printStackTrace();
				}
			}
		}, ChooserMode.EXPORT);
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
	
	public static class ExternalPorter extends ExImPorter {

		private File file;
		public String id, name;
		public String[] extensions;
		public boolean importer, exporter;
		
		/**
		 * @return new ScriptEngine instance with this porter loaded
		 * @throws ScriptException 
		 * @throws FileNotFoundException 
		 */
		public ScriptEngine eval() throws FileNotFoundException, ScriptException{
			ScriptEngine engine = newEngine();
			engine.eval(new FileReader(file));
			return engine;
		}
		
		@Override
		public boolean isInternal(){ return false; }
		
		@Override
		public String getId(){ return id; }
		
		@Override
		public String getName(){ return name; }
		
		@Override
		public String[] getExtensions(){ return extensions; }
		
		@Override
		public boolean isImporter(){ return importer; }
		
		@Override
		public boolean isExporter(){ return exporter; }
		
	}
	
	public static abstract class ExImPorter {
		
		public abstract String getId();
		
		public abstract String getName();
		
		public abstract String[] getExtensions();
		
		public abstract boolean isImporter();
		
		public abstract boolean isExporter();
		
		public abstract boolean isInternal();
		
		public boolean isValidFile(File pre){
			if(pre.isDirectory()) return true;
			for(String str : this.getExtensions())
				if(pre.getName().endsWith(str)) return true;
			return false;
		}
		
	}
	
	public static abstract class InternalPorter extends ExImPorter {
		
		/** @return new groupcompound based on data in the file */
		public abstract GroupCompound importModel(File file);
		
		/** @return result/status text; */
		public abstract String exportModel(GroupCompound compound, File file);
		
		@Override
		public boolean isInternal(){ return true; }
		
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
