package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleConfigReference implements Reference {

	public static VehicleConfigReference INSTANCE = new VehicleConfigReference();
	private ArrayList entries = new ArrayList();

	public VehicleConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Block", true));
		entries.add(of("Description", ARRAY_SIMPLE));
	}

	@Override
	public List<ConfigEntry> getEntries() {
		return entries;
	}

}
