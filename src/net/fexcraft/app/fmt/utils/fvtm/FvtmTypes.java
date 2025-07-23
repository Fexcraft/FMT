package net.fexcraft.app.fmt.utils.fvtm;

import net.fexcraft.app.fmt.animation.AnimRef;
import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.animation.AttrTranslator;
import net.fexcraft.app.fmt.animation.Translator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmTypes {

	public static ArrayList<ProgRef> PROGRAMS = new ArrayList<>();
	public static String[] PROGRAM_CATS;
	static {
		String general = "General";
		String fvtm_l = "FVTM Lights";
		String fvtm_r = "FVTM Rail";
		String fvtm_a = "FVTM Attribute Based";
		String fvtm_s = "FVTM Signs";
		String misc = "FVTM Misc";
		PROGRAM_CATS = new String[]{ general, fvtm_l, fvtm_a, fvtm_r, misc };
		//
		addProg(new Translator(), general, "Translator", "fvtm:translator");
 		//
		addProg(fvtm_l, "Glow/Light", "fvtm:glow");
		addProg(fvtm_l, "Vehicle Lights", "fvtm:lights");
		addProg(fvtm_l, "Vehicle Lights - Front", "fvtm:front_lights");
		addProg(fvtm_l, "Vehicle Lights - Back/Rear", "fvtm:back_lights");
		addProg(fvtm_l, "Vehicle Lights - Long", "fvtm:long_lights");
		addProg(fvtm_l, "Vehicle Lights - Fog", "fvtm:fog_lights");
		addProg(fvtm_l, "Vehicle Lights - Brake", "fvtm:brake_lights");
		addProg(fvtm_l, "Vehicle Lights - Reverse", "fvtm:reverse_lights");
		addProg(fvtm_l, "Vehicle Lights - Warning", "fvtm:warning_lights");
		addProg(fvtm_l, "Turn Indicator/Signal Left", "fvtm:turn_signal_left");
		addProg(fvtm_l, "Turn Indicator/Signal Right", "fvtm:turn_signal_right");
		addProg(fvtm_l, "Back Lights + Signal Left", "fvtm:back_lights_signal_left");
		addProg(fvtm_l, "Back Lights + Signal Right", "fvtm:back_lights_signal_right");
		addProg(fvtm_l, "Signal/Emergency Lights Channel 0", "fvtm:signal_lights_0");
		addProg(fvtm_l, "Signal/Emergency Lights Channel 1", "fvtm:signal_lights_1");
		addProg(fvtm_l, "Signal/Emergency Lights Channel 2", "fvtm:signal_lights_2");
		addProg(fvtm_l, "Signal/Emergency Lights Channel 3", "fvtm:signal_lights_3");
		//
		addProg(fvtm_r, "Rail Vehicle Lights - Forward", "fvtm:lights_rail_forward");
		addProg(fvtm_r, "Rail Vehicle Lights - Backward", "fvtm:lights_rail_backward");
		addProg(fvtm_r, "Bogie/Truck Auto-Rotation", "fvtm:bogie_auto");
		addProg(fvtm_r, "Bogie/Truck Auto-Rotation (Opposite)", "fvtm:bogie_auto_opposite");
		addProg(fvtm_r, "Bogie/Truck Auto-Rotation (Front)", "fvtm:bogie_front");
		addProg(fvtm_r, "Bogie/Truck Auto-Rotation (Rear)", "fvtm:bogie_rear");
		//
		addProg(new AttrTranslator(), fvtm_a, "Attr. Translator", "fvtm:attribute_translator",
			"attribute:s", "bool-type:b", "min:f", "max:f", "step:f", "axe:i:0:2");
		addProg(fvtm_a, "Attr. Lights", "fvtm:attribute_lights", "attribute:s");
		addProg(fvtm_a, "Attr. Lights - Signal/Emergency", "fvtm:attribute_signal_lights", "attribute:s", "channel:i:0:3");
		//
		addProg(fvtm_s, "Sign Base", "fvtm:sign_base");
		//
		addProg(misc, "Category Specific Visiblity", "fvtm:category_specific", "category:s");
		addProg(misc, "RGB/Color Channel", "fvtm:rgb_channel");
	}

	private static void addProg(String... arr){
		addProg(new AnimRef(arr[2]), arr);
	}

	private static void addProg(Animation anim, String... arr){
		PROGRAMS.add(new ProgRef(arr[0], arr[1], arr[2], anim, Arrays.copyOfRange(arr, 3, arr.length)));
	}

	public static ProgRef getProgRef(String id){
		for(ProgRef prog : PROGRAMS){
			if(prog.id.equals(id)) return prog;
		}
		return null;
	}

	public static record ProgRef(String cat, String name, String id, Animation anim, String... args){

		@Override
		public String toString(){
			return name;
		}

	}

}
