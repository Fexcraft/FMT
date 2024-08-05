package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

import java.util.HashMap;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartFunctionConfigReference {

	public static String[] invtypes = new String[]{ "item", "fluid", "energy", "container", "variable" };
	public static String[] contypes = new String[]{ "MEDIUM", "MICRO", "TINY", "SMALL", "LARGE" };
	public static final HashMap<String, ConfigReference> REFERENCES = new HashMap<>();
	static {
		REFERENCES.put("fvtm:wheel", new ConfigReference());
		REFERENCES.put("fvtm:bogie", new ConfigReference());
		REFERENCES.put("fvtm:wheel_positions", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:wheel_positions", OBJECT).add(
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
			}
		});
		REFERENCES.put("fvtm:seats", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:seats", OBJECT).add(
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
					of("relative", BOOLEAN),
					of("scale", DECIMAL).limit(1f, 0.06725f, 128f),
					of("filter", ARRAY_SIMPLE).add(TEXT_ENTRY)
				));
			}
		});
		REFERENCES.put("fvtm:engine", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("engine_speed", DECIMAL).limit(0.245f, 0, 64f));
				entries.add(of("idle_consumption", INTEGER).limit(1, 1, 500));
				entries.add(of("active_consumption", INTEGER).limit(1, 1, 500));
				entries.add(of("fuel_group", ARRAY_SIMPLE).add(of(TEXT).def("diesel")));
				entries.add(of("consumptions", OBJECT_KEY_VAL).add(of(DECIMAL)));
				entries.add(of("torque_chart", OBJECT_KEY_VAL).add(of(DECIMAL)));
				entries.add(of("min_rpm", INTEGER).limit(1000, 1, 1000000));
				entries.add(of("max_rpm", INTEGER).limit(6000, 1, 1000000));
			}
		});
		REFERENCES.put("fvtm:inventory", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("type", ENUM).enums(invtypes));
				entries.add(of("capacity", INTEGER).limit(0, 1, 6400000));
				entries.add(of("fluid", TEXT).def("minecraft:water"));
				entries.add(of("seats", ARRAY_SIMPLE).add(TEXT_ENTRY));
			}
		});
		REFERENCES.put("fvtm:container", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("type", ENUM).enums(contypes));
				entries.add(of("pos", VECTOR_ARRAY));
				entries.add(of("rot", DECIMAL).limit(0, -180, 180));
				entries.add(of("length", INTEGER).limit(6, 1, 24));
				entries.add(of("point", TEXT));
			}
		});
		REFERENCES.put("fvtm:part_slots", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:part_slots", OBJECT).conv((key, val) -> {
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
				entries.add(of("copy_rot", BOOLEAN));
			}
		});
		REFERENCES.put("fvtm:connector", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("pos", VECTOR_ARRAY));
				entries.add(of("type", ARRAY_SIMPLE).add(TEXT_ENTRY));
			}
		});
		REFERENCES.put("fvtm:color", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:color", OBJECT_KEY_VAL).add(of(COLOR)));
			}
		});
		REFERENCES.put("fvtm:tire", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("general_grip", DECIMAL).limit(1f, 0, 64f));
				entries.add(of("brake_grip", DECIMAL).limit(0.7f, 0, 64f));
				entries.add(of("stiffness", DECIMAL).limit(5.2f, 0, 64f));
				entries.add(of("steering_stiffness", DECIMAL).limit(5f, 0, 64f));
				entries.add(of("step_height", DECIMAL).limit(1f, 0, 64f));
				entries.add(of("material_table", STATIC).def("//TODO"));
			}
		});
		REFERENCES.put("fvtm:transmission", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("gear_ratios", ARRAY_SIMPLE).add(of(DECIMAL)));
				entries.add(of("automatic", BOOLEAN));
				entries.add(of("throttle_ratios_up", ARRAY_SIMPLE).add(of(DECIMAL)));
				entries.add(of("throttle_ratios_down", ARRAY_SIMPLE).add(of(DECIMAL)));
				entries.add(of("efficiency", DECIMAL).limit(0.7f, 1, 64f));
				entries.add(of("shift_speed", INTEGER).limit(40, 1, 2000));
			}
		});
		REFERENCES.put("fvtm:particle_emitter", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:particle_emitter", STATIC).def("//TODO"));
			}
		});
		REFERENCES.put("fvtm:interact_zone", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of("fvtm:interact_zone", OBJECT).conv((key, val) -> {
					JsonMap map = new JsonMap();
					JsonArray arr = val.asArray();
					if(arr.size() > 0) map.add("pos", new JsonArray(arr.get(0).float_value(), arr.get(1).float_value(), arr.get(2).float_value()));
					if(arr.size() > 3) map.add("range", arr.get(3).float_value());
					if(arr.size() > 4) map.add("point", arr.get(4).string_value());
					return map;
				}).add(
					of("pos", VECTOR_ARRAY),
					of("range", DECIMAL).limit(4, 1),
					of("point", TEXT).def("vehicle"),
					of("mode", ENUM).enums("none", "set", "expand")
				));
			}
		});
	}

}
