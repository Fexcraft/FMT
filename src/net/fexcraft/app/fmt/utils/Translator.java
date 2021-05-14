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
	public static String UNNAMED_POLYGON;
	
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
		else if(!(lang = new File("./resources/lang/" + Settings.LANGUAGE.value + ".lang")).exists()){
			Logging.log("Locale '" + local + "' not found, skipping in Settings defined language parsing.");
			source = "settings";
		}
		if(source != null && lang.exists()){
			if(loadLang(SELECTED, lang)) Logging.log("Loaded " + source + " specified translation.");
			else Logging.log("Error while loading " + source + " specified translation.");
		}
		//
		UNNAMED_POLYGON = translate("polygon.unnamed");
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

	public static String translate(String string){
		if(SELECTED.containsKey(string)) return SELECTED.get(string);
		return DEFAULT.containsKey(string) ? DEFAULT.get(string) : string;
	}
	
	public static String format(String str, Object... args){
		String string = translate(str);
		return String.format(string, args);
	}

	public static Translations translate(String... strs){
		String[] res = new String[strs.length];
		float[] len = new float[strs.length];
		for(int i = 0; i < res.length; i++){
			res[i] = translate(strs[i]);
			len[i] = FontSizeUtil.getWidth(res[i]);
		}
		return new Translations(res, len);
	}
	
	public static class Translations {
		
		public String[] results;
		public float[] lengths;
		public float longest, shortest;

		public Translations(String[] res, float[] len){
			this.results = res;
			this.lengths = len;
			shortest = lengths[0];
			longest = lengths[0];
			if(len.length < 2) return;
			for(int i = 1; i < len.length; i++){
				if(len[i] > longest) longest = len[i];
				if(len[i] < shortest) shortest = len[i];
			}
		}
		
	}

}
