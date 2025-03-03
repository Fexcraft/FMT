package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Widget;
import com.spinyowl.legui.style.Style;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PackDevEnv extends Widget {

	public static PackDevEnv INSTANCE;
	public static int def_width = 600;
	public static int def_height = 480;

	public PackDevEnv(){
		super(Settings.WORKSPACE_NAME.value);
		setSize(def_width, def_height);
	}

	public static void toggle(){
		if(INSTANCE == null) FMT.FRAME.getContainer().add(INSTANCE = new PackDevEnv());
		else if(visible()) INSTANCE.hide();
		else INSTANCE.show();
	}

	public static boolean visible(){
		return INSTANCE.getStyle().getDisplay() != Style.DisplayType.NONE;
	}

}
