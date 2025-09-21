package net.fexcraft.app.fmt.nui;

import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Hint extends Element {

	public Hint(){
		super();
	}

	@Override
	public void init(Object... args){
		z = 999;
		color(RGB.WHITE);
		text(args[0].toString());
	}

	@Override
	public Element text(Object ntext){
		w = FontRenderer.getWidth(ntext.toString(), FontRenderer.FontType.PLAIN) + 16;
		h = FontRenderer.getHeight(ntext.toString(), FontRenderer.FontType.PLAIN) + 4;
		rounded = true;
		return super.text(ntext).recompile();
	}
}
