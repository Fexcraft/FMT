package net.fexcraft.app.fmt.workspace;

import com.google.common.io.Files;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.BoolElm;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.utils.Formatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigUtils {

	public static void createNewFvtmPack(){
		Field name = new Field(Field.FieldType.TEXT, 500);
		Field pkid = new Field(Field.FieldType.TEXT, 500);
		FMT.UI.createDialog(510, 180, "utils.config.create_pack")
			.addText(0, "utils.config.create_pack.name")
			.addRowElm(1, name)
			.addText(2, "utils.config.create_pack.id")
			.addRowElm(3, pkid)
			.consumer(d -> {
				File folder = FMT.WORKSPACE.root_folder;
				String nam = name.get_text();
				String pid = pkid.get_text().replace(" ", "");
				File pr = new File(folder, nam + "/");
				File pkfd = new File(pr, "/assets/" + pid + "/");
				pkfd.mkdirs();
				//
				JsonMap map = new JsonMap();
				map.add("ID", pid);
				map.add("Name", nam);
				map.add("Version", "1.0.0");
				map.add("License", "All Rights Reserved");
				map.add("Dependencies", new JsonArray("gep"));
				map.add("Authors", SessionHandler.isLoggedIn() ? new JsonArray(SessionHandler.getUserName()) : new JsonArray());
				map.add("#info", "File generated via FMT.");
				JsonHandler.print(new File(pkfd, "addonpack.fvtm"), map, JsonHandler.PrintOption.DEFAULT);
				//
				map = new JsonMap();
				map.add("pack", new JsonMap("description", "Pack Resources", "pack_format", 3));
				JsonHandler.print(new File(pr, "/pack.mcmeta"), map, JsonHandler.PrintOption.DEFAULT);
				//
				map = new JsonMap();
				map.add("schemaVersion", 1);
				map.add("id", pid);
				map.add("version", "1.0.0");
				map.add("name", nam);
				map.add("description", "A pack for FVTM");
				map.add("authors", SessionHandler.isLoggedIn() ? new JsonArray(SessionHandler.getUserName()) : new JsonArray());
				map.add("contact", new JsonMap(
					"homepage", "https://fexcraft.net/",
					"sources", "https://github.com/Fexcraft/FMT"));
				map.add("license", "ARR");
				map.add("environment", "*");
				map.add("entrypoints", new JsonMap());
				map.add("mixins", new JsonArray());
				map.add("depends", new JsonMap(
					"fabricloader", "*",
					"minecraft", "*",
					"java", "*",
					"fabric-api", "*",
					"fvtm", "*",
					"fcl", "*"
				));
				JsonHandler.print(new File(pr, "/fabric.mod.json"), map, JsonHandler.PrintOption.DEFAULT);
				//
				try{
					File fl = new File(pr, "/META-INF/mods.toml");
					fl.getParentFile().mkdirs();
					FileWriter writer = new FileWriter(fl);
					writer.write("modLoader=\"javafml\"\n");
					writer.write("loaderVersion=\"[47,)\"\n");
					writer.write("license=\"All Rights Reserved\"\n");
					writer.write("issueTrackerURL=\"https://enter.your.url/here\"\n");
					writer.write("[[mods]]\n");
					writer.write("modId=\"fvtm\"\n");
					writer.write("version=\"1.0.0\"\n");
					writer.write("displayName=\"" + nam + "\"\n");
					writer.write("displayURL=\"https://fexcraft.net/wiki/mod/fvtm\"\n");
					writer.write("credits=\"Generated using FMT\" #optional\n");
					writer.write("authors=\"YourNameHere\"\n");
					writer.write("displayTest=\"IGNORE_ALL_VERSION\"\n\n");
					writer.write("description='''A pack for FVTM'''\n");
					writer.flush();
					writer.close();
				}
				catch(IOException e){
					e.printStackTrace();
				}
				//
				LangCache.genLangJson(new File(pr, "/assets/" + pid + "/lang/en_us.json"));
				LangCache.genLangFile(new File(pr, "/assets/" + pid + "/lang/en_us.lang"));
				//
				FMT.WORKSPACE.reloadPacks(() -> FMT.WORKSPACE.show());
			}, null)
			.buttons(100, DialogButton.CONTINUE);
	}

	public static void genAssetDirs(){
		FMT.WORKSPACE.selectPack(pack -> {
			Dialog dia = FMT.UI.createDialog(500, 80 + FvtmType.values().length * 30, "workspace.content_utils");
			dia.addBoldText(0, "editor.config.pack_utils.gen_asset_dirs.select");
			HashMap<FvtmType, Boolean> map = new HashMap();
			int row = 1;
			BoolElm elm;
			for(FvtmType value : FvtmType.values()){
				elm = dia.addBoolText(row++, value._name + " (" + value.folder + ")");
				elm.set(() -> map.get(value), b -> map.put(value, b));
				map.put(value, false);
			}
			dia.consumer(c -> {
				File fl;
				String folder;
				for(Map.Entry<FvtmType, Boolean> entry : map.entrySet()){
					if(!entry.getValue()) continue;
					folder = entry.getKey().folder;
					fl = new File(pack.file, "/assets/" + pack.id + "/config/" + folder);
					fl.mkdirs();
					fl = new File(pack.file, "/assets/" + pack.id + "/textures/" + folder.substring(0, folder.length() - 1));
					fl.mkdirs();
					fl = new File(pack.file, "/assets/" + pack.id + "/models/" + folder.substring(0, folder.length() - 1));
					fl.mkdirs();
				}
				FMT.WORKSPACE.reloadPacks(null);
			}, null);
			dia.buttons(100, DialogButton.CONFIRM, DialogButton.CANCEL);
		});
	}

	public static void genIconsInPack(){
		FMT.WORKSPACE.selectPack(pack -> {
			for(List<FileElm> files : pack.content.values()){
				for(FileElm elm : files){
					JsonMap map = JsonHandler.parse(elm.file);
					String cid = map.getString("ID", map.getString("RegistryName", null));
					if(cid == null) return;
					if(cid.contains(":")) cid = cid.split(":")[1];
					pack.lang.fill(cid, map.getString("Name", "Unnamed Content"));
					File file = new File(pack.file, "/assets/" + pack.id + "/models/item/" + cid + ".json");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					if(!file.exists()){
						map = new JsonMap();
						map.add("parent", "item/generated");
						map.add("textures", new JsonMap("layer0", pack.id + ":item/" + cid));
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					}
					file = new File(pack.file, "/assets/" + pack.id + "/items/" + cid + ".json");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					if(!file.exists()){
						map = new JsonMap();
						JsonMap mod = new JsonMap();
						mod.add("type", "minecraft:model");
						mod.add("model", pack.id + ":item/" + cid);
						map.add("model", mod);
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					}
					file = new File(pack.file, "/assets/" + pack.id + "/textures/item/" + cid + ".png");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					if(!file.exists()){
						try{
							Files.copy(new File("./resources/textures/icons/configeditor/rename.png"), file);
						}
						catch(Exception e){
							Logging.log(e);
						}
					}
				}
			}
			FMT.UI.createDialog(500, 80, "workspace.content_utils")
				.addText(0, "editor.config.pack_utils.gen_icons.complete")
				.buttons(100, DialogButton.OK);
			FMT.WORKSPACE.reloadPacks(null);
		});
	}

	public static void createNewContent(){
		FMT.WORKSPACE.selectPack(pack -> {
			FMT.WORKSPACE.selectContentType(type -> {
				Field conam = new Field(Field.FieldType.TEXT, 490);
				Field conid = new Field(Field.FieldType.TEXT, 490);
				FMT.UI.createDialog(500, 180, "workspace.content_utils")
					.addText(0, "editor.config.pack_utils.content_new.name")
					.addRowElm(1, conam)
					.addText(2, "editor.config.pack_utils.content_new.id")
					.addRowElm(3, conid)
					.consumer(d -> {
						String name = conam.get_text();
						File file = new File(pack.file, "/assets/" + pack.id + "/config/" + type.folder + "/" + name + "." + type.suffix);
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						String cid = conid.get_az09_();
						JsonMap map = new JsonMap();
						map.add("ID", cid);
						map.add("Name", name);
						map.add("Addon", pack.id);
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
						file = new File(pack.file, "/assets/" + pack.id + "/textures/item/" + cid + ".png");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						try{
							Files.copy(new File("./resources/textures/icons/configeditor/rename.png"), file);
						}
						catch(Exception e){
							Logging.log(e);
						}
						file = new File(pack.file, "/assets/" + pack.id + "/models/item/" + cid + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						map.add("parent", "item/generated");
						map.add("textures", new JsonMap("layer0", pack.id + ":item/" + cid));
						pack.lang.fill(cid, conam.get_text());
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
						file = new File(pack.file, "/assets/" + pack.id + "/items/" + cid + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						JsonMap mod = new JsonMap();
						mod.add("type", "minecraft:model");
						mod.add("model", pack.id + ":item/" + cid);
						map.add("model", mod);
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
						FMT.WORKSPACE.reloadPacks(null);
					}, null)
					.buttons(100, DialogButton.CONFIRM, DialogButton.CANCEL);
			});
		});
	}

	public static void genRoadAssets(){
		FMT.WORKSPACE.selectPack(pack -> {
			Field bid = new Field(Field.FieldType.TEXT, 490);
			Field bnm = new Field(Field.FieldType.TEXT, 490);
			Field tex = new Field(Field.FieldType.TEXT, 490);
			FMT.UI.createDialog(500, 240, "editor.config.pack_utils.road_assets.title")
				.addText(0, "editor.config.pack_utils.road_assets.block_id")
				.addRowElm(1, bid)
				.addText(2, "editor.config.pack_utils.road_assets.block_name")
				.addRowElm(3, bnm)
				.addText(4, "editor.config.pack_utils.road_assets.texture")
				.addRowElm(5, tex)
				.set_confirm(d -> {
					String roadid = bid.get_az09_();
					File file = new File(pack.file, "/assets/" + pack.id + "/blockstates/" + roadid + ".json");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					JsonMap map = new JsonMap();
					JsonMap vars = new JsonMap();
					for(int i = 0; i < 16; i++){
						JsonArray mods = new JsonArray();
						mods.add(new JsonMap("model", pack.id + ":" + roadid + "_" + i));
						mods.add(new JsonMap("model", pack.id + ":" + roadid + "_" + i, "y", 90));
						mods.add(new JsonMap("model", pack.id + ":" + roadid + "_" + i, "y", 180));
						mods.add(new JsonMap("model", pack.id + ":" + roadid + "_" + i, "y", 270));
						vars.add("height=" + i, mods);
					}
					map.add("variants", vars);
					JsonHandler.print(file, map);
					//
					String name = bnm.get_text();
					String tid = tex.get_text();
					for(int i = 0; i < 16; i++){
						file = new File(pack.file, "/assets/" + pack.id + "/blockstates/" + roadid + "_" + i + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						vars = new JsonMap();
						JsonArray mods = new JsonArray();
						mods.add(new JsonMap("model", pack.id + ":block/" + roadid + "_" + i));
						mods.add(new JsonMap("model", pack.id + ":block/" + roadid + "_" + i, "y", 90));
						mods.add(new JsonMap("model", pack.id + ":block/" + roadid + "_" + i, "y", 180));
						mods.add(new JsonMap("model", pack.id + ":block/" + roadid + "_" + i, "y", 270));
						vars.add("", mods);
						map.add("variants", vars);
						JsonHandler.print(file, map);
						//
						file = new File(pack.file, "/assets/" + pack.id + "/models/item/" + roadid + "_" + i + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						map.add("parent", pack.id + ":block/" + roadid + "_" + i);
						JsonHandler.print(file, map);
						//
						file = new File(pack.file, "/assets/" + pack.id + "/models/block/" + roadid + "_" + i + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						map.add("parent", "fvtm:block/asphalt_" + i);
						map.add("textures", new JsonMap("particle", tid, "texture", tid));
						JsonHandler.print(file, map);
						//
						file = new File(pack.file, "/assets/" + pack.id + "/items/" + roadid + "_" + i + ".json");
						if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
						map = new JsonMap();
						JsonMap mod = new JsonMap();
						mod.add("type", "minecraft:model");
						mod.add("model", pack.id + ":item/" + roadid + "_" + i);
						map.add("model", mod);
						JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					}
					FMT.WORKSPACE.reloadPacks(null);
				})
				.buttons(100, DialogButton.CONFIRM, DialogButton.CANCEL);
		});
	}

	public static void openJson(){
		FileChooser.choose("editor.config.file_utils.open_json.choose", FMT.WORKSPACE.root_folder, FileChooser.TYPE_JSON, false, file -> {
			try{
				if(file != null && file.exists()) FMT.WORKSPACE.open(file, JsonEditor::new);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
	}

	public static void mirrorLang(){
		FileChooser.choose("editor.config.file_utils.mirror_lang.choose", FMT.WORKSPACE.root_folder, FileChooser.TYPE_ANY, false, file -> {
			try{
				LinkedHashMap<String, LangCache.LangEntry> entries = new LinkedHashMap<>();
				Scanner scanner = new Scanner(file);
				String line;
				while(scanner.hasNextLine()){
					line = scanner.nextLine();
					if(!line.contains("=")){
						entries.put(line, new LangCache.LangEntry(null, false));
						continue;
					}
					String l = line.substring(0, line.indexOf("="));
					if(l.startsWith("item.")){
						l = l.substring(5, l.length() - 5).replace(":", ".");
						entries.put(l, new LangCache.LangEntry(line.substring(line.indexOf("=") + 1), true));
					}
					else{
						entries.put(l, new LangCache.LangEntry(line.substring(l.length() + 1), false));
					}
				}
				scanner.close();
				JsonMap map = new JsonMap();
				for(Map.Entry<String, LangCache.LangEntry> entry : entries.entrySet()){
					if(entry.getValue().item()) map.add("item." + entry.getKey(), net.fexcraft.lib.common.utils.Formatter.format(entry.getValue().name()));
					else if(entry.getValue().name() != null) map.add(entry.getKey(), Formatter.format(entry.getValue().name()));
				}
				File json = new File(file.getParentFile(), file.getName().substring(0, file.getName().indexOf(".")) + ".json");
				if(json.exists()) Files.copy(json, new File(json.getParentFile(), json.getName() + ".bkp"));
				JsonHandler.print(json, map, JsonHandler.PrintOption.SPACED);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
	}

}
