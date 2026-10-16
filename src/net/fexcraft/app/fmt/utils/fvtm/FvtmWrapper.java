package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SaveHandler;
import net.fexcraft.app.fmt.workspace.FileElm;
import net.fexcraft.app.fmt.workspace.FvtmPackElm;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.data.Content;
import net.fexcraft.mod.fvtm.data.ContentType;
import net.fexcraft.mod.fvtm.data.addon.AddonLocation;
import net.fexcraft.mod.fvtm.data.root.Textureable;
import net.fexcraft.mod.fvtm.model.DefaultModel;
import net.fexcraft.mod.fvtm.model.ModelGroup;
import net.fexcraft.mod.fvtm.util.QV3D;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.impl.AABBI;
import net.fexcraft.mod.uni.inv.ItemWrapper;
import net.fexcraft.mod.uni.inv.StackWrapper;
import net.fexcraft.mod.uni.world.AABB;
import net.fexcraft.mod.uni.world.WorldW;
import org.joml.Vector3f;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

import static net.fexcraft.app.fmt.utils.SaveHandler.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmWrapper extends FvtmResources {

	public static void start(){
		FvtmLogger.LOGGER = new FvtmLogger() {
			@Override
			protected void log0(Object obj){
				Logging.log("[fvtm] " + obj);
			}
		};
		AABB.SUPPLIER = AABBI::new;
		EnvInfo.CLIENT = true;
		EnvInfo.DEV = true;
		File ws = new File(Settings.WORKSPACE_ROOT.value);
		FvtmRegistry.init("FMT", new File(ws, "/local-fvtm"));
		INSTANCE = new FvtmWrapper();
		INSTANCE.init();
		INSTANCE.searchPacksInFolder(ws, AddonLocation.CONFIGPACK, false);
		INSTANCE.registerAttributes();
		INSTANCE.registerFunctions();
		INSTANCE.registerHandlers();
		INSTANCE.searchContent();
		FvtmResources.initModelSystem();
	}

	@Override
	public boolean searchPacksInResourcePacks(){
		return false;
	}

	@Override
	public void loadPackTextures(){

	}

	@Override
	public void createContentBlocks(){

	}

	@Override
	public void createContentItems(){

	}

	@Override
	public void registerFvtmRecipes(){

	}

	@Override
	public ItemWrapper getItemWrapper(String id){
		return null;
	}

	@Override
	public StackWrapper newStack0(ItemWrapper item){
		return null;
	}

	@Override
	public void initModelPrograms(){

	}

	@Override
	public InputStream getAssetInputStream(IDL loc, boolean log){
		return null;
	}

	@Override
	public boolean isModPresent(String s){
		return false;
	}

	@Override
	public IDL getExternalTexture0(String custom){
		return null;
	}

	@Override
	public void registerFvtmBlocks(){

	}

	@Override
	public void registerFvtmItems(){

	}

	@Override
	public double getMouseSensitivity(){
		return 0;
	}

	@Override
	public Object getBlockMaterial(String key, boolean allownull){
		return null;
	}

	@Override
	public void spawnRoadMarker(WorldW world, QV3D vector, UUID nid){

	}

	@Override
	public void spawnRailMarker(WorldW world, QV3D vector, UUID nid){

	}

	@Override
	public void linkItemContainer(ItemWrapper item){

	}

	//--//

	public static class ModelExternalData {

		public ContentType type;
		public Content content;

	}

	public static ModelExternalData getMED(String type, String cid){
		ModelExternalData data = new ModelExternalData();
		data.type = ContentType.valueOf(type);
		data.content = null;
		switch(data.type){
			case VEHICLE: data.content = FvtmRegistry.VEHICLES.get(cid); break;
			case PART: data.content = FvtmRegistry.PARTS.get(cid); break;
			case BLOCK: data.content = FvtmRegistry.BLOCKS.get(cid); break;
			case DECORATION: data.content = FvtmRegistry.DECORATIONS.get(cid); break;
			case WIRE_COMPONENT: data.content = FvtmRegistry.WIRE_COMPS.get(cid); break;
			case SIGN: data.content = FvtmRegistry.SIGNS.get(cid); break;
		}
		return data;
	}

	public static Model loadFvtmSave(Model model, File from, JsonMap map, boolean preview, boolean sub){
		FMT.WORKSPACE.loadIfNotLoaded();
		ModelExternalData data = getMED(map.getString("fvtm_type", ContentType.VEHICLE.name()), map.get("fvtm_id").string_value());
		model.exdata = data;
		model.name = map.get("name", data.content.getName());
		DefaultModel mod = (DefaultModel)data.content.getModel();
		int texSizeX = mod.tex_width;
		int texSizeY = mod.tex_height;
		model.scl = new Vector3f(16, 16, 16);
		model.orient = data.type == ContentType.VEHICLE || data.type == ContentType.PART ? ModelOrientation.FVTM4_DEFAULT : ModelOrientation.FVTM4_BLOCK;
		model.format = ModelFormat.UNIVERSAL;
		for(String creator : mod.getCreators()) model.addAuthor(creator, true);
		int format = map.getInteger("format", 2);
		for(IDL tex : ((Textureable.TextureHolder)data.content).getDefaultTextures()){
			FvtmPackElm pack = FMT.WORKSPACE.getFvtmPack(tex.space());
			for(FileElm com : pack.textures){
				String path = com.file.getPath().replace("\\", "/");
				String pid = path.substring(path.indexOf("/assets/") + 8, path.indexOf("/textures"));
				path = path.substring(path.indexOf("/textures/") + 1);
				if(tex.colon().equals(pid + ":" + path)){
					model.addTexGroup(new TextureGroup(model, tex.colon()));
				}
			}
		}
		model.texgroup = model.getTexGroup(((Textureable.TextureHolder)data.content).getDefaultTextures().get(0).colon());
		if(model.texgroup == null) model.genDefTexGroup();
		if(texSizeX > 0) model.texgroup.width = texSizeX;
		if(texSizeY > 0) model.texgroup.height = texSizeY;
		model.render_textured = map.getBoolean("render_textured", true);
		//
		model.pivots().clear();
		if(model.pivots().isEmpty()) model.addPivot(new Pivot("root", true));
		model.rerootpivots();//TODO swivel points
		//
		JsonMap groups = map.getMap("groups");
		if(groups.empty()) appendHedrons(model, data);
		else groups.entries().forEach(entry -> {
			ModelGroup mg = mod.get(entry.getKey());
			Group group = SaveHandler.loadModelGroup(model, model.pivots().get(0), entry, 0);
			for(Polyhedron hed : mg) group.add(new FvtmView(model, hed));
		});
		if(!preview) loadModelLocalData(model, map);
		loadModelHelpers(model, from, sub, map);
		loadModelExportValues(model, map);
		model.recompile();
		return model;
	}

	public static void appendHedrons(Model model, ModelExternalData data){
		for(ModelGroup mg : data.content.getModel().getGroups()){
			Group group = new Group(model, mg.name, model.getRootPivot().id);
			for(Polyhedron hed : mg) group.add(new FvtmView(model, hed));
			model.addGroup(group.pivot, group);
		}
	}

	public static void saveFvtmLoad(Model model, JsonMap map){
		ModelExternalData data = (ModelExternalData)model.exdata;
		map.add("fvtm_type", data.type.name());
		map.add("fvtm_id", data.content.getIDS());
		//TODO swivel points
		saveModelGroups(model, map, false);
		saveModelLocalData(model, map);
		saveModelHelpers(model, map);
		saveModelExportValues(model, map);
	}

}
