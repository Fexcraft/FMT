package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.update.UpdateHandler.update;
import static net.fexcraft.app.fmt.ui.GenericDialog.showOK;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelLoad;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelUnload;
import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

public class SaveHandler {
	
	public static int FORMAT = 4;

	public static void open(Model model, File file){
		if(file == null || !file.exists()){
			showOK("saveload.title", null, null, "saveload.open.nofile");
			return;
		}
		try{
			Settings.addRecentFile(file);
			TextureManager.clearGroups();
			ZipFile zip = new ZipFile(file);
			zip.stream().forEach(elm -> {
				if(elm.getName().equals("model.jtmt")){
					try{
						PreviewHandler.clear();
						FMT.MODEL = load(model, file, JsonHandler.parse(zip.getInputStream(elm)), false, false);
						FMT.MODEL.recompile();
						Model.SELECTED_POLYGONS = FMT.MODEL.count(true);
					}
					catch(IOException e){
						log(e);
					}
				}
				else if(elm.getName().equals("texture.png")){
					try{ //loads in old texture files
						if(FMT.MODEL.texgroup == null){
							TextureManager.addGroup(FMT.MODEL.texgroup = new TextureGroup("default"));
						}
						TextureManager.loadFromStream(zip.getInputStream(elm), "group-default", false, true);
						FMT.MODEL.texgroup.reAssignTexture();
						FMT.MODEL.recompile();
					}
					catch(IOException e){
						log(e);
					}
				}
				else if(elm.getName().startsWith("texture-")){
					try{
						String group = elm.getName().substring(elm.getName().indexOf("-") + 1).replace(".png", "");
						TextureManager.loadFromStream(zip.getInputStream(elm), "group-" + group, false, true);
						TextureManager.getGroup(group).reAssignTexture();
					}
					catch(IOException e){
						log(e);
					}
				}
			});
			zip.close();
			FMT.MODEL.file = file;
			update(new ModelLoad(FMT.MODEL));
			DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
		}
		catch(Exception e){
			log(e);
			showOK("saveload.title", null, null, "saveload.open.errors");
			return;
		}
	}

	@SuppressWarnings("unused")
	public static Model load(Model model, File from, JsonMap map, boolean preview, boolean sub){
		model.name = map.get("name", "Unnamed Model");
		model.texSizeX = map.get("texture_size_x", 256);
		model.texSizeY = map.get("texture_size_y", 256);
		model.opacity = map.get("opacity", 1f);
		model.scale = new Vector3f(map.getFloat("scale", 1f));
		model.orient = ModelOrientation.fromString(map.getString("orientation", null));
		model.format = ModelFormat.fromString(map.getString("target_format", null));
		if(map.has("creators")){
			map.getArrayElements("creators").forEach(elm -> {
				String auth = elm.string_value();
				boolean bool = auth.startsWith("!");
				if(bool) auth = auth.substring(1);
				model.addAuthor(auth, bool);
			});
		}
		int format = map.getInteger("format", 2);
		if(format == 1){
			JsonMap jmod = map.getMap("model");
			for(Entry<String, JsonValue<?>> entry : jmod.entries()){
				try{
					Group group = new Group(model, entry.getKey(), null);
					JsonArray array = entry.getValue().asArray();
					for(JsonValue<?> elm : array.elements()){
						group.add(Polygon.from(model, elm.asMap(), format));
					}
					model.addGroup(null, group);
				}
				catch(Exception e){
					log(e);
				}
			}
			return model;
		}
		if(map.has("textures") && !preview){
			map.getArrayElements("textures").forEach(elm -> TextureManager.addGroup(new TextureGroup(elm.string_value())));
		}
		if(map.has("texture_group")){
			if(preview){
				model.texhelper = map.get("texture_group").string_value();
			}
			else{
				model.texgroup = TextureManager.getGroup(map.get("texture_group").string_value());
			}
		}
		JsonMap pivots = map.getMap("pivots");
		model.pivots().clear();
		pivots.entries().forEach(entry -> {
			model.addPivot(new Pivot(entry.getKey(), entry.getValue().asMap()));
		});
		if(model.pivots().isEmpty()) model.addPivot(new Pivot("root", true));
		Pivot rootp = model.getRootPivot();
		JsonMap groups = map.getMap("groups");
		groups.entries().forEach(entry -> {
			try{
				Group group = new Group(model, entry.getKey(), rootp.id);
				JsonMap jsn = entry.getValue().asMap();
				group.minimized = jsn.getBoolean("minimized", false);
				group.selected = jsn.getBoolean("selected", false);
				group.visible = jsn.getBoolean("visible", true);
				if(jsn.has("color") && jsn.get("color").isNumber()){
					group.color.packed = jsn.get("color").integer_value();
				}
				group.pivot = jsn.getString("pivot", rootp.id);
				if(jsn.has("texture_group")){
					if(preview){
						group.texhelper = jsn.get("texture_group").string_value();
					}
					else{
						group.texgroup = TextureManager.getGroup(jsn.get("texture_group").string_value());
						group.texSizeX = jsn.get("texture_size_x").integer_value();
						group.texSizeY = jsn.get("texture_size_y").integer_value();
					}
				}
				if(jsn.has("offset")){
					JsonArray array = jsn.getArray("offset");
					group.pos.x = array.get(0).float_value();
					group.pos.y = array.get(1).float_value();
					group.pos.z = array.get(2).float_value();
				}
				if(jsn.has("polygons")){
					jsn.getArrayElements("polygons").forEach(elm -> {
						try{
							group.add(Polygon.from(model, elm.asMap(), format));
						}
						catch(Exception e){
							log(JsonHandler.toString(map, PrintOption.SPACED));
							log(e);
						}
					});
				}
				model.addGroup(group.pivot, group);
				//TODO load animations
			}
			catch(Throwable thr){
				log(thr);
			}
		});
		if(!preview){
			if(map.has("camera_pos")){
				JsonArray pos = map.getArray("camera_pos");
				FMT.CAM.pos.x = pos.get(0).float_value();
				FMT.CAM.pos.y = pos.get(1).float_value();
				FMT.CAM.pos.z = pos.get(2).float_value();
			}
			FMT.CAM.hor = map.get("camera_horizontal", FMT.CAM.hor);
			FMT.CAM.ver = map.get("camera_vertical", FMT.CAM.ver);
			//FMT.CAM.fov(Jsoniser.get(obj, "camera_fov", FMT.CAM.fov()));
		}
		if(map.has("helpers")){
			map.getArrayElements("helpers").forEach(elm -> {
				try{
					JsonMap jsn = elm.asMap();
					File file = new File(jsn.get("path").string_value());
					if(file.equals(from)) return;
					Model helper = null;
					if(jsn.get("name").string_value().startsWith("frame/")){
						helper = PreviewHandler.loadFrame(file);
					}
					else if(jsn.get("name").string_value().startsWith("fmtb/")){
						helper = PreviewHandler.loadFMTB(file);
					}
					else{
						//TODO find importer
						Object porter = null;
						if(porter == null){
							log("ERROR: Could not find importer for helper/preview '" + file.getPath() + "'!");
							return;
						}
						helper = PreviewHandler.load(file, porter, jsn);
					}
					if(helper == null) return;
					helper.name = jsn.get("name", "Unnamed Helper-Preview");
					if(jsn.has("opacity")){
						helper.opacity = jsn.get("opacity").float_value();
					}
					if(jsn.has("pos_x")){
						helper.pos = new Vector3f(jsn.get("pos_x").float_value(), jsn.get("pos_y").float_value(), jsn.get("pos_z").float_value());
					}
					else if(jsn.has("pos")){
						JsonArray pos = map.getArray("pos");
						helper.pos.x = pos.get(0).float_value();
						helper.pos.y = pos.get(1).float_value();
						helper.pos.z = pos.get(2).float_value();
					}
					if(jsn.has("rot_x")){
						helper.rot = new Vector3f(jsn.get("rot_x").float_value(), jsn.get("rot_y").float_value(), jsn.get("rot_z").float_value());
					}
					else if(jsn.has("rot")){
						JsonArray pos = map.getArray("rot");
						helper.rot.x = pos.get(0).float_value();
						helper.rot.y = pos.get(1).float_value();
						helper.rot.z = pos.get(2).float_value();
					}
					if(jsn.has("scale_x")){
						helper.scale = new Vector3f(jsn.get("scale_x").float_value(), jsn.get("scale_y").float_value(), jsn.get("scale_z").float_value());
					}
					else if(jsn.has("scale")){
						helper.scale = new Vector3f(jsn.get("scale").float_value());
					}
					if(jsn.has("invisible")){
						/*List<String> list = JsonUtil.getGson().fromJson(jsn.get("invisible").toString(), type);
						for(Group turbogroup : helper.groups()){
							turbogroup.visible = !list.contains(turbogroup.id);
						}*///TODO
					}
					helper.visible = jsn.get("visible", true);
					helper.subhelper = sub;
				}
				catch(Exception e){
					log(e);
				}
			});
		}
		if(map.has("export_values")){
			map.getMap("export_values").entries().forEach(entry -> {
				model.export_values.put(entry.getKey(), entry.getValue().string_value());
			});
		}
		if(map.has("export_array_values")){
			map.getMap("export_array_values").entries().forEach(entry -> {
				model.export_listed_values.put(entry.getKey(), new ArrayList<>());
				entry.getValue().asArray().elements().forEach(elm -> {
					model.export_listed_values.get(entry.getKey()).add(elm.string_value());
				});
			});
		}
		if(map.has("export_group_presets")){
			map.getMap("export_group_presets").entries().forEach(entry -> {
				model.export_group_preset_keys.add(entry.getKey());
				model.export_group_presets.add(entry.getValue().asArray().toStringList());
			});
		}
		model.recompile();
		return model;
	}

	public static void save(Model model, File file, Runnable run){
		save(model, file, run, false, true);
	}

	public static void save(Model model, File file, Runnable run, boolean backup, boolean open){
		file = file == null ? model.file : file;
		if(file == null){
			GenericDialog.showOC(null, () -> { if(run != null) run.run(); }, null, "saveload.save.nofile");
			return;
		}
		try{
			FileOutputStream fileout = new FileOutputStream(file);
			ZipOutputStream zipout = new ZipOutputStream(fileout);
			zipout.putNextEntry(new ZipEntry("marker.fmt"));
			zipout.write(new byte[]{ Byte.MIN_VALUE });
			zipout.closeEntry();
			int streams = 1 + TextureManager.getGroupAmount();
			InputStream[] arr = new InputStream[streams];
			arr[0] = new ByteArrayInputStream(modelToJTMT(model, false).toString().getBytes(StandardCharsets.UTF_8));
			if(arr.length > 1){
				for(int i = 0; i < TextureManager.getGroupAmount(); i++){
					TextureGroup group = TextureManager.getGroups().get(i);
					try{
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						group.texture.save();
						InputStream in = new FileInputStream(group.texture.getFile());
						IOUtils.copy(in, os);
						in.close();
						arr[1 + i] = new ByteArrayInputStream(os.toByteArray());
					}
					catch(Exception e){
						log(e);
					}
				}
			}
			zipout.setComment("FMTB Save File generated by FMT (Fexcraft Modelling Toolbox).");
			for(int i = 0; i < arr.length; i++){
				String entryname = i == 0 ? "model.jtmt" : "texture-%s.png";
				if(i > 0) entryname = String.format(entryname, TextureManager.getGroups().get(i - 1).name);
				zipout.putNextEntry(new ZipEntry(entryname));
				byte[] bytes = new byte[1024];
				int length;
				while((length = arr[i].read(bytes)) >= 0){
					zipout.write(bytes, 0, length);
				}
				zipout.closeEntry();
				arr[i].close();
			}
			zipout.close();
			fileout.close();
			if(!backup){
				if(file != null){
					log("Saved model as FMTB " + (arr.length > 1 ? " with texture." : "."));
				}
				if(Settings.OPEN_FOLDER_AFTER_SAVE.value && open && file.getParentFile() != null){
					FMT.openLink(file.getParentFile().getAbsolutePath());
				}
			}
		}
		catch(Exception e){
			log(e);
		}
		if(run != null) run.run();
	}
	
	public static JsonMap modelToJTMT(Model root, boolean export){
		Model model = root == null ? FMT.MODEL : root;
		JsonMap map = new JsonMap();
		map.add("format", FORMAT);
		map.add("name", model.name);
		map.add("texture_size_x", model.texSizeX);
		map.add("texture_size_y", model.texSizeY);
		map.add("orientation", model.orient.name().toLowerCase());
		map.add("target_format", model.format.name().toLowerCase());
		if(!export && model.opacity < 1f) map.add("opacity", model.opacity);
		if(model.scale != null && model.scale.x != 1f) map.add("scale", model.scale.x);
		JsonArray creators = new JsonArray();
		if(model.getAuthors().isEmpty()){
			//TODO add to creators if logged in
		}
		else{
			for(Entry<String, Boolean> entry : model.getAuthors().entrySet()){
				String name = entry.getKey();
				if(entry.getValue() && !export) name = "!" + name;
				creators.add(name);
			}
		}
		map.add("creators", creators);
		map.add("type", "jtmt");
		if(TextureManager.anyGroupsLoaded()){
			JsonArray textures = new JsonArray();
			for(TextureGroup group : TextureManager.getGroups()){
				textures.add(group.name);
			}
			map.add("textures", textures);
		}
		if(model.texgroup != null) map.add("texture_group", model.texgroup.name);
		JsonMap pivots = new JsonMap();
		for(Pivot pivot : model.pivots()){
			pivots.add(pivot.id, pivot.save());
		}
		map.add("pivots", pivots);
		JsonMap modobj = new JsonMap();
		for(Group group : model.allgroups()){
			JsonMap grobj = new JsonMap();
			JsonArray array = new JsonArray();
			if(!export){
				grobj.add("visible", group.visible);
				if(group.color != null){
					byte[] colarr = group.color.toByteArray();
					JsonArray colar = new JsonArray();
					colar.add(colarr[0]);
					colar.add(colarr[1]);
					colar.add(colarr[2]);
					grobj.add("color", colar);
				}
				grobj.add("minimized", group.minimized);
				grobj.add("selected", group.selected);
				if(group.texgroup != null){
					grobj.add("texture_group", group.texgroup.name);
					grobj.add("texture_size_x", group.texSizeX);
					grobj.add("texture_size_y", group.texSizeY);
				}
				//TODO animations
			}
			grobj.add("name", group.id);
			if(group.pivot != null) grobj.add("pivot", group.pivot);
			for(Polygon polygon : group){
				array.add(polygon.save(export));
			}
			grobj.add("polygons", array);
			modobj.add(group.id, grobj);
		}
		map.add("groups", modobj);
		if(!export){
			JsonArray array = new JsonArray();
			array.add(FMT.CAM.pos.x);
			array.add(FMT.CAM.pos.y);
			array.add(FMT.CAM.pos.z);
			map.add("camera_pos", array);
			map.add("camera_horizontal", FMT.CAM.hor);
			map.add("camera_vertical", FMT.CAM.ver);
		}
		if(!PreviewHandler.getLoaded().isEmpty() && !export){
			JsonArray array = new JsonArray();
			for(Model premod : PreviewHandler.getLoaded()){
				if(premod.subhelper) continue;
				JsonMap jsn = new JsonMap();
				jsn.add("name", premod.name);
				jsn.add("texture", premod.texhelper);
				if(premod.rot != null){
					jsn.add("rot_x", premod.rot.x);
					jsn.add("rot_y", premod.rot.y);
					jsn.add("rot_z", premod.rot.z);
				}
				if(premod.pos != null){
					jsn.add("pos_x", premod.pos.x);
					jsn.add("pos_y", premod.pos.y);
					jsn.add("pos_z", premod.pos.z);
				}
				if(premod.scale != null){
					jsn.add("scale_x", premod.scale.x);
					jsn.add("scale_y", premod.scale.y);
					jsn.add("scale_z", premod.scale.z);
				}
				if(premod.opacity < 1f){
					jsn.add("opacity", premod.opacity);
				}
				if(premod.allgroups().size() > 1){
					JsonArray invisible = new JsonArray();
					for(Group list : premod.allgroups()){
						if(!list.visible) invisible.add(list.id);
					}
					if(invisible.size() > 0){
						jsn.add("invisible", invisible);
					}
				}
				jsn.add("path", premod.file.toPath().toString());
				jsn.add("visible", premod.visible);
				array.add(jsn);
			}
			map.add("helpers", array);
		}
		if(!export){
			if(model.export_values.size() > 0){
				map.addMap("export_values");
				model.export_values.entrySet().forEach(entry -> {
					map.getMap("export_values").add(entry.getKey(), entry.getValue());
				});
			}
			if(model.export_listed_values.size() > 0){
				map.addMap("export_array_values");
				model.export_listed_values.entrySet().forEach(entry -> {
					JsonArray array = new JsonArray();
					entry.getValue().forEach(elm -> array.add(elm));
					map.getMap("export_array_values").add(entry.getKey(), array);
				});
			}
			if(model.export_group_presets.size() > 0){
				map.addMap("export_group_presets");
				for(int i = 0; i < model.export_group_preset_keys.size(); i++){
					JsonArray array = new JsonArray();
					model.export_group_presets.get(i).forEach(str -> array.add(str));
					map.getMap("export_group_presets").add(model.export_group_preset_keys.get(i), array);
				}
			}
		}
		return map;
	}

	public static void openDialog(File file){
		Runnable run = () -> {
			if(file == null){
				FileChooser.chooseFile(Translator.translate("saveload.open"), "./saves", FileChooser.TYPE_FMTB, false, task -> {
					UpdateHandler.update(new ModelUnload(FMT.MODEL));
					FMT.MODEL = new Model(task, null);
					Settings.addRecentFile(task);
					FMT.MODEL.load();
				});
			}
			else{
				UpdateHandler.update(new ModelUnload(FMT.MODEL));
				FMT.MODEL = new Model(file, null);
				Settings.addRecentFile(file);
				FMT.MODEL.load();
			}
		};
		if(FMT.MODEL.allgroups().isEmpty()){
			run.run();
			return;
		}
		shouldSaveDialog(run);
	}

	private static void shouldSaveDialog(Runnable run){
		GenericDialog.showYN("saveload.title", () -> saveDialogByState(run), () -> run.run(), "saveload.should_save");
	}

	public static void saveDialogByState(Runnable run){
		if(FMT.MODEL.file == null) saveAsDialog(run);
		else saveDialog(null, run);
	}

	public static void saveDialog(File file, Runnable run){
		GenericDialog.showCC("saveload.title", () -> {
			save(FMT.MODEL, file, run);
		}, null, "saveload.confirm_save", "#" + (file == null ? FMT.MODEL.file : file));
	}

	public static void saveAsDialog(Runnable run){
		FileChooser.chooseFile(Translator.translate("saveload.save"), "./saves", FileChooser.TYPE_FMTB, true, task -> {
			FMT.MODEL.file = task;
			saveDialog(task, run);
		});
	}
	
	public static void newDialog(){
		Runnable run = () -> {
			float width = 400;
	        Dialog dialog = new Dialog(Translator.translate("saveload.new"), width, 230);
	        Settings.applyComponentTheme(dialog.getContainer());
	        dialog.setResizable(true);
        	Label label0 = new Label(Translator.translate("saveload.new.name"), 10, 10, width - 20, 20);
        	dialog.getContainer().add(label0);
        	TextField field = new TextField("Unnamed Model", 10, 35, width - 20, 20);
        	dialog.getContainer().add(field);
        	Label label1 = new Label(Translator.translate("saveload.new.orientation"), 10, 65, width - 20, 20);
        	dialog.getContainer().add(label1);
        	SelectBox<String> box0 = new SelectBox<>(10, 90, width - 20, 20);
        	for(ModelOrientation orient : ModelOrientation.values()){
        		box0.addElement(orient.name());
        	}
			box0.setSelected(ModelOrientation.FVTM4_DEFAULT.ordinal(), true);
        	dialog.getContainer().add(box0);
        	Label label2 = new Label(Translator.translate("saveload.new.target_format"), 10, 120, width - 20, 20);
        	dialog.getContainer().add(label2);
        	SelectBox<String> box1 = new SelectBox<>(10, 145, width - 20, 20);
        	for(ModelFormat format : ModelFormat.values()){
        		box1.addElement(format.name);
        	}
        	box1.setSelected(0, true);
        	dialog.getContainer().add(box1);
        	//
            Button button0 = new Button(Translator.translate("dialog.button.confirm"), 10, 180, 100, 20);
            button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()){
        			UpdateHandler.update(new ModelUnload(FMT.MODEL));
        			FMT.MODEL = new Model(null, field.getTextState().getText());
        			FMT.MODEL.orient = ModelOrientation.valueOf(box0.getSelection());
        			FMT.MODEL.format = ModelFormat.fromName(box1.getSelection());
        			FMT.updateTitle();
        			DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
        			UpdateHandler.update(new ModelLoad(FMT.MODEL));
            		dialog.close();
            	}
            });
            dialog.getContainer().add(button0);
            //
            Button button1 = new Button(Translator.translate("dialog.button.cancel"), 120, 180, 100, 20);
            button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
            	if(CLICK == e.getAction()) dialog.close();
            });
            dialog.getContainer().add(button1);
            //
	        dialog.show(FMT.FRAME);
		};
		if(!FMT.MODEL.allgroups().isEmpty()){
			shouldSaveDialog(run);
		}
		else run.run();
	}

}
