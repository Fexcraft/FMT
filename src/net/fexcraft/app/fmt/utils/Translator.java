package net.fexcraft.app.fmt.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.TreeMap;

import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.lib.common.utils.Print;

public class Translator {
	
	private static TreeMap<String, String> DEF = new TreeMap<>(), SEL = new TreeMap<>();
	private static final File ROOT_FILE = new File("./resources/lang/default.lang");
	
	public static final void init() throws FileNotFoundException {
		Scanner scanner = new Scanner(ROOT_FILE, "UTF-8");
		while(scanner.hasNextLine()){
			String string = scanner.nextLine();
			if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
			String[] str = string.split("="); if(str.length < 2) continue; DEF.put(str[0], str[1]);
		} scanner.close();
		if(Settings.getLanguage().equals("default")){ Print.console("Langauge is set to default, skipping translation parsing."); return; }
		File file = new File("./resources/lang/" + Settings.getLanguage() + ".lang");
		if(!file.exists()){ Print.console("Tried to find lang file as specified in settings, but the file seems to be missing."); return; }
		//
		scanner = new Scanner(file, "UTF-8");
		while(scanner.hasNextLine()){
			String string = scanner.nextLine();
			if(string.length() < 3 || string.startsWith("#") || string.startsWith("//")) continue;
			if(string.startsWith("DEFAULT_CHARS = ")){
				FontRenderer.DEFAULT_CHARS += string.replace("DEFAULT_CHARS = ", "");
				Print.console("Applying new chars from land file."); continue;
			}
			String[] str = string.split("="); if(str.length < 2) continue; SEL.put(str[0], str[1]);
		} scanner.close();
	}
	
	public static void append(String key, String fill) {
		try{ Files.write(ROOT_FILE.toPath(), ("\n" + key + "=" + fill).getBytes(), StandardOpenOption.APPEND); }
		catch(IOException e){ e.printStackTrace(); }
	}
	
	public static String translate(String key){
		return SEL.containsKey(key) ? SEL.get(key) : DEF.containsKey(key) ? DEF.get(key) : key;
	}
	
	public static String translate(String key, String fill){
		if(SEL.containsKey(key)) return SEL.get(key);
		if(DEF.containsKey(key)) return DEF.get(key);
		DEF.put(key, fill); append(key, fill); return fill;
	}
	
	public static String format(String key, Object... objects){
		String string = translate(key); return String.format(string, objects);
	}
	
	public static String format(String key, String fill, Object... objects){
		String string = translate(key, fill); return String.format(string, objects);
	}

}
