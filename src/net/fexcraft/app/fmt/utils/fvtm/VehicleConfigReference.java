package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

import java.util.ArrayList;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class VehicleConfigReference extends ConfigReference {

	public static String[] vehtypes = new String[]{ "LAND", "WATER", "AIR", "RAIL", "HELI", "SPACE" };
	public static String[] attrtypes = new String[]{ "float", "integer", "boolean", "tristate", "string", "vector" };
	public static String[] spvars = new String[]{ "x", "y", "z", "yaw", "pitch", "roll" };
	//
	public static VehicleConfigReference INSTANCE = new VehicleConfigReference();

	public VehicleConfigReference(){
		entries.add(of("Addon", PACKID).required());
		entries.add(of("ID", TEXT).required());
		entries.add(of("Name", TEXT).def("Unnamed Vehicle", true));
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
		addAttrsEntry(entries);
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
		addPointsEntry(entries);
		entries.add(of("Sounds", OBJECT).add(
			of("volume", DECIMAL).limit(1, 0),
			of("pitch", DECIMAL).limit(1, 0)
		));
		entries.add(of("LiftingPoints", OBJECT).conv((key, val) -> {
			JsonMap map = new JsonMap();
			JsonArray arr = val.asArray();
			map.add("pos", new JsonArray(arr.get(0).float_value(), arr.get(1).float_value(), arr.get(2).float_value()));
			if(arr.size() > 3) map.add("pair", arr.get(3));
			if(arr.size() > 4) map.add("offset", arr.get(4));
			return map;
		}).add(
			of("pos", VECTOR_ARRAY).required(),
			of("pair", TEXT),
			of("offset", DECIMAL)
		));
		entries.add(of("PartSlots", OBJECT).conv((key, val) -> {
			JsonMap map = new JsonMap();
			JsonArray arr = val.asArray();
			if(arr.size() > 0) map.add("pos", new JsonArray(arr.get(0).float_value(), arr.get(1).float_value(), arr.get(2).float_value()));
			if(arr.size() > 3) map.add("type", arr.get(3).string_value());
			if(arr.size() > 4) map.add("radius", arr.get(4).float_value());
			if(arr.size() > 5) map.add("rot", arr.get(5).copy());
			if(arr.size() > 6) map.add("point", arr.get(6));
			return map;
		}).add(
			of("pos", VECTOR_ARRAY),
			of("type", TEXT),
			of("radius", DECIMAL).limit(0.25f, 0.0625f),
			of("rot", VECTOR_ARRAY),
			of("point", TEXT).def("vehicle")
		));
		entries.add(of("Catalog", OBJECT).add(
			of("name", TEXT).def("Catalog Entry Name"),
			of("parts", OBJECT_KEY_VAL).add(of(TEXT).def("pack-id:part-id")),
			of("recipe", ARRAY_SIMPLE).add(of(TEXT).def("pack-id:item-id")),
			of("colors", OBJECT_KEY_VAL).add(of(COLOR)),
			of("scale", DECIMAL).limit(1, 0.01f)
		));
		entries.add(of("InteractZones", OBJECT).conv((key, val) -> {
			JsonMap map = new JsonMap();
			JsonArray arr = val.asArray();
			if(arr.size() > 0) map.add("pos", new JsonArray(arr.get(0).float_value(), arr.get(1).float_value(), arr.get(2).float_value()));
			if(arr.size() > 3) map.add("range", arr.get(3).float_value());
			if(arr.size() > 4) map.add("point", arr.get(4).string_value());
			return map;
		}).add(
			of("pos", VECTOR_ARRAY),
			of("range", DECIMAL).limit(4, 1),
			of("point", TEXT).def("vehicle")
			//of("mode", ENUM).enums("none", "set", "expand")
		));
		entries.add(of("Events", ARRAY).add(TEXT_ENTRY));
		entries.add(of("DefaultSeats", OBJECT).add(
			of("pos", VECTOR_ARRAY),
			of("driver", BOOLEAN),
			of("min_yaw", DECIMAL).limit(-90f, -180, 180),
			of("max_yaw", DECIMAL).limit(90f, -180, 180),
			of("min_pitch", DECIMAL).limit(-80f, -180, 180),
			of("max_pitch", DECIMAL).limit(80f, -180, 180),
			of("def_yaw", DECIMAL).limit(0f, -180, 180),
			of("def_pitch", DECIMAL).limit(0f, -180, 180),
			of("sitting", BOOLEAN).def(true),
			of("swivel_point", TEXT).def("vehicle"),
			of("scale", DECIMAL).limit(1f, 0.06725f, 128f),
			of("filter", ARRAY_SIMPLE).add(TEXT_ENTRY),
			of("dismount", VECTOR_ARRAY)
		));
	}

	public static void addAttrsEntry(ArrayList entries){
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
	}

	public static void addPointsEntry(ArrayList entries){
		entries.add(of("SwivelPoints", OBJECT).add(
			of("pos", VECTOR_ARRAY),
			of("parent", TEXT).def("vehicle"),
			of("yaw", DECIMAL).limit(0, -180, 180),
			of("pitch", DECIMAL).limit(0, -180, 180),
			of("roll", DECIMAL).limit(0, -180, 180),
			of("movers", ARRAY).add(
				of(OBJECT_KEY_VAL).static_(true).add(
					of("attribute", TEXT).required(),
					of("var", ENUM).enums(spvars),
					of("speed", DECIMAL).limit(1f, 0f),
					of("min", DECIMAL).limit(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("max", DECIMAL).limit(Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("def", DECIMAL).limit(1f, Integer.MIN_VALUE, Integer.MAX_VALUE),
					of("loop", BOOLEAN)
				)
			)
		));
	}

}
