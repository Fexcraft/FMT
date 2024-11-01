package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Map;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import net.fexcraft.lib.common.utils.ObjParser;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.FRLObjParser;
import org.apache.commons.lang3.math.NumberUtils;

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

	public static void exModelData(){
		FileChooser.chooseDir(Translator.translate("Extract ModelData from FVTM Obj Models"), "./", folder -> {
			if(folder == null) return;
			extractModelData(folder);
		});
	}

	private static void extractModelData(File folder){
		for(File file : folder.listFiles()){
			if(file.isDirectory()){
				extractModelData(file);
				continue;
			}
			if(!file.getName().endsWith(".obj")) continue;
			try{
				ObjParser.ObjModel data = new ObjParser(new FileInputStream(file)).readComments(true).readModel(false).parse();
				Map<String, ArrayList<Polyhedron>> model = new FRLObjParser("", new FileInputStream(file)).parse();
				JsonMap map = new JsonMap();
				for(String comment : data.comments){
					if(!comment.contains(":")) continue;
					String key = comment.substring(0, comment.indexOf(":"));
					String val = comment.substring(comment.indexOf(":") + 2);
					if(key.equals("Program")){
						if(!map.has("Programs")) map.addArray("Programs");
						map.getArray("Programs").add(val);
					}
					else if(key.equals("Transform")){
						if(!map.has("Transforms")) map.addArray("Transforms");
						map.getArray("Transforms").add(val);
					}
					else{
						if(val.equals("true")) map.add(key, true);
						else if(val.equals("false")) map.add(key, false);
						else if(NumberUtils.isCreatable(val)) map.add(key, Double.parseDouble(val));
						else map.add(key, val);
					}
				}
				if(map.has("Programs")){
					JsonMap dict = new JsonMap();
					ArrayList<JsonValue<?>> remlist = new ArrayList<>();
					for(JsonValue<?> elm : map.getArray("Programs").value){
						String[] arr = elm.string_value().split(" ");
						if(!arr[1].equals("fvtm:bind_texture")) continue;
						if(!model.containsKey(arr[0])) continue;
						for(Polyhedron hedron : model.get(arr[0])){
							if(hedron.glObj.material.none()) continue;
							dict.add(hedron.glObj.material.id.substring(1), arr[2]);
						}
					}
					if(dict.not_empty()){
						map.add("MaterialDict", dict);
						//Logging.log(file.getName() + " " + dict);
					}
				}
				if(map.entries().size() > 0){
					JsonHandler.print(new File(file.getParentFile(), file.getName() + ".modeldata.json"), map, PrintOption.DEFAULT);
				}
			}
			catch(FileNotFoundException e){
				e.printStackTrace();
			}
		}
	}

}
