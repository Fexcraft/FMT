package net.fexcraft.app.fmt.utils.fvtm;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SignConfigReference extends ConfigReference {

	public static SignConfigReference INSTANCE = new SignConfigReference();

	public SignConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Sign", true));
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("Model", MODELLOC));
		entries.add(of("ModelData", SEPARATE));
		entries.add(of("Textures", ARRAY_SIMPLE).add(of(TEXLOC)).alt("Texture"));
		entries.add(of("Base", BOOLEAN).def(false));
		entries.add(of("Text", BOOLEAN).def(false));
	}

}
