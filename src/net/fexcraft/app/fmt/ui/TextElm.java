package net.fexcraft.app.fmt.ui;

import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextElm extends Element {

	public TextElm(float x, float y, float w){
		this(x, y, w, "");
	}

	public TextElm(float x, float y, float w, String text, Object... format){
		super();
		shape(ElmShape.NONE);
		pos(x, y);
		size(w, FS);
		translate(text, format);
	}

	public TextElm(float x, float y, float w, String text, RGB col){
		this(x, y, w, text);
		shape(ElmShape.RECTANGLE);
		hoverable(true);
		color(col);
	}

}
