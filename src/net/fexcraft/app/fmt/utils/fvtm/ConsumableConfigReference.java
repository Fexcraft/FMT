package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ConsumableConfigReference implements Reference {

	public static ConsumableConfigReference INSTANCE = new ConsumableConfigReference();
	private ArrayList entries = new ArrayList();

	public ConsumableConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Consumable", true));
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("ItemTexture", TEXLOC));
		entries.add(of("MaxItemStackSize", INTEGER).limit(64, 0, 64));
		entries.add(of("OreDictionary", TEXT));
		entries.add(of("ContainerItem", TEXT));
		//
		entries.add(of("HealAmount", INTEGER).limit(1, 0, 100));
		entries.add(of("Saturation", DECIMAL).limit(0.6f, 0, 100));
		entries.add(of("UseDuration", INTEGER).limit(32, 0, 1000));
		entries.add(of("WolfFood", BOOLEAN));
		entries.add(of("Drinkable", BOOLEAN));
		entries.add(of("AlwaysEdible", BOOLEAN));
	}

	@Override
	public List<ConfigEntry> getEntries() {
		return entries;
	}

}
