package net.fexcraft.app.fmt.workspace;

import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum FvtmType {

	VEHICLE("Vehicle", "vehicles", "vehicle"),
	PART("Part", "parts", "part"),
	BLOCK("Block", "blocks", "block"),
	MATERIAL("Material", "materials", "material"),
	CONSUMABLE("Consumable", "consumables", "consumable"),
	MULTIBLOCK("MultiBlock", "multiblocks", "multiblock"),
	CONTAINER("Shipping Container", "containers", "container"),
	RAILGAUGE("Rail Gauge", "railgauges", "railgauge"),
	DECORATION("Decoration", "decos", "deco"),
	CLOTH("Cloth", "clothes", "cloth"),
	FUEL("Fuel", "fuels", "fuel"),
	WIRE("Wire", "wires", "wire"),
	WIRE_DECO("Wire Deco", "wires", "wiredeco"),
	SIGN("Sign", "signs", "sign");

	public final Pair<VFileType, FvtmType> pair = Pair.of(VFileType.FVTM_CONFIG, this);
	public final String _name;
	public final String folder;
	public final String suffix;

	FvtmType(String _name, String folder, String suffix){
		this._name = _name;
		this.folder = folder;
		this.suffix = suffix;
	}

}
