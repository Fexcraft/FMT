package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;
import java.util.List;

import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.TEXT_ENTRY;
import static net.fexcraft.app.fmt.utils.fvtm.ConfigEntry.of;
import static net.fexcraft.app.fmt.utils.fvtm.EntryType.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ModelDataReference extends ConfigReference {

	public static ModelDataReference INSTANCE = new ModelDataReference();

	public ModelDataReference(){
		entries.add(of("Authors", ARRAY_SIMPLE).alt("Creators").add(TEXT_ENTRY));
		entries.add(of("TextureWidth", INTEGER).limit(256, 8, 892));
		entries.add(of("TextureHeight", INTEGER).limit(256, 8, 892));
		entries.add(of("Programs", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Transforms", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("SmoothShading", BOOLEAN).def(false));
		entries.add(of("Include", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Pivots", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("Offset", ARRAY_SIMPLE).add(TEXT_ENTRY));
		entries.add(of("FlipU", BOOLEAN).def(false));
		entries.add(of("FlipV", BOOLEAN).def(false));
		entries.add(of("FlipAxes", BOOLEAN).def(false));
		entries.add(of("FlipFaces", BOOLEAN).def(false));
		entries.add(of("SkipNormals", BOOLEAN).def(false));
	}

}
