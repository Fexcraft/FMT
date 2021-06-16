package net.fexcraft.app.fmt.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.math.Time;

public class Logging {

	private static final Logger LOGGER_MAIN = (Logger)LogManager.getLogger("FMT");
	
	public static void log(Object... log){
		for(Object obj : log) log(obj);
	}
	
	public static void log(Object obj){
		LOGGER_MAIN.log(Level.INFO, obj);
	}
	
	public static void log(Iterable<?> it){
		for(Object obj : it) log(obj);
	}
	
	public static void log(Level lvl, Object... log){
		for(Object obj : log) log(lvl, obj);
	}
	
	public static void log(Level lvl, Object obj){
		LOGGER_MAIN.log(lvl, obj);
	}
	
	public static void log(Throwable e){
		if(e.getCause() != null) log(e.getCause());
		log((String)null, e);
	}
	
	public static void log(String errorinfo, Throwable e){
		LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getLocalizedMessage());
		if(errorinfo != null) LOGGER_MAIN.log(Level.INFO, "INFO: " + errorinfo);
		//LOGGER_MAIN.log(Level.INFO, "ERROR: " + e.getMessage());
		for(StackTraceElement trace : e.getStackTrace()){
			LOGGER_MAIN.log(Level.ERROR, "\t" + trace);
		}
		try{
			String[] str = new String[e.getStackTrace().length + 1 + (errorinfo == null ? 0 : 1)];
			str[0] = "ERROR: " + e.getLocalizedMessage();
			if(errorinfo != null) str[1] = "INFO: " + errorinfo;
			int idx = errorinfo == null ? 1 : 2;
			for(int i = 0; i + idx < str.length; i++){
				str[i + idx] = "\t" + e.getStackTrace()[i];
			}
			/*DialogBox.show(600, "error.dialog_title", "dialogbox.button.ok", "toolbar.utils.clipboard.copy", null, () -> {
				String string = new String(str[0] + "\n");
				for(int i = 1; i < str.length; i++) string += str[i] + "\n";
				Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection sel = new StringSelection(string);
				cp.setContents(sel, sel);
			}, str).setResizable(true);*/
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void bar(String string){
		bar(string, false, 3);
	}
	
	public static void bar(String string, boolean log){
		bar(string, log, 3);
	}
	
	public static void bar(String string, boolean log, int secs){
		if(Settings.SHOW_BOTTOMBAR.value){
			FMT.bar.getTextState().setText(string);
			FMT.bar_timer = Time.getDate() + Time.SEC_MS * secs;
		}
		if(log) log(string);
	}

}
