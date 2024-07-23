package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
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
		entries.add(of("CreativeTab", TEXT).def("default", true));
		entries.add(of("Description", ARRAY_SIMPLE).add(of(TEXT).def("description entry", true)));
		entries.add(of("Categories", ARRAY_SIMPLE).alt("Category").add(TEXT_ENTRY));
		entries.add(of("Model", MODELLOC));
		entries.add(of("ModelData", SEPARATE));
		entries.add(of("Textures", ARRAY_SIMPLE).add(of(TEXLOC)).alt("Texture"));
		entries.add(of("ColorChannels", OBJECT_KEY_VAL).add(of(COLOR).def("#fdfdfd", true)));
		entries.add(of("ItemTexture", TEXLOC));
		entries.add(of("Disable3DItemModel", BOOLEAN).def(false));
		entries.add(of("VehicleType", ENUM).enums(vehtypes));
		entries.add(of("MaxKeys", INTEGER).limit(5, 1, 16));
		entries.add(of("ImpactWrench", INTEGER).limit(0, 0, 8));
		entries.add(of("KeyType", TEXT).def("gep:key", true));
		entries.add(of("Attributes", OBJECT).add(
			of("type", ENUM).enums(attrtypes),
			of("min", INTEGER).limit(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
			of("max", INTEGER).limit(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
			of("access", ARRAY_SIMPLE).add(of(TEXT)),
			of("target", TEXT).def("vehicle"),
			of("group", TEXT).def(""),
			of("perm", TEXT).def(""),
			of("sync", BOOLEAN).def(false),
			of("editable", BOOLEAN).def(true),
			of("icons", OBJECT_KEY_VAL).add(of(TEXT)),
			of("keys", OBJECT_KEY_VAL).add(of(TEXT)),
			of("mod-dep", ARRAY_SIMPLE).add(of(TEXT))
		));
		entries.add(of("WheelPositions", OBJECT).add(
			of("pos", VECTOR_ARRAY),
			of("mirror", BOOLEAN),
			of("hubsize", DECIMAL).limit(0, 0, 16),
			of("max_radius", DECIMAL).alt("radius").limit(0.75f, 0.0625f),
			of("min_wheel_radius", DECIMAL).alt("radius").limit(0.25f, 0.0625f),
			of("min_tire_radius", DECIMAL).alt("radius").limit(0.5f, 0.0625f),
			of("max_width", DECIMAL).alt("width").limit(0.75f, 0.0625f),
			of("min_wheel_width", DECIMAL).alt("width").limit(0.25f, 0.0625f),
			of("min_tire_width", DECIMAL).alt("width").limit(0.25f, 0.0625f),
			of("steering", BOOLEAN),
			of("required", BOOLEAN),
			of("relative", BOOLEAN),
			of("powered", TEXT),
			of("braking", BOOLEAN)
		));
		entries.add(of("SimplePhysics", OBJECT_KEY_VAL).static_(true).add(
			of("MaxPositiveThrottle", DECIMAL).limit(1f, 0f),
			of("MaxNegativeThrottle", DECIMAL).limit(1f, 0f),
			of("TurnLeftModifier", DECIMAL).limit(1f, 0f),
			of("TurnRightModifier", DECIMAL).limit(1f, 0f),
			of("WheelStepHeight", DECIMAL).limit(1f, 0f),
			of("WheelSpringStrength", DECIMAL).limit(0.5f, 0f),
			of("Bouyancy", DECIMAL).limit(0.25f, 0f),
			of("TrailerAdjustmentAxe", INTEGER).limit(1, 0)
		));
		entries.add(of("Trailer", BOOLEAN).def(false));
		entries.add(of("Wagon", BOOLEAN).def(false));
		entries.add(of("Tracked", BOOLEAN).def(false));
		entries.add(of("CouplerRange", DECIMAL).limit(1f, 0.01f, 4f));
		entries.add(of("InstalledParts", OBJECT_KEY_VAL).add(of(TEXT)));
		entries.add(of("SwivelPoints", OBJECT).add(
			of("pos", VECTOR_ARRAY),
			of("parent", TEXT).def("vehicle"),
			of("yaw", DECIMAL).limit(0, -180, 180),
			of("pitch", DECIMAL).limit(0, -180, 180),
			of("roll", DECIMAL).limit(0, -180, 180),
			of("movers", ARRAY).add(
				of(OBJECT).add(
					of("attribute", TEXT).required(),
					of("var", TEXT).def("x"),
					of("speed", DECIMAL).limit(1f, 0f),
					of("min", DECIMAL).limit(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("max", DECIMAL).limit(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("def", DECIMAL).limit(1f, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("loop", BOOLEAN)
				)
			)
		));
		entries.add(of("Sounds", OBJECT));
		entries.add(of("LiftingPoints", OBJECT));
		entries.add(of("PartSlots", OBJECT));
		entries.add(of("Catalog", OBJECT));
		entries.add(of("InteractZones", OBJECT));

	}

	@Override
	public List<ConfigEntry> getEntries() {
		return entries;
	}

	public String[] vehtypes = new String[]{ "LAND", "WATER", "AIR", "RAIL", "HELI", "SPACE" };
	public String[] attrtypes = new String[]{ "float", "integer", "boolean", "tristate", "string", "vector" };

}
