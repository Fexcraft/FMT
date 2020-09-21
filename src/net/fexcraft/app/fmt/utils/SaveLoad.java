package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToFMT;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.FileSelector;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.utils.texture.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class SaveLoad {

	public static void openModel(){
		FileSelector.select(Translator.translate("saveload.open"), new File("./saves").getAbsolutePath(), FileSelector.TYPE_FMTB, false, file -> openModel(file));
	}

	public static void openModel(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("saveload.title", null, null, "saveload.open.nofile");
			return;
		}
		try{
			TextureManager.clearGroups();
			ZipFile zip = new ZipFile(file);
			boolean[] updatetree = { false };
			zip.stream().forEach(elm -> {
				if(elm.getName().equals("model.jtmt")){
					try{
						HelperCollector.LOADED.clear();
						GroupCompound compound = parseModel(file, JsonUtil.getObjectFromInputStream(zip.getInputStream(elm)));
						FMTB.setModel(compound, false, false);
						FMTB.MODEL.updateFields();
						FMTB.MODEL.recompile();
						GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
					}
					catch(IOException e){
						log(e);
					}
				}
				else if(elm.getName().equals("texture.png")){
					try{ //loads in old texture files
						if(FMTB.MODEL.texgroup == null){
							TextureManager.addGroup(FMTB.MODEL.texgroup = new TextureGroup(new JsonPrimitive("default")));
						}
						TextureManager.loadTextureFromZip(zip.getInputStream(elm), "group-default", false, true);
						FMTB.MODEL.texgroup.reAssignTexture();
						FMTB.MODEL.recompile();
					}
					catch(IOException e){
						log(e);
					}
					updatetree[0] = true;
				}
				else if(elm.getName().startsWith("texture-")){
					try{
						String group = elm.getName().substring(elm.getName().indexOf("-") + 1).replace(".png", "");
						TextureManager.loadTextureFromZip(zip.getInputStream(elm), "group-" + group, false, true);
						TextureManager.getGroup(group).reAssignTexture();
					}
					catch(IOException e){
						log(e);
					}
					updatetree[0] = true;
				}
			});
			zip.close();
			FMTB.MODEL.file = file;
			if(updatetree[0]) Trees.textures.reOrderGroups();
			DiscordUtil.update(Settings.discordrpc_resettimeronnewmodel());
		}
		catch(Exception e){
			log(e);
			DialogBox.showOK("saveload.title", null, null, "saveload.open.errors");
			return;
		}
	}

	public static GroupCompound parseModel(File from, JsonObject obj){
		return getModel(from, obj, true); // FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
	}

	public static void checkIfShouldSave(boolean shouldclose, boolean shouldclear){
		TextureUpdate.HALT = true;
		if(FMTB.MODEL.countTotalMRTs() > 0){
			DialogBox.showYN("saveload.title", () -> {
				if(FMTB.MODEL.file == null){
					FileSelector.select(Translator.translate("saveload.save"), new File("./saves").getAbsolutePath(), FileSelector.TYPE_FMTB, true, file -> {
						if(file == null){
							DialogBox.show("saveload.title", "dialogbox.button.ok", "dialogbox.button.save", () -> {
								if(shouldclose) FMTB.get().close(true);
							}, () -> {
								checkIfShouldSave(shouldclose, shouldclear);
							}, "saveload.save.nofile");
						}
						else{
							FMTB.MODEL.file = file;
							saveModel(false, shouldclose);
							if(shouldclear){
								FMTB.setModel(new GroupCompound(null), true, true);
							}
							if(shouldclose){
								FMTB.get().close(true);
							}
						}
					});
				}
				else{
					saveModel(false, false);// shouldclose);
					if(shouldclear){
						FMTB.setModel(new GroupCompound(null), true, true);
					}
					if(shouldclose){
						FMTB.get().close(true);
					}
				}
			}, () -> {
				log("selected > no saving of current");
				if(shouldclear){
					FMTB.setModel(new GroupCompound(null), true, true);
				}
				if(shouldclose){
					FMTB.get().close(true);
				}
			}, "saveload.should_save");
		}
		else if(shouldclose){
			FMTB.get().close(true);
		}
	}

	public static void openNewModel(){
		checkIfShouldSave(false, true);
		// FMTB.MODEL = new GroupCompound();
	}

	public static void saveModel(boolean bool, boolean openfile){
		if(bool || FMTB.MODEL.file == null){
			FileSelector.select(Translator.translate("saveload.save"), new File("./saves").getAbsolutePath(), FileSelector.TYPE_FMTB, true, file -> {
				if(file == null){
					DialogBox.showOK("saveload.title", null, null, "saveload.save.nofile");
					return;
				}
				FMTB.MODEL.file = file;
				toFile(FMTB.MODEL, null, openfile);
				DialogBox.showOK("saveload.title", null, null, "saveload.save.success");
				return;
			});
		}
		else{
			toFile(FMTB.MODEL, null, openfile);
			return;
		}
	}

	public static void toFile(GroupCompound compound, File file, boolean openfile){
		try{
			FileOutputStream fileout = new FileOutputStream(file == null ? compound.file : file);
			ZipOutputStream zipout = new ZipOutputStream(fileout);
			zipout.putNextEntry(new ZipEntry("marker.fmt"));
			zipout.write(new byte[]{ Byte.MIN_VALUE });
			zipout.closeEntry();
			int streams = 1 + TextureManager.getGroupAmount();
			InputStream[] arr = new InputStream[streams];
			arr[0] = new ByteArrayInputStream(modelToJTMT(null, false).toString().getBytes(StandardCharsets.UTF_8));
			if(arr.length > 1){
				for(int i = 0; i < TextureManager.getGroupAmount(); i++){
					TextureGroup group = TextureManager.getGroupsFE().get(i);
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
			zipout.setComment("FMTB Save File generated by the FMT (Fexcraft Modelling Toolbox).");
			for(int i = 0; i < arr.length; i++){
				String entryname = i == 0 ? "model.jtmt" : "texture-%s.png";
				if(i > 0) entryname = String.format(entryname, TextureManager.getGroupsFE().get(i - 1).group);
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
			if(file == null){
				log("Saved model as FMTB Archive" + (arr.length > 1 ? " with texture." : "."));
			}
			file = file == null ? compound.file : file;
			if(openfile && file.getParentFile() != null){
				FMTB.openLink(file.getParentFile().getAbsolutePath());
			}
		}
		catch(Exception e){
			log(e);
		}
	}

	/**
	 * @param root
	 * @return JTMT save form of the Model/GroupCompound
	 */
	public static JsonObject modelToJTMT(GroupCompound root, boolean export){
		GroupCompound compound = root == null ? FMTB.MODEL : root;
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 3);
		obj.addProperty("name", compound.name);
		obj.addProperty("texture_size_x", compound.tx(null));
		obj.addProperty("texture_size_y", compound.ty(null));
		//obj.addProperty("texture_scale", compound.textureScale);
		if(!export && compound.opacity < 1f) obj.addProperty("opacity", compound.opacity);
		if(compound.scale != null && compound.scale.xCoord != 1f) obj.addProperty("scale", compound.scale.xCoord);
		JsonArray creators = new JsonArray();
		if(compound.getAuthors().isEmpty()){
			if(SessionHandler.isLoggedIn()) creators.add(SessionHandler.getUserName());
		}
		else{
			for(Entry<String, Boolean> entry : compound.getCreators().entrySet()){
				String name = entry.getKey();
				if(entry.getValue() && !export) name = "!" + name;
				creators.add(name);
			}
		}
		obj.add("creators", creators);
		obj.addProperty("type", "jtmt");
		if(!TextureManager.anyGroupsLoaded()){
			JsonArray textures = new JsonArray();
			for(TextureGroup group : TextureManager.getGroupsFE()){
				/*
				 * JsonObject ksn = new JsonObject(); ksn.addProperty("name", group.group); ksn.addProperty("path", group.texture.getFile().toPath().toString()); textures.add(ksn);
				 */
				textures.add(group.group);
			}
			obj.add("textures", textures);
		}
		if(compound.texgroup != null) obj.addProperty("texture_group", compound.texgroup.group);
		JsonObject model = new JsonObject();
		for(TurboList list : compound.getGroups()){
			JsonObject group = new JsonObject();
			JsonArray array = new JsonArray();
			if(!export){
				group.addProperty("visible", list.visible);
				if(list.color != null){
					byte[] colarr = list.color.toByteArray();
					JsonArray colar = new JsonArray();
					colar.add(colarr[0]);
					colar.add(colarr[1]);
					colar.add(colarr[2]);
					group.add("color", colar);
				}
				group.addProperty("minimized", list.minimized);
				group.addProperty("selected", list.selected);
				if(list.texgroup != null){
					group.addProperty("texture_group", list.texgroup.group);
					group.addProperty("texture_size_x", list.textureX);
					group.addProperty("texture_size_y", list.textureY);
					//group.addProperty("texture_scale", list.textureS);
				}
				if(list.exportoffset != null){
					group.addProperty("export_offset_x", list.exportoffset.xCoord);
					group.addProperty("export_offset_y", list.exportoffset.yCoord);
					group.addProperty("export_offset_z", list.exportoffset.zCoord);
				}
				if(!list.animations.isEmpty()){
					JsonArray animations = new JsonArray();
					for(Animation ani : list.animations){
						JsonObject jsn = new JsonObject();
						jsn.addProperty("id", ani.id);
						JsonArray settings = new JsonArray();
						for(Setting setting : ani.settings.values()){
							JsonObject sett = new JsonObject();
							sett.addProperty("id", setting.getId());
							sett.addProperty("type", setting.getType().name().toLowerCase());
							sett.add("value", setting.save());
							settings.add(sett);
						}
						jsn.addProperty("active", ani.active);
						jsn.add("settings", settings);
						animations.add(jsn);
					}
					group.add("animations", animations);
				}
			}
			group.addProperty("name", list.id);
			for(PolygonWrapper wrapper : list){
				array.add(wrapper.toJson(export));
			}
			group.add("polygons", array);
			model.add(list.id, group);
		}
		obj.add("groups", model);
		if(!export){
			JsonArray array = new JsonArray();
			array.add(FMTB.ggr.pos.xCoord);
			array.add(FMTB.ggr.pos.yCoord);
			array.add(FMTB.ggr.pos.zCoord);
			obj.add("camera_pos", array);
			array = new JsonArray();
			array.add(FMTB.ggr.rotation.xCoord);
			array.add(FMTB.ggr.rotation.yCoord);
			array.add(FMTB.ggr.rotation.zCoord);
			obj.add("camera_rot", array);
			obj.addProperty("camera_orbit_distance", FMTB.ggr.distance);
			array = new JsonArray();
			array.add(FMTB.ggr.orbit.xCoord);
			array.add(FMTB.ggr.orbit.yCoord);
			array.add(FMTB.ggr.orbit.zCoord);
			obj.add("camera_orbit", array);
		}
		if(!HelperCollector.LOADED.isEmpty() && !export){
			JsonArray array = new JsonArray();
			for(GroupCompound group : HelperCollector.LOADED){
				if(group.subhelper) continue;
				JsonObject jsn = new JsonObject();
				jsn.addProperty("name", group.name);
				jsn.addProperty("texture", group.helpertex);
				if(group.rot != null){
					jsn.addProperty("rot_x", group.rot.xCoord);
					jsn.addProperty("rot_y", group.rot.yCoord);
					jsn.addProperty("rot_z", group.rot.zCoord);
				}
				if(group.pos != null){
					jsn.addProperty("pos_x", group.pos.xCoord);
					jsn.addProperty("pos_y", group.pos.yCoord);
					jsn.addProperty("pos_z", group.pos.zCoord);
				}
				if(group.scale != null){
					jsn.addProperty("scale_x", group.scale.xCoord);
					jsn.addProperty("scale_y", group.scale.yCoord);
					jsn.addProperty("scale_z", group.scale.zCoord);
				}
				if(group.opacity < 1f){
					jsn.addProperty("opacity", group.opacity);
				}
				if(group.getGroups().size() > 1){
					JsonArray invisible = new JsonArray();
					for(TurboList list : group.getGroups()){
						if(!list.visible) invisible.add(list.id);
					}
					if(invisible.size() > 0){
						jsn.add("invisible", invisible);
					}
				}
				jsn.addProperty("path", group.origin.toPath().toString());
				jsn.addProperty("visible", group.visible);
				array.add(jsn);
			}
			obj.add("helpers", array);
		}
		return obj;
	}

	public static GroupCompound getModel(File from, JsonObject obj, boolean ggr_nopreview){
		return getModel(from, obj, ggr_nopreview, false);
	}

	public static GroupCompound getModel(File from, JsonObject obj, boolean ggr_nopreview, boolean subhelper){
		GroupCompound compound = new GroupCompound(from);
		compound.getGroups();
		compound.name = JsonUtil.getIfExists(obj, "name", "unnamed model");
		compound.textureSizeX = JsonUtil.getIfExists(obj, "texture_size_x", 256).intValue();
		compound.textureSizeY = JsonUtil.getIfExists(obj, "texture_size_y", 256).intValue();
		compound.opacity = JsonUtil.getIfExists(obj, "opacity", 1f).floatValue();
		float scale = JsonUtil.getIfExists(obj, "scale", 1f).floatValue();
		compound.scale = new Vec3f(scale, scale, scale);
		//compound.textureScale = JsonUtil.getIfExists(obj, "texture_scale", 1).intValue();
		compound.setAuthors(JsonUtil.jsonArrayToStringArray(JsonUtil.getIfExists(obj, "creators", new JsonArray()).getAsJsonArray()));
		if(JsonUtil.getIfExists(obj, "format", 2).intValue() == 1){
			JsonObject model = obj.get("model").getAsJsonObject();
			for(Entry<String, JsonElement> entry : model.entrySet()){
				try{
					TurboList list = new TurboList(entry.getKey());
					JsonArray array = entry.getValue().getAsJsonArray();
					for(JsonElement elm : array){
						list.add(JsonToFMT.parseWrapper(compound, elm.getAsJsonObject()));
					}
					compound.getGroups().add(list);
				}
				catch(Exception e){
					log(e);
				}
			}
			return compound;
		}
		if(obj.has("textures") && ggr_nopreview){
			obj.get("textures").getAsJsonArray().forEach(elm -> TextureManager.addGroup(new TextureGroup(elm)));
		}
		if(obj.has("texture_group")){
			if(ggr_nopreview)
				compound.texgroup = TextureManager.getGroup(obj.get("texture_group").getAsString());
			else
				compound.helpertex = obj.get("texture_group").getAsString();
		}
		JsonObject groups = obj.get("groups").getAsJsonObject();
		for(Entry<String, JsonElement> entry : groups.entrySet()){
			try{
				TurboList list = new TurboList(entry.getKey());
				JsonObject group = entry.getValue().getAsJsonObject();
				list.minimized = JsonUtil.getIfExists(group, "minimized", false);
				list.selected = JsonUtil.getIfExists(group, "selected", false);
				list.visible = JsonUtil.getIfExists(group, "visible", true);
				if(group.has("color")){
					JsonArray colorarr = group.get("color").getAsJsonArray();
					list.color = new RGB(colorarr.get(0).getAsByte(), colorarr.get(1).getAsByte(), colorarr.get(2).getAsByte());
				}
				if(group.has("texture")){// import old
					TextureManager.addGroup(new TextureGroup(list.id, new File(group.get("texture").getAsString())));
					int texx = group.get("texture_size_x").getAsInt();
					int texy = group.get("texture_size_y").getAsInt();
					list.setTexture(TextureManager.getGroup(list.id), texx, texy);
					//list.textureS = JsonUtil.getIfExists(obj, "texture_scale", 1).intValue();
				}
				if(group.has("texture_group")){
					if(ggr_nopreview){
						int texx = group.get("texture_size_x").getAsInt();
						int texy = group.get("texture_size_y").getAsInt();
						list.setTexture(TextureManager.getGroup(group.get("texture_group").getAsString()), texx, texy);
						//list.textureS = JsonUtil.getIfExists(obj, "texture_scale", 1).intValue();
					}
					else{
						list.helpertex = group.get("texture_group").getAsString();
					}
				}
				if(group.has("export_offset_x") || group.has("export_offset_y") || group.has("export_offset_z")){
					list.exportoffset = new Vec3f();
					list.exportoffset.xCoord = JsonUtil.getIfExists(obj, "export_offset_x", 0).floatValue();
					list.exportoffset.yCoord = JsonUtil.getIfExists(obj, "export_offset_y", 0).floatValue();
					list.exportoffset.zCoord = JsonUtil.getIfExists(obj, "export_offset_z", 0).floatValue();
				}
				JsonArray polygons = group.get("polygons").getAsJsonArray();
				for(JsonElement elm : polygons){
					try{
						list.add(JsonToFMT.parseWrapper(compound, elm.getAsJsonObject()));
					}
					catch(Exception e){
						log(elm.getAsJsonObject());
						log(e);
					}
				}
				compound.getGroups().add(list);
				if(group.has("animations")){
					JsonArray arr = group.get("animations").getAsJsonArray();
					for(JsonElement elm : arr){
						JsonObject animjsn = elm.getAsJsonObject();
						Animation anim = Animator.get(animjsn.get("id").getAsString());
						if(anim == null) continue;
						anim = anim.copy(list);
						JsonArray settin = animjsn.get("settings").getAsJsonArray();
						for(JsonElement elm0 : settin){
							JsonObject sett = elm0.getAsJsonObject();
							Setting setting = new Setting(sett.get("type").getAsString(), sett.get("id").getAsString(), sett.get("value"));
							for(Setting satt : anim.settings.values()){
								if(satt.getId().equals(setting.getId()) && satt.getType() == setting.getType()){
									satt.setValue(setting.getValue());
								}
							}
						}
						anim.active = JsonUtil.getIfExists(animjsn, "active", true);
						list.animations.add(anim);
						list.abutton.update();
						anim.button.setRoot(list.abutton);
					}
				}
			}
			catch(Exception e){
				log(e);
			}
		}
		if(obj.has("camera_pos") && ggr_nopreview){
			JsonArray pos = obj.getAsJsonArray("camera_pos");
			FMTB.ggr.pos.xCoord = pos.get(0).getAsFloat();
			FMTB.ggr.pos.yCoord = pos.get(1).getAsFloat();
			FMTB.ggr.pos.zCoord = pos.get(2).getAsFloat();
		}
		if(obj.has("camera_rot") && ggr_nopreview){
			JsonArray rot = obj.getAsJsonArray("camera_rot");
			FMTB.ggr.rotation.xCoord = rot.get(0).getAsFloat();
			FMTB.ggr.rotation.yCoord = rot.get(1).getAsFloat();
			FMTB.ggr.rotation.zCoord = rot.get(2).getAsFloat();
			FMTB.ggr.orbital.setAngles(FMTB.ggr.rotation.yCoord, 0, -FMTB.ggr.rotation.xCoord);
		}
		if(obj.has("camera_orbit_distance")){
			FMTB.ggr.distance = obj.get("camera_orbit_distance").getAsFloat();
		}
		if(obj.has("camera_orbit")){
			JsonArray rot = obj.getAsJsonArray("camera_orbit");
			FMTB.ggr.orbit.xCoord = rot.get(0).getAsFloat();
			FMTB.ggr.orbit.yCoord = rot.get(1).getAsFloat();
			FMTB.ggr.orbit.zCoord = rot.get(2).getAsFloat();
		}
		if(obj.has("helpers")){
			JsonArray arr = obj.get("helpers").getAsJsonArray();
			for(JsonElement elm : arr){
				try{
					JsonObject jsn = elm.getAsJsonObject();
					File file = new File(jsn.get("path").getAsString());
					if(file.equals(from)) continue;
					GroupCompound helperpreview = null;
					if(jsn.get("name").getAsString().startsWith("frame/")){
						helperpreview = HelperCollector.loadFrame(file);
					}
					else if(jsn.get("name").getAsString().startsWith("fmtb/")){
						helperpreview = HelperCollector.loadFMTB(file);
					}
					else{
						// TODO save/load the porter settings too, I guess.
						ExImPorter porter = PorterManager.getPorterFor(file, false);
						if(porter == null){
							log("ERROR: Could not find importer for helper/preview '" + file.getPath() + "'!");
							continue;
						}
						HashMap<String, Setting> map = new HashMap<>();
						porter.getSettings(false).forEach(setting -> map.put(setting.getId(), setting));
						helperpreview = HelperCollector.load(file, porter, map);
					}
					helperpreview.name = jsn.get("name").getAsString();
					helperpreview.button.update();
					if(jsn.has("opacity")){
						helperpreview.opacity = jsn.get("opacity").getAsFloat();
					}
					if(jsn.has("pos_x")){
						helperpreview.pos = new Vec3f(jsn.get("pos_x").getAsFloat(), jsn.get("pos_y").getAsFloat(), jsn.get("pos_z").getAsFloat());
					}
					if(jsn.has("rot_x")){
						helperpreview.rot = new Vec3f(jsn.get("rot_x").getAsFloat(), jsn.get("rot_y").getAsFloat(), jsn.get("rot_z").getAsFloat());
					}
					if(jsn.has("scale_x")){
						helperpreview.scale = new Vec3f(jsn.get("scale_x").getAsFloat(), jsn.get("scale_y").getAsFloat(), jsn.get("scale_z").getAsFloat());
					}
					if(jsn.has("invisible")){
						Type type = new TypeToken<List<String>>(){}.getType();
						List<String> list = JsonUtil.getGson().fromJson(jsn.get("invisible").toString(), type);
						for(TurboList turbo : helperpreview.getGroups()){
							turbo.visible = !list.contains(turbo.id);
							turbo.pbutton.updateColor();
						}
					}
					helperpreview.visible = JsonUtil.getIfExists(jsn, "visible", true);
					helperpreview.subhelper = subhelper;
				}
				catch(Exception e){
					log(e);
				}
			}
		}
		return compound;
	}

}