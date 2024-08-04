package net.fexcraft.app.fmt.utils.fvtm;

import java.util.HashMap;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartFunctionConfigReference {

	public static final HashMap<String, ConfigReference> REFERENCES = new HashMap<>();
	static {
		REFERENCES.put("fvtm:wheel", new ConfigReference());
		REFERENCES.put("fvtm:wheel_positions", new ConfigReference(){
			@Override
			public void init(){
				entries.add(of(OBJECT).add(
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
	}

}
