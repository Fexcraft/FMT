package net.fexcraft.app.fmt.utils.fvtm;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DecorationConfigReference extends ConfigReference {

	public static DecorationConfigReference INSTANCE = new DecorationConfigReference();

	public DecorationConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Material", true));
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("Model", MODELLOC));
		entries.add(of("ModelData", SEPARATE));
		entries.add(of("Textures", ARRAY_SIMPLE).add(of(TEXLOC)).alt("Texture"));
		entries.add(of("RandomTexture", BOOLEAN));
		entries.add(of("ColorChannels", OBJECT_KEY_VAL).add(of(COLOR).def("#fdfdfd", true)));
		entries.add(of("ItemTexture", TEXLOC));
		entries.add(of("Disable3DItemModel", BOOLEAN).def(false));
		entries.add(of("MaxItemStackSize", INTEGER).limit(64, 1, 64));
	}

}
