package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

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
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConverterUtils {

	public static void runIMTJ(){
		FileChooser.chooseDir(Translator.translate("utils.converter.itemmodeltexjson.title"), "./", folder -> {
			if(folder == null) return;
			for(File file : folder.listFiles()){
				try{
					Logging.log("Patching " + file);
					JsonMap map = JsonHandler.parse(file);
					if(map.has("textures")){
						map.getMap("textures").value.values().forEach(json -> {
							if(json.isValue()){
								((JsonValue<String>)json).value(json.string_value().replace(":items/", ":item/"));
							}
						});
					}
					JsonHandler.print(file, map, PrintOption.DEFAULT);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

}
