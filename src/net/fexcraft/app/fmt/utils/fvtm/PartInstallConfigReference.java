package net.fexcraft.app.fmt.utils.fvtm;

import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PartInstallConfigReference {

	public static ConfigReference DEFAULT = new ConfigReference(){
		@Override
		public void init(){
			entries.add(of("Handler", STATIC));
			entries.add(of("Removable", BOOLEAN).def(true));
			entries.add(of("Swappable", BOOLEAN));
			//entries.add(of("ConstOnly", BOOLEAN));
			entries.add(of("Compatible", ARRAY_SIMPLE).add(TEXT_ENTRY));
			entries.add(of("Incompatible", OBJECT_KEY_VAL).add(of(ARRAY_SIMPLE).add(TEXT_ENTRY)));
			entries.add(of("Required", OBJECT_KEY_VAL).add(of(ARRAY_SIMPLE).add(TEXT_ENTRY)));
		}
	};
	public static ConfigReference WHEEL = new ConfigReference(){
		@Override
		public void init(){
			entries.add(of("Handler", STATIC));
			entries.add(of("Radius", DECIMAL).limit(1f, 0f, 64f));
			entries.add(of("Width", DECIMAL).limit(0.25f, 0f, 64f));
			entries.add(of("HubSize", DECIMAL).limit(0f, 0f, 64f));
			entries.add(of("Removable", BOOLEAN).def(true));
			entries.add(of("Tireless", BOOLEAN).def(true));
		}
	};
	public static ConfigReference TIRE = new ConfigReference(){
		@Override
		public void init(){
			entries.add(of("Handler", STATIC));
			entries.add(of("OuterRadius", DECIMAL).limit(1f, 0f, 64f));
			entries.add(of("InnerRadius", DECIMAL).limit(0.75f, 0f, 64f));
			entries.add(of("Width", DECIMAL).limit(0.25f, 0f, 64f));
			entries.add(of("Removable", BOOLEAN).def(true));
		}
	};
	public static ConfigReference BOGIE = new ConfigReference(){
		@Override
		public void init(){
			entries.add(of("Handler", STATIC));
			entries.add(of("Removable", BOOLEAN).def(true));
			entries.add(of("Height", DECIMAL).limit(0.5f, 0f, 64f));
		}
	};

}
