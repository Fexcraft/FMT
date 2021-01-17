package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import net.fexcraft.app.fmt.settings.Settings;

public class Translator {
	
	public static final Map<String, String> DEFAULT = new HashMap<>();
	public static final Map<String, String> SELECTED = new HashMap<>();
	
	public static void init(){
		if(loadLang(DEFAULT, new File("./resources/lang/default.lang"))){
			Logging.log("Loaded default translation.");
		}
		String local = Locale.getDefault().toString().toLowerCase();
		File lang = null;
		String source = null;
		if(Settings.LANGUAGE.value.equals("null")){
			if(!(lang = new File("./resources/lang/" + local + ".lang")).exists()){
				Logging.log("Locale '" + local + "' not found, skipping JVM returned language parsing.");
			}
			source = "system";
		}
		if(!(lang = new File("./resources/lang/" + Settings.LANGUAGE.value + ".lang")).exists()){
			Logging.log("Locale '" + local + "' not found, skipping in Settings defined language parsing.");
			source = "settings";
		}
		if(source != null && lang.exists()){
			if(loadLang(SELECTED, lang)) Logging.log("Loaded " + source + " specified translation.");
			else Logging.log("Error while loading " + source + " specified translation.");
		}
	}

	private static boolean loadLang(Map<String, String> map, File file){
		try{
			Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
			while(scanner.hasNextLine()){
				String string = scanner.nextLine().trim();
				if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
				String[] str = string.split("=");
				if(str.length < 2) continue;
				map.put(str[0], str[1]);
			}
			scanner.close();
			return true;
		}
		catch(Exception e){
			Logging.log(e);
			return false;
		}
	}

}
