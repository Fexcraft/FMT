package net.fexcraft.app.fmt.ui.editors;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.components.CurrentColor;
import net.fexcraft.app.fmt.ui.components.ModelExports;
import net.fexcraft.app.fmt.ui.components.ModelGeneral;
import net.fexcraft.app.fmt.ui.components.MultiplierComponent;
import net.fexcraft.app.fmt.ui.components.PainterPaletteGradient;
import net.fexcraft.app.fmt.ui.components.PainterPaletteSpectrum;
import net.fexcraft.app.fmt.ui.components.PainterTools;
import net.fexcraft.app.fmt.ui.components.QuickAdd;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureEditor extends Editor {

	public TextureEditor(){
		super("painter", "Painting Utils", false);
		if(Settings.SHOW_QUICK_ADD.value) addComponent(new QuickAdd());
		addComponent(new MultiplierComponent());
		addComponent(new CurrentColor());
		addComponent(new PainterPaletteGradient(true));
		addComponent(new PainterPaletteSpectrum(true));
		addComponent(new PainterTools());
	}

}
