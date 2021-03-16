package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipFile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.texture.Texture;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.ZipUtil;

public class HelperCollector {

	public static final ArrayList<GroupCompound> LOADED = new ArrayList<>();
	public static int SELECTED = -1;

	public static final void reload(boolean loadedold){
		if(!loadedold) LOADED.clear();
		File root = new File("./helpers");
		if(!root.exists()) root.mkdirs();
	}

	public static final GroupCompound load(File file, ExImPorter exim, Map<String, Setting> settings){
		if(file == null || exim == null) return null;
		log("Loading Preview/Helper model: " + file.getName());
		GroupCompound compound = exim.importModel(file, settings);
		if(compound != null){
			if(!compound.name.startsWith("import/")){
				compound.name = "import/" + compound.name;
			}
			compound.porter = exim;
			add(compound);
		}
		return compound;
	}

	/** For loading FMTBs. */
	public static GroupCompound loadFMTB(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.nofile");
			return null;
		}
		GroupCompound compound = null;
		try{
			boolean conM = ZipUtil.contains(file, "model.jtmt"), conT = ZipUtil.contains(file, "texture.png");
			ZipFile zip = new ZipFile(file);
			JsonObject obj = JsonUtil.getObjectFromInputStream(zip.getInputStream(zip.getEntry("model.jtmt")));
			if(conM){
				compound = SaveLoad.getModel(file, obj, false, true);
				if(!compound.name.startsWith("fmtb/")) compound.name = "fmtb/" + compound.name;
			}
			else{
				DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.invalid_file");
				zip.close();
				return null;
			}
			if(conT){
				compound.helpertex = "./temp/" + compound.name;
				if(!TextureManager.containsTexture(compound.helpertex)){
					TextureManager.loadTextureFromZip(zip.getInputStream(zip.getEntry("texture.png")), compound.helpertex, false, false);
				}
			}
			if(obj.has("textures")){
				JsonArray array = obj.get("textures").getAsJsonArray();
				for(JsonElement elm : array){
					String group = elm.getAsString();
					String texid = "./temp/" + compound.name + "/" + group;
					if(!TextureManager.containsTexture(texid)){
						TextureManager.loadTextureFromZip(zip.getInputStream(zip.getEntry("texture-" + group + ".png")), texid, false, false);
					}
					for(TurboList list : compound.getGroups()){
						if(list.helpertex != null && list.helpertex.equals(group)){
							list.helpertex = texid;
							log("Applied '" + group + "' to " + list.id);
						}
					}
					if(compound.helpertex.equals(group)){
						compound.helpertex = texid;
						log("Applied '" + group + "' to " + compound.name);
					}
				}
			}
			compound.recompile();
			zip.close();
		}
		catch(Exception e){
			log(e);
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.errors");
		}
		if(compound != null) add(compound);
		return compound;
	}

	public static GroupCompound loadFrame(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_frame.nofile");
		}
		GroupCompound compound = null;
		try{
			Texture tex = TextureManager.loadTextureFromFile(file, "./temp/frame/" + file.getName(), false, false);
			compound = new GroupCompound(file);
			compound.getGroups().clear();
			compound.file = file;
			if(!compound.name.startsWith("frame/")) compound.name = "frame/" + file.getName();
			compound.helpertex = "./temp/frame/" + file.getName();
			compound.textureSizeX = tex.getWidth();
			compound.textureSizeY = tex.getHeight();
			BoxWrapper polygon = new BoxWrapper(compound);
			polygon.size.xCoord = tex.getWidth();
			polygon.size.yCoord = tex.getHeight();
			polygon.size.zCoord = 0.2f;
			polygon.textureX = polygon.textureY = 0;
			float w = tex.getWidth(), h = tex.getHeight();
			polygon.cuv.get(BoxFace.RIGHT).set(FaceUVType.OFFSET_FULL).value(new float[]{ 0, 0, w, 0, w, h, 0, h });
			polygon.cuv.get(BoxFace.LEFT).set(FaceUVType.OFFSET_ENDS).value(new float[]{ 0, 0, w, h });
			polygon.off.zCoord = -0.1f;
			polygon.off.xCoord = -(polygon.size.xCoord / 2);
			polygon.off.yCoord = -(polygon.size.yCoord / 2);
			compound.add(polygon, "frame", true);
		}
		catch(Exception e){
			log(e);
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_frame.errors");
		}
		if(compound != null) add(compound);
		return compound;
	}

	private static void add(GroupCompound compound){
		compound.clearSelection();
		compound.minimized = true;
		Trees.helper.addSub(compound.button);
		compound.getGroups().setAsHelperPreview(compound);
		LOADED.add(compound);
	}

	public static GroupCompound getSelected(){
		return SELECTED >= HelperCollector.LOADED.size() || SELECTED < 0 ? null : HelperCollector.LOADED.get(SELECTED);
	}

}
