package net.fexcraft.app.fmt.ui;

import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class RunElm extends Element {

	public RunElm(float x, float y, float w, String text, Consumer<ClickInfo> cons){
		pos(x, y);
		size(w, FS);
		onclick(cons);
		translate(text);
		color(GENERIC_FIELD.value);
		text_pos(5, -2);
		hoverable = true;
	}

}
