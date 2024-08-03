package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartConfigReference implements Reference {

	public static PartConfigReference INSTANCE = new PartConfigReference();
	private ArrayList entries = new ArrayList();

	public PartConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Material", true));
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("Model", MODELLOC));
		entries.add(of("ModelData", SEPARATE));
		entries.add(of("Textures", ARRAY_SIMPLE).add(of(TEXLOC)).alt("Texture"));
		entries.add(of("ItemTexture", TEXLOC));
		entries.add(of("Category", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Installation", SEPARATE));
		VehicleConfigReference.addAttrsEntry(entries);
		entries.add(of("AttributeModifiers", OBJECT_KEY_VAL).add(TEXT_ENTRY));
		entries.add(of("Functions", SEPARATE));
		VehicleConfigReference.addPointsEntry(entries);
		entries.add(of("Sounds", OBJECT).add(
			of("volume", DECIMAL).limit(1, 0),
			of("pitch", DECIMAL).limit(1, 0)
		));
		entries.add(of("Disable3DItemModel", BOOLEAN).def(false));
	}

	@Override
	public List<ConfigEntry> getEntries() {
		return entries;
	}

	public static String[] installhandlers = new String[]{ "default", "wheel", "bogie", "tire" };

}
