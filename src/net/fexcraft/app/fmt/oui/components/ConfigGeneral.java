package net.fexcraft.app.fmt.oui.components;

import com.google.common.io.Files;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.oui.FileChooser;
import net.fexcraft.app.fmt.oui.JsonEditor;
import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.utils.Formatter;

import java.io.File;
import java.util.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigGeneral {

	public static void createNewContent(){
		/*WorkspaceViewer.viewer().selectPackDialog(pack -> {
			WorkspaceViewer.viewer().selectContentTypeDialog(type -> {
				String typeL = type.toLowerCase();
				String typeS = typeL + (type.endsWith("th") ? "es" : "s");
				Dialog dialog = new Dialog(type + " Creation Settings", 420, 190);
				dialog.getContainer().add(new Label(type + " Name:", 10, 10, 400, 30));
				TextField name = new TextField(typeL + " name", 10, 40, 400, 30);
				dialog.getContainer().add(name);
				dialog.getContainer().add(new Label(type + " ID:", 10, 70, 400, 30));
				TextField pid = new TextField(typeL + "_id", 10, 100, 400, 30);
				dialog.getContainer().add(pid);
				dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 140, 100, 20, () -> {
					File file = new File(pack.file, "/assets/" + pack.id + "/config/" + typeS + "/" + name.getTextState().getText() + "." + typeL);
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					String pkid = pid.getTextState().getText();
					JsonMap map = new JsonMap();
					map.add("ID", pid.getTextState().getText());
					map.add("Name", name.getTextState().getText());
					map.add("Addon", pack.id);
					JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					file = new File(pack.file, "/assets/" + pack.id + "/textures/item/" + pkid + ".png");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					try{
						Files.copy(new File("./resources/textures/icons/configeditor/rename.png"), file);
					}
					catch(Exception e){
						Logging.log(e);
					}
					file = new File(pack.file, "/assets/" + pack.id + "/models/item/" + pkid + ".json");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					map = new JsonMap();
					map.add("parent", "item/generated");
					map.add("textures", new JsonMap("layer0", pack.id + ":item/" + pkid));
					pack.lang.fill(pkid, name.getTextState().getText());
					JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					file = new File(pack.file, "/assets/" + pack.id + "/items/" + pkid + ".json");
					if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
					map = new JsonMap();
					JsonMap mod = new JsonMap();
					mod.add("type", "minecraft:model");
					mod.add("model", pack.id + ":item/" + pkid);
					map.add("model", mod);
					JsonHandler.print(file, map, JsonHandler.PrintOption.DEFAULT);
					dialog.close();
					WorkspaceViewer.viewer().genView();
				}));
				dialog.setResizable(false);
				//dialog.show(FMT.FRAME);
			});
		});*/
	}

	public static void createNewIcon(){
		/*WorkspaceViewer.viewer().selectPackDialog(pack -> {
			Widget widget = new Widget(FMT.WIDTH / 2f - 128, FMT.HEIGHT / 2f - 149, 256, 296);
			widget.getTitleTextState().setText("Center the model inside.");
			widget.getStyle().setBorder(new SimpleLineBorder(FMT.rgba(0xffff00), 2));
			widget.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
			Logging.log(widget.getTitle().getSize());
			widget.getContainer().add(new RunButton("dialog.button.save", 0, 256, 256, 20, () -> {
				ByteBuffer buffer = ByteBuffer.allocateDirect(256 * 256 * 4);
				buffer.order(ByteOrder.nativeOrder());
				GL11.glReadPixels(FMT.WIDTH / 2 - 128, FMT.HEIGHT / 2 - 128, 256, 256, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				//FMT.FRAME.getContainer().remove(widget);
				FileChooser.chooseFile("Choose a Save Location", new File(pack.file, "/assets/" + pack.id + "/textures/").toPath().toString(), FileChooser.TYPE_PNG, true, file -> {
					if(file == null) return;
					BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
					byte[] arr0 = new byte[4];
					byte[] arr1 = new byte[4];
					int col;
					for(int x = 0; x < 256; x++){
						for(int y = 0; y < 256; y++){
							int yy = 255 - y;
							buffer.get((x + yy * 256) * 4, arr0);
							col = ByteUtils.getRGB(arr0);
							arr1[0] = arr0[3];
							arr1[1] = arr0[0];
							arr1[2] = arr0[1];
							arr1[3] = arr0[2];
							img.setRGB(x, y, col == Settings.BACKGROUND.value.packed ? 0x00000000 : ByteBuffer.wrap(arr1).getInt());
						}
					}
					try{
						ImageIO.write(img, "PNG", file);
						WorkspaceViewer.viewer().genView();
					}
					catch(IOException e){
						Logging.log(e);
					}
				});
			}));
			widget.getStyle().getBackground().setColor(ColorConstants.transparent());
			widget.setResizable(false);
			widget.setMinimizable(false);
			widget.setDraggable(false);
			//FMT.FRAME.getContainer().add(widget);
			widget.show();
		});*/
	}

	public static void genRoadAssets(){
		/*WorkspaceViewer.viewer().selectPackDialog(pack -> {
			Dialog dialog = new Dialog("Road Block Assets Generator Settings", 420, 260);
			dialog.getContainer().add(new Label("Block ID", 10, 10, 400, 30));
			TextField rid = new TextField("road", 10, 40, 400, 30);
			dialog.getContainer().add(rid);
			dialog.getContainer().add(new Label("Block Name", 10, 70, 400, 30));
			TextField bnm = new TextField("Road", 10, 100, 400, 30);
			dialog.getContainer().add(bnm);
			dialog.getContainer().add(new Label("Texture Adress", 10, 130, 400, 30));
			TextField tid = new TextField(pack.id + ":block/road", 10, 160, 400, 30);
			dialog.getContainer().add(tid);
			dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 200, 100, 20, () -> {
				String roadid = rid.getTextState().getText().trim();
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
				String name = bnm.getTextState().getText().trim();
				String texid = tid.getTextState().getText();
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
					map.add("textures", new JsonMap("particle", texid, "texture", texid));
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
				dialog.close();
				WorkspaceViewer.viewer().genView();
			}));
			dialog.setResizable(false);
			//dialog.show(FMT.FRAME);
		});*/
	}

	public static void openJson(){
		FileChooser.chooseFile("Choose a JSON file.", Settings.WORKSPACE_ROOT.value, FileChooser.TYPE_JSON, false, file -> {
			try{
				if(file != null && file.exists()) new JsonEditor(file);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
	}

	public static void mirrorLang(){
		FileChooser.chooseFile("Choose a lang file.", Settings.WORKSPACE_ROOT.value, FileChooser.TYPE_ANY, false, file -> {
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
					if(entry.getValue().item()) map.add("item." + entry.getKey(), Formatter.format(entry.getValue().name()));
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
