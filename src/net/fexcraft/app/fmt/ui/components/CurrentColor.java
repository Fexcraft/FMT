package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.ColorField;

public class CurrentColor extends EditorComponent {

	public CurrentColor(){
		super("painter.current", 110, false, true);
		this.add(new Label(translate(LANG_PREFIX + id + ".color"), L5, row(1), LW, HEIGHT));
		this.add(new ColorField(this, (c, b) -> {
			TexturePainter.updateColor(c, true);
		}, L5, row(1) + 0f, LW, HEIGHT + 0f).apply(TexturePainter.PRIMARY.packed));
		this.add(new ColorField(this, (c, b) -> {
			TexturePainter.updateColor(c, true);
		}, L5, row(1) + 0f, LW, HEIGHT + 0f).apply(TexturePainter.SECONDARY.packed));
	}

}
