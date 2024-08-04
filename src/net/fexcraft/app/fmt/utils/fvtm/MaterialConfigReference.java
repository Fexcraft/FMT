package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MaterialConfigReference extends ConfigReference {

	public static MaterialConfigReference INSTANCE = new MaterialConfigReference();

	public MaterialConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Material", true));
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("ItemTexture", TEXLOC));
		entries.add(of("MaxItemStackSize", INTEGER).limit(64, 0, 64));
		entries.add(of("OreDictionary", TEXT));
		entries.add(of("ContainerItem", TEXT));
		//
		entries.add(of("MaxItemDamage", INTEGER));
		entries.add(of("ItemBurnTime", INTEGER));
		entries.add(of("VehicleKey", BOOLEAN));
		entries.add(of("FuelContainer", BOOLEAN));
		entries.add(of("FuelCapacity", INTEGER));
		entries.add(of("FuelType", TEXT));
		entries.add(of("FuelGroup", TEXT));
		entries.add(of("ImpactWrench", INTEGER).limit(-1, -1, 8));
	}

}
