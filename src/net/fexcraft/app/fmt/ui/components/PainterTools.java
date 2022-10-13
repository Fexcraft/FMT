package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import org.liquidengine.legui.component.Label;

import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.texture.TexturePainter.Selection;
import net.fexcraft.app.fmt.texture.TexturePainter.Tool;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Icon;

public class PainterTools extends EditorComponent {

	public PainterTools(){
		super("painter.tools", 150, false, false);
		this.add(new Label(translate(LANG_PREFIX + id + ".selection"), L5, row(1), LW, HEIGHT));
		int yoff = row(1), xoff = 4, size = 34, idx = 0;
		row += 9;
		for(Selection sel : Selection.values()){
			if(sel == Selection.NONE) continue;
			add(new Icon(idx++, size, 2, xoff, yoff, "./resources/textures/icons/painter/" + sel.name().toLowerCase() + ".png", () -> {
				TexturePainter.setSelection(sel);
			}).addTooltip(LANG_PREFIX + id + "." + sel.name().toLowerCase()));
		}
		this.add(new Label(translate(LANG_PREFIX + id + ".tool"), L5, row(1), LW, HEIGHT));
		yoff = row(1);
		idx = 0;
		for(Tool tool : Tool.values()){
			add(new Icon(idx++, size, 2, xoff, yoff, "./resources/textures/icons/painter/" + tool.name().toLowerCase() + ".png", () -> {
				TexturePainter.setTool(tool);
			}).addTooltip(LANG_PREFIX + id + "." + tool.name().toLowerCase()));
		}
	}

}
