package net.fexcraft.lib.fmr;

/**
 * Custom Model Rendering Library<br>
 * Inspired by TMT (TurboModelThingy)
 * <hr>
 * Why such name? People already started thinking because of FCL that "Fexcraft.Net" is a mod and not a website/community,
 *  so why not seriously start calling mods that way? Not like it matters at this point.
 * <hr>
 * As requested, I tried to make this as non-mc dependent as possible.
 * <hr>
 * @author Ferdinand Calo' (FEX___96)
 * @license http://fexcraft.net/license?id=mods
 */
//@Mod(modid = "fmr", name = "Fexcraft Model Renderer", version = "1.0", acceptableRemoteVersions = "*", acceptedMinecraftVersions = "*")
public class FexcraftModelRenderer {
	
	public static final float MODELSCALE = 0.0625F;
	public static final float PI = 3.14159265358979323846f;
	public static boolean GENERIC_BOOLEAN = true;//Static.dev();
	
	/*private static final HashMap<ResourceLocation, ModelContainer> CONTAINERS = new HashMap<>();
	private static ModelContainer tempcontainer;
	
	public static ModelContainer getModel(ResourceLocation loc){
		return (tempcontainer = CONTAINERS.get(loc)) == null ? loadModel(loc) : tempcontainer;
	}

	private static ModelContainer loadModel(ResourceLocation loc){
		try{
			Class<?> clazz = Class.forName(loc.getResourcePath());
			return (ModelContainer)clazz.newInstance();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}*/
	
	//TODO check if possible to hook into vanilla/forge modelloading system
	
}