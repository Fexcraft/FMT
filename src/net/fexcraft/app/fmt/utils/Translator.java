package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;

import net.fexcraft.app.fmt.FMTB;

public class Translator {

	private static TreeMap<String, String> DEF = new TreeMap<>(), SEL = new TreeMap<>();
	private static final File ROOT_FILE = new File("./resources/lang/default.lang");

	public static final void init() throws FileNotFoundException{
		scanTo(ROOT_FILE, DEF, true);
		if(Settings.getLanguage().equals("default")){
			log("Langauge is set to default, skipping translation parsing.");
			return;
		}
		File file = new File("./resources/lang/" + Settings.getLanguage() + ".lang");
		if(!file.exists()){
			log("Tried to find lang file as specified in settings, but the file seems to be missing.");
			log("Resetting LANG_CODE setting to 'none'!");
			Settings.SETTINGS.get("language_code").setValue("none");
			return;
		}
		//
		log("Parsing '" + Settings.getLanguage() + "' language file.");
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
			log("Failed to format '" + key + "' as '" + string + "'!");
			for(Object object : objects) log("OBJ > " + object);
			log(e);
			return key;
		}
	}

	public static void showSelectDialog(Frame frame){
		try{
			Dialog dialog = new Dialog("FMT TRANSLATOR", 300, 110);
			Label select, applies;
			dialog.getContainer().add(select = new Label("SELECT LANGUAGE", 10, 5, 280, 24));
			dialog.getContainer().add(applies = new Label("APPLIES ON RESTART", 10, 60, 280, 24));
			Button button = new Button("EXIT", 200, 60, 90, 20);
			button.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				if(listener.getAction() == MouseClickAction.CLICK){
					FMTB.get().close(true);
				}
			});
			SelectBox<String> selectbox = new SelectBox<>(10, 30, 280, 24);
			File folder = new File("./resources/lang/");
			ArrayList<String[]> langdata = new ArrayList<>();
			for(File lang : folder.listFiles()){
				if(lang.getName().endsWith(".lang")){
					Scanner scanner = new Scanner(lang);
					String first = scanner.nextLine();
					if(first.startsWith("#FMT-LANG ")){
						String[] langs = first.replace("#FMT-LANG ", "").split("\\|");
						for(int i = 0; i < langs.length; i++) langs[i] = langs[i].trim();
						selectbox.addElement(langs[1]);
						langdata.add(langs);
					}
					else{
						langdata.add(new String[]{ lang.getName().replace(".lang", "") });
						selectbox.addElement(langdata.get(langdata.size() - 1)[0]);
					}
					scanner.close();
				}
			}
			selectbox.addSelectBoxChangeSelectionEventListener(listener -> {
				int i = selectbox.getElementIndex(listener.getNewValue());
				String[] arr = langdata.get(i);
				select.getTextState().setText(arr.length > 2 ? arr[2] : "SELECT A LANGUAGE");
				applies.getTextState().setText(arr.length > 3 ? arr[3] : "APPLIES ON RESTART");
				button.getTextState().setText(arr.length > 4 ? arr[4] : "EXIT");
				Settings.SETTINGS.get("language_code").validateAndApply(arr[0]);
			});
			selectbox.setVisibleCount(12);
			dialog.getContainer().add(selectbox);
			dialog.getContainer().add(button);
			dialog.setResizable(false);
			dialog.show(frame);
		}
		catch(FileNotFoundException e){
			log(e);
		}
	}

}
