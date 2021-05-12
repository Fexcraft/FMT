package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_LOAD;
import static net.fexcraft.app.fmt.ui.GenericDialog.showOK;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.joml.Vector3f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.ModelFormat;
import net.fexcraft.app.fmt.polygon.ModelOrientation;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.GenericDialog;
import net.fexcraft.app.fmt.ui.Toolbar;
import net.fexcraft.app.fmt.ui.fieds.TextField;
import net.fexcraft.lib.common.json.JsonUtil;

public class SaveHandler {

	public static void open(Model model, File file){
		if(file == null || !file.exists()){
			showOK("saveload.title", null, null, "saveload.open.nofile");
			return;
		}
		try{
			Toolbar.addRecent(file);
			TextureManager.clearGroups();
			ZipFile zip = new ZipFile(file);
			boolean[] updatetree = { false };
			zip.stream().forEach(elm -> {
				if(elm.getName().equals("model.jtmt")){
					try{
						PreviewHandler.clear();
						FMT.MODEL = load(model, file, JsonUtil.getObjectFromInputStream(zip.getInputStream(elm)), false, false);
						update(MODEL_LOAD, model);
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
							TextureManager.addGroup(FMT.MODEL.texgroup = new TextureGroup(new JsonPrimitive("default")));
						}
						TextureManager.loadFromStream(zip.getInputStream(elm), "group-default", false, true);
						FMT.MODEL.texgroup.reAssignTexture();
						FMT.MODEL.recompile();
					}
					catch(IOException e){
						log(e);
					}
					updatetree[0] = true;
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
					updatetree[0] = true;
				}
			});
			zip.close();
			FMT.MODEL.file = file;
			//if(updatetree[0]) //TODO update trees?
			DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
		}
		catch(Exception e){
			log(e);
			showOK("saveload.title", null, null, "saveload.open.errors");
			return;
		}
	}

	@SuppressWarnings("unused")
	public static Model load(Model model, File from, JsonObject obj, boolean preview, boolean sub){
		model.name = Jsoniser.get(obj, "name", "Unnamed Model");
		model.texSizeX = Jsoniser.get(obj, "texture_size_x", 256);
		model.texSizeY = Jsoniser.get(obj, "texture_size_y", 256);
		model.opacity = Jsoniser.get(obj, "opacity", 1f);
		model.scale = new Vector3f(Jsoniser.get(obj, "scale", 1f));
		model.orient = ModelOrientation.fromString(Jsoniser.get(obj, "orientation", null));
		model.format = ModelFormat.fromString(Jsoniser.get(obj, "target_format", null));
		if(obj.has("creators")){
			obj.get("creators").getAsJsonArray().forEach(elm -> {
				String auth = elm.getAsString();
				boolean bool = auth.startsWith("!");
				if(bool) auth = auth.substring(1);
				model.addAuthor(auth, bool);
			});
		}
		int format = Jsoniser.get(obj, "format", 2);
		if(format == 1){
			JsonObject jmod = obj.get("model").getAsJsonObject();
			for(Entry<String, JsonElement> entry : jmod.entrySet()){
				try{
					Group group = new Group(model, entry.getKey());
					JsonArray array = entry.getValue().getAsJsonArray();
					for(JsonElement elm : array){
						group.add(Polygon.from(model, elm.getAsJsonObject()));
					}
					model.addGroup(group);
				}
				catch(Exception e){
					log(e);
				}
			}
			return model;
		}
		if(obj.has("textures") && !preview){
			obj.get("textures").getAsJsonArray().forEach(elm -> TextureManager.addGroup(new TextureGroup(elm)));
		}
		if(obj.has("texture_group")){
			if(preview){
				model.texhelper = obj.get("texture_group").getAsString();
			}
			else{
				model.texgroup = TextureManager.getGroup(obj.get("texture_group").getAsString());
			}
		}
		JsonObject groups = obj.get("groups").getAsJsonObject();
		groups.entrySet().forEach(entry -> {
			try{
				Group group = new Group(model, entry.getKey());
				JsonObject jsn = entry.getValue().getAsJsonObject();
				group.minimized = Jsoniser.get(jsn, "minimized", false);
				group.selected = Jsoniser.get(jsn, "selected", false);
				group.visible = Jsoniser.get(jsn, "visible", true);
				if(jsn.has("color") && jsn.isJsonPrimitive()){
					group.color.packed = jsn.get("color").getAsInt();
				}
				if(jsn.has("texture_group")){
					if(preview){
						group.texhelper = jsn.get("texture_group").getAsString();
					}
					else{
						group.texgroup = TextureManager.getGroup(jsn.get("texture_group").getAsString());
						group.texSizeX = jsn.get("texture_size_x").getAsInt();
						group.texSizeY = jsn.get("texture_size_y").getAsInt();
					}
				}
				if(jsn.has("offset")){
					JsonArray array = jsn.get("offset").getAsJsonArray();
					group.pos.x = array.get(0).getAsFloat();
					group.pos.y = array.get(1).getAsFloat();
					group.pos.z = array.get(2).getAsFloat();
				}
				if(jsn.has("polygons")){
					jsn.get("polygons").getAsJsonArray().forEach(elm -> {
						try{
							group.add(Polygon.from(model, elm.getAsJsonObject()));
						}
						catch(Exception e){
							log(elm.getAsJsonObject());
							log(e);
						}
					});
				}
				model.addGroup(group);
				//TODO load animations
			}
			catch(Throwable thr){
				log(thr);
			}
		});
		if(!preview){
			if(obj.has("camera_pos")){
				JsonArray pos = obj.getAsJsonArray("camera_pos");
				FMT.CAM.pos.x = pos.get(0).getAsFloat();
				FMT.CAM.pos.y = pos.get(1).getAsFloat();
				FMT.CAM.pos.z = pos.get(2).getAsFloat();
			}
			FMT.CAM.hor = Jsoniser.get(obj, "camera_horizontal", FMT.CAM.hor);
			FMT.CAM.ver = Jsoniser.get(obj, "camera_vertical", FMT.CAM.ver);
			//FMT.CAM.fov(Jsoniser.get(obj, "camera_fov", FMT.CAM.fov()));
		}
		if(obj.has("helpers")){
			obj.get("helpers").getAsJsonArray().forEach(elm -> {
				try{
					JsonObject jsn = elm.getAsJsonObject();
					File file = new File(jsn.get("path").getAsString());
					if(file.equals(from)) return;
					Model helper = null;
					if(jsn.get("name").getAsString().startsWith("frame/")){
						helper = PreviewHandler.loadFrame(file);
					}
					else if(jsn.get("name").getAsString().startsWith("fmtb/")){
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
					helper.name = Jsoniser.get(jsn, "name", "Unnamed Helper-Preview");
					if(jsn.has("opacity")){
						helper.opacity = jsn.get("opacity").getAsFloat();
					}
					if(jsn.has("pos_x")){
						helper.pos = new Vector3f(jsn.get("pos_x").getAsFloat(), jsn.get("pos_y").getAsFloat(), jsn.get("pos_z").getAsFloat());
					}
					else if(jsn.has("pos")){
						JsonArray pos = obj.getAsJsonArray("pos");
						helper.pos.x = pos.get(0).getAsFloat();
						helper.pos.y = pos.get(1).getAsFloat();
						helper.pos.z = pos.get(2).getAsFloat();
					}
					if(jsn.has("rot_x")){
						helper.rot = new Vector3f(jsn.get("rot_x").getAsFloat(), jsn.get("rot_y").getAsFloat(), jsn.get("rot_z").getAsFloat());
					}
					else if(jsn.has("rot")){
						JsonArray pos = obj.getAsJsonArray("rot");
						helper.rot.x = pos.get(0).getAsFloat();
						helper.rot.y = pos.get(1).getAsFloat();
						helper.rot.z = pos.get(2).getAsFloat();
					}
					if(jsn.has("scale_x")){
						helper.scale = new Vector3f(jsn.get("scale_x").getAsFloat(), jsn.get("scale_y").getAsFloat(), jsn.get("scale_z").getAsFloat());
					}
					else if(jsn.has("scale")){
						helper.scale = new Vector3f(jsn.get("scale").getAsFloat());
					}
					if(jsn.has("invisible")){
						Type type = new TypeToken<List<String>>(){}.getType();
						List<String> list = JsonUtil.getGson().fromJson(jsn.get("invisible").toString(), type);
						for(Group turbogroup : helper.groups()){
							turbogroup.visible = !list.contains(turbogroup.id);
						}
					}
					helper.visible = JsonUtil.getIfExists(jsn, "visible", true);
					helper.subhelper = sub;
				}
				catch(Exception e){
					log(e);
				}
			});
		}
		model.recompile();
		return model;
	}

	public static void save(Model model, File file, Runnable run){
		file = file == null ? model.file : file;
		if(file == null){
			GenericDialog.showOC(null, () -> run.run(), null, "saveload.save.nofile");
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
			if(file != null){
				log("Saved model as FMTB " + (arr.length > 1 ? " with texture." : "."));
			}
			if(Settings.OPEN_FOLDER_AFTER_SAVE.value && file.getParentFile() != null){
				FMT.openLink(file.getParentFile().getAbsolutePath());
			}
		}
		catch(Exception e){
			log(e);
		}
	}
	
	public static JsonObject modelToJTMT(Model root, boolean export){
		Model model = root == null ? FMT.MODEL : root;
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 4);
		obj.addProperty("name", model.name);
		obj.addProperty("texture_size_x", model.texSizeX);
		obj.addProperty("texture_size_y", model.texSizeY);
		obj.addProperty("orientation", model.orient.name().toLowerCase());
		obj.addProperty("target_format", model.format.name().toLowerCase());
		if(!export && model.opacity < 1f) obj.addProperty("opacity", model.opacity);
		if(model.scale != null && model.scale.x != 1f) obj.addProperty("scale", model.scale.x);
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
		obj.add("creators", creators);
		obj.addProperty("type", "jtmt");
		if(!TextureManager.anyGroupsLoaded()){
			JsonArray textures = new JsonArray();
			for(TextureGroup group : TextureManager.getGroups()){
				textures.add(group.name);
			}
			obj.add("textures", textures);
		}
		if(model.texgroup != null) obj.addProperty("texture_group", model.texgroup.name);
		JsonObject modobj = new JsonObject();
		for(Group group : model.groups()){
			JsonObject grobj = new JsonObject();
			JsonArray array = new JsonArray();
			if(!export){
				grobj.addProperty("visible", group.visible);
				if(group.color != null){
					byte[] colarr = group.color.toByteArray();
					JsonArray colar = new JsonArray();
					colar.add(colarr[0]);
					colar.add(colarr[1]);
					colar.add(colarr[2]);
					grobj.add("color", colar);
				}
				grobj.addProperty("minimized", group.minimized);
				grobj.addProperty("selected", group.selected);
				if(group.texgroup != null){
					grobj.addProperty("texture_group", group.texgroup.name);
					grobj.addProperty("texture_size_x", group.texSizeX);
					grobj.addProperty("texture_size_y", group.texSizeY);
				}
				//TODO animations
			}
			grobj.addProperty("name", group.id);
			for(Polygon polygon : group){
				array.add(polygon.save(export));
			}
			grobj.add("polygons", array);
			modobj.add(group.id, grobj);
		}
		obj.add("groups", modobj);
		if(!export){
			JsonArray array = new JsonArray();
			array.add(FMT.CAM.pos.x);
			array.add(FMT.CAM.pos.y);
			array.add(FMT.CAM.pos.z);
			obj.add("camera_pos", array);
			obj.addProperty("camera_horizontal", FMT.CAM.hor);
			obj.addProperty("camera_vertical", FMT.CAM.ver);
		}
		if(!PreviewHandler.getLoaded().isEmpty() && !export){
			JsonArray array = new JsonArray();
			for(Model premod : PreviewHandler.getLoaded()){
				if(premod.subhelper) continue;
				JsonObject jsn = new JsonObject();
				jsn.addProperty("name", premod.name);
				jsn.addProperty("texture", premod.texhelper);
				if(premod.rot != null){
					jsn.addProperty("rot_x", premod.rot.x);
					jsn.addProperty("rot_y", premod.rot.y);
					jsn.addProperty("rot_z", premod.rot.z);
				}
				if(premod.pos != null){
					jsn.addProperty("pos_x", premod.pos.x);
					jsn.addProperty("pos_y", premod.pos.y);
					jsn.addProperty("pos_z", premod.pos.z);
				}
				if(premod.scale != null){
					jsn.addProperty("scale_x", premod.scale.x);
					jsn.addProperty("scale_y", premod.scale.y);
					jsn.addProperty("scale_z", premod.scale.z);
				}
				if(premod.opacity < 1f){
					jsn.addProperty("opacity", premod.opacity);
				}
				if(premod.groups().size() > 1){
					JsonArray invisible = new JsonArray();
					for(Group list : premod.groups()){
						if(!list.visible) invisible.add(list.id);
					}
					if(invisible.size() > 0){
						jsn.add("invisible", invisible);
					}
				}
				jsn.addProperty("path", premod.file.toPath().toString());
				jsn.addProperty("visible", premod.visible);
				array.add(jsn);
			}
			obj.add("helpers", array);
		}
		return obj;
	}

	public static void openDialog(){
		Runnable run = () -> {
			FileChooser.chooseFile(Translator.translate("saveload.open"), "./saves", FileChooser.TYPE_FMTB, false, task -> {
				UpdateHandler.update(UpdateType.MODEL_UNLOAD, FMT.MODEL);
				FMT.MODEL = new Model(task, null);
				FMT.MODEL.load();
			});
		};
		if(FMT.MODEL.groups().isEmpty()){
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
		}, null, "saveload.confirm_save", "#" + file);
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
        	box0.setSelected(0, true);
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
        			UpdateHandler.update(UpdateType.MODEL_UNLOAD, FMT.MODEL);
        			FMT.MODEL = new Model(null, field.getTextState().getText());
        			FMT.MODEL.orient = ModelOrientation.valueOf(box0.getSelection());
        			FMT.MODEL.format = ModelFormat.fromName(box1.getSelection());
        			FMT.updateTitle();
        			DiscordUtil.update(Settings.DISCORD_RESET_ON_NEW.value);
        			UpdateHandler.update(UpdateType.MODEL_LOAD, FMT.MODEL);
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
		if(!FMT.MODEL.groups().isEmpty()){
			shouldSaveDialog(run);
		}
		else run.run();
	}

}
