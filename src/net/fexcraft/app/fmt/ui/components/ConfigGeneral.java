package net.fexcraft.app.fmt.ui.components;

import com.google.common.io.Files;
import com.spinyowl.legui.component.CheckBox;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.style.border.SimpleLineBorder;
import com.spinyowl.legui.style.color.ColorConstants;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.JsonEditor;
import net.fexcraft.app.fmt.ui.PosCopyIcon;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.ui.workspace.WorkspaceViewer;
import net.fexcraft.app.fmt.update.UpdateEvent.HelperChanged;
import net.fexcraft.app.fmt.update.UpdateEvent.HelperRenamed;
import net.fexcraft.app.fmt.update.UpdateEvent.HelperSelected;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.utils.ByteUtils;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.PreviewHandler;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConfigGeneral extends EditorComponent {

	protected static final String genid = "config.general";

	public ConfigGeneral(){
		super(genid, 340, false, true);
		add(new Label(translate(LANG_PREFIX + genid + ".pack_utils"), L5, row(1), LW, HEIGHT));
		add(new RunButton("editor.component.config.general.pack_new", L5, row(1), LW, HEIGHT, ConfigGeneral::createNewPack));
		add(new RunButton("editor.component.config.general.gen_asset_dirs", L5, row(1), LW, HEIGHT, ConfigGeneral::genAssetDirs));
		add(new RunButton("editor.component.config.general.content_new", L5, row(1), LW, HEIGHT, ConfigGeneral::createNewContent));
		add(new RunButton("editor.component.config.general.icon_from_view", L5, row(1), LW, HEIGHT, ConfigGeneral::createNewIcon));
		add(new Label(translate(LANG_PREFIX + genid + ".file_utils"), L5, row(2), LW, HEIGHT));
		add(new RunButton("editor.component.config.general.open_json", L5, row(1), LW, HEIGHT, ConfigGeneral::openJson));
		add(new Label(translate(LANG_PREFIX + genid + ".run_utils"), L5, row(2), LW, HEIGHT));
		add(new RunButton("Run 1.12", L5, row(1), LW, HEIGHT, () -> WorkspaceViewer.run(true)));
		add(new RunButton("Run 1.2+", L5, row(1), LW, HEIGHT, () -> WorkspaceViewer.run(false)));

	}

	public static void createNewPack(){
		Dialog dialog = new Dialog("Pack Creation Settings", 420, 190);
		dialog.getContainer().add(new Label("Pack Name:", 10, 10, 400, 30));
		TextField name = new TextField("pack_name", 10, 40, 400, 30);
		dialog.getContainer().add(name);
		dialog.getContainer().add(new Label("Pack ID:", 10, 70, 400, 30));
		TextField pid = new TextField("pack_id", 10, 100, 400, 30);
		dialog.getContainer().add(pid);
		dialog.getContainer().add(new RunButton("dialog.button.confirm", 310, 140, 100, 20, () -> {
			File folder = new File(Settings.WORKSPACE_ROOT.value);
			String nam = name.getTextState().getText().replace(" ", "");
			File pr = new File(folder, nam + "/");
			File pkfd = new File(pr, "/assets/" + pid.getTextState().getText() + "/");
			pkfd.mkdirs();
			//
			JsonMap map = new JsonMap();
			map.add("ID", pid.getTextState().getText());
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
			LangCache.genLangJson(new File(pr, "/assets/" + pid.getTextState().getText() + "/lang/en_us.json"));
			LangCache.genLangFile(new File(pr, "/assets/" + pid.getTextState().getText() + "/lang/en_us.lang"));
			//
			dialog.close();
			WorkspaceViewer.viewer.genView();
		}));
		dialog.setResizable(false);
		dialog.show(FMT.FRAME);
	}

	public static void genAssetDirs(){
		WorkspaceViewer.viewer.selectPackDialog(pack -> {
			Dialog dialog = new Dialog("Please select Config Types.", 320, 270);
			HashMap<String, CheckBox> map = new HashMap<>();
			map.put("vehicles", new CheckBox("vehicles", 10, 10, 300, 20));
			map.put("parts", new CheckBox("parts", 10, 30, 300, 20));
			map.put("materials", new CheckBox("materials", 10, 50, 300, 20));
			map.put("consumables", new CheckBox("consumables", 10, 70, 300, 20));
			map.put("fuels", new CheckBox("fuels", 10, 90, 300, 20));
			map.put("blocks", new CheckBox("blocks", 10, 110, 300, 20));
			map.put("wires", new CheckBox("wires", 10, 130, 300, 20));
			map.put("decos", new CheckBox("decos", 10, 150, 300, 20));
			map.put("railgauges", new CheckBox("rail gauges", 10, 170, 300, 20));
			map.put("clothes", new CheckBox("clothes", 10, 190, 300, 20));
			for(CheckBox box : map.values()){
				box.getStyle().setPadding(0, 0, 0, 5);
			}
			dialog.getContainer().addAll(map.values());
			dialog.getContainer().add(new RunButton("dialog.button.confirm", 10, 220, 100, 20, () -> {
				dialog.close();
				File fl;
				for(Map.Entry<String, CheckBox> entry : map.entrySet()){
					if(!entry.getValue().isChecked()) continue;
					fl = new File(pack.file, "/assets/" + pack.id + "/config/" + entry.getKey());
					fl.mkdirs();
					fl = new File(pack.file, "/assets/" + pack.id + "/textures/" + entry.getKey().substring(0, entry.getKey().length() - 1));
					fl.mkdirs();
					fl = new File(pack.file, "/assets/" + pack.id + "/models/" + entry.getKey().substring(0, entry.getKey().length() - 1));
					fl.mkdirs();
				}
				WorkspaceViewer.viewer.genView();
			}));
			dialog.setResizable(false);
			dialog.show(FMT.FRAME);
		});
	}

	public static void createNewContent(){
		WorkspaceViewer.viewer.selectPackDialog(pack -> {
			WorkspaceViewer.viewer.selectContentTypeDialog(type -> {
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
					dialog.close();
					WorkspaceViewer.viewer.genView();
				}));
				dialog.setResizable(false);
				dialog.show(FMT.FRAME);
			});
		});
	}

	public static void createNewIcon(){
		WorkspaceViewer.viewer.selectPackDialog(pack -> {
			Widget widget = new Widget(FMT.WIDTH / 2f - 128, FMT.HEIGHT / 2f - 149, 256, 296);
			widget.getTitleTextState().setText("Center the model inside.");
			widget.getStyle().setBorder(new SimpleLineBorder(FMT.rgba(0xffff00), 2));
			widget.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
			Logging.log(widget.getTitle().getSize());
			widget.getContainer().add(new RunButton("dialog.button.save", 0, 256, 256, 20, () -> {
				ByteBuffer buffer = ByteBuffer.allocateDirect(256 * 256 * 4);
				buffer.order(ByteOrder.nativeOrder());
				GL11.glReadPixels(FMT.WIDTH / 2 - 128, FMT.HEIGHT / 2 - 128, 256, 256, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				FMT.FRAME.getContainer().remove(widget);
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
						WorkspaceViewer.viewer.genView();
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
			FMT.FRAME.getContainer().add(widget);
			widget.show();
		});
	}

	private static void openJson(){
		FileChooser.chooseFile("Choose a JSON file.", Settings.WORKSPACE_ROOT.value, FileChooser.TYPE_JSON, false, file -> {
			try{
				if(file != null && file.exists()) new JsonEditor(file);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		});
	}

}
