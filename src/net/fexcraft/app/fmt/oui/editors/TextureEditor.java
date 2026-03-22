package net.fexcraft.app.fmt.oui.editors;

import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.oui.components.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureEditor extends Editor {

	public TextureEditor(){
		super("painter", "Painting Utils", false);
		addComponent(new CurrentColor());
		addComponent(new PainterPalette());
		addComponent(new PainterTools());
	}

}
