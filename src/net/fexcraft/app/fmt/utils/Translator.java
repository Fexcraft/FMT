package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

import net.fexcraft.lib.common.utils.Print;

public class Translator {
	
	private static TreeMap<String, String> DEF = new TreeMap<>(), SEL = new TreeMap<>();
	
	public static final void init() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("./resources/lang/default.lang"));
		while(scanner.hasNextLine()){
			String string = scanner.nextLine();
			if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
			String[] str = string.split("="); if(str.length < 2) continue; DEF.put(str[0], str[1]);
		} scanner.close();
		if(Settings.getLanguage().equals("default")){ Print.console("Langauge is set to default, skipping translation parsing."); return; }
		File file = new File("./resources/lang/" + Settings.getLanguage() + ".lang");
		if(!file.exists()){ Print.console("Tried to find lang file as specified in settings, but the file seems to be missing."); return; }
		//
		scanner = new Scanner(file);
		while(scanner.hasNextLine()){
			String string = scanner.nextLine();
			if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
			String[] str = string.split("="); if(str.length < 2) continue; SEL.put(str[0], str[1]);
		} scanner.close();
	}
	
	public static String translate(String key){
		return SEL.containsKey(key) ? SEL.get(key) : DEF.containsKey(key) ? DEF.get(key) : key;
	}
	
	public static String format(String key, Object... objects){
		String string = translate(key); return String.format(string, objects);
	}

}
