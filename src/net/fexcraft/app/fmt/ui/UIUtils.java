package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.style.Style.DisplayType;

public class UIUtils {

	public static final void hide(Component com){
		com.getStyle().setDisplay(DisplayType.NONE);
	}

	public static final void show(Component com){
		com.getStyle().setDisplay(DisplayType.MANUAL);
	}

	public static final void hide(Component... coms){
		for(Component com : coms) hide(com);
	}

	public static final void show(Component... coms){
		for(Component com : coms) show(com);
	}

}
