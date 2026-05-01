package net.fexcraft.app.fmt.workspace;

import com.google.common.io.Files;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.BoolElm;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.Dialog.DialogButton;
import net.fexcraft.app.fmt.ui.Field;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
