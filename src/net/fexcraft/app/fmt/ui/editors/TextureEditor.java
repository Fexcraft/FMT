package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureEditor extends Editor {

	public TextureEditor(){
		super("painter", "Painting Utils", false);
		addComponent(new CurrentColor());
		addComponent(new PainterPaletteGradient(true));
		addComponent(new PainterPaletteSpectrum(true));
		addComponent(new PainterPaletteCustom());
		addComponent(new PainterTools());
	}

}
