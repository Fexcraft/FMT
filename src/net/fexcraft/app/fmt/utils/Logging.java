package net.fexcraft.app.fmt.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

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
	}

}
