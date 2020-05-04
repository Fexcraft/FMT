package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.TreeMap;

import net.fexcraft.lib.common.utils.Print;

public class Translator {

	private static TreeMap<String, String> DEF = new TreeMap<>(), SEL = new TreeMap<>();
	private static final File ROOT_FILE = new File("./resources/lang/default.lang");

	public static final void init() throws FileNotFoundException{
		scanTo(ROOT_FILE, DEF, true);
		if(Settings.getLanguage().equals("default")){
			Print.console("Langauge is set to default, skipping translation parsing.");
			return;
		}
		File file = new File("./resources/lang/" + Settings.getLanguage() + ".lang");
		if(!file.exists()){
			Print.console("Tried to find lang file as specified in settings, but the file seems to be missing.");
			return;
		}
		//
		Print.console("Parsing '" + Settings.getLanguage() + "' language file.");
		scanTo(file, SEL, false);
	}

	private static void scanTo(File file, TreeMap<String, String> mapto, boolean bool) throws FileNotFoundException {
		Scanner scanner = new Scanner(file, StandardCharsets.UTF_8.name());
		while(scanner.hasNextLine()){
			String string = scanner.nextLine().trim();
			if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
			String[] str = string.split("=");
			if(str.length < 2) continue;
			mapto.put(str[0], str[1]);
		}
		scanner.close();
	}

	public static String translate(String key){
		if(key.startsWith("#")) return key.substring(1);
		return SEL.containsKey(key) ? SEL.get(key) : DEF.containsKey(key) ? DEF.get(key) : key;
	}

	public static String format(String key, Object... objects){
		String string = translate(key);
		try{
			return String.format(string, objects);
		}
		catch(Exception e){
			Print.console("Failed to format '" + key + "' as '" + string + "'!");
			for(Object object : objects) Print.console("OBJ > " + object);
			e.printStackTrace();
			return key;
		}
	}

}
