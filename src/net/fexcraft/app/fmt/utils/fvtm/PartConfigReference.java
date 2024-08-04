package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartConfigReference extends ConfigReference {

	public static String[] installhandlers = new String[]{ "default", "wheel", "bogie", "tire" };
	public static String[] partfunctions = new String[]{
		"fvtm:wheel", "fvtm:wheel_positions", "fvtm:seats", "fvtm:engine",
		"fvtm:inventory", "fvtm:container", "fvtm:bogie", "fvtm:part_slots",
		"fvtm:connector", "fvtm:color", "fvtm:tire", "fvtm:transmission",
		"fvtm:particle_emitter", "fvtm:interact_zone"
	};
	public static PartConfigReference INSTANCE = new PartConfigReference();

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
		entries.add(of("Installation", ENUM_SEPARATE).enums(installhandlers).add(of("Handler", STATIC)));
		VehicleConfigReference.addAttrsEntry(entries);
		entries.add(of("AttributeModifiers", OBJECT_KEY_VAL).add(TEXT_ENTRY));
		entries.add(of("Functions", OBJECT_KEY_VAL).add(of(ENUM_SEPARATE).enums(partfunctions)));
		VehicleConfigReference.addPointsEntry(entries);
		entries.add(of("Sounds", OBJECT).add(
			of("volume", DECIMAL).limit(1, 0),
			of("pitch", DECIMAL).limit(1, 0)
		));
		entries.add(of("Disable3DItemModel", BOOLEAN).def(false));
	}

}
