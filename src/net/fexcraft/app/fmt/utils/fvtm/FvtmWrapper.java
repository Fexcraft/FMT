package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.mod.fvtm.FvtmLogger;
import net.fexcraft.mod.fvtm.FvtmRegistry;
import net.fexcraft.mod.fvtm.FvtmResources;
import net.fexcraft.mod.fvtm.data.addon.AddonLocation;
import net.fexcraft.mod.fvtm.util.QV3D;
import net.fexcraft.mod.uni.EnvInfo;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.impl.AABBI;
import net.fexcraft.mod.uni.inv.ItemWrapper;
import net.fexcraft.mod.uni.inv.StackWrapper;
import net.fexcraft.mod.uni.world.AABB;
import net.fexcraft.mod.uni.world.WorldW;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

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

}
