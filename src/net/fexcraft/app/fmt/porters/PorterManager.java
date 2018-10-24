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
import javax.swing.JOptionPane;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.app.fmt.utils.SaveLoad;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class PorterManager {
	
	private static final TreeMap<String, Porter> porters = new TreeMap<String, Porter>();
	
	public static final void load() throws NoSuchMethodException, FileNotFoundException, ScriptException{
		File root = new File("./resources/porters"); porters.clear();
		if(!root.exists()){ root.mkdirs(); }
		else{
			for(File file : root.listFiles()){
				if(file.getName().endsWith(".js")){
					try{
						ScriptEngine engine = newEngine(); engine.eval(new FileReader(file)); Invocable inv = (Invocable)engine;
						//
						Porter porter = new Porter(); porter.file = file;
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
	}

	private static ScriptEngine newEngine(){
		return new ScriptEngineManager().getEngineByName("nashorn");
	}

	public static void handleImport(){
		try{
			File file = SaveLoad.getFile("Select file to import.", new File("./models"), true);
			if(file == null){
				JOptionPane.showMessageDialog(null, "No valid file choosen.\nImport is cancelled.", "Notice.", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			//attempt to load via hardcoded methods first.
			if(HardcodedPorters.redirect(file)){
				JOptionPane.showMessageDialog(null, "Import complete.", "Status", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			//if it didn't, continue via the normal scripted porters
			Invocable inv = (Invocable) getPorterFor(file, false).eval();
			String result = (String) inv.invokeFunction("importModel", new File("./saves/").listFiles()[0]);
			SaveLoad.loadModel(JsonUtil.getObjectFromString(result));
			JOptionPane.showMessageDialog(null, "Import complete.", "Status", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(Exception e){
			e.printStackTrace(); JOptionPane.showMessageDialog(null, e, "Errors while importing Model.", JOptionPane.WARNING_MESSAGE);
		}
	}

	public static void handleExport(){
		try{
			File file = SaveLoad.getFile("Select file to export.", new File("./models"), false);
			if(file == null){
				JOptionPane.showMessageDialog(null, "No valid file choosen.\nExport is cancelled.", "Notice.", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			Invocable inv = (Invocable)getPorterFor(file, true).eval();
			String result = (String)inv.invokeFunction("exportModel", SaveLoad.modelToJTMT(true).toString(), file);
			JOptionPane.showMessageDialog(null, "Export complete.\n" + result, "Status", JOptionPane.INFORMATION_MESSAGE);
			Desktop.getDesktop().open(file.getParentFile());
		}
		catch(Exception e){
			e.printStackTrace(); JOptionPane.showMessageDialog(null, e, "Errors while exporting Model.", JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * @param file
	 * @return porter compatible with this file extension
	 */
	private static Porter getPorterFor(File file, boolean export){
		for(Porter porter : porters.values()){
			if((export && porter.exporter) || (!export && porter.importer)){
				for(String ext : porter.extensions){
					if(file.getName().endsWith(ext)) return porter;
				}
			}
		}
		return null;
	}
	
	public static class Porter {

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
		
	}
	
	//TODO integrated/java Porters

	/**
	 * @return
	 */
	public static List<Porter> getPorters(boolean export){
		return porters.values().stream().filter(pre -> export ? pre.exporter : pre.importer).collect(Collectors.<Porter>toList());
	}

}
