package net.fexcraft.app.fmt.nui.editor;

import net.fexcraft.app.fmt.nui.Element;

import static net.fexcraft.app.fmt.nui.editor.EditorTab.FF;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class SideButton extends Element {

	public SideButton(int idx, int y, String texture){
		pos(FF - 15 - (idx * 25), y + 5);
		size(20, 20);
		texture(texture);
	}

}
