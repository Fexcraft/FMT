package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelDataReference extends ConfigReference {

	public static String[] vehtypes = new String[]{ "LAND", "WATER", "AIR", "RAIL", "HELI", "SPACE" };
	public static String[] attrtypes = new String[]{ "float", "integer", "boolean", "tristate", "string", "vector" };
	public static String[] spvars = new String[]{ "x", "y", "z", "yaw", "pitch", "roll" };
	//
	public static ModelDataReference INSTANCE = new ModelDataReference();
	private ArrayList entries = new ArrayList();

	public ModelDataReference(){
		entries.add(of("Authors", ARRAY_SIMPLE).alt("Creators").add(TEXT_ENTRY));
		entries.add(of("TextureWidth", INTEGER).limit(256, 8, 892));
		entries.add(of("TextureHeight", INTEGER).limit(256, 8, 892));
		entries.add(of("Programs", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Transforms", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("SmoothShading", BOOLEAN).def(false));
		entries.add(of("Include", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Pivots", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Offset", ARRAY_SIMPLE).add(TEXT_ENTRY));
	}

}
