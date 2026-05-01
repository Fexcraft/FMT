package net.fexcraft.app.fmt.workspace;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum FvtmType {

	VEHICLE("Vehicle", "vehicles"),
	PART("Part", "parts"),
	BLOCK("Block", "blocks"),
	MATERIAL("Material", "materials"),
	CONSUMABLE("Consumable", "consumables"),
	MULTIBLOCK("MultiBlock", "multiblocks"),
	CONTAINER("Shipping Container", "containers"),
	RAILGAUGE("Rail Gauge", "railgauges"),
	DECORATION("Decoration", "decos"),
	CLOTH("Cloth", "clothes"),
	FUEL("Fuel", "fuels"),
	WIRE("Wire", "wires"),
	WIRE_DECO("Wire Deco", "wires"),
	SIGN("Sign", "signs");

	public final Pair<VFileType, FvtmType> pair = Pair.of(VFileType.FVTM_CONFIG, this);
	public final String _name;
	public final String folder;

	FvtmType(String _name, String folder){
		this._name = _name;
		this.folder = folder;
	}

}
