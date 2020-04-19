package net.fexcraft.app.fmt.utils;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToTMT;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.FileSelector;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;

public class SaveLoad {

	public static void openModel(){
		FileSelector.select(Translator.translate("saveload.open"), new File("./saves").getAbsolutePath(), FileSelector.TYPE_FMTB, false, file -> openModel(file));
	}
	
	public static void openModel(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("saveload.title", null, null, "saveload.open.nofile"); return;
		}
		try{
			ZipFile zip = new ZipFile(file);
			zip.stream().forEach(elm -> {
				if(elm.getName().equals("model.jtmt")){
					try{
						HelperCollector.LOADED.clear();
						GroupCompound compound = parseModel(file, JsonUtil.getObjectFromInputStream(zip.getInputStream(elm)));
						FMTB.setModel(compound, false); FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
						GroupCompound.SELECTED_POLYGONS = FMTB.MODEL.countSelectedMRTs();
					} catch(IOException e){ e.printStackTrace(); }
				}
				else if(elm.getName().equals("texture.png")){
					FMTB.MODEL.setTexture("./temp/" + FMTB.MODEL.name);
	            	try{ //in theory this should be always 2nd in the stream, so it is expected the model loaded already
						TextureManager.loadTextureFromZip(zip.getInputStream(elm), "./temp/" + FMTB.MODEL.name, false, true);
					} catch(IOException e){ e.printStackTrace(); }
				}
			}); zip.close(); FMTB.MODEL.file = file; DiscordUtil.update(Settings.discordrpc_resettimeronnewmodel());
		}
		catch(Exception e){
			e.printStackTrace();
			DialogBox.showOK("saveload.title", null, null, "saveload.open.errors"); return;
		}
	}
	
	public static GroupCompound parseModel(File from, JsonObject obj){
		return getModel(from, obj, true); //FMTB.MODEL.updateFields(); FMTB.MODEL.recompile();
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
							FMTB.MODEL.file = file; saveModel(false, shouldclose);
							if(shouldclear){ FMTB.setModel(new GroupCompound(null), true);}
							if(shouldclose){ FMTB.get().close(true); }
						}
					});
				}
				else{
					saveModel(false, false);//shouldclose);
					if(shouldclear){ FMTB.setModel(new GroupCompound(null), true); }
					if(shouldclose){ FMTB.get().close(true); }
				}
			}, () -> {
				Print.console("selected > no saving of current");
				if(shouldclear){ FMTB.setModel(new GroupCompound(null), true); }
				if(shouldclose){ FMTB.get().close(true); }
			}, "saveload.should_save");
		}
		else if(shouldclose){
			FMTB.get().close(true);
		}
	}

	public static void openNewModel(){
		checkIfShouldSave(false, true);
		//FMTB.MODEL = new GroupCompound();
	}

	public static void saveModel(boolean bool, boolean openfile){
		if(bool || FMTB.MODEL.file == null){
			FileSelector.select(Translator.translate("saveload.save"), new File("./saves").getAbsolutePath(), FileSelector.TYPE_FMTB, true, file -> {
				if(file == null){
					DialogBox.showOK("saveload.title", null, null, "saveload.save.nofile"); return;
				}
				FMTB.MODEL.file = file; toFile(FMTB.MODEL, null, openfile);
				DialogBox.showOK("saveload.title", null, null, "saveload.save.success"); return;
			});
		}
		else{
			toFile(FMTB.MODEL, null, openfile); return;
		} 
	}
	
	public static void toFile(GroupCompound compound, File file, boolean openfile){
		try{
	        FileOutputStream fileout = new FileOutputStream(file == null ? compound.file : file);
	        ZipOutputStream zipout = new ZipOutputStream(fileout);
	        zipout.putNextEntry(new ZipEntry("marker.fmt")); zipout.write(new byte[]{ Byte.MIN_VALUE }); zipout.closeEntry();
	        InputStream[] arr = new InputStream[compound.texture == null ? 1 : 2];
	        arr[0] = new ByteArrayInputStream(modelToJTMT(null, false).toString().getBytes(StandardCharsets.UTF_8));
	        if(arr.length > 1){
	        	try{
	        		ByteArrayOutputStream os = new ByteArrayOutputStream();
	        		ImageIO.write(TextureManager.getTexture(compound.texture, false).getImage(), "png", os);
	        		arr[1] = new ByteArrayInputStream(os.toByteArray());
	        	} catch(Exception e){ e.printStackTrace(); }
	        }
	        zipout.setComment("FMTB Save File generated by the FMT (Fexcraft Modelling Toolbox).");
	        for(int i = 0; i < arr.length; i++){
	            zipout.putNextEntry(new ZipEntry(i == 1 ? "texture.png" : "model.jtmt"));
	            byte[] bytes = new byte[1024]; int length;
	            while((length = arr[i].read(bytes)) >= 0){
	                zipout.write(bytes, 0, length);
	            } zipout.closeEntry(); arr[i].close();
	        }
	        zipout.close(); fileout.close();
	        if(file == null){
	        	Print.console("Saved model as FMTB Archive" + (arr.length > 1 ? " with texture." : "."));
	        }
	        file = file == null ? compound.file : file;
	        if(openfile && file.getParentFile() != null){
		        Desktop.getDesktop().open(file.getParentFile());
	        }
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * @param root 
	 * @return JTMT save form of the Model/GroupCompound
	 */
	public static JsonObject modelToJTMT(GroupCompound root, boolean export){
		GroupCompound compound = root == null ? FMTB.MODEL : root;
		JsonObject obj = new JsonObject();
		obj.addProperty("format", 2);
		obj.addProperty("name", compound.name);
		obj.addProperty("texture_size_x", compound.tx(null));
		obj.addProperty("texture_size_y", compound.ty(null));
		obj.addProperty("texture_scale", compound.textureScale);
		JsonArray creators = new JsonArray();
		if(compound.creators.isEmpty()){
			creators.add(SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "OfflineUser");
		}
		else{
			for(String str : compound.creators) creators.add(str);
			if(SessionHandler.isLoggedIn() && !compound.creators.contains(SessionHandler.getUserName())){
				creators.add(SessionHandler.getUserName());
			}
		}
		obj.add("creators", creators);
		obj.addProperty("type", "jtmt");
		JsonObject model = new JsonObject();
		for(TurboList list : compound.getGroups()){
			JsonObject group = new JsonObject(); JsonArray array = new JsonArray();
			if(!export){
				group.addProperty("visible", list.visible);
				if(list.color != null){
					byte[] colarr = list.color.toByteArray();
					JsonArray colar = new JsonArray();
					colar.add(colarr[0]); colar.add(colarr[1]); colar.add(colarr[2]);
					group.add("color", colar);
				}
				group.addProperty("minimized", list.minimized);
				group.addProperty("selected", list.selected);
				if(list.getGroupTexture() != null){
					group.addProperty("texture", list.getGroupTexture());
					group.addProperty("texture_size_x", list.textureX);
					group.addProperty("texture_size_y", list.textureY);
					group.addProperty("texture_scale", list.textureS);
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
				JsonObject jsn = new JsonObject();
				jsn.addProperty("name", group.name);
				jsn.addProperty("texture", group.texture);
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
				jsn.addProperty("path", group.origin.toPath().toString());
				jsn.addProperty("visible", group.visible);
				array.add(jsn);
			}
			obj.add("helpers", array);
		}
		return obj;
	}
	
	public static GroupCompound getModel(File from, JsonObject obj, boolean ggr){
		GroupCompound compound = new GroupCompound(from); compound.getGroups();
		compound.name = JsonUtil.getIfExists(obj, "name", "unnamed model");
		compound.textureSizeX = JsonUtil.getIfExists(obj, "texture_size_x", 256).intValue();
		compound.textureSizeY = JsonUtil.getIfExists(obj, "texture_size_y", 256).intValue();
		compound.textureScale = JsonUtil.getIfExists(obj, "texture_scale", 1).intValue();
		compound.creators = JsonUtil.jsonArrayToStringArray(JsonUtil.getIfExists(obj, "creators", new JsonArray()).getAsJsonArray());
		if(JsonUtil.getIfExists(obj, "format", 2).intValue() == 1){
			JsonObject model = obj.get("model").getAsJsonObject();
			for(Entry<String, JsonElement> entry : model.entrySet()){
				try{
					TurboList list = new TurboList(entry.getKey()); JsonArray array = entry.getValue().getAsJsonArray();
					for(JsonElement elm : array){ list.add(JsonToTMT.parseWrapper(compound, elm.getAsJsonObject())); }
					compound.getGroups().add(list);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			return compound;
		}
		JsonObject groups = obj.get("groups").getAsJsonObject();
		for(Entry<String, JsonElement> entry : groups.entrySet()){
			try{
				TurboList list = new TurboList(entry.getKey()); JsonObject group = entry.getValue().getAsJsonObject();
				list.minimized = JsonUtil.getIfExists(group, "minimized", false);
				list.selected = JsonUtil.getIfExists(group, "selected", false);
				list.visible = JsonUtil.getIfExists(group, "visible", true);
				if(group.has("color")){
					JsonArray colorarr = group.get("color").getAsJsonArray();
					list.color = new RGB(colorarr.get(0).getAsByte(), colorarr.get(1).getAsByte(), colorarr.get(2).getAsByte());
				}
				if(group.has("texture")){
					int texx = group.get("texture_size_x").getAsInt();
					int texy = group.get("texture_size_y").getAsInt();
					list.setTexture(group.get("texture").getAsString(), texx, texy);
					list.textureS = JsonUtil.getIfExists(obj, "texture_scale", 1).intValue();
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
						list.add(JsonToTMT.parseWrapper(compound, elm.getAsJsonObject()));
					}
					catch(Exception e){
						Print.console(elm.getAsJsonObject()); e.printStackTrace();
					}
				}
				compound.getGroups().add(list);
				if(group.has("animations")){
					JsonArray arr = group.get("animations").getAsJsonArray();
					for(JsonElement elm : arr){
						JsonObject animjsn = elm.getAsJsonObject();
						Animation anim = Animator.get(animjsn.get("id").getAsString());
						if(anim == null) continue; anim = anim.copy(list);
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
						list.animations.add(anim); list.abutton.update();
						anim.button.setRoot(list.abutton);
					}
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		if(obj.has("camera_pos") && ggr){
			JsonArray pos = obj.getAsJsonArray("camera_pos");
			FMTB.ggr.pos.xCoord = pos.get(0).getAsFloat();
			FMTB.ggr.pos.yCoord = pos.get(1).getAsFloat();
			FMTB.ggr.pos.zCoord = pos.get(2).getAsFloat();
		}
		if(obj.has("camera_rot") && ggr){
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
						//TODO save/load the porter settings too, I guess.
						ExImPorter porter = PorterManager.getPorterFor(file, false);
						HashMap<String, Setting> map = new HashMap<>();
						porter.getSettings(false).forEach(setting -> map.put(setting.getId(), setting));
						helperpreview = HelperCollector.load(file, porter, map);
					}
					helperpreview.name = jsn.get("name").getAsString();
					helperpreview.button.update();
					if(jsn.has("pos_x")){
						helperpreview.pos = new Vec3f(jsn.get("pos_x").getAsFloat(), jsn.get("pos_y").getAsFloat(), jsn.get("pos_z").getAsFloat());
					}
					if(jsn.has("rot_x")){
						helperpreview.rot = new Vec3f(jsn.get("rot_x").getAsFloat(), jsn.get("rot_y").getAsFloat(), jsn.get("rot_z").getAsFloat());
					}
					if(jsn.has("scale_x")){
						helperpreview.scale = new Vec3f(jsn.get("scale_x").getAsFloat(), jsn.get("scale_y").getAsFloat(), jsn.get("scale_z").getAsFloat());
					}
					helperpreview.visible = JsonUtil.getIfExists(jsn, "visible", true);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return compound;
	}
	
}