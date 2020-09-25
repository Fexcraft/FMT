package net.fexcraft.app.fmt.utils;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import net.fexcraft.app.fmt.ui.DialogBox;

public class Logging {

	private static final Logger LOGGER_MAIN = (Logger)LogManager.getLogger("FMT");
	
	public static void log(Object... log){
		for(Object obj : log) log(obj);
	}
	
	public static void log(Object obj){
		LOGGER_MAIN.log(Level.INFO, obj);
	}
	
	public static void log(Level lvl, Object... log){
		for(Object obj : log) log(lvl, obj);
	}
	
	public static void log(Level lvl, Object obj){
		LOGGER_MAIN.log(lvl, obj);
	}
	
	public static void log(Throwable e){
		LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getLocalizedMessage());
		//LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getMessage());
		for(StackTraceElement trace : e.getStackTrace()){
			LOGGER_MAIN.log(Level.ERROR, "\t" + trace);
		}
		try{
			String[] str = new String[e.getStackTrace().length + 1];
			str[0] = "ERROR: " + e.getLocalizedMessage();
			for(int i = 0; i + 1 < str.length; i++){
				str[i + 1] = "\t" + e.getStackTrace()[i];
			}
			DialogBox.show(600, "error.dialog_title", "dialogbox.button.ok", "toolbar.utils.clipboard.copy", null, () -> {
				String string = new String(str[0] + "\n");
				for(int i = 1; i < str.length; i++) string += str[i] + "\n";
				Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection sel = new StringSelection(string);
				cp.setContents(sel, sel);
			}, str).setResizable(true);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
