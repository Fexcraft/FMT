package net.fexcraft.app.fmt.utils;

import net.fexcraft.app.fmt.utils.Picker.PickType;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Selector {

	public static PickType TYPE = PickType.POLYGON;

	public static void set(PickType sel){
		TYPE = sel;
	}

}
