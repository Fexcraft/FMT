package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.style.Style.DisplayType;

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

	public static final void show(boolean bool, Component com){
		com.getStyle().setDisplay(bool ? DisplayType.MANUAL : DisplayType.NONE);
	}

}
