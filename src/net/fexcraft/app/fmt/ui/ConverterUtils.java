package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.File;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.MouseClickEvent;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConverterUtils {

	public static void runIMTJ(){
		FileChooser.chooseDir(Translator.translate("utils.converter.itemmodeltexjson.title"), "./", folder -> {
			if(folder == null) return;
			search(folder);
		});
	}

	private static void search(File folder){
		for(File file : folder.listFiles()){
			if(file.isDirectory()){
				search(file);
				continue;
			}
			try{
				Logging.log("Patching " + file);
				JsonMap map = JsonHandler.parse(file);
				if(map.has("textures")){
					map.getMap("textures").value.values().forEach(json -> {
						if(json.isValue()){
							String str = json.string_value();
							if(str.startsWith("items/")){
								str = str.replace("items/", "item/");
							}
							else if(str.startsWith("blocks/")){
								str = str.replace("blocks/", "block/");
							}
							else{
								str = str.replace(":items/", ":item/");
								str = str.replace(":blocks/", ":block/");
							}
							((JsonValue<String>)json).value(str);
						}
					});
				}
				JsonHandler.print(file, map, PrintOption.DEFAULT);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
