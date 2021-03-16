package net.fexcraft.app.fmt.ui.editor;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;

public class ShapeboxLabel extends Label {

	public ShapeboxLabel(int index, EditorWidget shapebox, String string, int x, int y, int w, int h){
		super(string, x, y, w, h);
		this.getStyle().setPadding(0, 0, 0, 25f);
		Panel colorpanel = new Panel(x, y, h, h);
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			colorpanel.getStyle().getBackground().setColor(FMTB.rgba(ShapeboxWrapper.cornercolors2[index]));
		});
		shapebox.getContainer().add(colorpanel);
	}

}
